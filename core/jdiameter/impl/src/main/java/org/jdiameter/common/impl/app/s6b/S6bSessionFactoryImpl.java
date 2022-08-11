package org.jdiameter.common.impl.app.s6b;

import org.jdiameter.api.Answer;
import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.Message;
import org.jdiameter.api.Request;
import org.jdiameter.api.SessionFactory;
import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppRequestEvent;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.app.StateChangeListener;
import org.jdiameter.api.s6b.ClientS6bSession;
import org.jdiameter.api.s6b.ClientS6bSessionListener;
import org.jdiameter.api.s6b.ServerS6bSession;
import org.jdiameter.api.s6b.ServerS6bSessionListener;
import org.jdiameter.api.s6b.events.S6bSessionTerminationAnswer;
import org.jdiameter.api.s6b.events.S6bSessionTerminationRequest;
import org.jdiameter.client.api.ISessionFactory;
import org.jdiameter.client.impl.app.s6b.ClientS6bSessionImpl;
import org.jdiameter.client.impl.app.s6b.IClientS6bSessionData;
import org.jdiameter.common.api.app.IAppSessionDataFactory;
import org.jdiameter.common.api.app.s6b.IClientS6bSessionContext;
import org.jdiameter.common.api.app.s6b.IS6bMessageFactory;
import org.jdiameter.common.api.app.s6b.IS6bSessionData;
import org.jdiameter.common.api.app.s6b.IS6bSessionFactory;
import org.jdiameter.common.api.app.s6b.IServerS6bSessionContext;
import org.jdiameter.common.api.data.ISessionDatasource;
import org.jdiameter.server.impl.app.s6b.IServerS6bSessionData;
import org.jdiameter.server.impl.app.s6b.ServerS6bSessionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ScheduledFuture;

/**
 * Default Diameter S6b Session Factory implementation.
 *
 * @author <a href="mailto:giokast90@gmail.com"> Giovanni Castillo </a>
 */
