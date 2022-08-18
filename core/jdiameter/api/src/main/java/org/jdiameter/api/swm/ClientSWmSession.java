package org.jdiameter.api.swm;

import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.app.StateMachine;
import org.jdiameter.api.swm.events.SWmAbortSessionAnswer;
import org.jdiameter.api.swm.events.SWmDiameterAARequest;
import org.jdiameter.api.swm.events.SWmDiameterEAPRequest;
import org.jdiameter.api.swm.events.SWmReAuthAnswer;


/**
 * Basic class for SWm Client Interface specific session.
 * Listener must be injected from constructor of implementation class.
 *
 * @author <a href="mailto:enmanuelcalero61@gmail.com"> Enmanuel Calero </a>
 */
public interface ClientSWmSession extends AppSession, StateMachine {


  void sendDiameterEAPRequest(final SWmDiameterEAPRequest request)
          throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

  void sendDiameterAARequest(final SWmDiameterAARequest request)
          throws InternalException, IllegalDiameterStateException, RouteException,
          OverloadException;

  void sendAbortSessionAnswer(final SWmAbortSessionAnswer answer)
          throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

  void sendReAuthAnswer(final SWmReAuthAnswer answer)
          throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

}
