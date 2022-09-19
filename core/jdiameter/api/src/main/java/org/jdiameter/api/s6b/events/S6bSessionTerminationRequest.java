package org.jdiameter.api.s6b.events;

import org.jdiameter.api.app.AppRequestEvent;

/**
 * The Session-Termination-Request (STR) command, indicated by the Command-Code field set to 275 and the "R" bit
 * set in the Command Flags field, is sent from a PDN GW to a 3GPP AAA server.
 *
 * @author <a href="mailto:giokast90@gmail.com"> Giovanni Castillo </a>
 */
public interface S6bSessionTerminationRequest extends AppRequestEvent {

  String _SHORT_NAME = "STR";
  String _LONG_NAME = "Session-Termination-Request";

  int code = 275;
}
