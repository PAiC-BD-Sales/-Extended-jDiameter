package org.jdiameter.common.impl.app.zh;

import org.jdiameter.api.Request;
import org.jdiameter.common.api.app.AppSessionDataLocalImpl;
import org.jdiameter.common.api.app.zh.IZhSessionData;
import org.jdiameter.common.api.app.zh.ZhSessionState;

import java.io.Serializable;

/**
 * @author <a href="mailto:kennymendieta89@gmail.com"> Kenny Mendieta </a>
 *
**/
public class ZhLocalSessionDataImpl extends AppSessionDataLocalImpl implements IZhSessionData {
  protected ZhSessionState state = ZhSessionState.NO_STATE_MAINTAINED;
  protected Request buffer;
  protected Serializable tsTimerId;

  @Override
  public void setZhSessionState(ZhSessionState state) {
        this.state = state;
    }

  @Override
  public ZhSessionState getZhSessionState() {
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
