package org.jdiameter.server.impl.app.zh;

import org.jdiameter.api.InternalException;
import org.jdiameter.api.app.AppEvent;
import org.jdiameter.api.app.StateEvent;

/**
 * @author <a href="mailto:kennymendieta89@gmail.com"> Kenny Mendieta </a>
 *
 */
public class Event implements StateEvent {
  enum Type {
    SEND_MESSAGE, TIMEOUT_EXPIRES, RECEIVE_MAR;
  }
  AppEvent request;
  AppEvent answer;
  Type type;

  public Event(Type type, AppEvent request, AppEvent answer) {
    this.request = request;
    this.answer = answer;
    this.type = type;
  }

  @Override
  public <E> E encodeType(Class<E> enumType) {
    return enumType == Type.class ? (E) type : null;
  }

  @Override
  public Enum getType() {
    return type;
  }

  public AppEvent getRequest() {
    return request;
  }

  public AppEvent getAnswer() {
    return answer;
  }

  @Override
  public void setData(Object data) {
    try {
      if (((AppEvent) data).getMessage().isRequest()) {
        request = (AppEvent) data;
      } else {
        answer = (AppEvent) data;
      }
    } catch (InternalException e) {
      throw new IllegalArgumentException(e);
    }
  }

  @Override
  public Object getData() {
    return request != null ? request : answer;
  }

  @Override
  public int compareTo(Object o) {
    return 0;
  }
}
