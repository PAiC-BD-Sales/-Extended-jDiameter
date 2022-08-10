package org.jdiameter.common.impl.app.swm;

import org.jdiameter.api.Answer;
import org.jdiameter.api.Request;
import org.jdiameter.api.swm.events.SWmDiameterEAPAnswer;
import org.jdiameter.common.impl.app.AppAnswerEventImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author <a href="mailto:enmanuelcalero61@gmail.com"> Enmanuel Calero </a>
 */
public class SWmDiameterEAPAnswerImpl extends AppAnswerEventImpl implements SWmDiameterEAPAnswer {

    private static final long serialVersionUID = 1L;
    protected static final Logger logger = LoggerFactory.getLogger(SWmDiameterEAPAnswerImpl.class);

    public SWmDiameterEAPAnswerImpl(Request message, long resultCode) {
        super(message.createAnswer(resultCode));
    }

    public SWmDiameterEAPAnswerImpl(Answer message) {
        super(message);
    }
}
