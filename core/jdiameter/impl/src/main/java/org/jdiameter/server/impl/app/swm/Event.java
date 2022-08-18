package org.jdiameter.server.impl.app.swm;

import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppEvent;
import org.jdiameter.api.app.AppRequestEvent;
import org.jdiameter.api.app.StateEvent;
import org.jdiameter.api.swm.events.SWmAbortSessionAnswer;
import org.jdiameter.api.swm.events.SWmAbortSessionRequest;
import org.jdiameter.api.swm.events.SWmDiameterEAPAnswer;
import org.jdiameter.api.swm.events.SWmDiameterEAPRequest;
import org.jdiameter.api.swm.events.SWmReAuthAnswer;
import org.jdiameter.api.swm.events.SWmReAuthRequest;

/**
 * @author <a href="mailto:enmanuelcalero61@gmail.com"> Enmanuel Calero </a>
 */
public class Event implements StateEvent {

  public enum Type {
    SEND_DEA, RECEIVE_DER,
    SEND_AAA, RECEIVE_AAR,
    SEND_STA, RECEIVE_STR,
    SEND_RAR, RECEIVE_RAA,
    SEND_ASR, RECEIVE_ASA,
    SEND_EVENT_ANSWER, RECEIVE_EVENT_REQUEST
  }

  Event.Type type;
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
          type = Type.RECEIVE_DER;
          break;
        case SWmAbortSessionRequest.code:
          type = Type.SEND_ASR;
          break;
        case SWmReAuthRequest.code:
          type = Type.SEND_RAR;
          break;
        //case RxAbortSessionRequest.code:
        //type = org.jdiameter.client.impl.app.rx.Event.Type.RECEIVE_ASR;
        //break;
        case 5:  //BUG FIX How do we know this is an event and not a session? Do we need to fix this? Does Rx do event?
          type = Type.RECEIVE_EVENT_REQUEST;
          break;
        default:
          throw new RuntimeException("Wrong command code value: " + request.getCommandCode());
      }
    } else {
      switch (answer.getCommandCode()) {
        case SWmDiameterEAPAnswer.code:
          type = Type.SEND_DEA;
          break;
        case SWmAbortSessionAnswer.code:
          type = Type.RECEIVE_ASA;
          break;
        case SWmReAuthAnswer.code:
          type = Type.RECEIVE_RAA;
          break;

                    /*
                case RxAbortSessionAnswer.code:
                    type = org.jdiameter.client.impl.app.rx.Event.Type.SEND_ASA;

                     */
        case 6: //BUG FIX How do we know this is an event and not a session? Do we need to fix this? Does Rx do event?
          type = Type.SEND_EVENT_ANSWER;
          break;
        default:
          throw new RuntimeException("Wrong CC-Request-Type value: " + answer.getCommandCode());
      }
    }
  }

  @Override
  public <E> E encodeType(Class<E> eClass) {
    return eClass == Event.Type.class ? (E) type : null;
  }

  @Override
  public Enum getType() {
    return type;
  }

  @Override
  public Object getData() {
    return this.request != null ? this.request : this.answer;
  }

  @Override
  public void setData(Object data) {
  }

  @Override
  public int compareTo(Object o) {
    return 0;
  }

  public AppEvent getRequest() {
    return request;
  }

  public AppEvent getAnswer() {
    return answer;
  }

}
