package org.jdiameter.common.impl.app.s6b;

import org.jdiameter.api.Answer;
import org.jdiameter.api.Request;
import org.jdiameter.api.s6b.events.S6bDiameterEAPAnswer;
import org.jdiameter.common.impl.app.AppAnswerEventImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author <a href="mailto:giokast90@gmail.com"> Giovanni Castillo </a>
 */
public class S6bDiameterEAPAnswerImpl extends AppAnswerEventImpl implements S6bDiameterEAPAnswer {

  private static final long serialVersionUID = 1L;
  protected static final Logger logger = LoggerFactory.getLogger(S6bDiameterEAPAnswerImpl.class);

  public S6bDiameterEAPAnswerImpl(Request message, long resultCode) {
    super(message.createAnswer(resultCode));
  }

  public S6bDiameterEAPAnswerImpl(Answer message) {
    super(message);
  }
}
