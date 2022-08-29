package org.jdiameter.common.impl.app.swm;

import org.jdiameter.api.Answer;
import org.jdiameter.api.Request;
import org.jdiameter.api.swm.events.SWmAbortSessionAnswer;
import org.jdiameter.common.impl.app.AppAnswerEventImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author <a href="mailto:enmanuelcalero61@gmail.com"> Enmanuel Calero </a>
 */
public class SWmAbortSessionAnswerImpl extends AppAnswerEventImpl implements SWmAbortSessionAnswer {

  private static final long serialVersionUID = 1L;
  protected static final Logger logger = LoggerFactory.getLogger(SWmAbortSessionAnswerImpl.class);

  public SWmAbortSessionAnswerImpl(Request message, long resultCode) {
    super(message.createAnswer(resultCode));
  }

  public SWmAbortSessionAnswerImpl(Answer message) {
    super(message);
  }
}
