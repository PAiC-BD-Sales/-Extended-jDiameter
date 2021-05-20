/*
 * TeleStax, Open Source Cloud Communications
 * Copyright 2011-2014, TeleStax Inc. and individual contributors
 * by the @authors tag.
 *
 * This program is free software: you can redistribute it and/or modify
 * under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 *
 * This file incorporates work covered by the following copyright and
 * permission notice:
 *
 *   JBoss, Home of Professional Open Source
 *   Copyright 2007-2011, Red Hat, Inc. and individual contributors
 *   by the @authors tag. See the copyright.txt in the distribution for a
 *   full listing of individual contributors.
 *
 *   This is free software; you can redistribute it and/or modify it
 *   under the terms of the GNU Lesser General Public License as
 *   published by the Free Software Foundation; either version 2.1 of
 *   the License, or (at your option) any later version.
 *
 *   This software is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *   Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public
 *   License along with this software; if not, write to the Free
 *   Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 *   02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jdiameter.server.impl.helpers;

import static org.jdiameter.client.impl.helpers.ExtensionPoint.InternalAgentProxy;
import static org.jdiameter.client.impl.helpers.ExtensionPoint.InternalAgentRedirect;
import static org.jdiameter.client.impl.helpers.ExtensionPoint.InternalConcurrentEntityFactory;
import static org.jdiameter.client.impl.helpers.ExtensionPoint.InternalConcurrentFactory;
import static org.jdiameter.client.impl.helpers.ExtensionPoint.InternalConnectionClass;
import static org.jdiameter.client.impl.helpers.ExtensionPoint.InternalElementParser;
import static org.jdiameter.client.impl.helpers.ExtensionPoint.InternalMessageParser;
import static org.jdiameter.client.impl.helpers.ExtensionPoint.InternalMetaData;
import static org.jdiameter.client.impl.helpers.ExtensionPoint.InternalPeerController;
import static org.jdiameter.client.impl.helpers.ExtensionPoint.InternalPeerFsmFactory;
import static org.jdiameter.client.impl.helpers.ExtensionPoint.InternalRealmController;
import static org.jdiameter.client.impl.helpers.ExtensionPoint.InternalRouterEngine;
import static org.jdiameter.client.impl.helpers.ExtensionPoint.InternalSessionDatasource;
import static org.jdiameter.client.impl.helpers.ExtensionPoint.InternalSessionFactory;
import static org.jdiameter.client.impl.helpers.ExtensionPoint.InternalStatisticFactory;
import static org.jdiameter.client.impl.helpers.ExtensionPoint.InternalStatisticProcessor;
import static org.jdiameter.client.impl.helpers.ExtensionPoint.InternalTimerFacility;
import static org.jdiameter.client.impl.helpers.ExtensionPoint.InternalTransportFactory;
import static org.jdiameter.server.impl.helpers.ExtensionPoint.InternalNetWork;
import static org.jdiameter.server.impl.helpers.ExtensionPoint.InternalNetworkGuard;
import static org.jdiameter.server.impl.helpers.ExtensionPoint.InternalOverloadManager;
import static org.jdiameter.server.impl.helpers.Parameters.AcceptUndefinedPeer;
import static org.jdiameter.server.impl.helpers.Parameters.BindDelay;
import static org.jdiameter.server.impl.helpers.Parameters.DuplicateProtection;
import static org.jdiameter.server.impl.helpers.Parameters.DuplicateSize;
import static org.jdiameter.server.impl.helpers.Parameters.DuplicateTimer;
import static org.jdiameter.server.impl.helpers.Parameters.OverloadEntryIndex;
import static org.jdiameter.server.impl.helpers.Parameters.OverloadEntryhighThreshold;
import static org.jdiameter.server.impl.helpers.Parameters.OverloadEntrylowThreshold;
import static org.jdiameter.server.impl.helpers.Parameters.OverloadMonitor;
import static org.jdiameter.server.impl.helpers.Parameters.OwnIPAddresses;
import static org.jdiameter.server.impl.helpers.Parameters.PeerAttemptConnection;
import static org.jdiameter.server.impl.helpers.Parameters.RealmEntryExpTime;
import static org.jdiameter.server.impl.helpers.Parameters.RealmEntryIsDynamic;
import static org.jdiameter.server.impl.helpers.Parameters.RealmHosts;
import static org.jdiameter.server.impl.helpers.Parameters.RealmLocalAction;
import static org.jdiameter.server.impl.helpers.Parameters.RealmName;
import static org.jdiameter.server.impl.helpers.Parameters.RequestTable;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.jdiameter.api.Configuration;
import org.jdiameter.client.impl.helpers.AppConfiguration;
import org.jdiameter.client.impl.helpers.Ordinal;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class provide loading and verification configuration for server from XML file
 *
 * @author erick.svenson@yahoo.com
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author joram.herrera2@gmail.com
 */
