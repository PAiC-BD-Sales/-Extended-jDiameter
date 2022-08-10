package org.jdiameter.client.impl.app.swm;

import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppEvent;
import org.jdiameter.api.app.AppRequestEvent;
import org.jdiameter.api.app.StateEvent;
import org.jdiameter.api.rx.events.RxAAAnswer;
import org.jdiameter.api.rx.events.RxAARequest;
import org.jdiameter.api.rx.events.RxAbortSessionAnswer;
import org.jdiameter.api.rx.events.RxAbortSessionRequest;
import org.jdiameter.api.rx.events.RxReAuthAnswer;
import org.jdiameter.api.rx.events.RxReAuthRequest;
import org.jdiameter.api.rx.events.RxSessionTermAnswer;
import org.jdiameter.api.rx.events.RxSessionTermRequest;
import org.jdiameter.api.swm.events.SWmAbortSessionAnswer;
import org.jdiameter.api.swm.events.SWmAbortSessionRequest;
import org.jdiameter.api.swm.events.SWmDiameterEAPAnswer;
import org.jdiameter.api.swm.events.SWmDiameterEAPRequest;

public class Event implements StateEvent {

  public enum Type {
    SEND_DER, RECEIVE_DEA,
    SEND_ASA, RECEIVE_ASR,
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

  Event(boolean isRequest, AppRequestEvent request, AppAnswerEvent answer) throws AvpDataException {

    this.answer = answer;
    this.request = request;

    if (isRequest) {
      switch (request.getCommandCode()) {
        case SWmDiameterEAPRequest.code:
          type = Type.SEND_DER;
          break;
        case SWmAbortSessionRequest.code:
          type = Event.Type.RECEIVE_ASR;
          break;
        //case RxReAuthRequest.code:
        //type = org.jdiameter.client.impl.app.rx.Event.Type.RECEIVE_RAR;
        //break;
        //case RxAbortSessionRequest.code:
        //type = org.jdiameter.client.impl.app.rx.Event.Type.RECEIVE_ASR;
        //break;
        case 5:  //BUG FIX How do we know this is an event and not a session? Do we need to fix this? Does Rx do event?
          type = Event.Type.SEND_EVENT_REQUEST;
          break;
        default:
          throw new RuntimeException("Wrong command code value: " + request.getCommandCode());
      }
    } else {
      switch (answer.getCommandCode()) {
        case SWmDiameterEAPAnswer.code:
          type = Event.Type.RECEIVE_DEA;
          break;
        case SWmAbortSessionAnswer.code:
          type = Event.Type.SEND_ASA;
          break;

                    /*
                    case RxReAuthAnswer.code:
                    type = org.jdiameter.client.impl.app.rx.Event.Type.SEND_RAA;
                    break;
                case RxAbortSessionAnswer.code:
                    type = org.jdiameter.client.impl.app.rx.Event.Type.SEND_ASA;

                     */
        case 6: //BUG FIX How do we know this is an event and not a session? Do we need to fix this? Does Rx do event?
          type = Event.Type.RECEIVE_EVENT_ANSWER;
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
