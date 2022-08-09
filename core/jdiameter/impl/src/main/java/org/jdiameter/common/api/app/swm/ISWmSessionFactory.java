package org.jdiameter.common.api.app.swm;

import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.app.StateChangeListener;
import org.jdiameter.api.swm.ClientSWmSessionListener;
import org.jdiameter.api.swm.ServerSWmSessionListener;
import org.jdiameter.common.api.app.IAppSessionFactory;


/**
 * Session Factory interface for Diameter 3GPP IMS SWm Reference Point.
 *
 * @author <a href="mailto:enmanuelcalero61@gmail.com"> Enmanuel Calero </a>
 */
public interface ISWmSessionFactory extends IAppSessionFactory {

    ClientSWmSessionListener getClientSessionListener();

    void setClientSessionListener(ClientSWmSessionListener clientSessionListener);

    ServerSWmSessionListener getServerSessionListener();

    void setServerSessionListener(ServerSWmSessionListener serverSessionListener);

    IServerSWmSessionContext getServerContextListener();

    void setServerContextListener(IServerSWmSessionContext serverContextListener);

    IClientSWmSessionContext getClientContextListener();

    void setClientContextListener(IClientSWmSessionContext clientContextListener);

    ISWmMessageFactory getMessageFactory();

    void setMessageFactory(ISWmMessageFactory messageFactory);

    StateChangeListener<AppSession> getStateListener();

    void setStateListener(StateChangeListener<AppSession> stateListener);

}
