package org.jdiameter.api.zh.events;

import org.jdiameter.api.app.AppRequestEvent;

public interface MultimediaAuthRequest extends AppRequestEvent {
  String _SHORT_NAME = "MAR";
  String _LONG_NAME = "Multimedia-Auth-Request";

  int code = 303;
}
