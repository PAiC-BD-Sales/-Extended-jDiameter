package org.jdiameter.api.swm.events;

import org.jdiameter.api.app.AppAnswerEvent;


/**
 * The Re-Auth-Answer (RAA) command shall be indicated by the Command-Code field set to 258 and the "R" bit cleared in
 * the Command Flags field, and shall be sent from a ePDG to a 3GPP AAA Server/Proxy
 *
 * @author <a href="mailto:enmanuelcalero61@gmail.com"> Enmanuel Calero </a>
 */
public interface SWmReAuthAnswer extends AppAnswerEvent {

  String _SHORT_NAME = "RAA";
  String _LONG_NAME = "Re-Auth-Answer";

  int code = 258;
}
