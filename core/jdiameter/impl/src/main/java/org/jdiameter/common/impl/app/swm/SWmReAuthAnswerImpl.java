package org.jdiameter.common.impl.app.swm;

import org.jdiameter.api.Answer;
import org.jdiameter.api.Request;
import org.jdiameter.api.swm.events.SWmReAuthAnswer;
import org.jdiameter.common.impl.app.AppAnswerEventImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:enmanuelcalero61@gmail.com"> Enmanuel Calero </a>
 */
public class SWmReAuthAnswerImpl extends AppAnswerEventImpl implements SWmReAuthAnswer {

  private static final long serialVersionUID = 1L;
  protected static final Logger logger = LoggerFactory.getLogger(SWmReAuthAnswerImpl.class);

  public SWmReAuthAnswerImpl(Request message, long resultCode) {
    super(message.createAnswer(resultCode));
  }

  public SWmReAuthAnswerImpl(Answer message) {
    super(message);
  }
}
