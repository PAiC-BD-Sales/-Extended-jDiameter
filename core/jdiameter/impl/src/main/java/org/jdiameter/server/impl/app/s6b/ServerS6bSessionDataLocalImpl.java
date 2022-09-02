package org.jdiameter.server.impl.app.s6b;

import org.jdiameter.common.api.app.AppSessionDataLocalImpl;
import org.jdiameter.common.api.app.s6b.ServerS6bSessionState;
import org.jdiameter.common.impl.app.s6b.S6bLocalSessionDataImpl;

/**
 * @author <a href="mailto:giokast90@gmail.com"> Giovanni Castillo </a>
 */
public class ServerS6bSessionDataLocalImpl extends S6bLocalSessionDataImpl implements IServerS6bSessionData {

  protected boolean stateless = true;
  protected ServerS6bSessionState state = ServerS6bSessionState.IDLE;

  /**
   *
   */
  public ServerS6bSessionDataLocalImpl() {

  }

  @Override
  public boolean isStateless() {
    return false;
  }

  @Override
  public void setStateless(boolean stateless) {
    this.stateless = stateless;
  }

  @Override
  public ServerS6bSessionState getServerS6bSessionState() {
    return state;
  }

  @Override
  public void setServerS6bSessionState(ServerS6bSessionState state) {
    this.state = state;
  }

}
