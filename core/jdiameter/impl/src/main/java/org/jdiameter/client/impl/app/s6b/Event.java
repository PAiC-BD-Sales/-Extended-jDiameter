package org.jdiameter.client.impl.app.s6b;

import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.app.AppEvent;
import org.jdiameter.api.app.StateEvent;
import org.jdiameter.api.s6b.events.S6bAAAnswer;
import org.jdiameter.api.s6b.events.S6bAARequest;
import org.jdiameter.api.s6b.events.S6bAbortSessionAnswer;
import org.jdiameter.api.s6b.events.S6bAbortSessionRequest;
import org.jdiameter.api.s6b.events.S6bDiameterEAPAnswer;
import org.jdiameter.api.s6b.events.S6bDiameterEAPRequest;
import org.jdiameter.api.s6b.events.S6bReAuthAnswer;
import org.jdiameter.api.s6b.events.S6bReAuthRequest;
import org.jdiameter.api.s6b.events.S6bSessionTerminationAnswer;
import org.jdiameter.api.s6b.events.S6bSessionTerminationRequest;

/**
 * @author <a href="mailto:giokast90@gmail.com"> Giovanni Castillo </a>
 */
public class Event implements StateEvent {

  public enum Type {
    SEND_AAR, RECEIVE_AAA, RECEIVE_AAR,
    SEND_STR, RECEIVE_STA, RECEIVE_STR,
    SEND_RAA, RECEIVE_RAR,
    SEND_RAR, SEND_ASR, RECEIVE_RAA,
    SEND_ASA, RECEIVE_ASR, RECEIVE_ASA,
    SEND_DER, RECEIVE_DEA, RECEIVE_DER,
    SEND_EVENT_REQUEST, RECEIVE_EVENT_ANSWER;
  }

  Type type;
  AppEvent request;
  AppEvent answer;

  Event(Type type) {
    this.type = type;
  }

  Event(Type type, AppEvent request, AppEvent answer) {
    this.type = type;
    this.answer = answer;
    this.request = request;
  }

  Event(boolean isRequest, AppEvent request, AppEvent answer) throws AvpDataException {

    this.answer = answer;
    this.request = request;

    if (isRequest) {
      switch (request.getCommandCode()) {
        case S6bDiameterEAPRequest.code:
          type = Type.SEND_DER;
          break;
        case S6bAbortSessionRequest.code:
          type = Type.RECEIVE_ASR;
          break;
        case S6bAARequest.code:
          type = Type.SEND_AAR;
          break;
        case S6bReAuthRequest.code:
          type = Type.RECEIVE_RAR;
          break;
        case S6bSessionTerminationRequest.code:
          type = Type.SEND_STR;
          break;
        case 5:  //BUG FIX How do we know this is an event and not a session? Do we need to fix this? Does S6b do event?
          type = Type.SEND_EVENT_REQUEST;
          break;
        default:
          throw new RuntimeException("Wrong command code value: " + request.getCommandCode());
      }
    } else {
      switch (answer.getCommandCode()) {
        case S6bDiameterEAPAnswer.code:
          type = Type.RECEIVE_DEA;
          break;
        case S6bAbortSessionAnswer.code:
          type = Type.SEND_ASA;
          break;
        case S6bAAAnswer.code:
          type = Type.RECEIVE_AAA;
          break;
        case S6bReAuthAnswer.code:
          type = Type.SEND_RAA;
          break;
        case S6bSessionTerminationAnswer.code:
          type = Type.RECEIVE_STA;
          break;
        case 6: //BUG FIX How do we know this is an event and not a session? Do we need to fix this? Does S6b do event?
          type = Type.RECEIVE_EVENT_ANSWER;
          break;
        default:
          throw new RuntimeException("Wrong CC-Request-Type value: " + answer.getCommandCode());
      }
    }
  }

  @Override
  public Enum getType() {
    return type;
  }

  @Override
  public int compareTo(Object o) {
    return 0;
  }

  @Override
  public Object getData() {
    return request != null ? request : answer;
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

  public AppEvent getRequest() {
    return request;
  }

  public AppEvent getAnswer() {
    return answer;
  }

  @Override
  public <E> E encodeType(Class<E> eClass) {
    return eClass == Type.class ? (E) type : null;
  }
}
