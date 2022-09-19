package org.mobicents.diameter.impl.ha.client.zh;

import org.jdiameter.api.zh.ClientZhSession;
import org.jdiameter.client.api.IContainer;
import org.jdiameter.client.impl.app.zh.IClientZhSessionData;
import org.jdiameter.common.api.app.zh.ZhSessionState;
import org.mobicents.diameter.impl.ha.common.zh.ZhSessionDataReplicatedImpl;
import org.mobicents.diameter.impl.ha.data.ReplicatedSessionDatasource;
import org.restcomm.cache.FqnWrapper;
import org.restcomm.cluster.MobicentsCluster;

/**
 *
 * @author <a href="mailto:kennymendieta89@gmail.com"> Kenny Mendieta </a>
 *
 */
public class ClientZhSessionDataReplicatedImpl extends ZhSessionDataReplicatedImpl implements IClientZhSessionData {
  /**
   * @param nodeFqnWrapper
   * @param mobicentsCluster
   * @param container
   */
  public ClientZhSessionDataReplicatedImpl(FqnWrapper nodeFqnWrapper, MobicentsCluster mobicentsCluster, IContainer container) {
    super(nodeFqnWrapper, mobicentsCluster, container);
    if (super.create()) {
      setAppSessionIface(this, ClientZhSession.class);
      setZhSessionState(ZhSessionState.NO_STATE_MAINTAINED);
    }
  }

  /**
   * @param sessionId
   * @param mobicentsCluster
   * @param container
   */
  public ClientZhSessionDataReplicatedImpl(String sessionId, MobicentsCluster mobicentsCluster, IContainer container) {
    this(
            FqnWrapper.fromRelativeElementsWrapper(ReplicatedSessionDatasource.SESSIONS_FQN, sessionId), mobicentsCluster, container);
  }
}
