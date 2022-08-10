package org.jdiameter.common.api.app.s6b;

import org.jdiameter.api.Message;
import org.jdiameter.api.s6b.ClientS6bSession;

/**
 * Diameter S6b Reference Point Client Additional listener.
 * Actions for FSM
 *
 * @author <a href="mailto:giokast90@gmail.com"> Giovanni Castillo </a>
 */
public interface IClientS6bSessionContext {

    void grantAccessOnDeliverFailure(ClientS6bSession clientS6bSessionImpl, Message request);

    void denyAccessOnDeliverFailure(ClientS6bSession clientS6bSessionImpl, Message request);

    void grantAccessOnFailureMessage(ClientS6bSession clientS6bSessionImpl);

    void denyAccessOnFailureMessage(ClientS6bSession clientS6bSessionImpl);

    void indicateServiceError(ClientS6bSession clientS6bSessionImpl);
}
