package org.jdiameter.common.impl.app.zh;

import org.jdiameter.api.Answer;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.Request;
import org.jdiameter.api.zh.events.MultimediaAuthAnswer;
import org.jdiameter.common.impl.app.AppRequestEventImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:kennymendieta89@gmail.com"> Kenny Mendieta </a>
 *
 */
public class MultimediaAuthAnswerImpl extends AppRequestEventImpl implements MultimediaAuthAnswer {

  private static final long serialVersionUID = 1L;

  protected static final Logger logger = LoggerFactory.getLogger(MultimediaAuthAnswerImpl.class);
    /**
     *
     * @param answer
     */
  public MultimediaAuthAnswerImpl(Answer answer) {
        super(answer);
    }
    /**
     *
     * @param request
     * @param resultCode
     */
  public MultimediaAuthAnswerImpl(Request request, long resultCode) {
    super(request.createAnswer(resultCode));
  }

  @Override
  public Avp getResultCodeAvp() throws AvpDataException {
    return null;
  }
}
