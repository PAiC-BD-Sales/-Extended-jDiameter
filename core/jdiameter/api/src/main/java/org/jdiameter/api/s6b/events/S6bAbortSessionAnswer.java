package org.jdiameter.api.s6b.events;

import org.jdiameter.api.app.AppAnswerEvent;

/**
 * The Abort-Session-Answer (ASA) command, indicated by the Command-Code field set to 274 and the "R" bit cleared in the
 * Command Flags field, is sent from a PDN GW to a 3GPP AAA Server/Proxy.
 *
 * @author <a href="mailto:enmanuelcalero61@gmail.com"> Enmanuel Calero </a>
 */
public interface S6bAbortSessionAnswer extends AppAnswerEvent {

  String _SHORT_NAME = "ASA";
  String _LONG_NAME = "Abort-Session-Answer";

  int code = 274;
}
