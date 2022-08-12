package org.jdiameter.api.swm.events;

import org.jdiameter.api.app.AppAnswerEvent;


/**
 * The AA-Answer (AAA) command, indicated by the Command-Code field set to 265 and the "R" bit cleared in the Command
 * Flags field, is sent from 3GPP AAA Server/Proxy to a ePDG.
 *
 * @author <a href="mailto:enmanuelcalero61@gmail.com"> Enmanuel Calero </a>
 */
public interface SWmDiameterAAAnswer extends AppAnswerEvent {
  String _SHORT_NAME = "AAA";
  String _LONG_NAME = "Diameter-AA-Answer";

  int code = 265;
}
