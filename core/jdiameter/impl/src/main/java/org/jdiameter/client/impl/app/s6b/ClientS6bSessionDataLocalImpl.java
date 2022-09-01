package org.jdiameter.client.impl.app.s6b;

import org.jdiameter.common.api.app.AppSessionDataLocalImpl;
import org.jdiameter.common.api.app.s6b.ClientS6bSessionState;
import org.jdiameter.common.impl.app.s6b.S6bLocalSessionDataImpl;

/**
 * @author <a href="mailto:giokast90@gmail.com"> Giovanni Castillo </a>
 */
public class ClientS6bSessionDataLocalImpl extends S6bLocalSessionDataImpl implements IClientS6bSessionData {

  protected boolean isEventBased = true;
  protected boolean requestTypeSet = false;
  protected ClientS6bSessionState state = ClientS6bSessionState.IDLE;

  /**
   *
   */
  public ClientS6bSessionDataLocalImpl() {
  }

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
  public ClientS6bSessionState getClientS6bSessionState() {
    return state;
  }

  @Override
  public void setClientS6bSessionState(ClientS6bSessionState state) {
    this.state = state;
  }

}
