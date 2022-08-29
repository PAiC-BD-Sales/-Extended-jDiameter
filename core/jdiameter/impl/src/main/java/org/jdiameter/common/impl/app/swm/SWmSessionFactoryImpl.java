package org.jdiameter.common.impl.app.swm;

import org.jdiameter.api.Answer;
import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.Message;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.Request;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.SessionFactory;
import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppRequestEvent;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.app.StateChangeListener;
import org.jdiameter.api.swm.ClientSWmSession;
import org.jdiameter.api.swm.ClientSWmSessionListener;
import org.jdiameter.api.swm.ServerSWmSession;
import org.jdiameter.api.swm.ServerSWmSessionListener;
import org.jdiameter.api.swm.events.SWmAbortSessionAnswer;
import org.jdiameter.api.swm.events.SWmAbortSessionRequest;
import org.jdiameter.api.swm.events.SWmDiameterAAAnswer;
import org.jdiameter.api.swm.events.SWmDiameterAARequest;
import org.jdiameter.api.swm.events.SWmDiameterEAPAnswer;
import org.jdiameter.api.swm.events.SWmDiameterEAPRequest;
import org.jdiameter.api.swm.events.SWmReAuthAnswer;
import org.jdiameter.api.swm.events.SWmReAuthRequest;
import org.jdiameter.api.swm.events.SWmSessionTermAnswer;
import org.jdiameter.api.swm.events.SWmSessionTermRequest;
import org.jdiameter.client.api.ISessionFactory;
import org.jdiameter.client.impl.app.swm.ClientSWmSessionImpl;
import org.jdiameter.client.impl.app.swm.IClientSWmSessionData;
import org.jdiameter.common.api.app.IAppSessionDataFactory;
import org.jdiameter.common.api.app.swm.IClientSWmSessionContext;
import org.jdiameter.common.api.app.swm.ISWmMessageFactory;
import org.jdiameter.common.api.app.swm.ISWmSessionData;
import org.jdiameter.common.api.app.swm.ISWmSessionFactory;
import org.jdiameter.common.api.app.swm.IServerSWmSessionContext;
import org.jdiameter.common.api.data.ISessionDatasource;
import org.jdiameter.server.impl.app.swm.IServerSWmSessionData;
import org.jdiameter.server.impl.app.swm.ServerSWmSessionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ScheduledFuture;

