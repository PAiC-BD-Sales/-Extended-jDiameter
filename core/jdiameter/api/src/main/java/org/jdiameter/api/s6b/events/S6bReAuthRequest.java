package org.jdiameter.api.s6b.events;

import org.jdiameter.api.app.AppAnswerEvent;

public interface S6bReAuthRequest extends AppAnswerEvent {
  String _SHORT_NAME = "RAR";
  String _LONG_NAME = "Re-Auth-Request"; // Re-Auth-Request-S6b

  int code = 258;
}
