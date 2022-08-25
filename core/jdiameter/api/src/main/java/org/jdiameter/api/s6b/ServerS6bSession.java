package org.jdiameter.api.s6b;

import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.app.StateMachine;
import org.jdiameter.api.s6b.events.S6bAbortSessionRequest;
import org.jdiameter.api.s6b.events.S6bDiameterEAPAnswer;
import org.jdiameter.api.s6b.events.S6bSessionTerminationAnswer;

/**
 * Basic class for S6b Server Interface specific session.
 * Listener must be injected from constructor of implementation class
 *
 * @author <a href="mailto:giokast90@gmail.com"> Giovanni Castillo </a>
 * @author <a href="mailto:enmanuelcalero61@gmail.com"> Enmanuel Calero </a>
 */
public interface ServerS6bSession extends AppSession, StateMachine {

  void sendSessionTerminationAnswer(S6bSessionTerminationAnswer answer)
          throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

  void sendDiameterEAPAnswer(S6bDiameterEAPAnswer answer)
          throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

  void sendAbortSessionRequest(S6bAbortSessionRequest request)
          throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

}
