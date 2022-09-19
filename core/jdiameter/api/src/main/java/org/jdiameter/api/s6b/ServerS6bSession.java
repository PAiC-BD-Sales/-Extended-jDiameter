package org.jdiameter.api.s6b;

import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.app.StateMachine;
import org.jdiameter.api.s6b.events.S6bAAAnswer;
import org.jdiameter.api.s6b.events.S6bAbortSessionAnswer;
import org.jdiameter.api.s6b.events.S6bAbortSessionRequest;
import org.jdiameter.api.s6b.events.S6bDiameterEAPAnswer;
import org.jdiameter.api.s6b.events.S6bReAuthAnswer;
import org.jdiameter.api.s6b.events.S6bSessionTerminationAnswer;

/**
 * Basic class for S6b Server Interface specific session.
 * Listener must be injected from constructor of implementation class
 *
 * @author <a href="mailto:giokast90@gmail.com"> Giovanni Castillo </a>
 * @author <a href="mailto:enmanuelcalero61@gmail.com"> Enmanuel Calero </a>
 * @author <a href="mailto:kennymendieta89@gmail.com"> Kenny Mendieta </a>
 */
public interface ServerS6bSession extends AppSession, StateMachine {

  void sendSessionTerminationAnswer(S6bSessionTerminationAnswer answer)
          throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

  void sendDiameterEAPAnswer(S6bDiameterEAPAnswer answer)
          throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

  void sendAbortSessionRequest(S6bAbortSessionRequest request)
          throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;
  /**
   * Send S6bAbortSessionAnswer to the server
   *
   * @param answer S6bAbortSessionAnswer event instance
   * @throws InternalException The InternalException signals that internal error is occurred.
   * @throws IllegalDiameterStateException The IllegalStateException signals that session has incorrect state (invalid).
   * @throws RouteException The NoRouteException signals that no route exist for a given realm.
   * @throws OverloadException The OverloadException signals that destination host is overloaded.
   */
  void sendAbortSessionAnswer(S6bAbortSessionAnswer answer)
        throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;
  /**
   * Send S6bReAuthAnswer to the server
   *
   * @param answer S6bReAuthAnswer event instance
   * @throws InternalException The InternalException signals that internal error is occurred.
   * @throws IllegalDiameterStateException The IllegalStateException signals that session has incorrect state (invalid).
   * @throws RouteException The NoRouteException signals that no route exist for a given realm.
   * @throws OverloadException The OverloadException signals that destination host is overloaded.
   */
  void sendReAuthAnswer(S6bReAuthAnswer answer)
          throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;
  /**
   * Send S6bAAAnswer to the server
   *
   * @param answer S6bAAAnswer event instance
   * @throws InternalException The InternalException signals that internal error is occurred.
   * @throws IllegalDiameterStateException The IllegalStateException signals that session has incorrect state (invalid).
   * @throws RouteException The NoRouteException signals that no route exist for a given realm.
   * @throws OverloadException The OverloadException signals that destination host is overloaded.
   */
  void sendAAAnswer(S6bAAAnswer answer)
          throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;
}