public class XMLConfiguration extends EmptyConfiguration {

  /**
   * Create instance of class and load file from defined input stream
   *
   * @param in input stream
   * @throws Exception
   */
  public XMLConfiguration(InputStream in) throws Exception {
    this(in, null, null, false);
  }

  /**
   * Create instance of class and load file from defined input stream
   *
   * @param in input stream
   * @param attributes attributes for DocumentBuilderFactory
   * @param  features features for DocumentBuilderFactory
   * @throws Exception
   */
  public XMLConfiguration(InputStream in, Hashtable<String, Object> attributes, Hashtable<String, Boolean> features) throws Exception {
    this(in, attributes, features, false);
  }

  /**
   * Create instance of class and load file from defined  file name
   *
   * @param filename configuration file name
   * @throws Exception
   */
  public XMLConfiguration(String filename) throws Exception {
    this(filename, null, null, false);
  }

  /**
   * Create instance of class and load file from defined input stream
   *
   * @param filename configuration file name
   * @param attributes attributes for DocumentBuilderFactory
   * @param  features features for DocumentBuilderFactory
   * @throws Exception
   */

  public XMLConfiguration(String filename, Hashtable<String, Object> attributes, Hashtable<String, Boolean> features) throws Exception {
    this(filename, attributes, features, false);
  }

  protected XMLConfiguration(Object in, Hashtable<String, Object> attributes, Hashtable<String, Boolean> features, boolean nop) throws Exception {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setNamespaceAware(true);
    if (attributes != null) {
      for (String key : attributes.keySet()) {
        factory.setAttribute(key, attributes.get(key));
      }
    }
    if (features != null) {
      for (String key : features.keySet()) {
        factory.setFeature(key, features.get(key));
      }
    }

    DocumentBuilder builder = factory.newDocumentBuilder();
    Document document;

    if (in instanceof InputStream) {
      document = builder.parse((InputStream) in);
    }
    else if (in instanceof String) {
      document = builder.parse(new File((String) in));
    }
    else {
      throw  new Exception("Unknown type of input data");
    }

    validate(document);
    processing(document);
  }

  protected void validate(Document document) throws Exception {
    SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
    Source schemaFile = new StreamSource(getClass().getResourceAsStream("/META-INF/jdiameter-server.xsd"));
    Schema schema = factory.newSchema(schemaFile);
    Validator validator = schema.newValidator();
    validator.validate(new DOMSource(document));
  }

  protected void processing(Document document) {
    Element element = document.getDocumentElement();
    NodeList c = element.getChildNodes();

    for (int i = 0; i < c.getLength(); i++) {
      String nodeName = c.item(i).getNodeName();
      if (nodeName.equals("LocalPeer")) {
        addLocalPeer(c.item(i));
      }
      else if (nodeName.equals("Parameters")) {
        addParameters(c.item(i));
      }
      else if (nodeName.equals("Security")) {
        addSecurity(c.item(i));
      }
      else if (nodeName.equals("Network")) {
        addNetwork(c.item(i));
      }
      else if (nodeName.equals("Extensions")) {
        addExtensions(c.item(i));
      }
    }
  }

  protected void addApplications(Node node) {
    NodeList c = node.getChildNodes();
    ArrayList<Configuration> items = new ArrayList<Configuration>();

    for (int i = 0; i < c.getLength(); i++) {
      String nodeName = c.item(i).getNodeName();
      if (nodeName.equals("ApplicationID")) {
        Configuration m = addApplicationID(c.item(i));
        if (m != null) {
          items.add(m);
        }
      }
    }

    add(org.jdiameter.client.impl.helpers.Parameters.ApplicationId, items.toArray(EMPTY_ARRAY));
  }

  protected Configuration addApplicationID(NodeList node) {
    for (int i = 0; i < node.getLength(); i++) {
      String nodeName = node.item(i).getNodeName();
      if (nodeName.equals("ApplicationID")) {
        return addApplicationID(node.item(i));
      }
    }
    return null;
  }

