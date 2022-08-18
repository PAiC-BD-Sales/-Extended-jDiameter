package org.jdiameter.common.api.app.swm;

import org.jdiameter.api.Answer;
import org.jdiameter.api.Request;
import org.jdiameter.api.swm.events.SWmAbortSessionAnswer;
import org.jdiameter.api.swm.events.SWmAbortSessionRequest;
import org.jdiameter.api.swm.events.SWmDiameterAAAnswer;
import org.jdiameter.api.swm.events.SWmDiameterAARequest;
import org.jdiameter.api.swm.events.SWmDiameterEAPAnswer;
import org.jdiameter.api.swm.events.SWmDiameterEAPRequest;
import org.jdiameter.api.swm.events.SWmReAuthAnswer;
import org.jdiameter.api.swm.events.SWmReAuthRequest;


/**
 * Diameter 3GPP IMS SWm Reference Point Message Factory
 *
 * @author <a href="mailto:enmanuelcalero61@gmail.com"> Enmanuel Calero </a>
 */
public interface ISWmMessageFactory {

  SWmDiameterAARequest createDiameterAARequest(Request request);

  SWmDiameterAAAnswer createDiameterAAAnswer(Answer answer);

  SWmAbortSessionRequest createAbortSessionRequest(Request request);

  SWmAbortSessionAnswer createAbortSessionAnswer(Answer answer);

  SWmDiameterEAPRequest createDiameterEAPRequest(Request request);

  SWmDiameterEAPAnswer createDiameterEAPAnswer(Answer answer);

  SWmReAuthRequest createReAuthRequest(Request request);

  SWmReAuthAnswer createReAuthAnswer(Answer answer);

  long[] getApplicationIds();
}
