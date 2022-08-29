package org.jdiameter.api.swm.events;

import org.jdiameter.api.app.AppRequestEvent;

/**
 * The Session-Termination-Request (STR) command, indicated by the Command-Code field set to 275 and the "R" bit set in
 * the Command Flags field, is sent from a ePDG to a 3GPP AAA Server/Proxy.
 *
 * @author <a href="mailto:enmanuelcalero61@gmail.com"> Enmanuel Calero </a>
 */
public interface SWmSessionTermRequest extends AppRequestEvent {

  String _SHORT_NAME = "STR";
  String _LONG_NAME = "Session-Termination-Request";

  int code = 275;
}
