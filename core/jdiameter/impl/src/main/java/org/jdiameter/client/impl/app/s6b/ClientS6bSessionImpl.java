package org.jdiameter.client.impl.app.s6b;

import org.jdiameter.api.Answer;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.EventListener;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.Message;
import org.jdiameter.api.NetworkReqListener;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.Request;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppEvent;
import org.jdiameter.api.app.AppRequestEvent;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.app.StateChangeListener;
import org.jdiameter.api.app.StateEvent;
import org.jdiameter.api.s6b.ClientS6bSession;
import org.jdiameter.api.s6b.ClientS6bSessionListener;
import org.jdiameter.api.s6b.events.S6bAAAnswer;
import org.jdiameter.api.s6b.events.S6bAARequest;
import org.jdiameter.api.s6b.events.S6bAbortSessionAnswer;
import org.jdiameter.api.s6b.events.S6bAbortSessionRequest;
import org.jdiameter.api.s6b.events.S6bDiameterEAPAnswer;
import org.jdiameter.api.s6b.events.S6bDiameterEAPRequest;
import org.jdiameter.api.s6b.events.S6bReAuthRequest;
import org.jdiameter.api.s6b.events.S6bSessionTerminationAnswer;
import org.jdiameter.api.s6b.events.S6bSessionTerminationRequest;
import org.jdiameter.api.swm.events.SWmDiameterAAAnswer;
import org.jdiameter.api.swm.events.SWmDiameterAARequest;
import org.jdiameter.client.api.IContainer;
import org.jdiameter.client.api.IMessage;
import org.jdiameter.client.api.ISessionFactory;
import org.jdiameter.client.api.parser.IMessageParser;
import org.jdiameter.client.api.parser.ParseException;
import org.jdiameter.common.api.app.IAppSessionState;
import org.jdiameter.common.api.app.s6b.ClientS6bSessionState;
import org.jdiameter.common.api.app.s6b.IClientS6bSessionContext;
import org.jdiameter.common.api.app.s6b.IS6bMessageFactory;
import org.jdiameter.common.api.app.swm.ClientSWmSessionState;
import org.jdiameter.common.impl.app.AppAnswerEventImpl;
import org.jdiameter.common.impl.app.AppRequestEventImpl;
import org.jdiameter.common.impl.app.s6b.AppS6bSessionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author <a href="mailto:giokast90@gmail.com"> Giovanni Castillo </a>
 */
public class ClientS6bSessionImpl extends AppS6bSessionImpl implements ClientS6bSession, NetworkReqListener, EventListener<Request, Answer> {

  private static final Logger logger = LoggerFactory.getLogger(ClientS6bSessionImpl.class);

  // Session State Handling ---------------------------------------------------
  protected Lock sendAndStateLock = new ReentrantLock();

  // Factories and Listeners --------------------------------------------------
  protected transient IS6bMessageFactory factory;
  protected transient ClientS6bSessionListener listener;
  protected transient IClientS6bSessionContext context;
  protected transient IMessageParser parser;
  protected IClientS6bSessionData sessionData;

  protected long[] authAppIds = new long[]{4};
  // Requested Action + Credit-Control and Direct-Debiting Failure-Handling ---

  // Session State Handling ---------------------------------------------------
  protected boolean isEventBased = false;

  protected byte[] buffer;

  protected String originHost, originRealm;

  private static final long DIAMETER_UNABLE_TO_DELIVER = 3002L;
  private static final long DIAMETER_TOO_BUSY = 3004L;
  private static final long DIAMETER_LOOP_DETECTED = 3005L;
  protected static final Set<Long> temporaryErrorCodes;

  static {
    HashSet<Long> tmp = new HashSet<>();
    tmp.add(DIAMETER_UNABLE_TO_DELIVER);
    tmp.add(DIAMETER_TOO_BUSY);
    tmp.add(DIAMETER_LOOP_DETECTED);
    temporaryErrorCodes = Collections.unmodifiableSet(tmp);
  }

