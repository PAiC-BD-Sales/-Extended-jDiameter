package org.jdiameter.common.impl.app.zh;

import org.jdiameter.api.Answer;
import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.Request;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.SessionFactory;
import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppRequestEvent;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.app.StateChangeListener;
import org.jdiameter.api.zh.ClientZhSession;
import org.jdiameter.api.zh.ClientZhSessionListener;
import org.jdiameter.api.zh.ServerZhSession;
import org.jdiameter.api.zh.ServerZhSessionListener;
import org.jdiameter.api.zh.events.MultimediaAuthAnswer;
import org.jdiameter.api.zh.events.MultimediaAuthRequest;
import org.jdiameter.client.api.ISessionFactory;
import org.jdiameter.client.impl.app.zh.IClientZhSessionData;
import org.jdiameter.client.impl.app.zh.ZhClientSessionImpl;
import org.jdiameter.common.api.app.IAppSessionDataFactory;
import org.jdiameter.common.api.app.zh.IZhMessageFactory;
import org.jdiameter.common.api.app.zh.IZhSessionData;
import org.jdiameter.common.api.app.zh.IZhSessionFactory;
import org.jdiameter.common.api.data.ISessionDatasource;
import org.jdiameter.server.impl.app.zh.IServerZhSessionData;
import org.jdiameter.server.impl.app.zh.ZhServerSessionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:kennymendieta89@gmail.com"> Kenny Mendieta </a>
 *
 */
public class ZhSessionFactoryImpl implements IZhSessionFactory, ServerZhSessionListener, ClientZhSessionListener, IZhMessageFactory, StateChangeListener<AppSession> {
    private static final Logger logger = LoggerFactory.getLogger(ZhSessionFactoryImpl.class);
    protected ISessionFactory sessionFactory;
    protected ServerZhSessionListener serverSessionListener;
    protected ClientZhSessionListener clientSessionListener;
    protected IZhMessageFactory messageFactory;
    protected StateChangeListener<AppSession> stateListener;
    protected ISessionDatasource iss;
    protected IAppSessionDataFactory<IZhSessionData> sessionDataFactory;

    public ZhSessionFactoryImpl() {
    }
    public ZhSessionFactoryImpl(SessionFactory sessionFactory) {
        super();
        init(sessionFactory);
    }

    public void init(SessionFactory sessionFactory) {
        this.sessionFactory = (ISessionFactory) sessionFactory;
        this.iss = this.sessionFactory.getContainer().getAssemblerFacility().getComponentInstance(ISessionDatasource.class);
        this.sessionDataFactory = (IAppSessionDataFactory<IZhSessionData>) this.iss.getDataFactory(IZhSessionData.class);
    }

