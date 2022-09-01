package org.jdiameter.api.s6b.events;

import org.jdiameter.api.app.AppRequestEvent;

public interface S6bReAuthRequest extends AppRequestEvent {
  String _SHORT_NAME = "RAR";
  String _LONG_NAME = "Re-Auth-Request"; // Re-Auth-Request-S6b

  int code = 258;
}
