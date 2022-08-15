package org.jdiameter.common.impl.app.zh;

import org.jdiameter.api.NetworkReqListener;
import org.jdiameter.api.app.StateChangeListener;
import org.jdiameter.api.app.StateMachine;
import org.jdiameter.client.api.ISessionFactory;
import org.jdiameter.common.api.app.zh.IZhMessageFactory;
import org.jdiameter.common.api.app.zh.IZhSessionData;
import org.jdiameter.common.impl.app.AppSessionImpl;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author <a href="mailto:kennymendieta89@gmail.com"> Kenny Mendieta </a>
 *
 */
public abstract class ZhSession extends AppSessionImpl implements NetworkReqListener, StateMachine {
  public static final int _TX_TIMEOUT = 30 * 1000;
  protected transient IZhMessageFactory messageFactory;
  protected static final String TIMER_NAME_MSG_TIMEOUT = "MSG_TIMEOUT";
  protected IZhSessionData sessionData;
  protected transient List<StateChangeListener> stateListeners = new CopyOnWriteArrayList<StateChangeListener>();
  protected Lock sendAndStateLock = new ReentrantLock();
  public ZhSession(ISessionFactory sf, IZhSessionData sessionData) {
    super(sf, sessionData);
    this.sessionData = sessionData;
  }
  public void addStateChangeNotification(StateChangeListener listener) {
    if (!stateListeners.contains(listener)) {
      stateListeners.add(listener);
    }
  }

    @SuppressWarnings("rawtypes")
  public void removeStateChangeNotification(StateChangeListener listener) {
        stateListeners.remove(listener);
    }

  public boolean isStateless() {
        return true;
    }

  @Override
  public boolean isReplicable() {
        return false;
    }

  protected void startMsgTimer() {
    try {
      sendAndStateLock.lock();
      sessionData.setTsTimerId(super.timerFacility.schedule(getSessionId(), TIMER_NAME_MSG_TIMEOUT, _TX_TIMEOUT));
    } finally {
      sendAndStateLock.unlock();
    }
  }

  protected void cancelMsgTimer() {
    try {
      sendAndStateLock.lock();
      final Serializable timerId = this.sessionData.getTsTimerId();
      if (timerId == null) {
        return;
      }
      super.timerFacility.cancel(timerId);
      this.sessionData.setTsTimerId(null);
    } finally {
      sendAndStateLock.unlock();
    }
  }
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((sessionData == null) ? 0 : sessionData.hashCode());
    return result;
  }
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!super.equals(obj)) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    ZhSession other = (ZhSession) obj;
    if (sessionData == null) {
      if (other.sessionData != null) {
        return false;
      }
    } else if (!sessionData.equals(other.sessionData)) {
        return false;
    }
    return true;
  }
}