  protected Configuration addApplicationID(Node node) {
    NodeList c = node.getChildNodes();
    AppConfiguration e = getInstance();

    if(node.hasAttributes()) {
      String id = getAttrValue(node,"id");
      e.add(org.jdiameter.client.impl.helpers.Parameters.AppId, ((id == null || id.length() == 0) ? -1 : Integer.valueOf(id)));
    }

    for (int i = 0; i < c.getLength(); i++) {
      String nodeName = c.item(i).getNodeName();
      if (nodeName.equals("VendorId")) {
        e.add(org.jdiameter.client.impl.helpers.Parameters.VendorId,   getLongValue(c.item(i)));
      }
      else if (nodeName.equals("AuthApplId")) {
        e.add(org.jdiameter.client.impl.helpers.Parameters.AuthApplId, getLongValue(c.item(i)));
      }
      else if (nodeName.equals("AcctApplId")) {
        e.add(org.jdiameter.client.impl.helpers.Parameters.AcctApplId, getLongValue(c.item(i)));
      }
    }
    return e;
  }

  protected void addParameters(Node node) {
    NodeList c = node.getChildNodes();
    for (int i = 0; i < c.getLength(); i++) {
      String nodeName = c.item(i).getNodeName();
      if (nodeName.equals("UseUriAsFqdn")) {
        add(org.jdiameter.client.impl.helpers.Parameters.UseUriAsFqdn, Boolean.valueOf(getValue(c.item(i))));
      }
      else if (nodeName.equals("SingleLocalPeer")) {
        add(org.jdiameter.client.impl.helpers.Parameters.SingleLocalPeer, Boolean.valueOf(getValue(c.item(i))));
      }
      else if (nodeName.equals("QueueSize")) {
        add(org.jdiameter.client.impl.helpers.Parameters.QueueSize, getIntValue(c.item(i)));
      }
      else if (nodeName.equals("MessageTimeOut")) {
        add(org.jdiameter.client.impl.helpers.Parameters.MessageTimeOut, getLongValue(c.item(i)));
      }
      else if (nodeName.equals("StopTimeOut")) {
        add(org.jdiameter.client.impl.helpers.Parameters.StopTimeOut, getLongValue(c.item(i)));
      }
      else if (nodeName.equals("CeaTimeOut")) {
        add(org.jdiameter.client.impl.helpers.Parameters.CeaTimeOut, getLongValue(c.item(i)));
      }
      else if (nodeName.equals("IacTimeOut")) {
        add(org.jdiameter.client.impl.helpers.Parameters.IacTimeOut, getLongValue(c.item(i)));
      }
      else if (nodeName.equals("DwaTimeOut")) {
        add(org.jdiameter.client.impl.helpers.Parameters.DwaTimeOut, getLongValue(c.item(i)));
      }
      else if (nodeName.equals("DpaTimeOut")) {
        add(org.jdiameter.client.impl.helpers.Parameters.DpaTimeOut, getLongValue(c.item(i)));
      }
      else if (nodeName.equals("RecTimeOut")) {
        add(org.jdiameter.client.impl.helpers.Parameters.RecTimeOut, getLongValue(c.item(i)));
      }
      else if (nodeName.equals("SessionTimeOut")) {
        add(org.jdiameter.client.impl.helpers.Parameters.SessionTimeOut, getLongValue(c.item(i)));
      }
      else if (nodeName.equals("BindDelay"))  {
        add(BindDelay, getLongValue(c.item(i)));
      }
      else if (nodeName.equals("ThreadPool")) {
        addThreadPool(c.item(i));
      }
      else if (nodeName.equals("PeerFSMThreadCount")) {
        add(org.jdiameter.client.impl.helpers.Parameters.PeerFSMThreadCount, getIntValue(c.item(i)));
      }
      else if (nodeName.equals("Statistics")) {
        addStatisticLogger(org.jdiameter.client.impl.helpers.Parameters.Statistics, c.item(i));
      }
      else if (nodeName.equals("Concurrent")) {
        addConcurrent(org.jdiameter.client.impl.helpers.Parameters.Concurrent, c.item(i));
      }
      else if (nodeName.equals("Dictionary")) {
        addDictionary(org.jdiameter.client.impl.helpers.Parameters.Dictionary, c.item(i));
      }
      else if (nodeName.equals("RequestTable")) {
        addRequestTable(RequestTable, c.item(i));
      }
      else {
        appendOtherParameter(c.item(i));
      }
    }
  }

