package org.jdiameter.common.api.app.swm;

import org.jdiameter.api.Message;
import org.jdiameter.api.swm.ClientSWmSession;

/**
 * Diameter 3GPP IMS SWm Reference Point Client Additional listener.
 * Actions for FSM
 *
 * @author <a href="mailto:enmanuelcalero61@gmail.com"> Enmanuel Calero </a>
 */
public interface IClientSWmSessionContext {

    void grantAccessOnDeliverFailure(ClientSWmSession clientCCASessionImpl, Message request);

    void denyAccessOnDeliverFailure(ClientSWmSession clientCCASessionImpl, Message request);

    void grantAccessOnFailureMessage(ClientSWmSession clientCCASessionImpl);

    void denyAccessOnFailureMessage(ClientSWmSession clientCCASessionImpl);

    void indicateServiceError(ClientSWmSession clientCCASessionImpl);
}
