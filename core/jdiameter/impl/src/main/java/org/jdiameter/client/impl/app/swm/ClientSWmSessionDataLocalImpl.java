package org.jdiameter.client.impl.app.swm;

import org.jdiameter.common.api.app.AppSessionDataLocalImpl;
import org.jdiameter.common.api.app.swm.ClientSWmSessionState;

public class ClientSWmSessionDataLocalImpl extends AppSessionDataLocalImpl implements IClientSWmSessionData {

    @Override
    public boolean isEventBased() {
        return false;
    }

    @Override
    public void setEventBased(boolean b) {

    }

    @Override
    public boolean isRequestTypeSet() {
        return false;
    }

    @Override
    public void setRequestTypeSet(boolean b) {

    }

    @Override
    public ClientSWmSessionState getClientSwmSessionState() {
        return null;
    }

    @Override
    public void setClientSwmSessionState(ClientSWmSessionState state) {

    }
}
