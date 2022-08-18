package org.jdiameter.api.swm.events;

import org.jdiameter.api.app.AppRequestEvent;

/**
 * The Re-Auth-Request (RAR) command shall be indicated by the Command-Code field set to 258 and
 * the "R" bit set in the Command Flags field, and shall be sent from a 3GPP AAA Server/Proxy to a ePDG.
 *
 * @author <a href="mailto:enmanuelcalero61@gmail.com"> Enmanuel Calero </a>
 */
public interface SWmReAuthRequest extends AppRequestEvent {

  String _SHORT_NAME = "RAR";
  String _LONG_NAME = "Re-Auth-Request";

  int code = 258;
}
