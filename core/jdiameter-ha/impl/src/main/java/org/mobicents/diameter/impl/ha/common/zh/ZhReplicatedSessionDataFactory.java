package org.mobicents.diameter.impl.ha.common.zh;

import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.zh.ClientZhSession;
import org.jdiameter.api.zh.ServerZhSession;
import org.jdiameter.common.api.app.IAppSessionDataFactory;
import org.jdiameter.common.api.app.zh.IZhSessionData;
import org.jdiameter.common.api.data.ISessionDatasource;
import org.mobicents.diameter.impl.ha.client.zh.ClientZhSessionDataReplicatedImpl;
import org.mobicents.diameter.impl.ha.data.ReplicatedSessionDatasource;
import org.mobicents.diameter.impl.ha.server.zh.ServerZhSessionDataReplicatedImpl;
import org.restcomm.cluster.MobicentsCluster;

/**
 *
 * @author <a href="mailto:kennymendieta89@gmail.com"> Kenny Mendieta </a>
 *
 */
public class ZhReplicatedSessionDataFactory implements IAppSessionDataFactory<IZhSessionData> {
  private ReplicatedSessionDatasource replicatedSessionDataSource;
  private MobicentsCluster mobicentsCluster;

  /**
   * @param replicatedSessionDataSource
   */
  public ZhReplicatedSessionDataFactory(ISessionDatasource replicatedSessionDataSource) {
    this.replicatedSessionDataSource = (ReplicatedSessionDatasource) replicatedSessionDataSource;
    this.mobicentsCluster = this.replicatedSessionDataSource.getMobicentsCluster();
  }
  @Override
  public IZhSessionData getAppSessionData(Class<? extends AppSession> clazz, String sessionId) {
    if (clazz.equals(ClientZhSession.class)) {
      ClientZhSessionDataReplicatedImpl data = new ClientZhSessionDataReplicatedImpl(sessionId, this.mobicentsCluster,
              this.replicatedSessionDataSource.getContainer());
      return data;
    } else if (clazz.equals(ServerZhSession.class)) {
      ServerZhSessionDataReplicatedImpl data = new ServerZhSessionDataReplicatedImpl(sessionId, this.mobicentsCluster,
              this.replicatedSessionDataSource.getContainer());
      return data;
    }
    throw new IllegalArgumentException();
  }
}
