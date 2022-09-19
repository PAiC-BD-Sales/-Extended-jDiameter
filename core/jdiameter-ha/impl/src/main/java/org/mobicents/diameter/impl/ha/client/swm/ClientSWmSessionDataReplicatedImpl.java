package org.mobicents.diameter.impl.ha.client.swm;


import org.jdiameter.api.swm.ClientSWmSession;
import org.jdiameter.client.api.IContainer;
import org.jdiameter.client.impl.app.swm.IClientSWmSessionData;
import org.jdiameter.common.api.app.swm.ClientSWmSessionState;
import org.mobicents.diameter.impl.ha.common.AppSessionDataReplicatedImpl;
import org.mobicents.diameter.impl.ha.data.ReplicatedSessionDatasource;
import org.restcomm.cache.FqnWrapper;
import org.restcomm.cluster.MobicentsCluster;

/**
 * @author <a href="mailto:enmanuelcalero61@gmail.com"> Enmanuel Calero </a>
 */
public class ClientSWmSessionDataReplicatedImpl extends AppSessionDataReplicatedImpl implements IClientSWmSessionData {

  private static final String EVENT_BASED = "EVENT_BASED";
  private static final String REQUEST_TYPE = "REQUEST_TYPE";
  private static final String STATE = "STATE";


  public ClientSWmSessionDataReplicatedImpl(FqnWrapper nodeFqnWrapper, MobicentsCluster mobicentsCluster, IContainer container) {
    super(nodeFqnWrapper, mobicentsCluster);

    if (super.create()) {
      setAppSessionIface(this, ClientSWmSession.class);
      setClientSWmSessionState(ClientSWmSessionState.IDLE);
    }
  }


  public ClientSWmSessionDataReplicatedImpl(String sessionId, MobicentsCluster mobicentsCluster, IContainer container) {
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
  public ClientSWmSessionState getClientSWmSessionState() {
    if (exists()) {
      return (ClientSWmSessionState) getNodeValue(STATE);
    } else {
      throw new IllegalStateException();
    }
  }

  @Override
  public void setClientSWmSessionState(ClientSWmSessionState state) {
    if (exists()) {
      putNodeValue(STATE, state);
    } else {
      throw new IllegalStateException();
    }
  }
}
