package org.jdiameter.client.impl.app.swm;

import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppEvent;
import org.jdiameter.api.app.AppRequestEvent;
import org.jdiameter.api.app.StateEvent;
import org.jdiameter.api.swm.events.SWmAbortSessionAnswer;
import org.jdiameter.api.swm.events.SWmAbortSessionRequest;
import org.jdiameter.api.swm.events.SWmDiameterAAAnswer;
import org.jdiameter.api.swm.events.SWmDiameterAARequest;
import org.jdiameter.api.swm.events.SWmDiameterEAPAnswer;
import org.jdiameter.api.swm.events.SWmDiameterEAPRequest;
import org.jdiameter.api.swm.events.SWmReAuthAnswer;
import org.jdiameter.api.swm.events.SWmReAuthRequest;
import org.jdiameter.api.swm.events.SWmSessionTermAnswer;
import org.jdiameter.api.swm.events.SWmSessionTermRequest;

public class Event implements StateEvent {

  public enum Type {
    SEND_DER, RECEIVE_DEA,
    SEND_AAR, RECEIVE_AAA,
    SEND_ASA, RECEIVE_ASR,
    SEND_RAA, RECEIVE_RAR,
    SEND_STR, RECEIVE_STA,
    SEND_EVENT_REQUEST, RECEIVE_EVENT_ANSWER
  }

  Type type;
  AppRequestEvent request;
  AppAnswerEvent answer;

  Event(Event.Type type, AppRequestEvent request, AppAnswerEvent answer) {
    this.type = type;
    this.answer = answer;
    this.request = request;
  }

  Event(boolean isRequest, AppRequestEvent request, AppAnswerEvent answer) {

    this.answer = answer;
    this.request = request;

    if (isRequest) {
      switch (request.getCommandCode()) {
        case SWmDiameterEAPRequest.code:
          type = Type.SEND_DER;
          break;

        case SWmAbortSessionRequest.code:
          type = Type.RECEIVE_ASR;
          break;

        case SWmDiameterAARequest.code:
          type = Type.SEND_AAR;
          break;

        case SWmReAuthRequest.code:
          type = Type.RECEIVE_RAR;
          break;

        case SWmSessionTermRequest.code:
          type = Event.Type.SEND_STR;
          break;

        case 5:
          type = Type.SEND_EVENT_REQUEST;
          break;
        default:
          throw new RuntimeException("Wrong command code value: " + request.getCommandCode());
      }
    } else {
      switch (answer.getCommandCode()) {
        case SWmDiameterEAPAnswer.code:
          type = Type.RECEIVE_DEA;
          break;

        case SWmAbortSessionAnswer.code:
          type = Type.SEND_ASA;
          break;

        case SWmDiameterAAAnswer.code:
          type = Type.RECEIVE_AAA;
          break;

        case SWmReAuthAnswer.code:
          type = Type.SEND_RAA;
          break;

        case SWmSessionTermAnswer.code:
          type = Type.RECEIVE_STA;
          break;

        case 6:
          type = Type.RECEIVE_EVENT_ANSWER;
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
  public void setData(Object o) {
  }

  @Override
  public Object getData() {
    return request != null ? request : answer;
  }

  public AppEvent getRequest() {
    return request;
  }

  public AppEvent getAnswer() {
    return answer;
  }

  @Override
  public int compareTo(Object o) {
    return 0;
  }
}
