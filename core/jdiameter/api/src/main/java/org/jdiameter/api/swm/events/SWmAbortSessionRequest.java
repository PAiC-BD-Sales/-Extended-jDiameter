package org.jdiameter.api.swm.events;

import org.jdiameter.api.app.AppRequestEvent;

public interface SWmAbortSessionRequest extends AppRequestEvent {

    String _SHORT_NAME = "ASR";
    String _LONG_NAME = "Abort-Session-Request";

    int code = 274;
}
