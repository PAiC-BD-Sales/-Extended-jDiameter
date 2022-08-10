package org.jdiameter.server.impl.app.swm;

import org.jdiameter.api.Answer;
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
import org.jdiameter.api.swm.ServerSWmSession;
import org.jdiameter.api.swm.ServerSWmSessionListener;
import org.jdiameter.api.swm.events.SWmAbortSessionAnswer;
import org.jdiameter.api.swm.events.SWmAbortSessionRequest;
import org.jdiameter.api.swm.events.SWmDiameterEAPAnswer;
import org.jdiameter.api.swm.events.SWmDiameterEAPRequest;
import org.jdiameter.client.api.ISessionFactory;
import org.jdiameter.common.api.app.IAppSessionState;
import org.jdiameter.common.api.app.swm.ISWmMessageFactory;
import org.jdiameter.common.api.app.swm.IServerSWmSessionContext;
import org.jdiameter.common.api.app.swm.ServerSWmSessionState;
import org.jdiameter.common.impl.app.AppAnswerEventImpl;
import org.jdiameter.common.impl.app.AppRequestEventImpl;
import org.jdiameter.common.impl.app.swm.AppSWmSessionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 3GPP IMS SWM Reference Point Server session implementation
 *
 * @author <a href="mailto:enmanuelcalero61@gmail.com"> Enmanuel Calero </a>
 */
public class ServerSWmSessionImpl extends AppSWmSessionImpl implements ServerSWmSession, NetworkReqListener, EventListener<Request, Answer> {


  private static final Logger logger = LoggerFactory.getLogger(ServerSWmSessionImpl.class);

  // Session State Handling ---------------------------------------------------
  //protected boolean stateless = true;
  //protected ServerGxSessionState state = ServerGxSessionState.IDLE;
  protected Lock sendAndStateLock = new ReentrantLock();

  // Factories and Listeners --------------------------------------------------
  protected transient ISWmMessageFactory factory;
  protected transient IServerSWmSessionContext context;
  protected transient ServerSWmSessionListener listener;

  protected long[] authAppIds;
  //protected String originHost, originRealm;
  protected IServerSWmSessionData sessionData;

