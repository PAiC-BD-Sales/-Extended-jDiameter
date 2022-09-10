package org.jdiameter.common.impl.app.s6b;

import org.jdiameter.api.Answer;
import org.jdiameter.api.Request;
import org.jdiameter.api.s6b.events.S6bReAuthAnswer;
import org.jdiameter.common.impl.app.AppAnswerEventImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author <a href="mailto:kennymendieta89@gmail.com"> Kenny Mendieta </a>
 */
public class S6bReAuthAnswerImpl extends AppAnswerEventImpl implements S6bReAuthAnswer {
  private static final long serialVersionUID = 1L;
  protected static final Logger logger = LoggerFactory.getLogger(S6bReAuthAnswerImpl.class);

  public S6bReAuthAnswerImpl(Request message, long resultCode) {
    super(message.createAnswer(resultCode));
  }

  public S6bReAuthAnswerImpl(Answer message) {
    super(message);
  }
}
