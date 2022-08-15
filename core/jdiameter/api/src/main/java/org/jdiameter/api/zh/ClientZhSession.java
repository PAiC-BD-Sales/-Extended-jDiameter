package org.jdiameter.api.zh;

import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.app.StateMachine;
import org.jdiameter.api.zh.events.MultimediaAuthRequest;

public interface ClientZhSession extends AppSession, StateMachine {
    /**
     * Send MultimediaAuthRequest to the server
     *
     * @param request MultimediaAuthRequest event instance
     * @throws InternalException The InternalException signals that internal error is occurred.
     * @throws IllegalDiameterStateException The IllegalStateException signals that session has incorrect state (invalid).
     * @throws RouteException The NoRouteException signals that no route exist for a given realm.
     * @throws OverloadException The OverloadException signals that destination host is overloaded.
     */
  void sendMultimediaAuthRequest(MultimediaAuthRequest request) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;;
}
