package org.jdiameter.api.swm.events;

import org.jdiameter.api.app.AppRequestEvent;


/**
 * The Abort-Session-Request (ASR) command shall be indicated by the Command-Code field set to 274 and the "R" bit set
 * in the Command Flags field, and shall be sent from a 3GPP AAA Server/Proxy to an ePDG.
 *
 * @author <a href="mailto:enmanuelcalero61@gmail.com"> Enmanuel Calero </a>
 */
public interface SWmAbortSessionRequest extends AppRequestEvent {

  String _SHORT_NAME = "ASR";
  String _LONG_NAME = "Abort-Session-Request";

  int code = 274;
}
