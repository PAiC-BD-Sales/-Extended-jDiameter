package org.jdiameter.api.swm.events;

import org.jdiameter.api.app.AppAnswerEvent;


/**
 * The Diameter-EAP-Answer (DER) command, indicated by the Command-Code field set to 268 and the "R" bit cleared
 * in the Command Flags field, is sent from a 3GPP AAA Server/Proxy to the ePDG.
 *
 * @author <a href="mailto:enmanuelcalero61@gmail.com"> Enmanuel Calero </a>
 */
public interface SWmDiameterEAPAnswer extends AppAnswerEvent {

  String _SHORT_NAME = "DEA";
  String _LONG_NAME = "Diameter-EAP-Answer";

  int code = 268;
}
