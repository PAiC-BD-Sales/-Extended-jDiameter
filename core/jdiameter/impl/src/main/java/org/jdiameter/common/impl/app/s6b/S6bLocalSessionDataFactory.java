package org.jdiameter.common.impl.app.s6b;

import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.s6b.ClientS6bSession;
import org.jdiameter.api.s6b.ServerS6bSession;
import org.jdiameter.client.impl.app.s6b.ClientS6bSessionDataLocalImpl;
import org.jdiameter.common.api.app.IAppSessionDataFactory;
import org.jdiameter.common.api.app.s6b.IS6bSessionData;
import org.jdiameter.server.impl.app.s6b.ServerS6bSessionDataLocalImpl;

/**
 * @author <a href="mailto:giokast90@gmail.com"> Giovanni Castillo </a>
 */
public class S6bLocalSessionDataFactory implements IAppSessionDataFactory<IS6bSessionData> {

  /* (non-Javadoc)
   * @see org.jdiameter.common.api.app.IAppSessionDataFactory#getAppSessionData(java.lang.Class, java.lang.String)
   */
  @Override
  public IS6bSessionData getAppSessionData(Class<? extends AppSession> clazz, String sessionId) {
    if (clazz.equals(ClientS6bSession.class)) {
      ClientS6bSessionDataLocalImpl data = new ClientS6bSessionDataLocalImpl();
      data.setSessionId(sessionId);
      return data;
    } else if (clazz.equals(ServerS6bSession.class)) {
      ServerS6bSessionDataLocalImpl data = new ServerS6bSessionDataLocalImpl();
      data.setSessionId(sessionId);
      return data;
    }
    throw new IllegalArgumentException(clazz.toString());
  }

}
