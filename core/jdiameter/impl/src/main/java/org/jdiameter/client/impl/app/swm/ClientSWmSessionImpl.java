package org.jdiameter.client.impl.app.swm;

import org.jdiameter.api.Answer;
import org.jdiameter.api.EventListener;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.NetworkReqListener;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.Request;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.app.StateChangeListener;
import org.jdiameter.api.app.StateEvent;
import org.jdiameter.api.rx.ClientRxSessionListener;
import org.jdiameter.api.swm.ClientSWmSession;
import org.jdiameter.api.swm.ClientSWmSessionListener;
import org.jdiameter.api.swm.events.SWmAbortSessionRequest;
import org.jdiameter.api.swm.events.SWmDiameterEAPAnswer;
import org.jdiameter.client.api.IContainer;
import org.jdiameter.client.api.ISessionFactory;
import org.jdiameter.client.api.parser.IMessageParser;
import org.jdiameter.client.impl.app.rx.IClientRxSessionData;
import org.jdiameter.common.api.app.rx.IClientRxSessionContext;
import org.jdiameter.common.api.app.rx.IRxMessageFactory;
import org.jdiameter.common.api.app.swm.IClientSWmSessionContext;
import org.jdiameter.common.api.app.swm.ISWmMessageFactory;
import org.jdiameter.common.impl.app.swm.AppSWmSessionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ClientSWmSessionImpl extends AppSWmSessionImpl implements ClientSWmSession, NetworkReqListener, EventListener<Request, Answer> {


    private static final Logger logger = LoggerFactory.getLogger(ClientSWmSessionImpl.class);

    protected Lock sendAndStateLock = new ReentrantLock();

    // Factories and Listeners --------------------------------------------------
    protected transient ISWmMessageFactory factory;
    protected transient ClientSWmSessionListener listener;
    protected transient IClientSWmSessionContext context;
    protected transient IMessageParser parser;
    protected IClientSWmSessionData sessionData;


    public ClientSWmSessionImpl(IClientSWmSessionData sessionData, ISWmMessageFactory fct, ISessionFactory sf, ClientSWmSessionListener lst,
                                IClientSWmSessionContext ctx, StateChangeListener<AppSession> stLst) {
        super(sf, sessionData);
        if (lst == null) {
            throw new IllegalArgumentException("Listener can not be null");
        }
        if (fct.getApplicationIds() == null) {
            throw new IllegalArgumentException("ApplicationId can not be less than zero");
        }

        this.context = ctx;

        this.authAppIds = fct.getApplicationIds();
        this.listener = lst;
        this.factory = fct;

        IContainer icontainer = sf.getContainer();
        this.parser = icontainer.getAssemblerFacility().getComponentInstance(IMessageParser.class);
        this.sessionData = sessionData;
        super.addStateChangeNotification(stLst);
    }

    @Override
    public void receivedSuccessMessage(Request request, Answer answer) {

    }

    @Override
    public void timeoutExpired(Request request) {

    }

    @Override
    public Answer processRequest(Request request) {
        return null;
    }

    @Override
    public boolean isStateless() {
        return false;
    }

    @Override
    public void addStateChangeNotification(StateChangeListener listener) {

    }

    @Override
    public void removeStateChangeNotification(StateChangeListener listener) {

    }

    @Override
    public boolean handleEvent(StateEvent event) throws InternalException, OverloadException {
        return false;
    }

    @Override
    public <E> E getState(Class<E> stateType) {
        return null;
    }

    @Override
    public void sendDiameterEAPAnswer(SWmDiameterEAPAnswer answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {

    }

    @Override
    public void sendAbortSessionRequest(SWmAbortSessionRequest request) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {

    }

    @Override
    public void onTimer(String timerName) {

    }
}
