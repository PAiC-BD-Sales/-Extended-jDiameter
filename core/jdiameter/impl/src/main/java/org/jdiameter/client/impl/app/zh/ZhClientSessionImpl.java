package org.jdiameter.client.impl.app.zh;

import org.jdiameter.api.Answer;
import org.jdiameter.api.EventListener;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.NetworkReqListener;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.Request;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.app.StateChangeListener;
import org.jdiameter.api.app.StateEvent;
import org.jdiameter.api.zh.ClientZhSession;
import org.jdiameter.api.zh.ClientZhSessionListener;
import org.jdiameter.api.zh.events.MultimediaAuthRequest;
import org.jdiameter.client.api.ISessionFactory;
import org.jdiameter.client.impl.app.slh.SLhClientSessionImpl;
import org.jdiameter.common.api.app.zh.IZhMessageFactory;
import org.jdiameter.common.api.app.zh.ZhSessionState;
import org.jdiameter.common.impl.app.AppRequestEventImpl;
import org.jdiameter.common.impl.app.zh.ZhSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:kennymendieta89@gmail.com"> Kenny Mendieta </a>
 *
 */
public class ZhClientSessionImpl extends ZhSession
        implements ClientZhSession, EventListener<Request, Answer>, NetworkReqListener {

  private static final Logger logger = LoggerFactory.getLogger(ZhClientSessionImpl.class);
  private transient ClientZhSessionListener listener;
  protected long appId = -1;
  protected IClientZhSessionData sessionData;
  public ZhClientSessionImpl(IClientZhSessionData sessionData, IZhMessageFactory fct, ISessionFactory sf, ClientZhSessionListener lst) {
    super(sf, sessionData);
    if (lst == null) {
      throw new IllegalArgumentException("Listener can not be null");
    }
    if (fct.getApplicationId() < 0) {
      throw new IllegalArgumentException("ApplicationId can not be less than zero");
    }

    this.appId = fct.getApplicationId();
    this.listener = lst;
    super.messageFactory = fct;
    this.sessionData = sessionData;
  }
  @Override
  public void receivedSuccessMessage(Request request, Answer answer) {
  }

  @Override
  public void timeoutExpired(Request request) {
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
  public boolean isStateless() {
    return false;
  }

  @Override
  public void addStateChangeNotification(StateChangeListener listener) {
  }
  @Override
  public void removeStateChangeNotification(StateChangeListener listener) {
  }
  @Override
  public boolean handleEvent(StateEvent event) throws InternalException, OverloadException {
    return false;
  }
  @Override
  public <E> E getState(Class<E> stateType) {
    return stateType == ZhSessionState.class ? (E) sessionData.getZhSessionState() : null;
  }
  @Override
  public void sendMultimediaAuthRequest(MultimediaAuthRequest request)
          throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
  }
  @Override
  public void onTimer(String timerName) {
  }

  private class RequestDelivery implements Runnable {
    ClientZhSession session;
    Request request;
    public void run() {
      try {
        switch (request.getCommandCode()) {
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
    ClientZhSession session;
    Answer answer;
    Request request;
    public void run() {

    }
  }
}
