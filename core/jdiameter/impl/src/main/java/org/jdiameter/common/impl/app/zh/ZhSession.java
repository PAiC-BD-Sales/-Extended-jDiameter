package org.jdiameter.common.impl.app.zh;

import org.jdiameter.api.NetworkReqListener;
import org.jdiameter.api.app.StateMachine;
import org.jdiameter.client.api.ISessionFactory;
import org.jdiameter.common.api.app.IAppSessionData;
import org.jdiameter.common.api.app.zh.IZhMessageFactory;
import org.jdiameter.common.impl.app.AppSessionImpl;

public abstract class ZhSession extends AppSessionImpl implements NetworkReqListener, StateMachine {

    protected transient IZhMessageFactory messageFactory;
    public ZhSession(ISessionFactory sf, IAppSessionData appSessionData) {
        super(sf, appSessionData);
    }
}
