package org.jdiameter.api.s6b.events;

import org.jdiameter.api.app.AppAnswerEvent;

public interface S6bAAnswer extends AppAnswerEvent {
  String _SHORT_NAME = "AAA";
  String _LONG_NAME = "AA-Answer"; // AA-Answer-S6b-PMIPv6

  int code = 265;
}
