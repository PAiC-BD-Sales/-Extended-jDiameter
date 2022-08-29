package org.mobicents.diameter.impl.ha.common.swm;

import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.swm.ClientSWmSession;
import org.jdiameter.api.swm.ServerSWmSession;
import org.jdiameter.common.api.app.IAppSessionDataFactory;
import org.jdiameter.common.api.app.swm.ISWmSessionData;
import org.jdiameter.common.api.data.ISessionDatasource;
import org.mobicents.diameter.impl.ha.client.swm.ClientSWmSessionDataReplicatedImpl;
import org.mobicents.diameter.impl.ha.data.ReplicatedSessionDatasource;
import org.mobicents.diameter.impl.ha.server.swm.ServerSWmSessionDataReplicatedImpl;
import org.restcomm.cluster.MobicentsCluster;

/**
 * @author <a href="mailto:enmanuelcalero61@gmail.com"> Enmanuel Calero </a>
 */
public class SWmReplicatedSessionDataFactory implements IAppSessionDataFactory<ISWmSessionData>  {

  private ReplicatedSessionDatasource replicatedSessionDataSource;
  private MobicentsCluster mobicentsCluster;

  public SWmReplicatedSessionDataFactory(ISessionDatasource replicatedSessionDataSource) { // Is this ok?
    super();
    this.replicatedSessionDataSource = (ReplicatedSessionDatasource) replicatedSessionDataSource;
    this.mobicentsCluster = this.replicatedSessionDataSource.getMobicentsCluster();
  }
  @Override
  public ISWmSessionData getAppSessionData(Class<? extends AppSession> clazz, String sessionId) {
    if (clazz.equals(ClientSWmSession.class)) {
      return new ClientSWmSessionDataReplicatedImpl(sessionId, this.mobicentsCluster, this.replicatedSessionDataSource.getContainer());
    }
    else if (clazz.equals(ServerSWmSession.class)) {
      ServerSWmSessionDataReplicatedImpl data = new ServerSWmSessionDataReplicatedImpl(sessionId, this.mobicentsCluster);
      return data;
    }
    throw new IllegalArgumentException();
  }
}
