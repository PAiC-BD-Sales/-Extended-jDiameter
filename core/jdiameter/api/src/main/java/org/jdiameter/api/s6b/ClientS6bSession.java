package org.jdiameter.api.s6b;

import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.app.StateMachine;
import org.jdiameter.api.s6b.events.S6bAARequest;
import org.jdiameter.api.s6b.events.S6bAbortSessionAnswer;
import org.jdiameter.api.s6b.events.S6bAbortSessionRequest;
import org.jdiameter.api.s6b.events.S6bDiameterEAPRequest;
import org.jdiameter.api.s6b.events.S6bReAuthRequest;
import org.jdiameter.api.s6b.events.S6bSessionTerminationRequest;

/**
 * Basic class for S6b Client Interface specific session.
 * Listener must be injected from constructor of implementation class.
 *
 * @author <a href="mailto:giokast90@gmail.com"> Giovanni Castillo </a>
 * @author <a href="mailto:kennymendieta89@gmail.com"> Kenny Mendieta </a>
 */
public interface ClientS6bSession extends AppSession, StateMachine {

  /**
   * Send S6bSessionTerminationRequest to the server
   *
   * @param request S6bSessionTerminationRequest instance
   * @throws InternalException The InternalException signals that internal error is occurred.
   * @throws IllegalDiameterStateException The IllegalStateException signals that session has incorrect state (invalid).
   * @throws RouteException The NoRouteException signals that no route exist for a given realm.
   * @throws OverloadException The OverloadException signals that destination host is overloaded.
   */
  void sendSessionTerminationRequest(final S6bSessionTerminationRequest request)
          throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

  /**
   * Send S6bDiameterEAPRequest to the server
   *
   * @param request S6bDiameterEAPRequest instance
   * @throws InternalException The InternalException signals that internal error is occurred.
   * @throws IllegalDiameterStateException The IllegalStateException signals that session has incorrect state (invalid).
   * @throws RouteException The NoRouteException signals that no route exist for a given realm.
   * @throws OverloadException The OverloadException signals that destination host is overloaded.
   */
  void sendDiameterEAPRequest(final S6bDiameterEAPRequest request)
          throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

  /**
   * Send S6bAbortSessionAnswer to the server
   *
   * @param answer S6bAbortSessionAnswer instance
   * @throws InternalException The InternalException signals that internal error is occurred.
   * @throws IllegalDiameterStateException The IllegalStateException signals that session has incorrect state (invalid).
   * @throws RouteException The NoRouteException signals that no route exist for a given realm.
   * @throws OverloadException The OverloadException signals that destination host is overloaded.
   */
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

  /**
   * Send S6bAARequest to the server
   *
   * @param request S6bAARequest instance
   * @throws InternalException The InternalException signals that internal error is occurred.
   * @throws IllegalDiameterStateException The IllegalStateException signals that session has incorrect state (invalid).
   * @throws RouteException The NoRouteException signals that no route exist for a given realm.
   * @throws OverloadException The OverloadException signals that destination host is overloaded.
   */
  void sendAARequest(final S6bAARequest request)
          throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;
  /**
   * Send S6bAbortSessionRequest to the server
   *
   * @param request S6bAbortSessionRequest instance
   * @throws InternalException The InternalException signals that internal error is occurred.
   * @throws IllegalDiameterStateException The IllegalStateException signals that session has incorrect state (invalid).
   * @throws RouteException The NoRouteException signals that no route exist for a given realm.
   * @throws OverloadException The OverloadException signals that destination host is overloaded.
   */
  void sendAbortSessionRequest(final S6bAbortSessionRequest request)
          throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;
}
