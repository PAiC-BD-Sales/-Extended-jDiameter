package org.jdiameter.common.impl.app.swm;

import org.jdiameter.api.Request;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.swm.events.SWmAbortSessionRequest;
import org.jdiameter.common.impl.app.AppRequestEventImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author <a href="mailto:enmanuelcalero61@gmail.com"> Enmanuel Calero </a>
 */
public class SWmAbortSessionRequestImpl extends AppRequestEventImpl implements SWmAbortSessionRequest {

  private static final long serialVersionUID = 1L;
  protected static final Logger logger = LoggerFactory.getLogger(SWmAbortSessionRequestImpl.class);

  public SWmAbortSessionRequestImpl(AppSession session, String destRealm, String destHost) {
    super(session.getSessions().get(0).createRequest(code, session.getSessionAppId(), destRealm, destHost));
  }

  public SWmAbortSessionRequestImpl(Request request) {
    super(request);
  }
}
