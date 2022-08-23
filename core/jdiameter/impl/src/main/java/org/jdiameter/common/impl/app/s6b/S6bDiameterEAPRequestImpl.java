package org.jdiameter.common.impl.app.s6b;

import org.jdiameter.api.Request;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.s6b.events.S6bDiameterEAPRequest;
import org.jdiameter.common.impl.app.AppRequestEventImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author <a href="mailto:giokast90@gmail.com"> Giovanni Castillo </a>
 */
public class S6bDiameterEAPRequestImpl extends AppRequestEventImpl implements S6bDiameterEAPRequest {

  private static final long serialVersionUID = 1L;
  protected static final Logger logger = LoggerFactory.getLogger(S6bDiameterEAPRequestImpl.class);

  public S6bDiameterEAPRequestImpl(AppSession session, String destRealm, String destHost) {
    super(session.getSessions().get(0).createRequest(code, session.getSessionAppId(), destRealm, destHost));
  }

  public S6bDiameterEAPRequestImpl(Request request) {
    super(request);
  }
}
