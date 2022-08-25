package org.jdiameter.api.zh.events;

import org.jdiameter.api.app.AppAnswerEvent;

public interface MultimediaAuthAnswer extends AppAnswerEvent {
  String _SHORT_NAME = "MAA";
  String _LONG_NAME = "Multimedia-Auth-Answer";

  int code = 303;
}
