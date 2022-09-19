package org.jdiameter.client.impl.app.s6b;

import org.jdiameter.common.api.app.s6b.ClientS6bSessionState;
import org.jdiameter.common.api.app.s6b.IS6bSessionData;

/**
 * @author <a href="mailto:giokast90@gmail.com"> Giovanni Castillo </a>
 */
public interface IClientS6bSessionData extends IS6bSessionData {

  boolean isEventBased();

  void setEventBased(boolean b);

  boolean isRequestTypeSet();

  void setRequestTypeSet(boolean b);

  ClientS6bSessionState getClientS6bSessionState();

  void setClientS6bSessionState(ClientS6bSessionState state);

}
