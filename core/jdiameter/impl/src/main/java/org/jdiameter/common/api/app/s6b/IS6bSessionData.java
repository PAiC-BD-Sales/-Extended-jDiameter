package org.jdiameter.common.api.app.s6b;

import org.jdiameter.api.Request;
import org.jdiameter.common.api.app.IAppSessionData;

import java.io.Serializable;

/**
 * Diameter S6b Reference Point Session Data
 *
 * @author <a href="mailto:giokast90@gmail.com"> Giovanni Castillo </a>
 */
public interface IS6bSessionData extends IAppSessionData {
  // marker interface
  void setS6bSessionState(ServerS6bSessionState state);
  ServerS6bSessionState getS6bSessionState();
  Serializable getTsTimerId();
  void setTsTimerId(Serializable tid);
  void setBuffer(Request buffer);
  Request getBuffer();
}
