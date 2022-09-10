package org.jdiameter.common.api.app.s6b;

import org.jdiameter.api.Answer;
import org.jdiameter.api.Request;
import org.jdiameter.api.s6b.events.S6bAAAnswer;
import org.jdiameter.api.s6b.events.S6bAARequest;
import org.jdiameter.api.s6b.events.S6bAbortSessionAnswer;
import org.jdiameter.api.s6b.events.S6bAbortSessionRequest;
import org.jdiameter.api.s6b.events.S6bDiameterEAPAnswer;
import org.jdiameter.api.s6b.events.S6bDiameterEAPRequest;
import org.jdiameter.api.s6b.events.S6bReAuthAnswer;
import org.jdiameter.api.s6b.events.S6bReAuthRequest;
import org.jdiameter.api.s6b.events.S6bSessionTerminationRequest;
import org.jdiameter.api.s6b.events.S6bSessionTerminationAnswer;

/**
 * Diameter S6b Reference Point Message Factory
 *
 * @author <a href="mailto:giokast90@gmail.com"> Giovanni Castillo </a>
 */
public interface IS6bMessageFactory {

  S6bSessionTerminationRequest createSessionTermRequest(Request request);

  S6bSessionTerminationAnswer createSessionTermAnswer(Answer answer);

  S6bDiameterEAPRequest createDiameterEAPRequest(Request request);

  S6bDiameterEAPAnswer createDiameterEAPAnswer(Answer answer);

  S6bAbortSessionRequest createAbortSessionRequest(Request request);

  S6bAbortSessionAnswer createAbortSessionAnswer(Answer answer);

  long[] getApplicationIds();
  S6bReAuthRequest createReAuthRequest(Request request);
  S6bReAuthAnswer createReAuthAnswer(Answer answer);
  S6bAAAnswer createAAAnswer(Answer answer);
  S6bAARequest createAARequest(Request request);
}
