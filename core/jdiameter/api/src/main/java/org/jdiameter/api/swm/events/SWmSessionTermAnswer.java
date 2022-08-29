package org.jdiameter.api.swm.events;

import org.jdiameter.api.app.AppAnswerEvent;

/**
 * The Session-Termination-Answer (STA) command, indicated by the Command-Code field set to 275 and the "R" bit clear
 * in the Command Flags field, is sent from a 3GPP AAA Server/Proxy to a ePDG.
 *
 * @author <a href="mailto:enmanuelcalero61@gmail.com"> Enmanuel Calero </a>
 */

public interface SWmSessionTermAnswer extends AppAnswerEvent {

  String _SHORT_NAME = "STA";
  String _LONG_NAME = "Session-Termination-Answer";

  int code = 275;
}
