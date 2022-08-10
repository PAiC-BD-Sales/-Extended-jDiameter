package org.jdiameter.api.swm.events;

import org.jdiameter.api.app.AppAnswerEvent;

public interface SWmAbortSessionAnswer extends AppAnswerEvent {

  String _SHORT_NAME = "ASA";
  String _LONG_NAME = "Abort-Session-Answer";

  int code = 274;
}
