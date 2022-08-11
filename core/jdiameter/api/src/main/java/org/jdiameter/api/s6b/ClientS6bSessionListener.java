package org.jdiameter.api.s6b;

import org.jdiameter.api.InternalException;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppRequestEvent;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.s6b.events.S6bSessionTerminationRequest;
import org.jdiameter.api.s6b.events.S6bSessionTerminationAnswer;

/**
 * This interface defines the possible actions for the different states in the client
 * S6b Interface state machine.
 *
 * @author <a href="mailto:giokast90@gmail.com"> Giovanni Castillo </a>
 */
public interface ClientS6bSessionListener {

    void doSessionTerminationAnswer(ClientS6bSession session, S6bSessionTerminationRequest request, S6bSessionTerminationAnswer answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

    void doOtherEvent(AppSession session, AppRequestEvent request, AppAnswerEvent answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;
}
