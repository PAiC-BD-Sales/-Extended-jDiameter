package org.jdiameter.client.impl.app.swm;

import org.jdiameter.common.api.app.swm.ClientSWmSessionState;
import org.jdiameter.common.api.app.swm.ISWmSessionData;

public interface IClientSWmSessionData extends ISWmSessionData {
    boolean isEventBased();

    void setEventBased(boolean b);

    boolean isRequestTypeSet();

    void setRequestTypeSet(boolean b);

    ClientSWmSessionState getClientSwmSessionState();

    void setClientSwmSessionState(ClientSWmSessionState state);
}
