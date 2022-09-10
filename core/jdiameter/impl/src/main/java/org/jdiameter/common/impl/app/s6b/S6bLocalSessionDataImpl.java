package org.jdiameter.common.impl.app.s6b;

import org.jdiameter.api.Request;
import org.jdiameter.common.api.app.AppSessionDataLocalImpl;
import org.jdiameter.common.api.app.s6b.IS6bSessionData;
import org.jdiameter.common.api.app.s6b.ServerS6bSessionState;

import java.io.Serializable;

/**
 * @author <a href="mailto:kennymendieta89@gmail.com"> Kenny Mendieta </a>
 *
 **/
public class S6bLocalSessionDataImpl extends AppSessionDataLocalImpl implements IS6bSessionData {
  protected ServerS6bSessionState state = ServerS6bSessionState.IDLE;
  protected Request buffer;
  protected Serializable tsTimerId;

  @Override
  public void setS6bSessionState(ServerS6bSessionState state) {
    this.state = state;
  }

  @Override
  public ServerS6bSessionState getS6bSessionState() {
    return state;
  }

  @Override
  public Serializable getTsTimerId() {
    return tsTimerId;
  }

  @Override
  public void setTsTimerId(Serializable tid) {
    this.tsTimerId = tid;
  }

  @Override
  public void setBuffer(Request buffer) {
    this.buffer = buffer;
  }

  @Override
  public Request getBuffer() {
    return buffer;
  }
}