public class S6bSessionFactoryImpl implements IS6bSessionFactory, ClientS6bSessionListener, ServerS6bSessionListener, StateChangeListener<AppSession>,
                                                      IS6bMessageFactory, IServerS6bSessionContext, IClientS6bSessionContext {

  // Message timeout value (in milliseconds)
  protected int defaultDirectDebitingFailureHandling = 0;
  protected int defaultAAFailureHandling = 0;
  // its seconds
  protected long defaultValidityTime = 60;
  protected long defaultTxTimerValue = 30;
  // local not replicated listeners:
  protected ClientS6bSessionListener clientSessionListener;
  protected ServerS6bSessionListener serverSessionListener;
  protected StateChangeListener<AppSession> stateListener;
  protected IServerS6bSessionContext serverContextListener;
  protected IClientS6bSessionContext clientContextListener;
  protected IS6bMessageFactory messageFactory;
  protected static final Logger logger = LoggerFactory.getLogger(S6bSessionFactoryImpl.class);
  protected ISessionDatasource iss;
  protected ISessionFactory sessionFactory = null;
  protected IAppSessionDataFactory<IS6bSessionData> sessionDataFactory;

  public S6bSessionFactoryImpl(SessionFactory sessionFactory) {
    super();

    this.sessionFactory = (ISessionFactory) sessionFactory;
    this.iss = this.sessionFactory.getContainer().getAssemblerFacility().getComponentInstance(ISessionDatasource.class);
    this.sessionDataFactory = (IAppSessionDataFactory<IS6bSessionData>) this.iss.getDataFactory(IS6bSessionData.class);

    if (this.sessionDataFactory == null) {
      logger.debug("Initialized S6b SessionDataFactory is null");
    }
  }

  public S6bSessionFactoryImpl(SessionFactory sessionFactory, int defaultDirectDebitingFailureHandling, int defaultAAFailureHandling,
                               long defaultValidityTime, long defaultTxTimerValue) {
    this(sessionFactory);

    this.defaultDirectDebitingFailureHandling = defaultDirectDebitingFailureHandling;
    this.defaultAAFailureHandling = defaultAAFailureHandling;
    this.defaultValidityTime = defaultValidityTime;
    this.defaultTxTimerValue = defaultTxTimerValue;
  }

  /**
   * @return the clientSessionListener
   */
  @Override
  public ClientS6bSessionListener getClientSessionListener() {
    return clientSessionListener != null ? clientSessionListener : this;
  }

  /**
   * @param clientSessionListener the clientSessionListener to set
   */
  @Override
  public void setClientSessionListener(final ClientS6bSessionListener clientSessionListener) {
    this.clientSessionListener = clientSessionListener;
  }

  /**
   * @return the serverSessionListener
   */
  @Override
  public ServerS6bSessionListener getServerSessionListener() {
    return serverSessionListener != null ? serverSessionListener : this;
  }

  /**
   * @param serverSessionListener the serverSessionListener to set
   */
  @Override
  public void setServerSessionListener(ServerS6bSessionListener serverSessionListener) {
    this.serverSessionListener = serverSessionListener;
  }

  /**
   * @return the serverContextListener
   */
  @Override
  public IServerS6bSessionContext getServerContextListener() {
    return serverContextListener != null ? serverContextListener : this;
  }

  /**
   * @param serverContextListener the serverContextListener to set
   */
  @Override
  public void setServerContextListener(IServerS6bSessionContext serverContextListener) {
    this.serverContextListener = serverContextListener;
  }

  /**
   * @return the clientContextListener
   */
  @Override
  public IClientS6bSessionContext getClientContextListener() {
    return clientContextListener != null ? clientContextListener : this;
  }

  /**
   * @return the messageFactory
   */
  @Override
  public IS6bMessageFactory getMessageFactory() {
    return messageFactory != null ? messageFactory : this;
  }

  /**
   * @param messageFactory the messageFactory to set
   */
  @Override
  public void setMessageFactory(final IS6bMessageFactory messageFactory) {
    this.messageFactory = messageFactory;
  }

  /**
   * @param clientContextListener the clientContextListener to set
   */
  @Override
  public void setClientContextListener(IClientS6bSessionContext clientContextListener) {
    this.clientContextListener = clientContextListener;
  }

  /**
   * @return the sessionFactory
   */
  public SessionFactory getSessionFactory() {
    return sessionFactory;
  }

  /**
   * @param sessionFactory the sessionFactory to set
   */
  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = (ISessionFactory) sessionFactory;
  }

  /**
   * @return the stateListener
   */
  @Override
  public StateChangeListener<AppSession> getStateListener() {
    return stateListener != null ? stateListener : this;
  }

  /**
   * @param stateListener the stateListener to set
   */
  @Override
  public void setStateListener(StateChangeListener<AppSession> stateListener) {
    this.stateListener = stateListener;
  }

  @Override
  public AppSession getSession(String sessionId, Class<? extends AppSession> aClass) {
    if (sessionId == null) {
      throw new IllegalArgumentException("SessionId must not be null");
    }
    if (!this.iss.exists(sessionId)) {
      return null;
    }
    AppSession appSession = null;
    try {
      if (aClass == ClientS6bSession.class) {
        IClientS6bSessionData sessionData = (IClientS6bSessionData) this.sessionDataFactory.getAppSessionData(ClientS6bSession.class, sessionId);
        ClientS6bSessionImpl clientSession = new ClientS6bSessionImpl(sessionData, this.getMessageFactory(), sessionFactory, this.getClientSessionListener(), this.getClientContextListener(), this.getStateListener());

        clientSession.getSessions().get(0).setRequestListener(clientSession);
        appSession = clientSession;
      } else if (aClass == ServerS6bSession.class) {
        IServerS6bSessionData sessionData = (IServerS6bSessionData) this.sessionDataFactory.getAppSessionData(ServerS6bSession.class, sessionId);
        ServerS6bSessionImpl serverSession = new ServerS6bSessionImpl(sessionData, this.getMessageFactory(), sessionFactory, this.getServerSessionListener(), this.getServerContextListener(), this.getStateListener());

        serverSession.getSessions().get(0).setRequestListener(serverSession);
        appSession = serverSession;
      } else {
        throw new IllegalArgumentException("Wrong session class: " + aClass + ". Supported[" + ClientS6bSession.class + "," + ServerS6bSession.class + "]");
      }
    } catch (Exception e) {
      logger.error("Failure to obtain new S6b Session.", e);
    }

    return appSession;
  }


  @Override
  public AppSession getNewSession(String sessionId, Class<? extends AppSession> aClass, ApplicationId applicationId, Object[] args) {
    AppSession appSession = null;
    try {
      // FIXME:
      if (aClass == ClientS6bSession.class) {
        if (sessionId == null) {
          if (args != null && args.length > 0 && args[0] instanceof Request) {
            Request request = (Request) args[0];
            sessionId = request.getSessionId();
          } else {
            sessionId = this.sessionFactory.getSessionId();
          }
        }
        IClientS6bSessionData sessionData = (IClientS6bSessionData) this.sessionDataFactory.getAppSessionData(ClientS6bSession.class, sessionId);
        sessionData.setApplicationId(applicationId);
        ClientS6bSessionImpl clientSession = new ClientS6bSessionImpl(sessionData, this.getMessageFactory(), sessionFactory, this.getClientSessionListener(), this.getClientContextListener(), this.getStateListener());
        // this goes first!
        iss.addSession(clientSession);
        clientSession.getSessions().get(0).setRequestListener(clientSession);
        appSession = clientSession;
      } else if (aClass == ServerS6bSession.class) {
        if (sessionId == null) {
          if (args != null && args.length > 0 && args[0] instanceof Request) {
            Request request = (Request) args[0];
            sessionId = request.getSessionId();
          } else {
            sessionId = this.sessionFactory.getSessionId();
          }
        }
        IServerS6bSessionData sessionData = (IServerS6bSessionData) this.sessionDataFactory.getAppSessionData(ServerS6bSession.class, sessionId);
        sessionData.setApplicationId(applicationId);
        ServerS6bSessionImpl serverSession = new ServerS6bSessionImpl(sessionData, this.getMessageFactory(), sessionFactory, this.getServerSessionListener(), this.getServerContextListener(), this.getStateListener());
        iss.addSession(serverSession);
        serverSession.getSessions().get(0).setRequestListener(serverSession);
        appSession = serverSession;
      } else {
        throw new IllegalArgumentException("Wrong session class: " + aClass + ". Supported[" + ClientS6bSession.class + "," + ServerS6bSession.class + "]");
      }
    } catch (Exception e) {
      logger.error("Failure to obtain new S6b Session.", e);
    }

    return appSession;
  }


  // Default implementation of methods so there are no exception!

  // Message Handlers --------------------------------------------------------
  @Override
  public void doSessionTerminationRequest(ServerS6bSession session, S6bSessionTerminationRequest request) throws InternalException {
  }

  @Override
  public void doSessionTerminationAnswer(ClientS6bSession session, S6bSessionTerminationRequest request, S6bSessionTerminationAnswer answer) throws InternalException {
  }

  @Override
  public void doOtherEvent(AppSession session, AppRequestEvent request, AppAnswerEvent answer) throws InternalException {
  }

  // Message Factory Methods -------------------------------------------------
  @Override
  public S6bSessionTerminationRequest createSessionTermRequest(Request request) {
    return new S6bSessionTerminationRequestImpl(request);
  }

  @Override
  public S6bSessionTerminationAnswer createSessionTermAnswer(Answer answer) {
    return new S6bSessionTerminationAnswerImpl(answer);
  }

  // Context Methods ----------------------------------------------------------
  @Override
  public void stateChanged(Enum oldState, Enum newState) {
    logger.info("Diameter S6b SessionFactory :: stateChanged :: oldState[{}], newState[{}]", oldState, newState);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.jdiameter.api.app.StateChangeListener#stateChanged(java.lang.Object, java.lang.Enum, java.lang.Enum)
   */
  @Override
  public void stateChanged(AppSession source, Enum oldState, Enum newState) {
    logger.info("Diameter S6b SessionFactory :: stateChanged :: source[{}], oldState[{}], newState[{}]", new Object[]{source, oldState, newState});
  }

  // FIXME: add ctx methods proxy calls!
  @Override
  public void sessionSupervisionTimerExpired(ServerS6bSession session) {
    // this.resourceAdaptor.sessionDestroyed(session.getSessions().get(0).getSessionId(), session);
    session.release();
  }

  @Override
  public void sessionSupervisionTimerReStarted(ServerS6bSession session, ScheduledFuture future) {
    // TODO Complete this method.
  }

  @Override
  public void sessionSupervisionTimerStarted(ServerS6bSession session, ScheduledFuture future) {
    // TODO Complete this method.
  }

  @Override
  public void sessionSupervisionTimerStopped(ServerS6bSession session, ScheduledFuture future) {
    // TODO Complete this method.
  }

  public void timeoutExpired(Request request) {
    // FIXME What should we do when there's a timeout?
  }

  public void denyAccessOnTxExpire(ServerS6bSession clientS6bSessionImpl) {
    clientS6bSessionImpl.release();
  }

  @Override
  public void grantAccessOnDeliverFailure(ClientS6bSession clientS6bSessionImpl, Message request) {

  }

  @Override
  public void denyAccessOnDeliverFailure(ClientS6bSession clientS6bSessionImpl, Message request) {

  }

  @Override
  public void grantAccessOnFailureMessage(ClientS6bSession clientS6bSessionImpl) {

  }

  @Override
  public void denyAccessOnFailureMessage(ClientS6bSession clientS6bSessionImpl) {

  }

  @Override
  public void indicateServiceError(ClientS6bSession clientS6bSessionImpl) {

  }

  @Override
  public long[] getApplicationIds() {
    return new long[]{16777236};
  }
}
