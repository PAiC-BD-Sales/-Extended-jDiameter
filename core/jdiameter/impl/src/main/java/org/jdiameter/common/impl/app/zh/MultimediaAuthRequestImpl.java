package org.jdiameter.common.impl.app.zh;

import org.jdiameter.api.Message;
import org.jdiameter.api.zh.events.MultimediaAuthRequest;
import org.jdiameter.common.impl.app.AppRequestEventImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MultimediaAuthRequestImpl extends AppRequestEventImpl implements MultimediaAuthRequest {
    private static final long serialVersionUID = 1L;

    protected static final Logger logger = LoggerFactory.getLogger(MultimediaAuthRequestImpl.class);
    public MultimediaAuthRequestImpl(Message message) {
        super(message);
        message.setRequest(true);
    }


}
