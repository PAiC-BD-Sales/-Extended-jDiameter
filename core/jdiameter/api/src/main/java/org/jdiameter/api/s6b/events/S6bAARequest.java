package org.jdiameter.api.s6b.events;

import org.jdiameter.api.app.AppAnswerEvent;

public interface S6bAARequest extends AppAnswerEvent {
  String _SHORT_NAME = "AAR";
  String _LONG_NAME = "AA-Request"; // AA-Request-S6b-PMIPv6

  int code = 265;
}
