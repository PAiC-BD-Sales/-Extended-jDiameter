package org.jdiameter.common.api.app.s6b;

import org.jdiameter.api.s6b.ServerS6bSession;

import java.util.concurrent.ScheduledFuture;

/**
 * Diameter S6b Reference Point Server Additional listener
 * Actions for FSM
 *
 * @author <a href="mailto:giokast90@gmail.com"> Giovanni Castillo </a>
 */
public interface IServerS6bSessionContext {

  void sessionSupervisionTimerExpired(ServerS6bSession session);

  /**
   * This is called always when Tcc starts
   *
   * @param session
   * @param future
   */
  void sessionSupervisionTimerStarted(ServerS6bSession session, ScheduledFuture future);

  void sessionSupervisionTimerReStarted(ServerS6bSession session, ScheduledFuture future);

  void sessionSupervisionTimerStopped(ServerS6bSession session, ScheduledFuture future);

}
