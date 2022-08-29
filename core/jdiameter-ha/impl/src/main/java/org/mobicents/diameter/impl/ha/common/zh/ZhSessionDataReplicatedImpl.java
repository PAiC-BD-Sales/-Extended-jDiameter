package org.mobicents.diameter.impl.ha.common.zh;

import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.Request;
import org.jdiameter.client.api.IContainer;
import org.jdiameter.client.api.IMessage;
import org.jdiameter.client.api.parser.IMessageParser;
import org.jdiameter.client.api.parser.ParseException;
import org.jdiameter.common.api.app.zh.IZhSessionData;
import org.jdiameter.common.api.app.zh.ZhSessionState;
import org.mobicents.diameter.impl.ha.common.AppSessionDataReplicatedImpl;
import org.restcomm.cache.FqnWrapper;
import org.restcomm.cluster.MobicentsCluster;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.nio.ByteBuffer;

/**
 *
 * @author <a href="mailto:kennymendieta89@gmail.com"> Kenny Mendieta </a>
 *
 */
public abstract class ZhSessionDataReplicatedImpl extends AppSessionDataReplicatedImpl implements IZhSessionData {
  private static final Logger logger = LoggerFactory.getLogger(ZhSessionDataReplicatedImpl.class);
  private static final String STATE = "STATE";
  private static final String BUFFER = "BUFFER";
  private static final String TS_TIMERID = "TS_TIMERID";

  private IMessageParser messageParser;

  /**
   * @param nodeFqnWrapper
   * @param mobicentsCluster
   * @param container
   */
  public ZhSessionDataReplicatedImpl(FqnWrapper nodeFqnWrapper, MobicentsCluster mobicentsCluster, IContainer container) {
    super(nodeFqnWrapper, mobicentsCluster);
    this.messageParser = container.getAssemblerFacility().getComponentInstance(IMessageParser.class);
  }
  public void setZhSessionState(ZhSessionState state) {
    if (exists()) {
      putNodeValue(STATE, state);
    } else {
      throw new IllegalStateException();
    }
  }

  public ZhSessionState getZhSessionState() {
    if (exists()) {
      return (ZhSessionState) getNodeValue(STATE);
    } else {
      throw new IllegalStateException();
    }
  }

  public Serializable getTsTimerId() {
    if (exists()) {
      return (Serializable) getNodeValue(TS_TIMERID);
    } else {
      throw new IllegalStateException();
    }
  }

  public void setTsTimerId(Serializable tid) {
    if (exists()) {
      putNodeValue(TS_TIMERID, tid);
    } else {
      throw new IllegalStateException();
    }
  }

  public Request getBuffer() {
    byte[] data = (byte[]) getNodeValue(BUFFER);
    if (data != null) {
      try {
        return (Request) this.messageParser.createMessage(ByteBuffer.wrap(data));
      } catch (AvpDataException e) {
        logger.error("Unable to recreate message from buffer.");
        return null;
      }
    } else {
      return null;
    }
  }

  public void setBuffer(Request buffer) {
    if (buffer != null) {
      try {
        byte[] data = this.messageParser.encodeMessage((IMessage) buffer).array();
        putNodeValue(BUFFER, data);
      }
      catch (ParseException e) {
        logger.error("Unable to encode message to buffer.");
      }
    } else {
      removeNodeValue(BUFFER);
    }
  }
}
