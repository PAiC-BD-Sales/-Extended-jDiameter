package org.jdiameter.common.impl.app.swm;

import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.rx.ServerRxSession;
import org.jdiameter.api.swm.ClientSWmSession;
import org.jdiameter.client.impl.app.swm.ClientSWmSessionDataLocalImpl;
import org.jdiameter.common.api.app.IAppSessionDataFactory;
import org.jdiameter.common.api.app.swm.ISWmSessionData;
import org.jdiameter.server.impl.app.swm.ServerSWmSessionDataLocalImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SWmLocalSessionDataFactory implements IAppSessionDataFactory<ISWmSessionData> {

    protected static final Logger logger = LoggerFactory.getLogger(SWmLocalSessionDataFactory.class);

    @Override
    public ISWmSessionData getAppSessionData(Class<? extends AppSession> clazz, String sessionId) {
        if (clazz.equals(ClientSWmSession.class)) {
            logger.info("EJD -> is Client session");
            ClientSWmSessionDataLocalImpl data = new ClientSWmSessionDataLocalImpl();
            data.setSessionId(sessionId);
            return data;
        }
        else if (clazz.equals(ServerRxSession.class)) {
            logger.info("EJD -> is Server session");
            ServerSWmSessionDataLocalImpl data = new ServerSWmSessionDataLocalImpl();
            data.setSessionId(sessionId);
            return data;
        }
        throw new IllegalArgumentException(clazz.toString());
    }
}
