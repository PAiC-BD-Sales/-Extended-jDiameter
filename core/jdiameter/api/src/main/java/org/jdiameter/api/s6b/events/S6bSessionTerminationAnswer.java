package org.jdiameter.api.s6b.events;

import org.jdiameter.api.app.AppAnswerEvent;

/**
 * The Session-Termination-Answer (STA) command, indicated by the Command-Code field set to 275 and the "R" bit
 * cleared in the Command Flags field, is sent from a 3GPP AAA server to a PDN GW.
 *
 * @author <a href="mailto:giokast90@gmail.com"> Giovanni Castillo </a>
 * */
public interface S6bSessionTerminationAnswer extends AppAnswerEvent {

    String _SHORT_NAME = "STA";
    String _LONG_NAME = "Session-Termination-Answer";

    int code = 275;
}
