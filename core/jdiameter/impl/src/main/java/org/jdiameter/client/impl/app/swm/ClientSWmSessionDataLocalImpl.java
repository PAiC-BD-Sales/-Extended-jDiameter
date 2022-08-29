package org.jdiameter.client.impl.app.swm;

import org.jdiameter.common.api.app.AppSessionDataLocalImpl;
import org.jdiameter.common.api.app.swm.ClientSWmSessionState;

public class ClientSWmSessionDataLocalImpl extends AppSessionDataLocalImpl implements IClientSWmSessionData {


  protected boolean isEventBased = true;
  protected boolean requestTypeSet = false;
  protected ClientSWmSessionState state = ClientSWmSessionState.IDLE;


  @Override
  public boolean isEventBased() {
    return isEventBased;
  }

  @Override
  public void setEventBased(boolean isEventBased) {
    this.isEventBased = isEventBased;
  }

  @Override
  public boolean isRequestTypeSet() {
    return requestTypeSet;
  }

  @Override
  public void setRequestTypeSet(boolean requestTypeSet) {
    this.requestTypeSet = requestTypeSet;
  }

  @Override
  public ClientSWmSessionState getClientSWmSessionState() {
    return state;
  }

  @Override
  public void setClientSWmSessionState(ClientSWmSessionState state) {
    this.state = state;
  }
}