  // Session Based Queue
  protected ArrayList<Event> eventQueue = new ArrayList<>();

  public ClientS6bSessionImpl(IClientS6bSessionData sessionData, IS6bMessageFactory fct, ISessionFactory sf, ClientS6bSessionListener lst,
                              IClientS6bSessionContext ctx, StateChangeListener<AppSession> stLst) {
    super(sf, sessionData);
    if (lst == null) {
      throw new IllegalArgumentException("Listener can not be null");
    }
    if (fct.getApplicationIds() == null) {
      throw new IllegalArgumentException("ApplicationId can not be less than zero");
    }

    this.context = ctx;

    this.authAppIds = fct.getApplicationIds();
    this.listener = lst;
    this.factory = fct;

    IContainer icontainer = sf.getContainer();
    this.parser = icontainer.getAssemblerFacility().getComponentInstance(IMessageParser.class);
    this.sessionData = sessionData;
    super.addStateChangeNotification(stLst);
  }

  @Override
  public void sendSessionTerminationRequest(S6bSessionTerminationRequest request) throws InternalException, OverloadException {
    try {
      this.handleEvent(new Event(true, request, null));
    } catch (AvpDataException e) {
      throw new InternalException(e);
    }
  }

  @Override
  public void sendDiameterEAPRequest(S6bDiameterEAPRequest request) throws InternalException, OverloadException {
    this.handleEvent(new Event(Event.Type.SEND_DER, request, null));
  }

  @Override
  public void sendAbortSessionAnswer(S6bAbortSessionAnswer answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    this.handleEvent(new Event(Event.Type.SEND_ASA, null, answer));
  }

