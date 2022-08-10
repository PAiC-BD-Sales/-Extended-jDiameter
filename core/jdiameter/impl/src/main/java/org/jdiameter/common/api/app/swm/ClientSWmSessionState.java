package org.jdiameter.common.api.app.swm;

import org.jdiameter.common.api.app.IAppSessionState;


/**
 * Diameter 3GPP IMS SWm Reference Point Client Session States
 *
 * @author <a href="mailto:enmanuelcalero61@gmail.com"> Enmanuel Calero </a>
 */
public enum ClientSWmSessionState implements IAppSessionState<ClientSWmSessionState> {

  IDLE(0),
  PENDING_EVENT(1),
  PENDING_INITIAL(2),
  PENDING_UPDATE(3),
  PENDING_TERMINATION(4),
  PENDING_BUFFERED(5),
  OPEN(6);

  private int stateValue = -1;

  ClientSWmSessionState(int stateV) {
    this.stateValue = stateV;
  }

  @Override
  public int getValue() {
    return stateValue;
  }

  @Override
  public ClientSWmSessionState fromInt(int val) throws IllegalArgumentException {
    switch (val) {
      case 0:
        return IDLE;
      case 1:
        return PENDING_EVENT;
      case 2:
        return PENDING_INITIAL;
      case 3:
        return PENDING_UPDATE;
      case 4:
        return PENDING_TERMINATION;
      case 5:
        return PENDING_BUFFERED;
      case 6:
        return OPEN;
      default:
        throw new IllegalArgumentException("Illegal value of int representation!!!!");
    }
  }
}