  protected void addThreadPool(Node item) {
    AppConfiguration threadPoolConfiguration = org.jdiameter.client.impl.helpers.EmptyConfiguration.getInstance();
    NamedNodeMap attributes = item.getAttributes();

    for (int index = 0; index < attributes.getLength(); index++) {
      Node n = attributes.item(index);

      int v = Integer.parseInt(n.getNodeValue());
      if (n.getNodeName().equals("size")) {
        threadPoolConfiguration.add(org.jdiameter.client.impl.helpers.Parameters.ThreadPoolSize, v);
      }
      else if (n.getNodeName().equals("priority")) {
        threadPoolConfiguration.add(org.jdiameter.client.impl.helpers.Parameters.ThreadPoolPriority, v);
      }
      else {
        //log.error("Unkonwn attribute on " + item.getNodeName() + ", attribute name: " + n.getNodeName());
      }
    }
    if (!threadPoolConfiguration.isAttributeExist(org.jdiameter.client.impl.helpers.Parameters.ThreadPoolSize.ordinal())) {
      threadPoolConfiguration.add(org.jdiameter.client.impl.helpers.Parameters.ThreadPoolSize,
          org.jdiameter.client.impl.helpers.Parameters.ThreadPoolSize.defValue());
    }
    if (!threadPoolConfiguration.isAttributeExist(org.jdiameter.client.impl.helpers.Parameters.ThreadPoolPriority.ordinal())) {
      threadPoolConfiguration.add(org.jdiameter.client.impl.helpers.Parameters.ThreadPoolPriority,
          org.jdiameter.client.impl.helpers.Parameters.ThreadPoolPriority.defValue());
    }
    this.add(org.jdiameter.client.impl.helpers.Parameters.ThreadPool, threadPoolConfiguration);
  }

  protected void addConcurrent(org.jdiameter.client.impl.helpers.Parameters name, Node node) {
    NodeList c = node.getChildNodes();
    List<Configuration> items = new ArrayList<Configuration>();
    for (int i = 0; i < c.getLength(); i++) {
      String nodeName = c.item(i).getNodeName();
      if (nodeName.equals("Entity")) {
        addConcurrentEntity(items, c.item(i));
      }
    }
    add(name, items.toArray(new Configuration[items.size()]));
  }

  protected void addConcurrentEntity(List<Configuration> items, Node node) {
    AppConfiguration cfg = getInstance();
    String name = node.getAttributes().getNamedItem("name").getNodeValue();
    cfg.add(org.jdiameter.client.impl.helpers.Parameters.ConcurrentEntityName, name);
    if (node.getAttributes().getNamedItem("description") != null) {
      String descr = node.getAttributes().getNamedItem("description").getNodeValue();
      cfg.add(org.jdiameter.client.impl.helpers.Parameters.ConcurrentEntityDescription, descr);
    }
    if (node.getAttributes().getNamedItem("size") != null) {
      String size = node.getAttributes().getNamedItem("size").getNodeValue();
      cfg.add(org.jdiameter.client.impl.helpers.Parameters.ConcurrentEntityPoolSize, Integer.parseInt(size));
    }
    items.add(cfg);
  }


  protected void addStatisticLogger(org.jdiameter.client.impl.helpers.Parameters name, Node node) {
    String pause = node.getAttributes().getNamedItem("pause").getNodeValue();
    String delay = node.getAttributes().getNamedItem("delay").getNodeValue();
    String enabled = node.getAttributes().getNamedItem("enabled").getNodeValue();
    String active_records;
    if (node.getAttributes().getNamedItem("active_records") != null) {
      active_records = node.getAttributes().getNamedItem("active_records").getNodeValue();
    } else {
      active_records = (String) org.jdiameter.client.impl.helpers.Parameters.StatisticsActiveList.defValue();
    }
    add(name,
        getInstance().
        add(org.jdiameter.client.impl.helpers.Parameters.StatisticsLoggerPause, Long.parseLong(pause)).
        add(org.jdiameter.client.impl.helpers.Parameters.StatisticsLoggerDelay, Long.parseLong(delay)).
        add(org.jdiameter.client.impl.helpers.Parameters.StatisticsEnabled, Boolean.parseBoolean(enabled)).
        add(org.jdiameter.client.impl.helpers.Parameters.StatisticsActiveList, active_records));
  }

  protected void addDictionary(org.jdiameter.client.impl.helpers.Parameters name, Node node) {
    AppConfiguration dicConfiguration = getInstance();

    Node param = node.getAttributes().getNamedItem("class");
    if (param != null) {
      String clazz = param.getNodeValue();
      dicConfiguration.add(org.jdiameter.client.impl.helpers.Parameters.DictionaryClass, clazz);
    }

    param =  node.getAttributes().getNamedItem("enabled");
    if (param != null) {
      String enabled = param.getNodeValue();
      dicConfiguration.add(org.jdiameter.client.impl.helpers.Parameters.DictionaryEnabled, Boolean.valueOf(enabled));
    }

    param =  node.getAttributes().getNamedItem("sendLevel");
    if (param != null) {
      String sendLevel = param.getNodeValue();
      dicConfiguration.add(org.jdiameter.client.impl.helpers.Parameters.DictionarySendLevel, sendLevel);
    }

    param =  node.getAttributes().getNamedItem("receiveLevel");
    if (param != null) {
      String receiveLevel = param.getNodeValue();
      dicConfiguration.add(org.jdiameter.client.impl.helpers.Parameters.DictionaryReceiveLevel, receiveLevel);
    }

    add(name, dicConfiguration);
  }

