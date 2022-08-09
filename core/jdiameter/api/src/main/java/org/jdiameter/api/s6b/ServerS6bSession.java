package org.jdiameter.api.s6b;

import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.app.StateMachine;
import org.jdiameter.api.s6b.events.S6bAAAnswer;
import org.jdiameter.api.s6b.events.S6bAbortSessionRequest;
import org.jdiameter.api.s6b.events.S6bReAuthRequest;
import org.jdiameter.api.s6b.events.S6bSessionTerminationAnswer;

/**
 * Basic class for S6b Server Interface specific session.
 * Listener must be injected from constructor of implementation class
 *
 * @author <a href="mailto:giokast90@gmail.com"> Giovanni Castillo </a>
 */
public interface ServerS6bSession extends AppSession, StateMachine {

    void sendAAAnswer(S6bAAAnswer answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

    void sendSessionTerminationAnswer(S6bSessionTerminationAnswer answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

    void sendReAuthRequest(S6bReAuthRequest request) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

    void sendAbortSessionRequest(S6bAbortSessionRequest request) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;
}
