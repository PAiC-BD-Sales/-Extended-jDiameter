package org.jdiameter.common.api.app.s6b;

import org.jdiameter.common.api.app.IAppSessionState;

/**
 * Diameter S6b Reference Point Client Session States
 *
 * @author <a href="mailto:giokast90@gmail.com"> Giovanni Castillo </a>
 */
public enum ClientS6bSessionState implements IAppSessionState<ClientS6bSessionState> {

    IDLE(0),
    PENDING_AAR(1),
    PENDING_STR(2),
    PENDING_EVENT(3),
    PENDING_BUFFERED(4),
    OPEN(5);
    private int stateValue = -1;

    ClientS6bSessionState(int stateV) {
        this.stateValue = stateV;
    }

    @Override
    public ClientS6bSessionState fromInt(int v) throws IllegalArgumentException {
        switch (v) {
            case 0:
                return IDLE;
            case 1:
                return PENDING_AAR;
            case 2:
                return PENDING_STR;
            case 3:
                return PENDING_EVENT;
            case 4:
                return PENDING_BUFFERED;
            case 5:
                return OPEN;
            default:
                throw new IllegalArgumentException("Illegal value of int representation!!!!");
        }
    }

    @Override
    public int getValue() {
        return stateValue;
    }
}
