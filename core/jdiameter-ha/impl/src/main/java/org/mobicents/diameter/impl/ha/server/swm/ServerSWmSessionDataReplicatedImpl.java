package org.mobicents.diameter.impl.ha.server.swm;


import org.jdiameter.api.swm.ServerSWmSession;
import org.jdiameter.common.api.app.swm.ServerSWmSessionState;
import org.jdiameter.server.impl.app.swm.IServerSWmSessionData;
import org.mobicents.diameter.impl.ha.common.AppSessionDataReplicatedImpl;
import org.mobicents.diameter.impl.ha.data.ReplicatedSessionDatasource;
import org.restcomm.cache.FqnWrapper;
import org.restcomm.cluster.MobicentsCluster;

/**
 * @author <a href="mailto:enmanuelcalero61@gmail.com"> Enmanuel Calero </a>
 */
public class ServerSWmSessionDataReplicatedImpl extends AppSessionDataReplicatedImpl implements IServerSWmSessionData {


  private static final String STATELESS = "STATELESS";
  private static final String STATE = "STATE";


  public ServerSWmSessionDataReplicatedImpl(FqnWrapper nodeFqnWrapper, MobicentsCluster mobicentsCluster) {
    super(nodeFqnWrapper, mobicentsCluster);

    if (super.create()) {
      setAppSessionIface(this, ServerSWmSession.class);
      setServerSWmSessionState(ServerSWmSessionState.IDLE);
    }
  }

  public ServerSWmSessionDataReplicatedImpl(String sessionId, MobicentsCluster mobicentsCluster) {
    this(
            FqnWrapper.fromRelativeElementsWrapper(ReplicatedSessionDatasource.SESSIONS_FQN, sessionId),
            mobicentsCluster
    );
  }

  @Override
  public boolean isStateless() {
    if (exists()) {
      return toPrimitive((Boolean) getNodeValue(STATELESS), true);
    } else {
      throw new IllegalStateException();
    }
  }

  @Override
  public void setStateless(boolean stateless) {
    if (exists()) {
      putNodeValue(STATELESS, stateless);
    } else {
      throw new IllegalStateException();
    }
  }

  @Override
  public ServerSWmSessionState getServerSWmSessionState() {
    if (exists()) {
      return (ServerSWmSessionState) getNodeValue(STATE);
    } else {
      throw new IllegalStateException();
    }
  }

  @Override
  public void setServerSWmSessionState(ServerSWmSessionState state) {
    if (exists()) {
      putNodeValue(STATE, state);
    } else {
      throw new IllegalStateException();
    }
  }
}
