package org.jdiameter.server.impl.app.s6b;

import org.jdiameter.common.api.app.s6b.IS6bSessionData;
import org.jdiameter.common.api.app.s6b.ServerS6bSessionState;

/**
 * @author <a href="mailto:giokast90@gmail.com"> Giovanni Castillo </a>
 */
public interface IServerS6bSessionData extends IS6bSessionData {

    boolean isStateless();

    void setStateless(boolean stateless);

    ServerS6bSessionState getServerS6bSessionState();

    void setServerS6bSessionState(ServerS6bSessionState state);

}
