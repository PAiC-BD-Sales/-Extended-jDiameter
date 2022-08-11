package org.jdiameter.common.api.app.s6b;

import org.jdiameter.api.Answer;
import org.jdiameter.api.Request;
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

    long[] getApplicationIds();

}
