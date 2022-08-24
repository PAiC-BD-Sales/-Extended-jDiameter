package org.mobicents.diameter.impl.ha.client.s6b;

import org.jdiameter.api.s6b.ServerS6bSession;
import org.jdiameter.common.api.app.s6b.ServerS6bSessionState;
import org.jdiameter.server.impl.app.s6b.IServerS6bSessionData;
import org.mobicents.diameter.impl.ha.common.AppSessionDataReplicatedImpl;
import org.mobicents.diameter.impl.ha.data.ReplicatedSessionDatasource;
import org.restcomm.cache.FqnWrapper;
import org.restcomm.cluster.MobicentsCluster;

/**
 * @author <a href="mailto:giokast90@gmail.com"> Giovanni Castillo </a>
 */
public class ServerS6bSessionDataReplicatedImpl extends AppSessionDataReplicatedImpl implements IServerS6bSessionData {

  private static final String STATELESS = "STATELESS";
  private static final String STATE = "STATE";

  public ServerS6bSessionDataReplicatedImpl(FqnWrapper nodeFqnWrapper, MobicentsCluster mobicentsCluster) {
    super(nodeFqnWrapper, mobicentsCluster);

    if (super.create()) {
      setAppSessionIface(this, ServerS6bSession.class);
      setServerS6bSessionState(ServerS6bSessionState.IDLE);
    }
  }

  public ServerS6bSessionDataReplicatedImpl(String sessionId, MobicentsCluster mobicentsCluster) {
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
  public ServerS6bSessionState getServerS6bSessionState() {
    if (exists()) {
      return (ServerS6bSessionState) getNodeValue(STATE);
    } else {
      throw new IllegalStateException();
    }
  }

  @Override
  public void setServerS6bSessionState(ServerS6bSessionState state) {
    if (exists()) {
      putNodeValue(STATE, state);
    } else {
      throw new IllegalStateException();
    }
  }
}
