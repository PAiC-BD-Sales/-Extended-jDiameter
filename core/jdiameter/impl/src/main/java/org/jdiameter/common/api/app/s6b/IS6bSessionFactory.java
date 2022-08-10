package org.jdiameter.common.api.app.s6b;

import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.app.StateChangeListener;
import org.jdiameter.api.s6b.ClientS6bSessionListener;
import org.jdiameter.api.s6b.ServerS6bSessionListener;
import org.jdiameter.common.api.app.IAppSessionFactory;

/**
 * Session Factory interface for S6b Reference Point.
 *
 * @author <a href="mailto:giokast90@gmail.com"> Giovanni Castillo </a>
 */
public interface IS6bSessionFactory extends IAppSessionFactory {

    /**
     * Get stack wide listener for sessions. In local mode it has
     * similar effect as setting this directly in app session.
     * However clustered session use this value when recreated!
     *
     * @return the clientSessionListener
     */
    ClientS6bSessionListener getClientSessionListener();

    /**
     * Set stack wide listener for sessions. In local mode it has
     * similar effect as setting this directly in app session.
     * However clustered session use this value when recreated!
     *
     * @param clientSessionListener the clientSessionListener to set
     */
    void setClientSessionListener(ClientS6bSessionListener clientSessionListener);

    /**
     * Get stack wide listener for sessions. In local mode it has similar
     * effect as setting this directly in app session.
     * However clustered session use this value when recreated!
     *
     * @return the serverSessionListener
     */
    ServerS6bSessionListener getServerSessionListener();

    /**
     * Set stack wide listener for sessions. In local mode it has similar
     * effect as setting this directly in app session.
     * However clustered session use this value when recreated!
     *
     * @param serverSessionListener the serverSessionListener to set
     */
    void setServerSessionListener(ServerS6bSessionListener serverSessionListener);

    /**
     * @return the serverContextListener
     */
    IServerS6bSessionContext getServerContextListener();

    /**
     * @param serverContextListener the serverContextListener to set
     */
    void setServerContextListener(IServerS6bSessionContext serverContextListener);

    /**
     * @return the clientContextListener
     */
    IClientS6bSessionContext getClientContextListener();

    /**
     * @return the messageFactory
     */
    IS6bMessageFactory getMessageFactory();

    /**
     * @param messageFactory the messageFactory to set
     */
    void setMessageFactory(IS6bMessageFactory messageFactory);

    /**
     * @param clientContextListener the clientContextListener to set
     */
    void setClientContextListener(IClientS6bSessionContext clientContextListener);

    /**
     * @return the stateListener
     */
    StateChangeListener<AppSession> getStateListener();

    /**
     * @param stateListener the stateListener to set
     */
    void setStateListener(StateChangeListener<AppSession> stateListener);
}
