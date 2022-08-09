package org.jdiameter.common.api.app.swm;

import org.jdiameter.common.api.app.IAppSessionState;

/**
 * Diameter 3GPP IMS SWm Reference Point Server Session states
 *
 * @author <a href="mailto:enmanuelcalero61@gmail.com"> Enmanuel Calero </a>
 */
public enum ServerSWmSessionState implements IAppSessionState<ServerSWmSessionState> {

    IDLE(0),
    OPEN(1);

    private int stateRepresentation = -1;

    ServerSWmSessionState(int v) {
        this.stateRepresentation = v;
    }

    @Override
    public int getValue() {
        return stateRepresentation;
    }

    @Override
    public ServerSWmSessionState fromInt(int val) throws IllegalArgumentException {
        switch (val) {
            case 0:
                return IDLE;

            case 1:
                return OPEN;

            default:
                throw new IllegalArgumentException("Illegal value of int representation!!!!");
        }
    }
}
