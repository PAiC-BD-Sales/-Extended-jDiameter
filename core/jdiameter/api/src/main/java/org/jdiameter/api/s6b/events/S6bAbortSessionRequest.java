package org.jdiameter.api.s6b.events;

import org.jdiameter.api.app.AppAnswerEvent;


/**
 * The Abort-Session-Request (ASR) command, indicated by the Command-Code field set to 274 and the "R" bit set in
 * the Command Flags field, is sent from a 3GPP AAA Server/Proxy to a PDN GW.
 *
 * @author <a href="mailto:enmanuelcalero61@gmail.com"> Enmanuel Calero </a>
 */
public interface S6bAbortSessionRequest extends AppAnswerEvent {

  String _SHORT_NAME = "ASR";
  String _LONG_NAME = "Abort-Session-Request";

  int code = 274;
}
