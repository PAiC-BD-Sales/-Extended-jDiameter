package org.jdiameter.client.impl.app.swm;

import org.jdiameter.api.app.AppEvent;
import org.jdiameter.api.app.StateEvent;

public class Event implements StateEvent {

    public enum Type {
        SEND_DER,           RECEIVE_DEA,
        SEND_EVENT_REQUEST, RECEIVE_EVENT_ANSWER
    }

    Type type;
    AppEvent data;

    Event(Type type, AppEvent data) {
        this.type = type;
        this.data = data;
    }

    @Override
    public <E> E encodeType(Class<E> eClass) {
        return eClass == Type.class ? (E) type : null;
    }

    @Override
    public Enum getType() {
        return type;
    }

    @Override
    public void setData(Object o) {
        data = (AppEvent) o;
    }

    @Override
    public Object getData() {
        return data;
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }
}
