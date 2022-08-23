package org.jdiameter.api.swm;

import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.app.StateMachine;
import org.jdiameter.api.swm.events.SWmAbortSessionRequest;
import org.jdiameter.api.swm.events.SWmDiameterAAAnswer;
import org.jdiameter.api.swm.events.SWmDiameterEAPAnswer;
import org.jdiameter.api.swm.events.SWmReAuthRequest;
import org.jdiameter.api.swm.events.SWmSessionTermAnswer;


/**
 * Basic class for SWm Server Interface specific session.
 * Listener must be injected from constructor of implementation class
 *
 * @author <a href="mailto:enmanuelcalero61@gmail.com"> Enmanuel Calero </a>
 */
public interface ServerSWmSession extends AppSession, StateMachine {

  void sendDiameterEAPAnswer(SWmDiameterEAPAnswer answer)
          throws InternalException, IllegalDiameterStateException, RouteException, OverloadException, AvpDataException;

  void sendDiameterAAAnswer(SWmDiameterAAAnswer answer)
          throws InternalException, IllegalDiameterStateException, RouteException, OverloadException, AvpDataException;

  void sendAbortSessionRequest(SWmAbortSessionRequest request)
          throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

  void sendSessionTermAnswer(SWmSessionTermAnswer answer)
          throws InternalException, IllegalDiameterStateException, RouteException, OverloadException, AvpDataException;

  void sendReAuthRequest(SWmReAuthRequest request)
          throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

}