public class SWmSessionFactoryImpl implements ISWmSessionFactory, ClientSWmSessionListener, ServerSWmSessionListener, StateChangeListener<AppSession>,
        ISWmMessageFactory, IServerSWmSessionContext, IClientSWmSessionContext {


  // Message timeout value (in milliseconds)
  protected int defaultDirectDebitingFailureHandling = 0;
  protected int defaultAAFailureHandling = 0;
  // its seconds
  protected long defaultValidityTime = 60;
  protected long defaultTxTimerValue = 30;
  // local not replicated listeners:
  protected ClientSWmSessionListener clientSessionListener;
  protected ServerSWmSessionListener serverSessionListener;
  protected StateChangeListener<AppSession> stateListener;
  protected IServerSWmSessionContext serverContextListener;
  protected IClientSWmSessionContext clientContextListener;
  protected ISWmMessageFactory messageFactory;
  protected static final Logger logger = LoggerFactory.getLogger(SWmSessionFactoryImpl.class);
  protected ISessionDatasource iss;
  protected ISessionFactory sessionFactory = null;
  protected IAppSessionDataFactory<ISWmSessionData> sessionDataFactory;


  public SWmSessionFactoryImpl(SessionFactory sessionFactory) {
    super();

    this.sessionFactory = (ISessionFactory) sessionFactory;
    this.iss = this.sessionFactory.getContainer().getAssemblerFacility().getComponentInstance(ISessionDatasource.class);
    this.sessionDataFactory = (IAppSessionDataFactory<ISWmSessionData>) this.iss.getDataFactory(ISWmSessionData.class);

    if (this.sessionDataFactory == null) {
      logger.debug("Initialized Rx SessionDataFactory is null");
    }
  }

  public SWmSessionFactoryImpl(SessionFactory sessionFactory, int defaultDirectDebitingFailureHandling, int defaultAAFailureHandling,
                               long defaultValidityTime, long defaultTxTimerValue) {
    this(sessionFactory);

    this.defaultDirectDebitingFailureHandling = defaultDirectDebitingFailureHandling;
    this.defaultAAFailureHandling = defaultAAFailureHandling;
    this.defaultValidityTime = defaultValidityTime;
    this.defaultTxTimerValue = defaultTxTimerValue;
  }

  // Default implementation of methods so there are no exception!
  @Override
  public void doDiameterEAPAnswer(ClientSWmSession session, SWmDiameterEAPRequest request, SWmDiameterEAPAnswer answer)
          throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {

  }

  @Override
  public void doDiameterAAAnswer(ClientSWmSession session, SWmDiameterAARequest request, SWmDiameterAAAnswer answer)
          throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {

  }

  @Override
  public void doAbortSessionRequest(ClientSWmSession session, SWmAbortSessionRequest request)
          throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {

  }

  @Override
  public void doReAuthRequest(ClientSWmSession session, SWmReAuthRequest request)
          throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {

  }

  @Override
  public void doSessionTermAnswer(ClientSWmSession session, SWmSessionTermRequest request, SWmSessionTermAnswer answer)
          throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {

  }

  @Override
  public void doDiameterEAPRequest(ServerSWmSession session, SWmDiameterEAPRequest request)
          throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {

  }

  @Override
  public void doDiameterAARequest(ServerSWmSession session, SWmDiameterAARequest request)
          throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {

  }

  @Override
  public void doAbortSessionAnswer(ServerSWmSession session, SWmAbortSessionRequest request, SWmAbortSessionAnswer answer)
          throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {

  }

  @Override
  public void doReAuthAnswer(ServerSWmSession session, SWmReAuthRequest request, SWmReAuthAnswer answer)
          throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {

  }

  @Override
  public void doSessionTermRequest(ServerSWmSession session, SWmSessionTermRequest request)
          throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {

  }

  @Override
  public void doOtherEvent(AppSession session, AppRequestEvent request, AppAnswerEvent answer)
          throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {

  }


  @Override
  public SWmDiameterAARequest createDiameterAARequest(Request request) {
    return new SWmDiameterAARequestImpl(request);
  }

  @Override
  public SWmDiameterAAAnswer createDiameterAAAnswer(Answer answer) {
    return new SWmDiameterAAAnswerImpl(answer);
  }

  // Message Factory Methods -------------------------------------------------
  @Override
  public SWmAbortSessionRequest createAbortSessionRequest(Request request) {
    return new SWmAbortSessionRequestImpl(request);
  }

  @Override
  public SWmAbortSessionAnswer createAbortSessionAnswer(Answer answer) {
    return new SWmAbortSessionAnswerImpl(answer);
  }

  @Override
  public SWmDiameterEAPRequest createDiameterEAPRequest(Request request) {
    return new SWmDiameterEAPRequestImpl(request);
  }

  @Override
  public SWmDiameterEAPAnswer createDiameterEAPAnswer(Answer answer) {
    return new SWmDiameterEAPAnswerImpl(answer);
  }

  @Override
  public SWmReAuthRequest createReAuthRequest(Request request) {
    return new SWmReAuthRequestImpl(request);
  }

  @Override
  public SWmReAuthAnswer createReAuthAnswer(Answer answer) {
    return new SWmReAuthAnswerImpl(answer);
  }

  @Override
  public SWmSessionTermRequest createSessionTermRequest(Request request) {
    return new SWmSessionTermRequestImpl(request);
  }

  @Override
  public SWmSessionTermAnswer createSessionTermAnswer(Answer answer) {
    return new SWmSessionTermAnswerImpl(answer);
  }

  @Override
  public long[] getApplicationIds() {
    // FIXME: What should we do here?
    return new long[]{16777236};
  }

  @Override
  public ClientSWmSessionListener getClientSessionListener() {
    return clientSessionListener != null ? clientSessionListener : this;
  }

  @Override
  public void setClientSessionListener(ClientSWmSessionListener clientSessionListener) {
    this.clientSessionListener = clientSessionListener;
  }

  @Override
  public ServerSWmSessionListener getServerSessionListener() {
    return serverSessionListener != null ? serverSessionListener : this;
  }

  @Override
  public void setServerSessionListener(ServerSWmSessionListener serverSessionListener) {
    this.serverSessionListener = serverSessionListener;
  }

  @Override
  public IServerSWmSessionContext getServerContextListener() {
    return serverContextListener != null ? serverContextListener : this;
  }

  @Override
  public void setServerContextListener(IServerSWmSessionContext serverContextListener) {
    this.serverContextListener = serverContextListener;
  }

  @Override
  public IClientSWmSessionContext getClientContextListener() {
    return clientContextListener != null ? clientContextListener : this;
  }

  @Override
  public void setClientContextListener(IClientSWmSessionContext clientContextListener) {
    this.clientContextListener = clientContextListener;
  }

  @Override
  public ISWmMessageFactory getMessageFactory() {
    return messageFactory != null ? messageFactory : this;
  }

  @Override
  public void setMessageFactory(ISWmMessageFactory messageFactory) {
    this.messageFactory = messageFactory;
  }

  public SessionFactory getSessionFactory() {
    return sessionFactory;
  }

  /**
   * @param sessionFactory the sessionFactory to set
   */
  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = (ISessionFactory) sessionFactory;
  }

  @Override
  public StateChangeListener<AppSession> getStateListener() {
    return stateListener != null ? stateListener : this;
  }

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
      if (aClass == ClientSWmSession.class) {
        IClientSWmSessionData sessionData = (IClientSWmSessionData) this.sessionDataFactory.getAppSessionData(ClientSWmSession.class, sessionId);
        ClientSWmSessionImpl clientSession = new ClientSWmSessionImpl(sessionData, this.getMessageFactory(), sessionFactory, this.getClientSessionListener(),
                this.getClientContextListener(), this.getStateListener());

        clientSession.getSessions().get(0).setRequestListener(clientSession);
        appSession = clientSession;
      } else if (aClass == ServerSWmSession.class) {
        IServerSWmSessionData sessionData = (IServerSWmSessionData) this.sessionDataFactory.getAppSessionData(ServerSWmSession.class, sessionId);
        ServerSWmSessionImpl serverSession = new ServerSWmSessionImpl(sessionData, this.getMessageFactory(), sessionFactory, this.getServerSessionListener(),
                this.getServerContextListener(), this.getStateListener());

        serverSession.getSessions().get(0).setRequestListener(serverSession);
        appSession = serverSession;
      } else {
        throw new IllegalArgumentException("Wrong session class: " + aClass + ". Supported[" + ClientSWmSession.class + "," + ServerSWmSession.class + "]");
      }
    } catch (Exception e) {
      logger.error("Failure to obtain new SWm Session.", e);
    }

    return appSession;
  }

  @Override
  public AppSession getNewSession(String sessionId, Class<? extends AppSession> aClass, ApplicationId applicationId, Object[] args) {
    AppSession appSession = null;
    try {
      // FIXME:
      if (aClass == ClientSWmSession.class) {
        if (sessionId == null) {
          if (args != null && args.length > 0 && args[0] instanceof Request) {
            Request request = (Request) args[0];
            sessionId = request.getSessionId();
          } else {
            sessionId = this.sessionFactory.getSessionId();
          }
        }
        IClientSWmSessionData sessionData = (IClientSWmSessionData) this.sessionDataFactory.getAppSessionData(ClientSWmSession.class, sessionId);
        sessionData.setApplicationId(applicationId);
        ClientSWmSessionImpl clientSession = new ClientSWmSessionImpl(sessionData, this.getMessageFactory(), sessionFactory, this.getClientSessionListener(),
                this.getClientContextListener(), this.getStateListener());
        // this goes first!
        iss.addSession(clientSession);
        clientSession.getSessions().get(0).setRequestListener(clientSession);
        appSession = clientSession;
      } else if (aClass == ServerSWmSession.class) {
        if (sessionId == null) {
          if (args != null && args.length > 0 && args[0] instanceof Request) {
            Request request = (Request) args[0];
            sessionId = request.getSessionId();
          } else {
            sessionId = this.sessionFactory.getSessionId();
          }
        }
        IServerSWmSessionData sessionData = (IServerSWmSessionData) this.sessionDataFactory.getAppSessionData(ServerSWmSession.class, sessionId);
        sessionData.setApplicationId(applicationId);
        ServerSWmSessionImpl serverSession = new ServerSWmSessionImpl(sessionData, this.getMessageFactory(), sessionFactory, this.getServerSessionListener(),
                this.getServerContextListener(), this.getStateListener());
        iss.addSession(serverSession);
        serverSession.getSessions().get(0).setRequestListener(serverSession);
        appSession = serverSession;
      } else {
        throw new IllegalArgumentException("Wrong session class: " + aClass + ". Supported[" + ClientSWmSession.class + "," + ServerSWmSession.class + "]");
      }
    } catch (Exception e) {
      logger.error("Failure to obtain new SWm Session.", e);
    }

    return appSession;
  }


  @Override
  public void stateChanged(Enum oldState, Enum newState) {
    logger.info("Diameter SWm SessionFactory :: stateChanged :: oldState[{}], newState[{}]", oldState, newState);
  }

  @Override
  public void stateChanged(AppSession source, Enum oldState, Enum newState) {
    logger.info("Diameter SWm SessionFactory :: stateChanged :: source[{}], oldState[{}], newState[{}]", new Object[]{source, oldState, newState});
  }


  @Override
  public void grantAccessOnDeliverFailure(ClientSWmSession clientCCASessionImpl, Message request) {

  }

  @Override
  public void denyAccessOnDeliverFailure(ClientSWmSession clientCCASessionImpl, Message request) {

  }

  @Override
  public void grantAccessOnFailureMessage(ClientSWmSession clientCCASessionImpl) {

  }

  @Override
  public void denyAccessOnFailureMessage(ClientSWmSession clientCCASessionImpl) {

  }

  @Override
  public void indicateServiceError(ClientSWmSession clientCCASessionImpl) {

  }

  @Override
  public void sessionSupervisionTimerExpired(ServerSWmSession session) {
    session.release();
  }

  @Override
  public void sessionSupervisionTimerStarted(ServerSWmSession session, ScheduledFuture future) {

  }

  @Override
  public void sessionSupervisionTimerReStarted(ServerSWmSession session, ScheduledFuture future) {

  }

  @Override
  public void sessionSupervisionTimerStopped(ServerSWmSession session, ScheduledFuture future) {

  }
}
