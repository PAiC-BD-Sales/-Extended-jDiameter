package org.jdiameter.api.s6b;

import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppRequestEvent;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.s6b.events.S6bAARequest;
import org.jdiameter.api.s6b.events.S6bAbortSessionAnswer;
import org.jdiameter.api.s6b.events.S6bAbortSessionRequest;
import org.jdiameter.api.s6b.events.S6bDiameterEAPRequest;
import org.jdiameter.api.s6b.events.S6bReAuthRequest;
import org.jdiameter.api.s6b.events.S6bSessionTerminationRequest;

public interface ServerS6bSessionListener {

  /**
   * Notifies this ServerS6bSessionListener that the ServerS6bSession has received a STR message.
   *
   * @param session parent application session (FSM)
   * @param request request object
   * @throws InternalException             The InternalException signals that internal error is occurred.
   * @throws IllegalDiameterStateException The IllegalStateException signals that session has incorrect state (invalid).
   * @throws RouteException                The NoRouteException signals that no route exist for a given realm.
   * @throws OverloadException             The OverloadException signals that destination host is overloaded.
   */
  void doSessionTerminationRequest(ServerS6bSession session, S6bSessionTerminationRequest request)
          throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;
  /**
   * Notifies this ServerS6bSessionListener that the ServerS6bSession has received not STR message,
   * now it can be even AAA.
   *
   * @param session parent application session (FSM)
   * @param request request object
   * @param answer  answer object
   * @throws InternalException             The InternalException signals that internal error is occurred.
   * @throws IllegalDiameterStateException The IllegalStateException signals that session has incorrect state (invalid).
   * @throws RouteException                The NoRouteException signals that no route exist for a given realm.
   * @throws OverloadException             The OverloadException signals that destination host is overloaded.
   */
  void doOtherEvent(AppSession session, AppRequestEvent request, AppAnswerEvent answer)
          throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;
  void doDiameterEAPRequest(ServerS6bSession session, S6bDiameterEAPRequest request)
          throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;
  void doAbortSessionAnswer(ServerS6bSession session, S6bAbortSessionRequest request, S6bAbortSessionAnswer answer)
          throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;
  void doReAuthRequestEvent(ServerS6bSession session, S6bReAuthRequest request)
          throws InternalException, IllegalDiameterStateException, RouteException, OverloadException, AvpDataException;
  void doAARequestEvent(ServerS6bSession session, S6bAARequest request)
          throws InternalException, IllegalDiameterStateException, RouteException, OverloadException, AvpDataException;
  void doAbortSessionRequestEvent(ServerS6bSession session, S6bAbortSessionRequest request)
          throws InternalException, IllegalDiameterStateException, RouteException, OverloadException, AvpDataException;
}
