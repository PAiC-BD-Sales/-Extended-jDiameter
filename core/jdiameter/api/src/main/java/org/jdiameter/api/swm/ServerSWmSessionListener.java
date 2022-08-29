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
import org.jdiameter.api.swm.events.SWmDiameterAARequest;
import org.jdiameter.api.swm.events.SWmDiameterEAPRequest;
import org.jdiameter.api.swm.events.SWmReAuthAnswer;
import org.jdiameter.api.swm.events.SWmReAuthRequest;
import org.jdiameter.api.swm.events.SWmSessionTermRequest;

public interface ServerSWmSessionListener {
  void doDiameterEAPRequest(ServerSWmSession session, SWmDiameterEAPRequest request)
          throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

  void doDiameterAARequest(ServerSWmSession session, SWmDiameterAARequest request)
          throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

  void doAbortSessionAnswer(ServerSWmSession session, SWmAbortSessionRequest request, SWmAbortSessionAnswer answer)
          throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

  void doReAuthAnswer(ServerSWmSession session, SWmReAuthRequest request, SWmReAuthAnswer answer)
          throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

  void doSessionTermRequest(ServerSWmSession session, SWmSessionTermRequest request)
          throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

  void doOtherEvent(AppSession session, AppRequestEvent request, AppAnswerEvent answer)
          throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;
}
