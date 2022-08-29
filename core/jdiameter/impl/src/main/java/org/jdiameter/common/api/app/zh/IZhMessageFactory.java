package org.jdiameter.common.api.app.zh;

import org.jdiameter.api.Answer;
import org.jdiameter.api.Request;
import org.jdiameter.api.zh.events.MultimediaAuthAnswer;
import org.jdiameter.api.zh.events.MultimediaAuthRequest;

/**
 * @author <a href="mailto:kennymendieta89@gmail.com"> Kenny Mendieta </a>
 *
 */
public interface IZhMessageFactory {
  MultimediaAuthRequest createMultimediaAuthRequest(Request request);
  MultimediaAuthAnswer createMultimediaAuthAnswer(Answer answer);
    /**
     * Returns the Application-Id that this message factory is related to
     *
     * @return the Application-Id value
     */
  long getApplicationId();

}
