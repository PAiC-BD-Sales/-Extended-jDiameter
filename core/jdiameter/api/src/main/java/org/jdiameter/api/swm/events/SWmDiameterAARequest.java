package org.jdiameter.api.swm.events;

import org.jdiameter.api.app.AppRequestEvent;


/**
 * The AA-Request (AAR) command, indicated by the Command-Code field set to 265 and the "R" bit set in the Command
 * Flags field, is sent from a ePDG to a 3GPP AAA Server/Proxy.
 *
 * @author <a href="mailto:enmanuelcalero61@gmail.com"> Enmanuel Calero </a>
 */
public interface SWmDiameterAARequest extends AppRequestEvent {
  String _SHORT_NAME = "AAR";
  String _LONG_NAME = "Diameter-AA-Request";

  int code = 265;
}
