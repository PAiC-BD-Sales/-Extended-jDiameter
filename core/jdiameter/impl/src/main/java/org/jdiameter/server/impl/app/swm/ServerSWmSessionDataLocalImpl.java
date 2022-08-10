package org.jdiameter.server.impl.app.swm;

import org.jdiameter.common.api.app.AppSessionDataLocalImpl;
import org.jdiameter.common.api.app.swm.ServerSWmSessionState;

/**
 *
 * @author <a href="mailto:enmanuelcalero61@gmail.com"> Enmanuel Calero </a>
 */
public class ServerSWmSessionDataLocalImpl extends AppSessionDataLocalImpl implements IServerSWmSessionData {

    protected boolean stateless = true;
    protected ServerSWmSessionState state = ServerSWmSessionState.IDLE;


    @Override
    public boolean isStateless() {
        return stateless;
    }

    @Override
    public void setStateless(boolean stateless) {
        this.stateless = stateless;
    }

    @Override
    public ServerSWmSessionState getServerSWmSessionState() {
        return state;
    }

    @Override
    public void setServerSWmSessionState(ServerSWmSessionState state) {
        this.state = state;
    }
}
