package org.jdiameter.server.impl.app.s6b;

import org.jdiameter.api.Answer;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.EventListener;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
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
import org.jdiameter.api.s6b.ServerS6bSession;
import org.jdiameter.api.s6b.ServerS6bSessionListener;
import org.jdiameter.api.s6b.events.S6bAAAnswer;
import org.jdiameter.api.s6b.events.S6bAARequest;
import org.jdiameter.api.s6b.events.S6bAbortSessionAnswer;
import org.jdiameter.api.s6b.events.S6bAbortSessionRequest;
import org.jdiameter.api.s6b.events.S6bDiameterEAPAnswer;
import org.jdiameter.api.s6b.events.S6bDiameterEAPRequest;
import org.jdiameter.api.s6b.events.S6bReAuthAnswer;
import org.jdiameter.api.s6b.events.S6bReAuthRequest;
import org.jdiameter.api.s6b.events.S6bSessionTerminationAnswer;
import org.jdiameter.api.s6b.events.S6bSessionTerminationRequest;
import org.jdiameter.client.api.ISessionFactory;
import org.jdiameter.common.api.app.IAppSessionState;
import org.jdiameter.common.api.app.s6b.IS6bMessageFactory;
import org.jdiameter.common.api.app.s6b.IServerS6bSessionContext;
import org.jdiameter.common.api.app.s6b.ServerS6bSessionState;
import org.jdiameter.common.impl.app.AppAnswerEventImpl;
import org.jdiameter.common.impl.app.AppRequestEventImpl;
import org.jdiameter.common.impl.app.s6b.AppS6bSessionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author <a href="mailto:giokast90@gmail.com"> Giovanni Castillo </a>
 */
public class ServerS6bSessionImpl extends AppS6bSessionImpl implements ServerS6bSession, NetworkReqListener, EventListener<Request, Answer> {

  private static final Logger logger = LoggerFactory.getLogger(ServerS6bSessionImpl.class);

  protected Lock sendAndStateLock = new ReentrantLock();

  // Factories and Listeners --------------------------------------------------
  protected transient IS6bMessageFactory factory = null;
  protected transient IServerS6bSessionContext context = null;
  protected transient ServerS6bSessionListener listener = null;

  protected long[] authAppIds = new long[]{4};
  protected IServerS6bSessionData sessionData;

  public ServerS6bSessionImpl(IServerS6bSessionData sessionData, IS6bMessageFactory fct, ISessionFactory sf, ServerS6bSessionListener lst,
                              IServerS6bSessionContext ctx, StateChangeListener<AppSession> stLst) {
    super(sf, sessionData);
    if (lst == null) {
      throw new IllegalArgumentException("Listener can not be null");
    }
    if (fct.getApplicationIds() == null) {
      throw new IllegalArgumentException("ApplicationId can not be less than zero");
    }

    context = ctx;

    authAppIds = fct.getApplicationIds();
    listener = lst;
    factory = fct;
    this.sessionData = sessionData;
    super.addStateChangeNotification(stLst);
  }

  @Override
  public void sendSessionTerminationAnswer(S6bSessionTerminationAnswer answer) throws InternalException, OverloadException {
    handleEvent(new Event(false, null, answer));
  }

  @Override
  public void sendDiameterEAPAnswer(S6bDiameterEAPAnswer answer) throws InternalException, OverloadException {
    handleEvent(new Event(false, null, answer));
  }

