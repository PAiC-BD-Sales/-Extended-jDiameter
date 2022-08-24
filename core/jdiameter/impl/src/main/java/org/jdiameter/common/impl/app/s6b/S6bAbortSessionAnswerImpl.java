package org.jdiameter.common.impl.app.s6b;

import org.jdiameter.api.Answer;
import org.jdiameter.api.Request;
import org.jdiameter.api.s6b.events.S6bAbortSessionAnswer;
import org.jdiameter.common.impl.app.AppAnswerEventImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author <a href="mailto:enmanuelcalero61@gmail.com"> Enmanuel Calero </a>
 */
public class S6bAbortSessionAnswerImpl extends AppAnswerEventImpl implements S6bAbortSessionAnswer {

  private static final long serialVersionUID = 1L;
  protected static final Logger logger = LoggerFactory.getLogger(S6bAbortSessionAnswerImpl.class);

  public S6bAbortSessionAnswerImpl(Request message, long resultCode) {
    super(message.createAnswer(resultCode));
  }

  public S6bAbortSessionAnswerImpl(Answer message) {
    super(message);
  }
}
