package org.jdiameter.api.s6b;

import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.app.StateMachine;
import org.jdiameter.api.s6b.events.S6bAbortSessionAnswer;
import org.jdiameter.api.s6b.events.S6bDiameterEAPRequest;
import org.jdiameter.api.s6b.events.S6bReAuthRequest;
import org.jdiameter.api.s6b.events.S6bSessionTerminationRequest;

/**
 * Basic class for S6b Client Interface specific session.
 * Listener must be injected from constructor of implementation class.
 *
 * @author <a href="mailto:giokast90@gmail.com"> Giovanni Castillo </a>
 */
public interface ClientS6bSession extends AppSession, StateMachine {

  void sendSessionTerminationRequest(final S6bSessionTerminationRequest request)
          throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

  void sendDiameterEAPRequest(final S6bDiameterEAPRequest request)
          throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

  void sendAbortSessionAnswer(final S6bAbortSessionAnswer answer)
          throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

  /**
   * Send S6bReAuthRequest to the server
   *
   * @param request S6bReAuthRequest event instance
   * @throws InternalException The InternalException signals that internal error is occurred.
   * @throws IllegalDiameterStateException The IllegalStateException signals that session has incorrect state (invalid).
   * @throws RouteException The NoRouteException signals that no route exist for a given realm.
   * @throws OverloadException The OverloadException signals that destination host is overloaded.
   */
  void sendReAuthRequest(final S6bReAuthRequest request)
          throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

}