  protected void addRequestTable(org.jdiameter.client.impl.helpers.Parameters name, Node node) {
    AppConfiguration tableConfiguration = getInstance();

    Node param = node.getAttributes().getNamedItem("size");
    if (param != null) {
      String size = param.getNodeValue();
      tableConfiguration.add(Parameters.RequestTableSize, Integer.parseInt(size));
    }

    param = node.getAttributes().getNamedItem("clear_size");
    if (param != null) {
      String size = param.getNodeValue();
      tableConfiguration.add(Parameters.RequestTableClearSize,  Integer.parseInt(size));
    }

    add(name, tableConfiguration);
  }

  protected void addSecurity(Node node) {
    NodeList c = node.getChildNodes();
    List<Configuration> items = new ArrayList<Configuration>();
    for (int i = 0; i < c.getLength(); i++) {
      String nodeName = c.item(i).getNodeName();
      if (nodeName.equals("SecurityData")) {
        items.add(addSecurityData(c.item(i)));
      }
    }
    add(org.jdiameter.client.impl.helpers.Parameters.Security, items.toArray(EMPTY_ARRAY));
  }

  protected Configuration addSecurityData(Node node) {
    AppConfiguration sd = getInstance().add(org.jdiameter.client.impl.helpers.Parameters.SDName, node.getAttributes().getNamedItem("name").getNodeValue())
        .add(org.jdiameter.client.impl.helpers.Parameters.SDProtocol, node.getAttributes().getNamedItem("protocol").getNodeValue())
        .add(org.jdiameter.client.impl.helpers.Parameters.SDEnableSessionCreation,
            Boolean.valueOf(node.getAttributes().getNamedItem("enable_session_creation").getNodeValue()))
        .add(org.jdiameter.client.impl.helpers.Parameters.SDUseClientMode,
            Boolean.valueOf(node.getAttributes().getNamedItem("use_client_mode").getNodeValue()));

    NodeList c = node.getChildNodes();

    for (int i = 0; i < c.getLength(); i++) {
      Node cnode = c.item(i);
      String nodeName = cnode.getNodeName();
      if (nodeName.equals("CipherSuites")) {
        sd.add(org.jdiameter.client.impl.helpers.Parameters.CipherSuites, cnode.getTextContent().trim());
      }
      if (nodeName.equals("KeyData")) {
        sd.add(org.jdiameter.client.impl.helpers.Parameters.KeyData, getInstance().add(org.jdiameter.client.impl.helpers.Parameters.KDManager,
            cnode.getAttributes().getNamedItem("manager").getNodeValue())
            .add(org.jdiameter.client.impl.helpers.Parameters.KDStore, cnode.getAttributes().getNamedItem("store").getNodeValue())
            .add(org.jdiameter.client.impl.helpers.Parameters.KDFile, cnode.getAttributes().getNamedItem("file").getNodeValue())
            .add(org.jdiameter.client.impl.helpers.Parameters.KDPwd, cnode.getAttributes().getNamedItem("pwd").getNodeValue()));
      }
      if (nodeName.equals("TrustData")) {
        sd.add(org.jdiameter.client.impl.helpers.Parameters.TrustData, getInstance().add(org.jdiameter.client.impl.helpers.Parameters.TDManager,
            cnode.getAttributes().getNamedItem("manager").getNodeValue())
            .add(org.jdiameter.client.impl.helpers.Parameters.TDStore, cnode.getAttributes().getNamedItem("store").getNodeValue())
            .add(org.jdiameter.client.impl.helpers.Parameters.TDFile, cnode.getAttributes().getNamedItem("file").getNodeValue())
            .add(org.jdiameter.client.impl.helpers.Parameters.TDPwd, cnode.getAttributes().getNamedItem("pwd").getNodeValue()));
      }
    }
    return sd;
  }
  protected void addNetwork(Node node) {
    NodeList c = node.getChildNodes();
    for (int i = 0; i < c.getLength(); i++) {
      String nodeName = c.item(i).getNodeName();
      if (nodeName.equals("Peers")) {
        addPeers(c.item(i));
      }
      else if (nodeName.equals("Realms")) {
        addRealms(c.item(i));
      }
    }
  }

