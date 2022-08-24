package org.mobicents.diameter.impl.ha.client.s6b;

import org.jdiameter.api.s6b.ClientS6bSession;
import org.jdiameter.client.api.IContainer;
import org.jdiameter.common.api.app.s6b.ClientS6bSessionState;
import org.jdiameter.client.impl.app.s6b.IClientS6bSessionData;
import org.mobicents.diameter.impl.ha.common.AppSessionDataReplicatedImpl;
import org.mobicents.diameter.impl.ha.data.ReplicatedSessionDatasource;
import org.restcomm.cache.FqnWrapper;
import org.restcomm.cluster.MobicentsCluster;

/**
 * @author <a href="mailto:giokast90@gmail.com"> Giovanni Castillo </a>
 */
public class ClientS6bSessionDataReplicatedImpl extends AppSessionDataReplicatedImpl implements IClientS6bSessionData {

  private static final String EVENT_BASED = "EVENT_BASED";
  private static final String REQUEST_TYPE = "REQUEST_TYPE";
  private static final String STATE = "STATE";

  public ClientS6bSessionDataReplicatedImpl(FqnWrapper nodeFqnWrapper, MobicentsCluster mobicentsCluster, IContainer container) {
    super(nodeFqnWrapper, mobicentsCluster);

    if (super.create()) {
      setAppSessionIface(this, ClientS6bSession.class);
      setClientS6bSessionState(ClientS6bSessionState.IDLE);
    }
  }


  public ClientS6bSessionDataReplicatedImpl(String sessionId, MobicentsCluster mobicentsCluster, IContainer container) {
    this(
            FqnWrapper.fromRelativeElementsWrapper(ReplicatedSessionDatasource.SESSIONS_FQN, sessionId),
            mobicentsCluster, container
    );
  }

  @Override
  public boolean isEventBased() {
    if (exists()) {
      return toPrimitive((Boolean) getNodeValue(EVENT_BASED), true);
    } else {
      throw new IllegalStateException();
    }
  }

  @Override
  public void setEventBased(boolean isEventBased) {
    if (exists()) {
      putNodeValue(EVENT_BASED, isEventBased);
    } else {
      throw new IllegalStateException();
    }
  }

  @Override
  public boolean isRequestTypeSet() {
    if (exists()) {
      return toPrimitive((Boolean) getNodeValue(REQUEST_TYPE), false);
    } else {
      throw new IllegalStateException();
    }
  }

  @Override
  public void setRequestTypeSet(boolean requestTypeSet) {
    if (exists()) {
      putNodeValue(REQUEST_TYPE, requestTypeSet);
    } else {
      throw new IllegalStateException();
    }
  }

  @Override
  public ClientS6bSessionState getClientS6bSessionState() {
    if (exists()) {
      return (ClientS6bSessionState) getNodeValue(STATE);
    } else {
      throw new IllegalStateException();
    }
  }

  @Override
  public void setClientS6bSessionState(ClientS6bSessionState state) {
    if (exists()) {
      putNodeValue(STATE, state);
    } else {
      throw new IllegalStateException();
    }
  }
}
