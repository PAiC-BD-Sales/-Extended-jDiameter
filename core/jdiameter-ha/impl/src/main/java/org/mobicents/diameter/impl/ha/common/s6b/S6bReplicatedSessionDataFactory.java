package org.mobicents.diameter.impl.ha.common.s6b;

import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.s6b.ClientS6bSession;
import org.jdiameter.api.s6b.ServerS6bSession;
import org.jdiameter.common.api.app.IAppSessionDataFactory;
import org.jdiameter.common.api.app.s6b.IS6bSessionData;
import org.jdiameter.common.api.data.ISessionDatasource;
import org.mobicents.diameter.impl.ha.client.s6b.ClientS6bSessionDataReplicatedImpl;
import org.mobicents.diameter.impl.ha.server.s6b.ServerS6bSessionDataReplicatedImpl;
import org.mobicents.diameter.impl.ha.data.ReplicatedSessionDatasource;
import org.restcomm.cluster.MobicentsCluster;

/**
 * @author <a href="mailto:giokast90@gmail.com"> Giovanni Castillo </a>
 * @author <a href="mailto:kennymendieta89@gmail.com"> Kenny Mendieta </a>
 */
public class S6bReplicatedSessionDataFactory implements IAppSessionDataFactory<IS6bSessionData> {

  private ReplicatedSessionDatasource replicatedSessionDataSource;
  private MobicentsCluster mobicentsCluster;

  public S6bReplicatedSessionDataFactory(ISessionDatasource replicatedSessionDataSource) { // Is this ok?
    super();
    this.replicatedSessionDataSource = (ReplicatedSessionDatasource) replicatedSessionDataSource;
    this.mobicentsCluster = this.replicatedSessionDataSource.getMobicentsCluster();
  }

  @Override
  public IS6bSessionData getAppSessionData(Class<? extends AppSession> clazz, String sessionId) {
    if (clazz.equals(ClientS6bSession.class)) {
      return new ClientS6bSessionDataReplicatedImpl(sessionId, this.mobicentsCluster, this.replicatedSessionDataSource.getContainer());
    } else if (clazz.equals(ServerS6bSession.class)) {
      ServerS6bSessionDataReplicatedImpl data = new ServerS6bSessionDataReplicatedImpl(
              sessionId,
              this.mobicentsCluster,
              this.replicatedSessionDataSource.getContainer());
      return data;
    }
    throw new IllegalArgumentException();
  }
}
