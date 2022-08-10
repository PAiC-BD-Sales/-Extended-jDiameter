package org.jdiameter.common.impl.app.zh;

import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.zh.ClientZhSession;
import org.jdiameter.api.zh.ServerZhSession;
import org.jdiameter.client.impl.app.slh.ClientSLhSessionDataLocalImpl;
import org.jdiameter.client.impl.app.zh.ClientZhSessionDataLocalImpl;
import org.jdiameter.common.api.app.IAppSessionDataFactory;
import org.jdiameter.common.api.app.zh.IZhSessionData;

public class ZhLocalSessionDataFactory implements IAppSessionDataFactory<IZhSessionData> {
    @Override
    public IZhSessionData getAppSessionData(Class<? extends AppSession> clazz, String sessionId) {
        if (clazz.equals(ClientZhSession.class)) {
            ClientZhSessionDataLocalImpl data = new ClientZhSessionDataLocalImpl();
            data.setSessionId(sessionId);
            return data;
        } else if (clazz.equals(ServerZhSession.class)) {

        } else {
            throw new IllegalArgumentException("Invalid Session Class: " + clazz);
        }
    }
}
