package org.jdiameter.server.impl.app.s6b;

import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppEvent;
import org.jdiameter.api.app.AppRequestEvent;
import org.jdiameter.api.app.StateEvent;
import org.jdiameter.api.s6b.events.S6bDiameterEAPAnswer;
import org.jdiameter.api.s6b.events.S6bDiameterEAPRequest;
import org.jdiameter.api.s6b.events.S6bAbortSessionAnswer;
import org.jdiameter.api.s6b.events.S6bAbortSessionRequest;
import org.jdiameter.api.s6b.events.S6bReAuthAnswer;
import org.jdiameter.api.s6b.events.S6bReAuthRequest;
import org.jdiameter.api.s6b.events.S6bSessionTerminationAnswer;
import org.jdiameter.api.s6b.events.S6bSessionTerminationRequest;

/**
 * @author <a href="mailto:giokast90@gmail.com"> Giovanni Castillo </a>
 */
public class Event implements StateEvent {

  public enum Type {
    SEND_DEA, RECEIVE_DER,
    SEND_AAA, RECEIVE_AAR,
    SEND_STA, RECEIVE_STR,
    SEND_RAR, RECEIVE_RAA,
    SEND_ASR, RECEIVE_ASA,
    SEND_EVENT_ANSWER, RECEIVE_EVENT_REQUEST;
  }

  Type type;
  AppRequestEvent request;
  AppAnswerEvent answer;

  Event(Type type) {
    this.type = type;
  }

  Event(Type type, AppRequestEvent request, AppAnswerEvent answer) {
    this.type = type;
    this.answer = answer;
    this.request = request;
  }

  Event(boolean isRequest, AppRequestEvent request, AppAnswerEvent answer) {

    this.answer = answer;
    this.request = request;

    if (isRequest) {
      switch (request.getCommandCode()) {
        case S6bDiameterEAPRequest.code:
          type = Type.RECEIVE_DER;
          break;
        case S6bSessionTerminationRequest.code:
          type = Type.RECEIVE_STR;
          break;
        case S6bAbortSessionRequest.code:
          type = Type.SEND_ASR;
          break;
        case S6bReAuthRequest.code:
          type = Type.SEND_RAR;
          break;
        case 5: //BUG FIX How do we know this is an event and not a session? Do we need to fix this? Does S6b do event?
          type = Type.RECEIVE_EVENT_REQUEST;
          break;
        default:
          throw new RuntimeException("Wrong command code value: " + request.getCommandCode());
      }
    } else {
      switch (answer.getCommandCode()) {
        case S6bDiameterEAPAnswer.code:
          type = Type.SEND_DEA;
          break;
        case S6bSessionTerminationAnswer.code:
          type = Type.SEND_STA;
          break;
        case S6bAbortSessionAnswer.code:
          type = Type.RECEIVE_ASA;
          break;
        case S6bReAuthAnswer.code:
          type = Type.RECEIVE_RAA;
          break;
        case 6:  //BUG FIX How do we know this is an event and not a session? Do we need to fix this? Does S6b do event?
          type = Type.SEND_EVENT_ANSWER;
          break;
        default:
          throw new RuntimeException("Wrong CC-Request-Type value: " + answer.getCommandCode());
      }
    }
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
  public int compareTo(Object o) {
    return 0;
  }

  @Override
  public Object getData() {
    return this.request != null ? this.request : this.answer;
  }

  @Override
  public void setData(Object data) {
    // data = (AppEvent) o;
    // FIXME: What should we do here?! Is it request or answer?
  }

  public AppEvent getRequest() {
    return request;
  }

  public AppEvent getAnswer() {
    return answer;
  }
}