  protected void addPeers(Node node) {
    NodeList c = node.getChildNodes();
    ArrayList<Configuration> items = new ArrayList<Configuration>();
    for (int i = 0; i < c.getLength(); i++) {
      String nodeName = c.item(i).getNodeName();
      if (nodeName.equals("Peer")) {
        items.add(addPeer(c.item(i)));
      }
    }
    add(org.jdiameter.client.impl.helpers.Parameters.PeerTable, items.toArray(EMPTY_ARRAY));
  }

  protected void addRealms(Node node) {
    NodeList c = node.getChildNodes();
    ArrayList<Configuration> items = new ArrayList<Configuration>();
    for (int i = 0; i < c.getLength(); i++) {
      String nodeName = c.item(i).getNodeName();
      if (nodeName.equals("Realm")) {
        items.add(addRealm(c.item(i)));
      }
    }
    add(org.jdiameter.client.impl.helpers.Parameters.RealmTable, items.toArray(EMPTY_ARRAY));
  }

  protected Configuration addPeer(Node node) {
    String rating = node.getAttributes().getNamedItem("rating").getNodeValue();
    String connecting = node.getAttributes().getNamedItem("attempt_connect").getNodeValue();
    String name = node.getAttributes().getNamedItem("name").getNodeValue();
    AppConfiguration c = getInstance();
    c.add(org.jdiameter.client.impl.helpers.Parameters.PeerRating, Integer.parseInt(rating));
    c.add(PeerAttemptConnection, Boolean.valueOf(connecting));
    c.add(org.jdiameter.client.impl.helpers.Parameters.PeerName, name);
    if (node.getAttributes().getNamedItem("ip") != null) {
      c.add(org.jdiameter.client.impl.helpers.Parameters.PeerIp, node.getAttributes().getNamedItem("ip").getNodeValue());
    }
    if (node.getAttributes().getNamedItem("host") != null) {
      c.add(org.jdiameter.client.impl.helpers.Parameters.PeerHost, node.getAttributes().getNamedItem("host").getNodeValue());
    }
    if (node.getAttributes().getNamedItem("portRange") != null) {
      c.add(org.jdiameter.client.impl.helpers.Parameters.PeerLocalPortRange, node.getAttributes().getNamedItem("portRange").getNodeValue());
    }
    if (node.getAttributes().getNamedItem("standby_addresses") != null) {
      c.add(org.jdiameter.client.impl.helpers.Parameters.PeerStandbyAddresses, node.getAttributes().getNamedItem("standby_addresses").getNodeValue());
    }
    if (node.getAttributes().getNamedItem("security_ref") != null) {
      c.add(org.jdiameter.client.impl.helpers.Parameters.SecurityRef, node.getAttributes().getNamedItem("security_ref").getNodeValue());
    }
    return c;
  }

  protected void addLocalPeer(Node node) {
    NodeList c = node.getChildNodes();
    if (node.getAttributes().getNamedItem("security_ref") != null) {
      add(org.jdiameter.client.impl.helpers.Parameters.SecurityRef, node.getAttributes().getNamedItem("security_ref").getNodeValue());
    }

    for (int i = 0; i < c.getLength(); i++) {
      String nodeName = c.item(i).getNodeName();
      if (nodeName.equals("URI")) {
        add(org.jdiameter.client.impl.helpers.Parameters.OwnDiameterURI, getValue(c.item(i)));
      }
      addIPAddress(c.item(i));
      if (nodeName.equals("Realm")) {
        add(org.jdiameter.client.impl.helpers.Parameters.OwnRealm, getValue(c.item(i)));
      }
      if (nodeName.equals("VendorID")) {
        add(org.jdiameter.client.impl.helpers.Parameters.OwnVendorID, getLongValue(c.item(i)));
      }
      if (nodeName.equals("ProductName")) {
        add(org.jdiameter.client.impl.helpers.Parameters.OwnProductName, getValue(c.item(i)));
      }
      if (nodeName.equals("FirmwareRevision")) {
        add(org.jdiameter.client.impl.helpers.Parameters.OwnFirmwareRevision, getLongValue(c.item(i)));
      }
      if (nodeName.equals("Applications")) {
        addApplications(c.item(i));
      }
      if (nodeName.equals("OverloadMonitor")) {
        addOverloadMonitor(c.item(i));
      }
    }
  }

