package org.jdiameter.common.api.app.swm;

import org.jdiameter.api.swm.ServerSWmSession;

import java.util.concurrent.ScheduledFuture;


/**
 * Diameter 3GPP IMS SWm Reference Point Server Additional listener
 * Actions for FSM
 *
 * @author <a href="mailto:enmanuelcalero61@gmail.com"> Enmanuel Calero </a>
 */
public interface IServerSWmSessionContext {

    void sessionSupervisionTimerExpired(ServerSWmSession session);

    void sessionSupervisionTimerStarted(ServerSWmSession session, ScheduledFuture future);

    void sessionSupervisionTimerReStarted(ServerSWmSession session, ScheduledFuture future);

    void sessionSupervisionTimerStopped(ServerSWmSession session, ScheduledFuture future);
}
