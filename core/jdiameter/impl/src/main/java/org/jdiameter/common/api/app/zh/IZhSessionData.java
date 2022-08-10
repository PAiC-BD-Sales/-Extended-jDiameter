package org.jdiameter.common.api.app.zh;

import org.jdiameter.api.Request;
import org.jdiameter.common.api.app.IAppSessionData;

import java.io.Serializable;

public interface IZhSessionData extends IAppSessionData {

    void setZhSessionState(ZhSessionState state);

    ZhSessionState getZhSessionState();

    Serializable getTsTimerId();

    void setTsTimerId(Serializable tid);

    void setBuffer(Request buffer);

    Request getBuffer();
}
