package org.jdiameter.api.swm;

import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppRequestEvent;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.swm.events.SWmAbortSessionAnswer;
import org.jdiameter.api.swm.events.SWmAbortSessionRequest;
import org.jdiameter.api.swm.events.SWmDiameterEAPAnswer;
import org.jdiameter.api.swm.events.SWmDiameterEAPRequest;

public interface ClientSWmSessionListener {

    void doDiameterEAPAnswer(ClientSWmSession session, SWmDiameterEAPRequest request, SWmDiameterEAPAnswer answer)
            throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

    void doAbortSessionRequest(ClientSWmSession session, SWmAbortSessionRequest request)
            throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

    void doOtherEvent(AppSession session, AppRequestEvent request, AppAnswerEvent answer)
            throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;
}