  @Override
  public void sendReAuthRequest(S6bReAuthRequest request) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    this.handleEvent(new Event(Event.Type.SEND_RAR, request, null));
  }

  @Override
  public void sendAARequest(S6bAARequest request) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    this.handleEvent(new Event(Event.Type.SEND_AAR, request, null));
  }

  @Override
  public void sendAbortSessionRequest(S6bAbortSessionRequest request)
          throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    this.handleEvent(new Event(Event.Type.SEND_ASR, request, null));
  }

  @Override
  public boolean isStateless() {
    return false;
  }

  public boolean isEventBased() {
    return this.isEventBased;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <E> E getState(Class<E> stateType) {
    return stateType == ClientS6bSessionState.class ? (E) sessionData.getClientS6bSessionState() : null;
  }

  @Override
  public boolean handleEvent(StateEvent event) throws InternalException, OverloadException {
    return this.isEventBased() ? handleEventForEventBased(event) : handleEventForSessionBased(event);
  }

  protected boolean handleEventForEventBased(StateEvent event) throws InternalException, OverloadException {
    try {
      sendAndStateLock.lock();
      final ClientS6bSessionState state = this.sessionData.getClientS6bSessionState();
      Event localEvent = (Event) event;
      Event.Type eventType = (Event.Type) localEvent.getType();
      switch (state) {
        case IDLE:
          switch (eventType) {
            case SEND_EVENT_REQUEST:
              // Current State: IDLE
              // Event: Client or device requests a one-time service
              // Action: Send AA event request
              // New State: PENDING_E
              setState(ClientS6bSessionState.PENDING_EVENT);
              try {
                dispatchEvent(localEvent.getRequest());
              }
              catch (Exception e) {
                // This handles failure to send in PendingI state in FSM table
                logger.debug("Failure handling send event request", e);
                handleSendFailure(e, eventType, localEvent.getRequest().getMessage());
              }
              break;
            default:
              logger.warn("Event Based Handling - Wrong event type ({}) on state {}", eventType, state);
              break;
          }
          break;

        case PENDING_EVENT:
          switch (eventType) {
            case RECEIVE_EVENT_ANSWER:
              AppAnswerEvent answer = (AppAnswerEvent) localEvent.getAnswer();
              try {
                long resultCode = answer.getResultCodeAvp().getUnsigned32();
                if (isSuccess(resultCode)) {
                  // Current State: PENDING_EVENT
                  // Event: Successful AA event answer received
                  // Action: Grant service to end user
                  // New State: IDLE
                  setState(ClientS6bSessionState.IDLE, false);
                }
                if (isProvisional(resultCode) || isFailure(resultCode)) {
                  handleFailureMessage(answer, (AppRequestEvent) localEvent.getRequest(), eventType);
                }
                deliverS6bAAAnswer((S6bAARequest) localEvent.getRequest(), (S6bAAAnswer) localEvent.getAnswer());
              } catch (AvpDataException e) {
                logger.debug("Failure handling received answer event", e);
                setState(ClientS6bSessionState.IDLE, false);
              }
              break;
            default:
              logger.warn("Event Based Handling - Wrong event type ({}) on state {}", eventType, state);
              break;
          }
        case PENDING_BUFFERED:
          switch (eventType) {
            case RECEIVE_EVENT_ANSWER:
              setState(ClientS6bSessionState.IDLE, false);
              buffer = null;
              deliverS6bAAAnswer((S6bAARequest) localEvent.getRequest(), (S6bAAAnswer) localEvent.getAnswer());
              break;
            default:
              logger.warn("Event Based Handling - Wrong event type ({}) on state {}", eventType, state);
              break;
          }

        default:
          logger.warn("Event Based Handling - Wrong event type ({}) on state {}", eventType, state);
          break;
      }

      dispatch();
      return true;
    } catch (Exception e) {
      throw new InternalException(e);
    } finally {
      sendAndStateLock.unlock();
    }
  }

  protected boolean handleEventForSessionBased(StateEvent event) throws InternalException, OverloadException {
    try {
      sendAndStateLock.lock();
      final ClientS6bSessionState state = this.sessionData.getClientS6bSessionState();
      Event localEvent = (Event) event;
      Event.Type eventType = (Event.Type) localEvent.getType();
      switch (state) {
        case IDLE:
          switch (eventType) {
            case SEND_AAR:
              break;
            default:
              logger.warn("Session Based Handling - Wrong event type ({}) on state {}", eventType, state);
              break;
          }
          break;
        case PENDING_AAR:
          switch (eventType) {
            case RECEIVE_AAA:
              break;
            case SEND_AAR:
            case SEND_STR:
              // Current State: PENDING_AAR
              // Event: User service terminated
              // Action: Queue termination event
              // New State: PENDING_AAR

              // Current State: PENDING_AAR
              // Event: Change in request
              // Action: Queue changed rating condition event
              // New State: PENDING_AAR
              eventQueue.add(localEvent);
              break;
            case RECEIVE_RAR:
            case SEND_RAA:
              break;
            case RECEIVE_ASR:
              deliverS6bAbortSessionRequest((S6bAbortSessionRequest) localEvent.getRequest());
              break;
            case SEND_ASA:
              try {
                dispatchEvent(localEvent.getAnswer());
              }
              catch (Exception e) {
                handleSendFailure(e, eventType, localEvent.getRequest().getMessage());
              }
              break;
            default:
              logger.warn("Session Based Handling - Wrong event type ({}) on state {}", eventType, state);
              break;
          }
          break;

        case PENDING_STR:
          AppAnswerEvent stanswer = (AppAnswerEvent) localEvent.getAnswer();
          switch (eventType) {
            case RECEIVE_STA:
              long resultCode = stanswer.getResultCodeAvp().getUnsigned32();
              if (isSuccess(resultCode)) {
                // Current State: PENDING_STR
                // Event: Successful ST answer received
                // New State: IDLE
                setState(ClientS6bSessionState.IDLE, false);
              } else if (isProvisional(resultCode) || isFailure(resultCode)) {
                handleFailureMessage(stanswer, (AppRequestEvent) localEvent.getRequest(), eventType);
              }
              deliverS6bSessionTerminationAnswer((S6bSessionTerminationRequest) localEvent.getRequest(), (S6bSessionTerminationAnswer) localEvent.getAnswer());
              break;
            case SEND_AAR:
              break;
            default:
              logger.warn("Session Based Handling - Wrong event type ({}) on state {}", eventType, state);
              break;
          }
          break;
        case OPEN:
          switch (eventType) {
            case SEND_DER:
              try {
                dispatchEvent(localEvent.getRequest());
              } catch (Exception e) {
                handleSendFailure(e, eventType, localEvent.getRequest().getMessage());
              }
              break;
            case RECEIVE_DEA:
              deliverDiameterEAPAnswer((S6bDiameterEAPRequest) localEvent.getRequest(), (S6bDiameterEAPAnswer) localEvent.getAnswer());
              break;
            case SEND_STR:
              // Current State: OPEN
              // Event: Session Termination event request received to be sent
              // Action: Terminate end user's service, send STR termination request
              // New State: PENDING STR

              setState(ClientS6bSessionState.PENDING_STR);
              try {
                dispatchEvent(localEvent.getRequest());
              } catch (Exception e) {
                handleSendFailure(e, eventType, localEvent.getRequest().getMessage());
              }
              break;
            case RECEIVE_RAR:
            case SEND_RAA:
              break;
            case RECEIVE_ASR:
              deliverS6bAbortSessionRequest((S6bAbortSessionRequest) localEvent.getRequest());
              break;
            case SEND_ASA:
              try {
                dispatchEvent(localEvent.getAnswer());
              }
              catch (Exception e) {
                handleSendFailure(e, eventType, localEvent.getRequest().getMessage());
              }
              break;
            default:
              logger.warn("Session Based Handling - Wrong event type ({}) on state {}", eventType, state);
              break;
          }
          break;
        default:
          // any other state is bad
          setState(ClientS6bSessionState.IDLE, true);
          break;
      }
      dispatch();
      return true;
    } catch (Exception e) {
      throw new InternalException(e);
    } finally {
      sendAndStateLock.unlock();
    }
  }

  @Override
  public Answer processRequest(Request request) {
    RequestDelivery rd = new RequestDelivery();
    rd.session = this;
    rd.request = request;
    super.scheduler.execute(rd);
    return null;
  }

  @Override
  public void receivedSuccessMessage(Request request, Answer answer) {
    AnswerDelivery ad = new AnswerDelivery();
    ad.session = this;
    ad.request = request;
    ad.answer = answer;
    super.scheduler.execute(ad);

  }

  @Override
  public void timeoutExpired(Request request) {
  }

  protected void setState(ClientS6bSessionState newState) {
    setState(newState, true);
  }

  @SuppressWarnings("unchecked")
  protected void setState(ClientS6bSessionState newState, boolean release) {
    try {
      IAppSessionState oldState = this.sessionData.getClientS6bSessionState();
      this.sessionData.setClientS6bSessionState(newState);
      for (StateChangeListener i : stateListeners) {
        i.stateChanged(this, (Enum) oldState, (Enum) newState);
      }

      if (newState == ClientS6bSessionState.IDLE) {
        if (release) {
          this.release();
        }

      }
    } catch (Exception e) {
      if (logger.isDebugEnabled()) {
        logger.debug("Failure switching to state " + this.sessionData.getClientS6bSessionState() + " (release=" + release + ")", e);
      }
    }
  }

  @Override
  public void release() {
    if (isValid()) {
      try {
        this.sendAndStateLock.lock();
        super.release();
      } catch (Exception e) {
        logger.debug("Failed to release session", e);
      } finally {
        sendAndStateLock.unlock();
      }
    } else {
      logger.debug("Trying to release an already invalid session, with Session ID '{}'", getSessionId());
    }
  }

  protected void handleSendFailure(Exception e, Event.Type eventType, Message request) throws Exception {
    logger.debug("Failed to send message, type: {} message: {}, failure: {}", new Object[]{eventType, request, e != null ? e.getLocalizedMessage() : ""});
  }

  protected void handleFailureMessage(final AppAnswerEvent event, final AppRequestEvent request, final Event.Type eventType) {
    try {
      setState(ClientS6bSessionState.IDLE);
    } catch (Exception e) {
      if (logger.isDebugEnabled()) {
        logger.debug("Failure handling failure message for Event " + event + " (" + eventType + ") and Request " + request, e);
      }
    }
  }

  /**
   * This makes checks on queue, moves it to proper state if event there is
   * present on Open state ;]
   */
  protected void dispatch() {
    // Event Based ----------------------------------------------------------
    if (isEventBased()) {
      // Current State: IDLE
      // Event: Request in storage
      // Action: Send stored request
      // New State: PENDING_B
      if (buffer != null) {
        setState(ClientS6bSessionState.PENDING_BUFFERED);
        try {
          dispatchEvent(new AppRequestEventImpl(messageFromBuffer(ByteBuffer.wrap(buffer))));
        } catch (Exception e) {
          try {
            handleSendFailure(e, Event.Type.SEND_EVENT_REQUEST, messageFromBuffer(ByteBuffer.wrap(buffer)));
          } catch (Exception e1) {
            logger.error("Failure handling buffer send failure", e1);
          }
        }
      }
    } // Session Based --------------------------------------------------------
    else {
      if (sessionData.getClientS6bSessionState() == ClientS6bSessionState.OPEN && eventQueue.size() > 0) {
        try {
          this.handleEvent(eventQueue.remove(0));
        } catch (Exception e) {
          logger.error("Failure handling queued event", e);
        }
      }
    }
  }

  protected void deliverDiameterEAPAnswer(S6bDiameterEAPRequest request, S6bDiameterEAPAnswer answer) {
    try {
      listener.doDiameterEAPAnswer(this, request, answer);
    } catch (Exception e) {
      logger.debug("Failure delivering DEA", e);
    }
  }

  protected void deliverS6bSessionTerminationAnswer(S6bSessionTerminationRequest request, S6bSessionTerminationAnswer answer) {
    try {
      listener.doSessionTerminationAnswer(this, request, answer);
    } catch (Exception e) {
      logger.warn("Failure delivering STA", e);
    }
  }

  protected void deliverS6bAbortSessionRequest(S6bAbortSessionRequest request) {
    try {
      listener.doAbortSessionRequest(this, request);
    }
    catch (Exception e) {
      logger.debug("Failure delivering ASR", e);
    }
  }

  protected void deliverS6bAAAnswer(S6bAARequest request, S6bAAAnswer answer) {
    try {
      listener.doAAAnswerEvent(this, request, answer);
    }
    catch (Exception e) {
      logger.debug("Failure delivering AAA", e);
    }
  }

  protected void dispatchEvent(AppEvent event) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    session.send(event.getMessage(), this);
  }

  protected boolean isProvisional(long resultCode) {
    return resultCode >= 1000 && resultCode < 2000;
  }

  protected boolean isSuccess(long resultCode) {
    return resultCode >= 2000 && resultCode < 3000;
  }

  protected boolean isFailure(long code) {
    return (!isProvisional(code) && !isSuccess(code) && ((code >= 3000 && code < 6000)) && !temporaryErrorCodes.contains(code));
  }

  /* (non-Javadoc)
   * @see org.jdiameter.common.impl.app.AppSessionImpl#isReplicable()
   */
  @Override
  public boolean isReplicable() {
    return true;
  }

  /* (non-Javadoc)
   * @see org.jdiameter.common.impl.app.AppSessionImpl#relink(org.jdiameter.client.api.IContainer)
   */
  private Message messageFromBuffer(ByteBuffer request) throws InternalException {
    if (request != null) {
      Message m;
      try {
        m = parser.createMessage(request);
        return m;
      } catch (AvpDataException e) {
        throw new InternalException("Failed to decode message.", e);
      }
    }
    return null;
  }

  private ByteBuffer messageToBuffer(IMessage msg) throws InternalException {
    try {
      return parser.encodeMessage(msg);
    } catch (ParseException e) {
      throw new InternalException("Failed to encode message.", e);
    }
  }

  private class RequestDelivery implements Runnable {
    ClientS6bSession session;
    Request request;

    @Override
    public void run() {
      try {
        switch (request.getCommandCode()) {
          case S6bAbortSessionRequest.code:
            handleEvent(new Event(Event.Type.RECEIVE_ASR, factory.createAbortSessionRequest(request), null));
            break;
          default:
            listener.doOtherEvent(session, new AppRequestEventImpl(request), null);
            break;
        }
      } catch (Exception e) {
        logger.debug("Failure processing request", e);
      }
    }
  }

  private class AnswerDelivery implements Runnable {

    ClientS6bSession session;
    Answer answer;
    Request request;

    @Override
    public void run() {
      try {
        switch (request.getCommandCode()) {
          case S6bSessionTerminationAnswer.code:
            final S6bSessionTerminationRequest mySTRequest = factory.createSessionTermRequest(request);
            final S6bSessionTerminationAnswer mySTAnswer = factory.createSessionTermAnswer(answer);
            handleEvent(new Event(false, mySTRequest, mySTAnswer));
            break;
          case S6bDiameterEAPAnswer.code:
            final S6bDiameterEAPRequest _DERequest = factory.createDiameterEAPRequest(request);
            final S6bDiameterEAPAnswer _DEAnswer = factory.createDiameterEAPAnswer(answer);
            handleEvent(new Event(false, _DERequest, _DEAnswer));
            break;
          default:
            listener.doOtherEvent(session, new AppRequestEventImpl(request), new AppAnswerEventImpl(answer));
            break;
        }
      } catch (Exception e) {
        logger.debug("Failure processing success message", e);
      }
    }
  }

  /* (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + Arrays.hashCode(authAppIds);
    result = prime * result + (isEventBased ? 1231 : 1237);
    result = prime * result + ((originHost == null) ? 0 : originHost.hashCode());
    result = prime * result + ((originRealm == null) ? 0 : originRealm.hashCode());
    result = prime * result + ((sessionData == null) ? 0 : (sessionData.getClientS6bSessionState() == null ? 0 :
                                                                    sessionData.getClientS6bSessionState().hashCode()));
    return result;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }

    ClientS6bSessionImpl other = (ClientS6bSessionImpl) obj;
    if (!Arrays.equals(authAppIds, other.authAppIds)) {
      return false;
    }
    if (isEventBased != other.isEventBased) {
      return false;
    }
    if (originHost == null) {
      if (other.originHost != null) {
        return false;
      }
    } else if (!originHost.equals(other.originHost)) {
      return false;
    }
    if (originRealm == null) {
      if (other.originRealm != null) {
        return false;
      }
    } else if (!originRealm.equals(other.originRealm)) {
      return false;
    }
    if (sessionData == null) {
      if (other.sessionData != null) {
        return false;
      }
    } else if (sessionData.getClientS6bSessionState() == null) {
      if (other.sessionData.getClientS6bSessionState() != null) {
        return false;
      }
    } else if (!sessionData.getClientS6bSessionState().equals(other.sessionData.getClientS6bSessionState())) {
      return false;
    }


    return true;
  }

  @Override
  public void onTimer(String timerName) {
    if (timerName.equals(IDLE_SESSION_TIMER_NAME)) {
      checkIdleAppSession();
    } else {
      logger.warn("Received an unknown timer '{}' for Session-ID '{}'", timerName, getSessionId());
    }
  }
}