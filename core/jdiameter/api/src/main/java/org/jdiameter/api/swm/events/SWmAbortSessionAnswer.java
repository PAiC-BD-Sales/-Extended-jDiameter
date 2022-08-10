package org.jdiameter.api.swm.events;

import org.jdiameter.api.app.AppAnswerEvent;


/**
 * The Abort-Session-Answer (ASA) command shall be indicated by the Command-Code field set to 274 and the "R" bit cleared
 * in the Command Flags field, and shall be sent from a ePDG to a 3GPP AAA Server/Proxy.
 *
 * @author <a href="mailto:enmanuelcalero61@gmail.com"> Enmanuel Calero </a>
 */
public interface SWmAbortSessionAnswer extends AppAnswerEvent {

  String _SHORT_NAME = "ASA";
  String _LONG_NAME = "Abort-Session-Answer";

  int code = 274;
}