  private void addOverloadMonitor(Node node) {
    NodeList c = node.getChildNodes();
    ArrayList<Configuration> items = new ArrayList<Configuration>();
    for (int i = 0; i < c.getLength(); i++) {
      String nodeName = c.item(i).getNodeName();
      if (nodeName.equals("Entry")) {
        items.add(addOverloadMonitorItem(c.item(i)));
      }
    }
    add(OverloadMonitor, items.toArray(EMPTY_ARRAY));
  }

  private Configuration addOverloadMonitorItem(Node node) {
    return getInstance().
        add(OverloadEntryIndex, Integer.valueOf(getAttrValue(node, "index"))).
        add(OverloadEntrylowThreshold, Double.valueOf(getAttrValue(node, "lowThreshold"))).
        add(OverloadEntryhighThreshold, Double.valueOf(getAttrValue(node, "highThreshold"))).
        add(org.jdiameter.client.impl.helpers.Parameters.ApplicationId, addApplicationID(node.getChildNodes()));
  }

  protected void addIPAddress(Node node) {
    String nodeName = node.getNodeName();
    if (nodeName.equals("IPAddresses")) {
      addIPAddresses(node);
    }
  }

  private void addIPAddresses(Node node) {
    NodeList c = node.getChildNodes();
    ArrayList<Configuration> items = new ArrayList<Configuration>();
    for (int i = 0; i < c.getLength(); i++) {
      String nodeName = c.item(i).getNodeName();
      if (nodeName.equals("IPAddress")) {
        items.add(addIPAddressItem(c.item(i)));
      }
    }
    add(OwnIPAddresses, items.toArray(EMPTY_ARRAY));
  }

  protected Configuration addIPAddressItem(Node node) {
    return getInstance().
        add(org.jdiameter.client.impl.helpers.Parameters.OwnIPAddress, getValue(node));
  }

  protected Configuration addRealm(Node node) {
    AppConfiguration realmEntry = getInstance();
    realmEntry.
      add(org.jdiameter.client.impl.helpers.Parameters.ApplicationId, new Configuration[] {addApplicationID(node.getChildNodes())}).
      add(RealmName,  getAttrValue(node, "name")).
      add(RealmHosts, getAttrValue(node, "peers")).
      add(RealmLocalAction,    getAttrValue(node, "local_action")).
      add(RealmEntryIsDynamic, Boolean.valueOf(getAttrValue(node, "dynamic"))).
      add(RealmEntryExpTime,   Long.valueOf(getAttrValue(node, "exp_time")));

    NodeList childNodes = node.getChildNodes();
    for (int i = 0; i < childNodes.getLength(); i++) {
      String nodeName = childNodes.item(i).getNodeName();
      if (nodeName.equals("Agent")) {
        realmEntry.add(org.jdiameter.client.impl.helpers.Parameters.Agent, addAgent(childNodes.item(i)));
      }
    }

    return getInstance().add(org.jdiameter.client.impl.helpers.Parameters.RealmEntry, realmEntry);
  }

  protected Configuration addAgent(Node node) {
    AppConfiguration agentConf = getInstance();
    NodeList agentChildren = node.getChildNodes();

    for (int index = 0; index < agentChildren.getLength(); index++) {
      Node n = agentChildren.item(index);
      if (n.getNodeName().equals("Properties")) {
        agentConf.add(org.jdiameter.client.impl.helpers.Parameters.Properties, getProperties(n).toArray(EMPTY_ARRAY));
      }
    }

    return agentConf;
  }

  protected List<Configuration> getProperties(Node node) {
    List<Configuration> props = new ArrayList<Configuration>();
    NodeList propertiesChildren = node.getChildNodes();

    for (int index = 0; index < propertiesChildren.getLength(); index++) {
      Node n = propertiesChildren.item(index);
      if (n.getNodeName().equals("Property")) {
        AppConfiguration property = getInstance();
        property.add(org.jdiameter.client.impl.helpers.Parameters.PropertyName,
            n.getAttributes().getNamedItem(org.jdiameter.client.impl.helpers.Parameters.PropertyName.name()).getNodeValue());
        property.add(org.jdiameter.client.impl.helpers.Parameters.PropertyValue,
            n.getAttributes().getNamedItem(org.jdiameter.client.impl.helpers.Parameters.PropertyValue.name()).getNodeValue());
        props.add(property);
      }
    }

    return props;
  }

  protected void appendOtherParameter(Node node) {
    String nodeName = node.getNodeName();
    if (nodeName.equals("DuplicateProtection")) {
      add(DuplicateProtection, Boolean.valueOf(getValue(node)));
    }
    if (nodeName.equals("DuplicateTimer")) {
      add(DuplicateTimer, getLongValue(node));
    }
    if (nodeName.equals("DuplicateSize")) {
      add(DuplicateSize, getIntValue(node));
    }
    if (nodeName.equals("AcceptUndefinedPeer")) {
      add(AcceptUndefinedPeer, Boolean.valueOf(getValue(node)));
    }
  }