  @Override
  public void sendAbortSessionRequest(S6bAbortSessionRequest request)
          throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    send(Event.Type.SEND_ASR, request, null);
  }

  @Override
  public void sendAbortSessionAnswer(S6bAbortSessionAnswer answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {

  }

  @Override
  public void sendReAuthAnswer(S6bReAuthAnswer answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {

  }

  @Override
  public void sendAAAnswer(S6bAAAnswer answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {

  }

  @Override
  public boolean isStateless() {
    return this.sessionData.isStateless();
  }

  @Override
  @SuppressWarnings("unchecked")
  public <E> E getState(Class<E> stateType) {
    return stateType == ServerS6bSessionState.class ? (E) this.sessionData.getServerS6bSessionState() : null;
  }

  @Override
  public boolean handleEvent(StateEvent event) throws InternalException, OverloadException {
    ServerS6bSessionState newState = null;

    try {
      sendAndStateLock.lock();

      // Can be null if there is no state transition, transition to IDLE state should terminate this app session
      final Event localEvent = (Event) event;
      final ServerS6bSessionState state = this.sessionData.getServerS6bSessionState();

      //Its kind of awkward, but with two state on server side its easier to go through event types?
      //but for sake of FSM readability
      final Event.Type eventType = (Event.Type) localEvent.getType();
      switch (state) {
        case IDLE:
          switch (eventType) {
            case RECEIVE_DER:
              listener.doDiameterEAPRequest(this, (S6bDiameterEAPRequest) localEvent.getRequest());
              break;
            case SEND_DEA:
              dispatchEvent(localEvent.getAnswer());
              break;
            case RECEIVE_AAR:
              break;
            case RECEIVE_EVENT_REQUEST:
              listener.doAARequestEvent(this, (S6bAARequest) localEvent.getRequest());
              break;
            case SEND_EVENT_ANSWER:
              break;
            case SEND_AAA:
              S6bAAAnswer answer = (S6bAAAnswer) localEvent.getAnswer();
              try {
                long resultCode = answer.getResultCodeAvp().getUnsigned32();
                // Current State: IDLE
                // Event: AA initial request received and successfully processed
                // Action: Send AAinitial answer
                // New State: OPEN
                if (isSuccess(resultCode)) {
                  newState = ServerS6bSessionState.OPEN;
                } else {
                  newState = ServerS6bSessionState.IDLE;
                }
                dispatchEvent(localEvent.getAnswer());
              } catch (AvpDataException e) {
                throw new InternalException(e);
              }
              break;
            default:
              throw new InternalException("Wrong state: " + ServerS6bSessionState.IDLE + " one event: " + eventType + " " + localEvent.getRequest() + " " +
                      localEvent.getAnswer());
          } //end switch eventType
          break;

        case OPEN:
          switch (eventType) {
            case RECEIVE_DER:
              listener.doDiameterEAPRequest(this, (S6bDiameterEAPRequest) localEvent.getRequest());
              break;
            case SEND_DEA:
              dispatchEvent(localEvent.getAnswer());
              break;
            case RECEIVE_AAR:
              listener.doAARequestEvent(this, (S6bAARequest) localEvent.getRequest());
              break;
            case SEND_AAA:
              S6bAAAnswer answer = (S6bAAAnswer) localEvent.getAnswer();
              try {
                if (isSuccess(answer.getResultCodeAvp().getUnsigned32())) {
                  logger.info("Sending AAA with result code " + answer.getResultCodeAvp().getUnsigned32());
                  newState = ServerS6bSessionState.OPEN;
                } else {
                  newState = ServerS6bSessionState.IDLE;
                }
              } catch (AvpDataException e) {
                throw new InternalException(e);
              }
              dispatchEvent(localEvent.getAnswer());
              break;
            case RECEIVE_STR:
              listener.doSessionTerminationRequest(this, (S6bSessionTerminationRequest) localEvent.getRequest());
              break;
            case SEND_STA:
              S6bSessionTerminationAnswer STA = (S6bSessionTerminationAnswer) localEvent.getAnswer();
              try {
                if (isSuccess(STA.getResultCodeAvp().getUnsigned32())) {
                  // Current State: OPEN
                  // Event: AA update request received and successfully processed
                  // Action: Send AA update answer
                  // New State: OPEN
                } else {
                  // Current State: OPEN
                  // Event: AA update request received but not successfully processed
                  // Action: Send AA update answer with Result-Code != SUCCESS
                  // New State: IDLE
                  // It's a failure, we wait for Tcc to fire -- FIXME: Alexandre: Should we?
                  newState = ServerS6bSessionState.IDLE;
                }
              } catch (AvpDataException e) {
                throw new InternalException(e);
              } finally {
                newState = ServerS6bSessionState.IDLE;
              }
              dispatchEvent(localEvent.getAnswer());
              break;
            case RECEIVE_RAA:
              listener.doReAuthAnswerEvent(this, (S6bReAuthRequest) localEvent.getRequest(), (S6bReAuthAnswer) localEvent.getAnswer());
              break;
            case RECEIVE_ASA:
              listener.doAbortSessionAnswer(this, (S6bAbortSessionRequest) localEvent.getRequest(), (S6bAbortSessionAnswer) localEvent.getAnswer());
              break;
            case SEND_RAR:
            case SEND_ASR:
              dispatchEvent(localEvent.getRequest());
              break;
          } //end switch eventtype
          break;
      }
      return true;
    } catch (Exception e) {
      throw new InternalException(e);
    } finally {
      if (newState != null) {
        setState(newState);
      }
      sendAndStateLock.unlock();
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see org.jdiameter.common.impl.app.AppSessionImpl#isReplicable()
   */
  @Override
  public boolean isReplicable() {
    return true;
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
    AnswerDelivery rd = new AnswerDelivery();
    rd.session = this;
    rd.request = request;
    rd.answer = answer;
    super.scheduler.execute(rd);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.jdiameter.common.impl.app.AppSessionImpl#onTimer(java.lang.String)
   */
  @Override
  public void onTimer(String timerName) {
    if (timerName.equals(IDLE_SESSION_TIMER_NAME)) {
      checkIdleAppSession();
    } else {
      logger.warn("Received an unknown timer '{}' for Session-ID '{}'", timerName, getSessionId());
    }
  }

  @Override
  public void timeoutExpired(Request request) {
    //  context.timeoutExpired(request);
    //FIXME: Should we release ?
  }

  protected boolean isProvisional(long resultCode) {
    return resultCode >= 1000 && resultCode < 2000;
  }

  protected boolean isSuccess(long resultCode) {
    return resultCode >= 2000 && resultCode < 3000;
  }

  protected void setState(ServerS6bSessionState newState) {
    setState(newState, true);
  }

  @SuppressWarnings("unchecked")
  protected void setState(ServerS6bSessionState newState, boolean release) {
    IAppSessionState oldState = this.sessionData.getServerS6bSessionState();
    this.sessionData.setServerS6bSessionState(newState);

    for (StateChangeListener i : stateListeners) {
      i.stateChanged(this, (Enum) oldState, (Enum) newState);
    }
    if (newState == ServerS6bSessionState.IDLE) {
      if (release) {
        // NOTE: do EVERYTHING before release.
        this.release();
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

  protected void send(Event.Type type, AppRequestEvent request, AppAnswerEvent answer) throws InternalException {
    try {
      sendAndStateLock.lock();
      if (type != null) {
        handleEvent(new Event(type, request, answer));
      }
    } catch (Exception e) {
      throw new InternalException(e);
    } finally {
      sendAndStateLock.unlock();
    }
  }

  protected void dispatchEvent(AppEvent event) throws InternalException {
    try {
      session.send(event.getMessage(), this);
      // Store last destination information
    } catch (Exception e) {
      //throw new InternalException(e);
      logger.debug("Failure trying to dispatch event", e);
    }
  }

  private class RequestDelivery implements Runnable {

    ServerS6bSession session;
    Request request;

    @Override
    public void run() {
      try {
        switch (request.getCommandCode()) {
          case S6bDiameterEAPRequest.code:
            handleEvent(new Event(true, factory.createDiameterEAPRequest(request), null));
            break;
          case S6bSessionTerminationRequest.code:
            handleEvent(new Event(true, factory.createSessionTermRequest(request), null));
            break;
          default:
            listener.doOtherEvent(session, new AppRequestEventImpl(request), null);
            break;
        }
      } catch (Exception e) {
        logger.debug("Failed to process request message", e);
      }
    }
  }

  private class AnswerDelivery implements Runnable {

    ServerS6bSession session;
    Answer answer;
    Request request;

    @Override
    public void run() {
      try {
        // FIXME: baranowb: add message validation here!!!
        // We handle CCR, STR, ACR, ASR other go into extension
        switch (request.getCommandCode()) {
          case S6bAbortSessionRequest.code:
            handleEvent(new Event(Event.Type.RECEIVE_ASA, factory.createAbortSessionRequest(request), factory.createAbortSessionAnswer(answer)));
            break;
          default:
            listener.doOtherEvent(session, new AppRequestEventImpl(request), new AppAnswerEventImpl(answer));
            break;
        }
      } catch (Exception e) {
        logger.debug("Failed to process success message", e);
      }
    }
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((sessionData == null) ? 0 : sessionData.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!super.equals(obj)) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    ServerS6bSessionImpl other = (ServerS6bSessionImpl) obj;
    if (sessionData == null) {
      if (other.sessionData != null) {
        return false;
      }
    } else if (!sessionData.equals(other.sessionData)) {
      return false;
    }

    return true;
  }

}
