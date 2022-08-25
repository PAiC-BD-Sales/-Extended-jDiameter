package org.jdiameter.api.zh;

import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppRequestEvent;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.zh.events.MultimediaAuthAnswer;
import org.jdiameter.api.zh.events.MultimediaAuthRequest;

public interface ClientZhSessionListener {
  void doOtherEvent(AppSession session, AppRequestEvent request, AppAnswerEvent answer)
    throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

  void doMultimediaAuthAnswerEvent(ClientZhSession session, MultimediaAuthRequest request, MultimediaAuthAnswer answer)
    throws InternalException, IllegalDiameterStateException, RouteException, OverloadException, AvpDataException;
}