  protected void addExtensions(Node node) {
    NodeList c = node.getChildNodes();
    for (int i = 0; i < c.getLength(); i++) {
      String nodeName = c.item(i).getNodeName();
      if (nodeName.equals("MetaData")) {
        addInternalExtension(InternalMetaData, getValue(c.item(i)));
      }
      else if (nodeName.equals("MessageParser")) {
        addInternalExtension(InternalMessageParser, getValue(c.item(i)));
      }
      else if (nodeName.equals("ElementParser")) {
        addInternalExtension(InternalElementParser, getValue(c.item(i)));
      }
      else if (nodeName.equals("RouterEngine")) {
        addInternalExtension(InternalRouterEngine, getValue(c.item(i)));
      }
      else if (nodeName.equals("PeerController")) {
        addInternalExtension(InternalPeerController, getValue(c.item(i)));
      }
      else if (nodeName.equals("RealmController")) {
        addInternalExtension(InternalRealmController, getValue(c.item(i)));
      }
      else if (nodeName.equals("SessionFactory")) {
        addInternalExtension(InternalSessionFactory, getValue(c.item(i)));
      }
      else if (nodeName.equals("TransportFactory")) {
        addInternalExtension(InternalTransportFactory, getValue(c.item(i)));
      }
      else if (nodeName.equals("Connection")) {
        addInternalExtension(InternalConnectionClass, getValue(c.item(i)));
      }
      else if (nodeName.equals("NetworkGuard")) {
        addInternalExtension(InternalNetworkGuard, getValue(c.item(i)));
      }
      else if (nodeName.equals("PeerFsmFactory")) {
        addInternalExtension(InternalPeerFsmFactory, getValue(c.item(i)));
      }
      else if (nodeName.equals("StatisticFactory")) {
        addInternalExtension(InternalStatisticFactory, getValue(c.item(i)));
      }
      else if (nodeName.equals("ConcurrentFactory")) {
        addInternalExtension(InternalConcurrentFactory, getValue(c.item(i)));
      }
      else if (nodeName.equals("ConcurrentEntityFactory")) {
        addInternalExtension(InternalConcurrentEntityFactory, getValue(c.item(i)));
      }
      else if (nodeName.equals("StatisticProcessor")) {
        addInternalExtension(InternalStatisticProcessor, getValue(c.item(i)));
      }
      else if (nodeName.equals("NetWork")) {
        addInternalExtension(InternalNetWork, getValue(c.item(i)));
      }
      else if (nodeName.equals("SessionDatasource")) {
        addInternalExtension(InternalSessionDatasource, getValue(c.item(i)));
      }
      else if (nodeName.equals("TimerFacility")) {
        addInternalExtension(InternalTimerFacility, getValue(c.item(i)));
      }
      else if (nodeName.equals("AgentRedirect")) {
        addInternalExtension(InternalAgentRedirect, getValue(c.item(i)));
      }
      else if (nodeName.equals("AgentConfiguration")) {
        add(org.jdiameter.client.impl.helpers.ExtensionPoint.InternalAgentConfiguration, getValue(c.item(i)));
      }
      else if (nodeName.equals("AgentProxy")) {
        addInternalExtension(InternalAgentProxy, getValue(c.item(i)));
      }
      else if (nodeName.equals("OverloadManager")) {
        addInternalExtension(InternalOverloadManager, getValue(c.item(i)));
      }
      else {
        appendOtherExtension(c.item(i));
      }
    }
  }

  protected void addInternalExtension(Ordinal ep, String value) {
    Configuration[] extensionConfs = this.getChildren(org.jdiameter.client.impl.helpers.Parameters.Extensions.ordinal());
    AppConfiguration internalExtensions = (AppConfiguration) extensionConfs[org.jdiameter.client.impl.helpers.ExtensionPoint.Internal.id()];
    internalExtensions.add(ep, value);
  }

  private void appendOtherExtension(Node item) {
    // Nothing to do here, so far
  }

  protected Long getLongValue(Node node) {
    return new Long(getValue(node));
  }

  protected Integer getIntValue(Node node) {
    return new Integer(getValue(node));
  }

  protected String getValue(Node node) {
    return node.getAttributes().getNamedItem("value").getNodeValue();
  }

  protected String getAttrValue(Node node, String name) {
    return node.getAttributes().getNamedItem(name).getNodeValue();
  }

}
