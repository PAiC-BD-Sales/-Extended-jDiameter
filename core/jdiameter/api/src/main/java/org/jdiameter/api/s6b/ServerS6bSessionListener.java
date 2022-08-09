package org.jdiameter.api.s6b;

import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppRequestEvent;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.s6b.events.S6bSessionTerminationRequest;

public interface ServerS6bSessionListener {

    /**
     * Notifies this ServerS6bSessionListener that the ServerS6bSession has received a STR message.
     *
     * @param session parent application session (FSM)
     * @param request request object
     * @throws InternalException The InternalException signals that internal error is occurred.
     * @throws IllegalDiameterStateException The IllegalStateException signals that session has incorrect state (invalid).
     * @throws RouteException The NoRouteException signals that no route exist for a given realm.
     * @throws OverloadException The OverloadException signals that destination host is overloaded.
     */
    void doSessionTerminationRequest(ServerS6bSession session, S6bSessionTerminationRequest request) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

    /**
     * Notifies this ServerS6bSessionListener that the ServerS6bSession has received not STR message,
     * now it can be even AAA.
     *
     * @param session parent application session (FSM)
     * @param request request object
     * @param answer answer object
     * @throws InternalException The InternalException signals that internal error is occurred.
     * @throws IllegalDiameterStateException The IllegalStateException signals that session has incorrect state (invalid).
     * @throws RouteException The NoRouteException signals that no route exist for a given realm.
     * @throws OverloadException The OverloadException signals that destination host is overloaded.
     */
    void doOtherEvent(AppSession session, AppRequestEvent request, AppAnswerEvent answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;
}