    @Override
    public void doMultimediaAuthAnswerEvent(ClientZhSession session, MultimediaAuthRequest request, MultimediaAuthAnswer answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException, AvpDataException {
        logger.info(
                "Diameter Zh Session Factory :: doMultimediaAuthAnswerEvent :: session[{}], Request[{}], Answer[{}]",
                new Object[] { session, request, answer });
    }

    @Override
    public void doOtherEvent(AppSession session, AppRequestEvent request, AppAnswerEvent answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
        logger.info("Diameter Zh Session Factory :: doOtherEvent :: session[{}], Request[{}], Answer[{}]", new Object[] { session, request, answer });
    }

    @Override
    public void doMultimediaAuthRequestEvent(ServerZhSession session, MultimediaAuthRequest request) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException, AvpDataException {
        logger.info("Diameter Zh Session Factory :: doMultimediaAuthRequestEvent :: session[{}], Request[{}]", session, request);
    }

    @Override
    public AppSession getNewSession(String sessionId, Class<? extends AppSession> aClass, ApplicationId applicationId, Object[] args) {
        AppSession appSession = null;

        try {
            if (aClass == ServerZhSession.class) {
                if (sessionId == null) {
                    if (args != null && args.length > 0 && args[0] instanceof Request) {
                        Request request = (Request) args[0];
                        sessionId = request.getSessionId();
                    } else {
                        sessionId = this.sessionFactory.getSessionId();
                    }
                }
                IServerZhSessionData sessionData = (IServerZhSessionData) this.sessionDataFactory.getAppSessionData(ServerZhSession.class, sessionId);
                sessionData.setApplicationId(applicationId);
                ZhServerSessionImpl serverSession = new ZhServerSessionImpl(sessionData, getMessageFactory(), sessionFactory, getServerZhSessionListener());
                iss.addSession(serverSession);
                serverSession.getSessions().get(0).setRequestListener(serverSession);
                appSession = serverSession;
            } else if (aClass == ClientZhSession.class) {
                if (sessionId == null) {
                    if (args != null && args.length > 0 && args[0] instanceof Request) {
                        Request request = (Request) args[0];
                        sessionId = request.getSessionId();
                    } else {
                        sessionId = this.sessionFactory.getSessionId();
                    }
                }
                IClientZhSessionData sessionData = (IClientZhSessionData) this.sessionDataFactory.getAppSessionData(ClientZhSession.class, sessionId);
                sessionData.setApplicationId(applicationId);
                ZhClientSessionImpl clientSession = new ZhClientSessionImpl(sessionData, getMessageFactory(), sessionFactory, getClientSessionListener());
                iss.addSession(clientSession);
                clientSession.getSessions().get(0).setRequestListener(clientSession);
                appSession = clientSession;
            } else {
                throw new IllegalArgumentException(
                        "Wrong session class: " + aClass + ". Supported[" + ServerZhSession.class + ", " + ClientZhSession.class + "]");
            }

        } catch (Exception e) {
            logger.error("Failure to obtain new Zh Session.", e);
        }
        return appSession;
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
            if (aClass == ServerZhSession.class) {
                IServerZhSessionData sessionData = (IServerZhSessionData) this.sessionDataFactory.getAppSessionData(ServerZhSession.class, sessionId);
                ZhServerSessionImpl serverSession = new ZhServerSessionImpl(sessionData, getMessageFactory(), sessionFactory, getServerZhSessionListener());
                serverSession.getSessions().get(0).setRequestListener(serverSession);
                appSession = serverSession;
            } else if (aClass == ClientZhSession.class) {
                IClientZhSessionData sessionData = (IClientZhSessionData) this.sessionDataFactory.getAppSessionData(ClientZhSession.class, sessionId);
                ZhClientSessionImpl clientSession = new ZhClientSessionImpl(sessionData, getMessageFactory(), sessionFactory, getClientSessionListener());
                clientSession.getSessions().get(0).setRequestListener(clientSession);
                appSession = clientSession;
            } else {
                throw new IllegalArgumentException(
                        "Wrong session class: " + aClass + ". Supported[" + ServerZhSession.class + ", " + ClientZhSession.class + "]");
            }
        } catch (Exception e) {
            logger.error("Failure to obtain new SLh Session.", e);
        }
        return appSession;
    }

    /**
     * @return the serverSessionListener
     */
    @Override
    public ServerZhSessionListener getServerZhSessionListener() {
        return serverSessionListener != null ? serverSessionListener : this;
    }

    /**
     * @param serverSessionListener the serverSessionListener to set
     */
    @Override
    public void setServerSessionListener(ServerZhSessionListener serverSessionListener) {
        this.serverSessionListener = serverSessionListener;
    }

    /**
     * @return the serverSessionListener
     */
    @Override
    public ClientZhSessionListener getClientSessionListener() {
        return clientSessionListener != null ? clientSessionListener : this;
    }

    /**
     * @param clientSessionListener the clientSessionListener to set
     */
    @Override
    public void setClientSessionListener(ClientZhSessionListener clientSessionListener) {
        this.clientSessionListener = clientSessionListener;
    }

    /**
     * @return the messageFactory
     */
    @Override
    public IZhMessageFactory getMessageFactory() {
        return messageFactory != null ? messageFactory : this;
    }

    /**
     * @param messageFactory the messageFactory to set
     */
    @Override
    public void setMessageFactory(IZhMessageFactory messageFactory) {
        this.messageFactory = messageFactory;
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
    public void stateChanged(Enum oldState, Enum newState) {
        logger.info("Diameter Zh Session Factory :: stateChanged :: oldState[{}], newState[{}]", oldState, newState);
    }
    @Override
    public void stateChanged(AppSession source, Enum oldState, Enum newState) {
        logger.info("Diameter Zh Session Factory :: stateChanged :: Session, [{}], oldState[{}], newState[{}]", new Object[] { source, oldState, newState });
    }

    @Override
    public MultimediaAuthRequest createMultimediaAuthRequest(Request request) {
        return new MultimediaAuthRequestImpl(request);
    }

    @Override
    public MultimediaAuthAnswer createMultimediaAuthAnswer(Answer answer) {
        return new MultimediaAuthAnswerImpl(answer);
    }

    @Override
    public long getApplicationId() {
        // TODO: Review this value
        return 0;
    }
}
