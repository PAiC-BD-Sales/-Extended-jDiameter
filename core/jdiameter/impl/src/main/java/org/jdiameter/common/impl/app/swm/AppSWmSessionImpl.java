package org.jdiameter.common.impl.app.swm;

import org.jdiameter.api.NetworkReqListener;
import org.jdiameter.api.app.StateMachine;
import org.jdiameter.client.api.ISessionFactory;
import org.jdiameter.common.api.app.IAppSessionData;
import org.jdiameter.common.impl.app.AppSessionImpl;

public abstract class AppSWmSessionImpl extends AppSessionImpl implements NetworkReqListener, StateMachine  {

    public AppSWmSessionImpl(ISessionFactory sf, IAppSessionData appSessionData) {
        super(sf, appSessionData);
    }
}
