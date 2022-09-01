package org.jdiameter.common.impl.app.s6b;

import org.jdiameter.api.Request;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.s6b.events.S6bAARequest;
import org.jdiameter.common.impl.app.AppAnswerEventImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author <a href="mailto:kennymendieta89@gmail.com"> Kenny Mendieta </a>
 */
public class S6bAARequestImpl extends AppAnswerEventImpl implements S6bAARequest {
  private static final long serialVersionUID = 1L;
  protected static final Logger logger = LoggerFactory.getLogger(S6bAARequestImpl.class);

  public S6bAARequestImpl(AppSession session, String destRealm, String destHost) {
    super(session.getSessions().get(0).createRequest(code, session.getSessionAppId(), destRealm, destHost));
  }

  public S6bAARequestImpl(Request message) {
    super(message);
  }
}
