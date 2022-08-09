package org.jdiameter.api.s6b;

import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.app.StateMachine;
import org.jdiameter.api.s6b.events.S6bAARequest;
import org.jdiameter.api.s6b.events.S6bAbortSessionAnswer;
import org.jdiameter.api.s6b.events.S6bReAuthAnswer;
import org.jdiameter.api.s6b.events.S6bSessionTerminationRequest;

/**
 * Basic class for S6b Client Interface specific session.
 * Listener must be injected from constructor of implementation class.
 *
 * @author <a href="mailto:giokast90@gmail.com"> Giovanni Castillo </a>
 */
public interface ClientS6bSession extends AppSession, StateMachine {

    void sendAARequest(final S6bAARequest request) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

    void sendSessionTerminationRequest(final S6bSessionTerminationRequest request) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

    void sendReAuthAnswer(final S6bReAuthAnswer answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

    void sendAbortSessionAnswer(final S6bAbortSessionAnswer answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

}
