package org.jdiameter.api.s6b;

import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppRequestEvent;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.s6b.events.S6bAbortSessionRequest;
import org.jdiameter.api.s6b.events.S6bDiameterEAPAnswer;
import org.jdiameter.api.s6b.events.S6bDiameterEAPRequest;
import org.jdiameter.api.s6b.events.S6bReAuthAnswer;
import org.jdiameter.api.s6b.events.S6bReAuthRequest;
import org.jdiameter.api.s6b.events.S6bSessionTerminationRequest;
import org.jdiameter.api.s6b.events.S6bSessionTerminationAnswer;

/**
 * This interface defines the possible actions for the different states in the client
 * S6b Interface state machine.
 *
 * @author <a href="mailto:giokast90@gmail.com"> Giovanni Castillo </a>
 */
public interface ClientS6bSessionListener {

  void doSessionTerminationAnswer(ClientS6bSession session,
                                  S6bSessionTerminationRequest request,
                                  S6bSessionTerminationAnswer answer)
          throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

  void doDiameterEAPAnswer(ClientS6bSession session, S6bDiameterEAPRequest request, S6bDiameterEAPAnswer answer)
          throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;


  void doAbortSessionRequest(ClientS6bSession session, S6bAbortSessionRequest request)
          throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

  void doReAuthAnswerEvent(ClientS6bSession session, S6bReAuthRequest request, S6bReAuthAnswer answer)
          throws InternalException, IllegalDiameterStateException, RouteException, OverloadException, AvpDataException;
  void doOtherEvent(AppSession session,
                    AppRequestEvent request,
                    AppAnswerEvent answer)
          throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;
}
