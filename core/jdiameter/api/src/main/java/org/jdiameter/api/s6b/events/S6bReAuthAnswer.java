package org.jdiameter.api.s6b.events;

import org.jdiameter.api.app.AppAnswerEvent;

public interface S6bReAuthAnswer extends AppAnswerEvent {
  String _SHORT_NAME = "ASA";
  String _LONG_NAME = "Re-Auth-Answer"; // Re-Auth-Answer-S6b

  int code = 258;
}