  public ServerSWmSessionImpl(IServerSWmSessionData sessionData, ISWmMessageFactory fct, ISessionFactory sf, ServerSWmSessionListener lst,
                              IServerSWmSessionContext ctx, StateChangeListener<AppSession> stLst) {
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
  public void receivedSuccessMessage(Request request, Answer answer) {
    ServerSWmSessionImpl.AnswerDelivery rd = new ServerSWmSessionImpl.AnswerDelivery();
    rd.session = this;
    rd.request = request;
    rd.answer = answer;
    super.scheduler.execute(rd);
  }

  @Override
  public Answer processRequest(Request request) {
    ServerSWmSessionImpl.RequestDelivery rd = new ServerSWmSessionImpl.RequestDelivery();
    rd.session = this;
    rd.request = request;
    super.scheduler.execute(rd);
    return null;
  }

  @Override
  public boolean isStateless() {
    return this.sessionData.isStateless();
  }

  @Override
  public boolean isReplicable() {
    return true;
  }

  @Override
  public boolean handleEvent(StateEvent event) throws InternalException, OverloadException {
    ServerSWmSessionState newState = null;

    try {
      sendAndStateLock.lock();

      // Can be null if there is no state transition, transition to IDLE state should terminate this app session
      final Event localEvent = (Event) event;
      final ServerSWmSessionState state = this.sessionData.getServerSWmSessionState();

      //Its kind of awkward, but with two state on server side its easier to go through event types?
      //but for sake of FSM readability
      final Event.Type eventType = (Event.Type) localEvent.getType();
      switch (state) {
        case IDLE:
          switch (eventType) {
            case RECEIVE_DER:
              listener.doDiameterEAPRequest(this, (SWmDiameterEAPRequest) localEvent.getRequest());
              break;
            case RECEIVE_AAR:
              //listener.doAARequest(this, (RxAARequest) localEvent.getRequest());
              break;

            case RECEIVE_EVENT_REQUEST:
              // Current State: IDLE
              // Event: AA event request received and successfully processed
              // Action: Send AA event answer
              // New State: IDLE
              //listener.doAARequest(this, (RxAARequest) localEvent.getRequest());
              break;

            // case SEND_EVENT_ANSWER:
            // // Current State: IDLE
            // // Event: AAR event request received and successfully processed
            // // Action: Send AA event answer
            // // New State: IDLE
            //
            // newState = ServerRxSessionState.IDLE;
            // dispatchEvent(localEvent.getAnswer());
            // break;

            case SEND_AAA:
                            /*
                            RxAAAnswer answer = (RxAAAnswer) localEvent.getAnswer();
                            try {
                                long resultCode = answer.getResultCodeAvp().getUnsigned32();
                                // Current State: IDLE
                                // Event: AA initial request received and successfully processed
                                // Action: Send AAinitial answer
                                // New State: OPEN
                                if (isSuccess(resultCode)) {
                                    newState = ServerRxSessionState.OPEN;
                                } // Current State: IDLE
                                // Event: AA initial request received but not successfully processed
                                // Action: Send AA initial answer with Result-Code != SUCCESS
                                // New State: IDLE
                                else {
                                    newState = ServerRxSessionState.IDLE;
                                }
                                dispatchEvent(localEvent.getAnswer());
                            }
                            catch (AvpDataException e) {
                                throw new InternalException(e);
                            }

                             */
              break;
            default:
              throw new InternalException("Wrong state: " + ServerSWmSessionState.IDLE + " one event: " + eventType + " " + localEvent.getRequest() + " " +
                      localEvent.getAnswer());
          } //end switch eventType
          break;

        case OPEN:
          switch (eventType) {
            case RECEIVE_DER:
              listener.doDiameterEAPRequest(this, (SWmDiameterEAPRequest) localEvent.getRequest());
            case RECEIVE_AAR:
              // listener.doAARequest(this, (RxAARequest) localEvent.getRequest());
              break;

            case SEND_AAA:
                            /*
                            RxAAAnswer answer = (RxAAAnswer) localEvent.getAnswer();
                            try {
                                if (isSuccess(answer.getResultCodeAvp().getUnsigned32())) {
                                    // Current State: OPEN
                                    // Event: AA update request received and successfully processed
                                    // Action: Send AA update answer
                                    // New State: OPEN
                                }
                                else {
                                    // Current State: OPEN
                                    // Event: AA update request received but not successfully processed
                                    // Action: Send AA update answer with Result-Code != SUCCESS
                                    // New State: IDLE
                                    // It's a failure, we wait for Tcc to fire -- FIXME: Alexandre: Should we?
                                    newState = ServerRxSessionState.IDLE;
                                }
                            }
                            catch (AvpDataException e) {
                                throw new InternalException(e);
                            }
                            dispatchEvent(localEvent.getAnswer());

                             */
              break;
            case RECEIVE_STR:
              // listener.doSessionTermRequest(this, (RxSessionTermRequest) localEvent.getRequest());
              break;
            case SEND_STA:
                            /*
                            RxSessionTermAnswer STA = (RxSessionTermAnswer) localEvent.getAnswer();
                            try {
                                if (isSuccess(STA.getResultCodeAvp().getUnsigned32())) {
                                    // Current State: OPEN
                                    // Event: AA update request received and successfully processed
                                    // Action: Send AA update answer
                                    // New State: OPEN
                                }
                                else {
                                    // Current State: OPEN
                                    // Event: AA update request received but not successfully processed
                                    // Action: Send AA update answer with Result-Code != SUCCESS
                                    // New State: IDLE
                                    // It's a failure, we wait for Tcc to fire -- FIXME: Alexandre: Should we?
                                    newState = ServerRxSessionState.IDLE;
                                }
                            }
                            catch (AvpDataException e) {
                                throw new InternalException(e);
                            }
                            finally {
                                newState = ServerRxSessionState.IDLE;
                            }
                            dispatchEvent(localEvent.getAnswer());

                             */
              break;

            case RECEIVE_RAA:
              //listener.doReAuthAnswer(this, (RxReAuthRequest) localEvent.getRequest(), (RxReAuthAnswer) localEvent.getAnswer());
              break;
            case SEND_RAR:
              //dispatchEvent(localEvent.getRequest());
              break;
            case RECEIVE_ASA:
              listener.doAbortSessionAnswer(this, (SWmAbortSessionRequest) localEvent.getRequest(), (SWmAbortSessionAnswer) localEvent.getAnswer());
              break;
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

  @Override
  @SuppressWarnings("unchecked")
  public <E> E getState(Class<E> stateType) {
    return stateType == ServerSWmSessionState.class ? (E) this.sessionData.getServerSWmSessionState() : null;
  }

  @Override
  public void sendDiameterEAPAnswer(SWmDiameterEAPAnswer answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {

  }

  @Override
  public void sendAbortSessionRequest(SWmAbortSessionRequest request) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {

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

  protected void setState(ServerSWmSessionState newState) {
    setState(newState, true);
  }

  @SuppressWarnings("unchecked")
  protected void setState(ServerSWmSessionState newState, boolean release) {
    IAppSessionState<ServerSWmSessionState> oldState = this.sessionData.getServerSWmSessionState();
    this.sessionData.setServerSWmSessionState(newState);

    for (StateChangeListener i : stateListeners) {
      i.stateChanged(this, (Enum) oldState, (Enum) newState);
    }
    if (newState == ServerSWmSessionState.IDLE) {
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

    ServerSWmSession session;
    Request request;

    @Override
    public void run() {
      try {
        switch (request.getCommandCode()) {
                    /*
                    case RxAARequest.code:
                        handleEvent(new org.jdiameter.server.impl.app.rx.Event(true, factory.createAARequest(request), null));
                        break;
                    case RxSessionTermRequest.code:
                        handleEvent(new org.jdiameter.server.impl.app.rx.Event(true, factory.createSessionTermRequest(request), null));
                        break;

                     */
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

    ServerSWmSession session;
    Answer answer;
    Request request;

    @Override
    public void run() {
      try {
        // FIXME: baranowb: add message validation here!!!
        // We handle CCR, STR, ACR, ASR other go into extension
        switch (request.getCommandCode()) {
                    /*
                    case RxReAuthRequest.code:
                        handleEvent(new org.jdiameter.server.impl.app.rx.Event(org.jdiameter.server.impl.app.rx.Event.Type.RECEIVE_RAA, factory.createReAuthRequest(request), factory.createReAuthAnswer(answer)));
                        break;

                     */
          case SWmAbortSessionRequest.code:
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
}
