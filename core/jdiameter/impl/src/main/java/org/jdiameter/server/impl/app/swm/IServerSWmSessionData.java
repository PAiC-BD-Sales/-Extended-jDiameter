package org.jdiameter.server.impl.app.swm;

import org.jdiameter.common.api.app.swm.ISWmSessionData;
import org.jdiameter.common.api.app.swm.ServerSWmSessionState;


/**
 *
 * @author <a href="mailto:enmanuelcalero61@gmail.com"> Enmanuel Calero </a>
 */
public interface IServerSWmSessionData extends ISWmSessionData {

    boolean isStateless();

    void setStateless(boolean stateless);

    ServerSWmSessionState getServerSWmSessionState();

    void setServerSWmSessionState(ServerSWmSessionState state);
}
