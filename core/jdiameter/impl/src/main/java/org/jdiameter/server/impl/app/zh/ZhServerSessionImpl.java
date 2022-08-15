package org.jdiameter.server.impl.app.zh;

import org.jdiameter.api.Answer;
import org.jdiameter.api.EventListener;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.NetworkReqListener;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.Request;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.app.AppEvent;
import org.jdiameter.api.app.StateChangeListener;
import org.jdiameter.api.app.StateEvent;
import org.jdiameter.api.zh.ServerZhSession;
import org.jdiameter.api.zh.ServerZhSessionListener;
import org.jdiameter.api.zh.events.MultimediaAuthAnswer;
import org.jdiameter.client.api.ISessionFactory;
import org.jdiameter.common.api.app.zh.IZhMessageFactory;
import org.jdiameter.common.api.app.zh.ZhSessionState;
import org.jdiameter.common.impl.app.AppAnswerEventImpl;
import org.jdiameter.common.impl.app.AppRequestEventImpl;
import org.jdiameter.common.impl.app.zh.ZhSession;
import org.jdiameter.server.impl.app.zh.Event.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jdiameter.api.zh.events.MultimediaAuthRequest;

public class ZhServerSessionImpl extends ZhSession
        implements ServerZhSession, EventListener<Request, Answer>, NetworkReqListener {

  private static final Logger logger = LoggerFactory.getLogger(ZhServerSessionImpl.class);

    // Factories and Listeners
    // --------------------------------------------------
  private transient ServerZhSessionListener listener;
  protected long appId = -1;
  protected IServerZhSessionData sessionData;

  public ZhServerSessionImpl(IServerZhSessionData sessionData, IZhMessageFactory fct, ISessionFactory sf, ServerZhSessionListener lst) {
    super(sf, sessionData);
    if (lst == null) {
      throw new IllegalArgumentException("Listener can not be null");
    }
    if ((this.appId = fct.getApplicationId()) < 0) {
      throw new IllegalArgumentException("ApplicationId can not be less than zero");
    }

    this.listener = lst;
    super.messageFactory = fct;
    this.sessionData = sessionData;
  }

  @Override
  public void receivedSuccessMessage(Request request, Answer answer) {
    AnswerDelivery rd = new AnswerDelivery();
    rd.session = this;
    rd.request = request;
    rd.answer = answer;
    super.scheduler.execute(rd);
  }

  @Override
  public void timeoutExpired(Request request) {
    try {
      handleEvent(new Event(Event.Type.TIMEOUT_EXPIRES, new AppRequestEventImpl(request), null));
    } catch (Exception e) {
      logger.debug("Failed to process timeout message", e);
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
  public boolean handleEvent(StateEvent event) throws InternalException, OverloadException {
    try {
      sendAndStateLock.lock();
      if (!super.session.isValid()) {
        // FIXME: throw new InternalException("Generic session is not valid.");
        return false;
      }
      final ZhSessionState state = this.sessionData.getZhSessionState();
      ZhSessionState newState;
      Event localEvent = (Event) event;
      Event.Type eventType = (Type) event.getType();
      switch (state) {
        case NO_STATE_MAINTAINED:
          switch (eventType) {
            case RECEIVE_MAR:
              this.sessionData.setBuffer((Request) ((AppEvent) event.getData()).getMessage());
              super.cancelMsgTimer();
              super.startMsgTimer();
              newState = ZhSessionState.NO_STATE_MAINTAINED;
              setState(newState);
              listener.doMultimediaAuthRequestEvent(this, (MultimediaAuthRequest) event.getData());
              break;
            case SEND_MESSAGE:
              super.session.send(((AppEvent) event.getData()).getMessage(), this);
              newState = ZhSessionState.NO_STATE_MAINTAINED;
              setState(newState);
              break;
            default:
              logger.error("Wrong action in Zh Server FSM. State: NO_STATE_MAINTAINED, Event Type: {}", eventType);
              break;
          }
          break;
        case STATE_MAINTAINED:
          switch (eventType) {
            case TIMEOUT_EXPIRES:
              newState = ZhSessionState.TIMEOUT;
              setState(newState);
              break;
            case SEND_MESSAGE:
              try {
                super.session.send(((AppEvent) event.getData()).getMessage(), this);
              } finally {
                newState = ZhSessionState.TERMINATED;
                setState(newState);
              }
              break;
            default:
              throw new InternalException(
                                    "Should not receive more messages after initial. Command: " + event.getData());
          }
          break;
        case TIMEOUT:
          throw new InternalException("Cant receive message in state TIMEOUT. Command: " + event.getData());
        case TERMINATED:
          throw new InternalException("Cant receive message in state TERMINATED. Command: " + event.getData());
        default:
          logger.error("SLh Server FSM in wrong state: {}", state);
          break;
      }
    } catch (Exception e) {
      throw new InternalException(e);
    } finally {
      sendAndStateLock.unlock();
    }
    return true;
  }

  @Override
  public <E> E getState(Class<E> stateType) {
    return stateType == ZhSessionState.class ? (E) this.sessionData.getZhSessionState() : null;
  }

  protected void setState(ZhSessionState newState) {
    ZhSessionState oldState = this.sessionData.getZhSessionState();
    this.sessionData.setZhSessionState(newState);
    for (StateChangeListener i : stateListeners) {
      i.stateChanged(this, oldState, newState);
    }
    if (newState == ZhSessionState.TERMINATED || newState == ZhSessionState.TIMEOUT) {
      super.cancelMsgTimer();
      this.release();
    }
  }

  @Override
  public void sendMultimediaAuthAnswer(MultimediaAuthAnswer answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    send(Event.Type.SEND_MESSAGE, null, answer);
  }

  @Override
  public void onTimer(String timerName) {
    if (timerName.equals(ZhSession.TIMER_NAME_MSG_TIMEOUT)) {
      try {
        sendAndStateLock.lock();
        try {
          handleEvent(
              new Event(Event.Type.TIMEOUT_EXPIRES, new AppRequestEventImpl(this.sessionData.getBuffer()), null));
        } catch (Exception e) {
          logger.debug("Failure handling Timeout event.");
        }
        this.sessionData.setBuffer(null);
        this.sessionData.setTsTimerId(null);
      } finally {
        sendAndStateLock.unlock();
      }
    }
  }

  public void release() {
    if (isValid()) {
      try {
        sendAndStateLock.lock();
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

  protected void send(Event.Type type, AppEvent request, AppEvent answer) throws InternalException {
    try {
      if (type != null) {
        handleEvent(new Event(type, request, answer));
      }
    } catch (Exception e) {
      throw new InternalException(e);
    }
  }

  private class RequestDelivery implements Runnable {
    ServerZhSession session;
    Request request;
    public void run() {
      try {
        switch (request.getCommandCode()) {
          case MultimediaAuthRequest.code:
            handleEvent(new Event(Event.Type.RECEIVE_MAR, messageFactory.createMultimediaAuthRequest(request), null));
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
    ServerZhSession session;
    Answer answer;
    Request request;
    public void run() {
      try {
        switch (answer.getCommandCode()) {
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
