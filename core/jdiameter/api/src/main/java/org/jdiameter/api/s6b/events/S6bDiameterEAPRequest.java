package org.jdiameter.api.s6b.events;

import org.jdiameter.api.app.AppRequestEvent;

/**
 * The Diameter-EAP-Request (DER) command, indicated by the Command-Code field set to 268 and the "R" bit
 * set in the Command Flags field, is sent from a ePDG to a 3GPP AAA Server/Proxy.
 *
 * @author <a href="mailto:giokast90@gmail.com"> Giovanni Castillo </a>
 */
public interface S6bDiameterEAPRequest extends AppRequestEvent {

  String _SHORT_NAME = "DER";
  String _LONG_NAME = "Diameter-EAP-Request";

  int code = 268;
}
