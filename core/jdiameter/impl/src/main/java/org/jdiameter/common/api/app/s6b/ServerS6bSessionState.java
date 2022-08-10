package org.jdiameter.common.api.app.s6b;

import org.jdiameter.common.api.app.IAppSessionState;

/**
 * Diameter S6b Reference Point Server Session states
 *
 * @author <a href="mailto:giokast90@gmail.com"> Giovanni Castillo </a>
 */
public enum ServerS6bSessionState implements IAppSessionState<ServerS6bSessionState> {

    IDLE(0),
    OPEN(1);

    private int stateRepresentation = -1;

    ServerS6bSessionState(int v) {
        this.stateRepresentation = v;
    }

    @Override
    public ServerS6bSessionState fromInt(int v) throws IllegalArgumentException {
        switch (v) {
            case 0:
                return IDLE;

            case 1:
                return OPEN;

            default:
                throw new IllegalArgumentException("Illegal value of int representation!!!!");
        }
    }

    @Override
    public int getValue() {
        return stateRepresentation;
    }

}
