package org.jdiameter.common.api.app.zh;

import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.app.StateChangeListener;
import org.jdiameter.api.zh.ClientZhSessionListener;
import org.jdiameter.api.zh.ServerZhSessionListener;
import org.jdiameter.common.api.app.IAppSessionFactory;

/**
 * @author <a href="mailto:kennymendieta89@gmail.com"> Kenny Mendieta </a>
 *
 */
public interface IZhSessionFactory extends IAppSessionFactory {
    /**
     * Get stack wide listener for sessions. In local mode it has similar effect
     * as setting this directly in app session. However clustered session use this value when recreated!
     *
     * @return the serverSessionListener
     */
  ServerZhSessionListener getServerZhSessionListener();
    /**
     * Set stack wide listener for sessions. In local mode it has similar effect
     * as setting this directly in app session. However clustered session use this value when recreated!
     *
     * @param serverSessionListener the serverSessionListener to set
     */
  void setServerSessionListener(ServerZhSessionListener serverSessionListener);
    /**
     * Get stack wide listener for sessions. In local mode it has similar effect
     * as setting this directly in app session. However clustered session use this value when recreated!
     *
     * @return the clientSessionListener
     */
  ClientZhSessionListener getClientSessionListener();
    /**
     * Set stack wide listener for sessions. In local mode it has similar effect
     * as setting this directly in app session. However clustered session use this value when recreated!
     *
     * @param clientSessionListener the clientSessionListener to set
     */
  void setClientSessionListener(ClientZhSessionListener clientSessionListener);
    /**
     * @return the messageFactory
     */
  IZhMessageFactory getMessageFactory();
    /**
     * @param messageFactory the messageFactory to set
     */
  void setMessageFactory(IZhMessageFactory messageFactory);
    /**
     * @return the stateListener
     */
  StateChangeListener<AppSession> getStateListener();
    /**
     * @param stateListener the stateListener to set
     */
  void setStateListener(StateChangeListener<AppSession> stateListener);
}
