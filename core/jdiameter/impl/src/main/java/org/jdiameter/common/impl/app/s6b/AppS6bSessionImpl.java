package org.jdiameter.common.impl.app.s6b;

import org.jdiameter.api.NetworkReqListener;
import org.jdiameter.api.app.StateChangeListener;
import org.jdiameter.api.app.StateMachine;
import org.jdiameter.client.api.ISessionFactory;
import org.jdiameter.common.api.app.s6b.IS6bSessionData;
import org.jdiameter.common.impl.app.AppSessionImpl;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author <a href="mailto:giokast90@gmail.com"> Giovanni Castillo </a>
 */
public abstract class AppS6bSessionImpl extends AppSessionImpl implements NetworkReqListener, StateMachine {

  protected Lock sendAndStateLock = new ReentrantLock();
  //FIXME: those must be recreated from local resources!
  //FIXME: change this to single ref!
  protected transient List<StateChangeListener> stateListeners = new CopyOnWriteArrayList<>();

  public AppS6bSessionImpl(ISessionFactory sf, IS6bSessionData sessionData) {
    super(sf, sessionData);
  }

  @Override
  public void addStateChangeNotification(StateChangeListener listener) {
    if (!stateListeners.contains(listener)) {
      stateListeners.add(listener);
    }
  }

  @Override
  public void removeStateChangeNotification(StateChangeListener listener) {
    stateListeners.remove(listener);
  }

  @Override
  public void release() {
    super.release();
  }
}
