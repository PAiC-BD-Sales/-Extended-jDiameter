package org.jdiameter.client.impl.app.swm;

import org.jdiameter.api.Answer;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.EventListener;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.Message;
import org.jdiameter.api.NetworkReqListener;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.Request;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppEvent;
import org.jdiameter.api.app.AppRequestEvent;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.app.StateChangeListener;
import org.jdiameter.api.app.StateEvent;
import org.jdiameter.api.auth.events.ReAuthRequest;
import org.jdiameter.api.rx.events.RxAAAnswer;
import org.jdiameter.api.rx.events.RxAARequest;
import org.jdiameter.api.rx.events.RxAbortSessionRequest;
import org.jdiameter.api.rx.events.RxReAuthRequest;
import org.jdiameter.api.rx.events.RxSessionTermAnswer;
import org.jdiameter.api.rx.events.RxSessionTermRequest;
import org.jdiameter.api.swm.ClientSWmSession;
import org.jdiameter.api.swm.ClientSWmSessionListener;
import org.jdiameter.api.swm.events.SWmAbortSessionAnswer;
import org.jdiameter.api.swm.events.SWmAbortSessionRequest;
import org.jdiameter.api.swm.events.SWmDiameterEAPAnswer;
import org.jdiameter.api.swm.events.SWmDiameterEAPRequest;
import org.jdiameter.client.api.IContainer;
import org.jdiameter.client.api.ISessionFactory;
import org.jdiameter.client.api.parser.IMessageParser;
import org.jdiameter.common.api.app.IAppSessionState;
import org.jdiameter.common.api.app.rx.ClientRxSessionState;
import org.jdiameter.common.api.app.swm.ClientSWmSessionState;
import org.jdiameter.common.api.app.swm.IClientSWmSessionContext;
import org.jdiameter.common.api.app.swm.ISWmMessageFactory;
import org.jdiameter.common.impl.app.AppRequestEventImpl;
import org.jdiameter.common.impl.app.swm.AppSWmSessionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ClientSWmSessionImpl extends AppSWmSessionImpl implements ClientSWmSession, NetworkReqListener, EventListener<Request, Answer> {

//TODO Add the DER command
    private static final Logger logger = LoggerFactory.getLogger(ClientSWmSessionImpl.class);

    protected Lock sendAndStateLock = new ReentrantLock();

    // Factories and Listeners --------------------------------------------------
    protected transient ISWmMessageFactory factory;
    protected transient ClientSWmSessionListener listener;
    protected transient IClientSWmSessionContext context;
    protected transient IMessageParser parser;
    protected IClientSWmSessionData sessionData;

    protected long[] authAppIds;

    protected byte[] buffer;

    protected String originHost, originRealm;

    protected ArrayList<Event> eventQueue = new ArrayList<>();


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


    public boolean isEventBased() {
        return sessionData.isEventBased();
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
    public boolean handleEvent(StateEvent event) throws InternalException, OverloadException {
        return this.isEventBased() ? handleEventForEventBased(event) : handleEventForSessionBased(event);
    }


    /**
     * This makes checks on queue, moves it to proper state if event there is
     * present on Open state ;
     */
    protected void dispatch() {
        // Event Based ----------------------------------------------------------
        if (isEventBased()) {
            // Current State: IDLE
            // Event: Request in storage
            // Action: Send stored request
            // New State: PENDING_B
            if (buffer != null) {
                setState(ClientSWmSessionState.PENDING_BUFFERED);
                try {
                    dispatchEvent(new AppRequestEventImpl(messageFromBuffer(ByteBuffer.wrap(buffer))));
                }
                catch (Exception e) {
                    try {
                        handleSendFailure(e, Event.Type.SEND_EVENT_REQUEST, messageFromBuffer(ByteBuffer.wrap(buffer)));
                    }
                    catch (Exception e1) {
                        logger.error("Failure handling buffer send failure", e1);
                    }
                }
            }
        } // Session Based --------------------------------------------------------
        else {
            if (sessionData.getClientSWmSessionState() == ClientSWmSessionState.OPEN && eventQueue.size() > 0) {
                try {
                    this.handleEvent(eventQueue.remove(0));
                }
                catch (Exception e) {
                    logger.error("Failure handling queued event", e);
                }
            }
        }
    }

    private Message messageFromBuffer(ByteBuffer request) throws InternalException {
        if (request != null) {
            Message m;
            try {
                m = parser.createMessage(request);
                return m;
            }
            catch (AvpDataException e) {
                throw new InternalException("Failed to decode message.", e);
            }
        }
        return null;
    }

    protected void handleSendFailure(Exception e, Event.Type eventType, Message request) throws Exception {
        logger.debug("Failed to send message, type: {} message: {}, failure: {}", eventType, request, e != null ? e.getLocalizedMessage() : "");
    }

    protected void handleFailureMessage(final AppAnswerEvent event, final AppRequestEvent request, final Event.Type eventType) {
        try {
            setState(ClientSWmSessionState.IDLE);
        }
        catch (Exception e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Failure handling failure message for Event " + event + " (" + eventType + ") and Request " + request, e);
            }
        }
    }

    protected void dispatchEvent(AppEvent event) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
        session.send(event.getMessage(), this);
    }

    protected boolean isProvisional(long resultCode) {
        return resultCode >= 1000 && resultCode < 2000;
    }

    protected boolean isSuccess(long resultCode) {
        return resultCode >= 2000 && resultCode < 3000;
    }

    protected boolean isFailure(long code) {
        //return (!isProvisional(code) && !isSuccess(code) && ((code >= 3000 && code < 6000)) && !temporaryErrorCodes.contains(code));
        return (!isProvisional(code) && !isSuccess(code) && ((code >= 3000 && code < 6000)) );
    }
    protected void setState(ClientSWmSessionState newState) {
        setState(newState, true);
    }

    @SuppressWarnings("unchecked")
    protected void setState(ClientSWmSessionState newState, boolean release) {
        try {
            IAppSessionState<ClientSWmSessionState> oldState = this.sessionData.getClientSWmSessionState();
            this.sessionData.setClientSWmSessionState(newState);
            for (StateChangeListener i : stateListeners) {
                i.stateChanged(this, (Enum) oldState, (Enum) newState);
            }

            if (newState == ClientSWmSessionState.IDLE) {
                if (release) {
                    this.release();
                }

            }
        }
        catch (Exception e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Failure switching to state " + this.sessionData.getClientSWmSessionState() + " (release=" + release + ")", e);
            }
        }
    }

    @Override
    public void release() {
        if (isValid()) {
            try {
                this.sendAndStateLock.lock();
                super.release();
            }
            catch (Exception e) {
                logger.debug("Failed to release session", e);
            }
            finally {
                sendAndStateLock.unlock();
            }
        }
        else {
            logger.debug("Trying to release an already invalid session, with Session ID '{}'", getSessionId());
        }
    }

    protected boolean handleEventForEventBased(StateEvent event) throws InternalException {
        try {
            sendAndStateLock.lock();
            final ClientSWmSessionState state = this.sessionData.getClientSWmSessionState();
            Event localEvent = (Event) event;
            Event.Type eventType = (Event.Type) localEvent.getType();
            switch (state) {
                case IDLE:
                    switch (eventType) {
                        case SEND_EVENT_REQUEST:
                            // Current State: IDLE
                            // Event: Client or device requests a one-time service
                            // Action: Send AA event request
                            // New State: PENDING_E
                            setState(ClientSWmSessionState.PENDING_EVENT);
                            try {
                                dispatchEvent(localEvent.getRequest());
                            }
                            catch (Exception e) {
                                // This handles failure to send in PendingI state in FSM table
                                logger.debug("Failure handling send event request", e);
                                handleSendFailure(e, eventType, localEvent.getRequest().getMessage());
                            }
                            break;
                        default:
                            logger.warn("Event Based Handling - Wrong event type ({}) on state {}", eventType, state);
                            break;
                    }
                    break;

                case PENDING_EVENT:
                    switch (eventType) {
                        case RECEIVE_EVENT_ANSWER:
                            AppAnswerEvent answer = (AppAnswerEvent) localEvent.getAnswer();
                            try {
                                long resultCode = answer.getResultCodeAvp().getUnsigned32();
                                if (isSuccess(resultCode)) {
                                    // Current State: PENDING_E
                                    // Event: Successful AA event answer received
                                    // Action: Grant service to end user
                                    // New State: IDLE
                                    setState(ClientSWmSessionState.IDLE, false);
                                }
                                if (isProvisional(resultCode) || isFailure(resultCode)) {
                                    handleFailureMessage(answer, (AppRequestEvent) localEvent.getRequest(), eventType);
                                }
                                //TODO check AAAnswer
                                // deliverRxAAAnswer((RxAARequest) localEvent.getRequest(), (RxAAAnswer) localEvent.getAnswer());
                            }
                            catch (AvpDataException e) {
                                logger.debug("Failure handling received answer event", e);
                                setState(ClientSWmSessionState.IDLE, false);
                            }
                            break;
                        default:
                            logger.warn("Event Based Handling - Wrong event type ({}) on state {}", eventType, state);
                            break;
                    }
                    break;

                case PENDING_BUFFERED:
                    switch (eventType) {
                        case RECEIVE_EVENT_ANSWER:
                            // Current State: PENDING_B
                            // Event: Successful CC answer received
                            // Action: Delete request
                            // New State: IDLE
                            setState(ClientSWmSessionState.IDLE, false);
                            //this.sessionData.setBuffer(null);
                            buffer = null;
                            //TODO check AAAnswer
                            // deliverRxAAAnswer((RxAARequest) localEvent.getRequest(), (RxAAAnswer) localEvent.getAnswer());
                            break;
                        default:
                            logger.warn("Event Based Handling - Wrong event type ({}) on state {}", eventType, state);
                            break;
                    }
                    break;

                default:
                    logger.warn("Event Based Handling - Wrong event type ({}) on state {}", eventType, state);
                    break;
            }

            dispatch();
            return true;
        }
        catch (Exception e) {
            throw new InternalException(e);
        }
        finally {
            sendAndStateLock.unlock();
        }
    }

    protected boolean handleEventForSessionBased(StateEvent event) throws InternalException {
        try {
            sendAndStateLock.lock();
            final ClientSWmSessionState state = this.sessionData.getClientSWmSessionState();
            Event localEvent = (Event) event;
            Event.Type eventType = (Event.Type) localEvent.getType();
            switch (state) {
                case IDLE:
                    switch (eventType) {
                        /*
                        case SEND_AAR:
                            // Current State: IDLE
                            // Event: Client or device requests access/service
                            // Action: Send AAR
                            // New State: PENDING_AAR
                            setState(ClientRxSessionState.PENDING_AAR);
                            try {
                                dispatchEvent(localEvent.getRequest());
                            }
                            catch (Exception e) {
                                // This handles failure to send in PendingI state in FSM table
                                handleSendFailure(e, eventType, localEvent.getRequest().getMessage());
                            }
                            break;

                         */
                        default:
                            logger.warn("Session Based Handling - Wrong event type ({}) on state {}", eventType, state);
                            break;
                    }
                    break;
                    /*
                    case PENDING_AAR:
                    AppAnswerEvent answer = (AppAnswerEvent) localEvent.getAnswer();
                    switch (eventType) {
                        case RECEIVE_AAA:
                            long resultCode = answer.getResultCodeAvp().getUnsigned32();
                            if (isSuccess(resultCode)) {
                                // Current State: PENDING_AAR
                                // Event: Successful AA answer received
                                // New State: OPEN
                                setState(ClientRxSessionState.OPEN);
                            }
                            else if (isProvisional(resultCode) || isFailure(resultCode)) {
                                handleFailureMessage(answer, (AppRequestEvent) localEvent.getRequest(), eventType);
                            }
                            deliverRxAAAnswer((RxAARequest) localEvent.getRequest(), (RxAAAnswer) localEvent.getAnswer());
                            break;
                        case SEND_AAR:
                        case SEND_STR:
                            // Current State: PENDING_AAR
                            // Event: User service terminated
                            // Action: Queue termination event
                            // New State: PENDING_AAR

                            // Current State: PENDING_AAR
                            // Event: Change in request
                            // Action: Queue changed rating condition event
                            // New State: PENDING_AAR
                            eventQueue.add(localEvent);
                            break;
                        case RECEIVE_RAR:
                            deliverReAuthRequest((RxReAuthRequest) localEvent.getRequest());
                            break;
                        case SEND_RAA:
                            // Current State: PENDING_U
                            // Event: RAR received
                            // Action: Send RAA
                            // New State: PENDING_U
                            try {
                                dispatchEvent(localEvent.getAnswer());
                            }
                            catch (Exception e) {
                                handleSendFailure(e, eventType, localEvent.getRequest().getMessage());
                            }
                            break;
                        case RECEIVE_ASR:
                            deliverAbortSessionRequest((RxAbortSessionRequest) localEvent.getRequest());
                            break;
                        case SEND_ASA:
                            try {
                                dispatchEvent(localEvent.getAnswer());
                            }
                            catch (Exception e) {
                                handleSendFailure(e, eventType, localEvent.getRequest().getMessage());
                            }
                            break;
                        default:
                            logger.warn("Session Based Handling - Wrong event type ({}) on state {}", eventType, state);
                            break;
                    }
                    break;
                     */

                /*
                case PENDING_STR:
                    AppAnswerEvent stanswer = (AppAnswerEvent) localEvent.getAnswer();
                    switch (eventType) {
                        case RECEIVE_STA:
                            long resultCode = stanswer.getResultCodeAvp().getUnsigned32();
                            if (isSuccess(resultCode)) {
                                // Current State: PENDING_STR
                                // Event: Successful ST answer received
                                // New State: IDLE
                                setState(ClientRxSessionState.IDLE, false);
                            }
                            else if (isProvisional(resultCode) || isFailure(resultCode)) {
                                handleFailureMessage(stanswer, (AppRequestEvent) localEvent.getRequest(), eventType);
                            }
                            deliverRxSessionTermAnswer((RxSessionTermRequest) localEvent.getRequest(), (RxSessionTermAnswer) localEvent.getAnswer());
                            break;
                        case SEND_AAR:
                            try {
                                // Current State: PENDING_STR
                                // Event: Change in AA request
                                // Action: -
                                // New State: PENDING_STR
                                dispatchEvent(localEvent.getRequest());
                                // No transition
                            }
                            catch (Exception e) {
                                // This handles failure to send in PendingI state in FSM table
                                // handleSendFailure(e, eventType);
                            }
                            break;
                        //                        case RECEIVE_STA:
                        //                            // Current State: PENDING_T
                        //                            // Event: Successful CC termination answer received
                        //                            // Action: -
                        //                            // New State: IDLE
                        //
                        //                            // Current State: PENDING_T
                        //                            // Event: Failure to send, temporary error, or failed answer
                        //                            // Action: -
                        //                            // New State: IDLE
                        //
                        //                            //FIXME: Alex broke this, setting back "true" ?
                        //                            setState(ClientRxSessionState.IDLE, false);
                        //                            //setState(ClientRxSessionState.IDLE, true);
                        //                            deliverRxSessionTermAnswer((RxSessionTermRequest) localEvent.getRequest(), (RxSessionTermAnswer) localEvent.getAnswer());
                        //                            //setState(ClientRxSessionState.IDLE, true);
                        //                            break;
                        default:
                            logger.warn("Session Based Handling - Wrong event type ({}) on state {}", eventType, state);
                            break;
                    }
                    break;

                 */
                case OPEN:
                    switch (eventType) {
                        /*
                        case SEND_AAR:
                            // Current State: OPEN
                            // Event: Updated AAR send by AF
                            // Action: Send AAR update request
                            // New State: PENDING_AAR

                            setState(ClientRxSessionState.PENDING_AAR);
                            try {
                                dispatchEvent(localEvent.getRequest());
                            }
                            catch (Exception e) {
                                // This handles failure to send in PendingI state in FSM table
                                handleSendFailure(e, eventType, localEvent.getRequest().getMessage());
                            }
                            break;

                         */
                        /*
                        case SEND_STR:
                            // Current State: OPEN
                            // Event: Session Termination event request received to be sent
                            // Action: Terminate end user's service, send STR termination request
                            // New State: PENDING STR

                            setState(ClientRxSessionState.PENDING_STR);
                            try {
                                dispatchEvent(localEvent.getRequest());
                            }
                            catch (Exception e) {
                                handleSendFailure(e, eventType, localEvent.getRequest().getMessage());
                            }
                            break;

                         */
                        /*
                        case RECEIVE_RAR:
                            deliverReAuthRequest((RxReAuthRequest) localEvent.getRequest());
                            break;
                        case SEND_RAA:
                            try {
                                dispatchEvent(localEvent.getAnswer());
                            }
                            catch (Exception e) {
                                handleSendFailure(e, eventType, localEvent.getRequest().getMessage());
                            }
                            break;


                         */
                        case RECEIVE_ASR:
                            deliverAbortSessionRequest((SWmAbortSessionRequest) localEvent.getRequest());
                            break;
                        case SEND_ASA:
                            try {
                                dispatchEvent(localEvent.getAnswer());
                            }
                            catch (Exception e) {
                                handleSendFailure(e, eventType, localEvent.getRequest().getMessage());
                            }
                            break;
                        default:
                            logger.warn("Session Based Handling - Wrong event type ({}) on state {}", eventType, state);
                            break;
                    }
                    break;
                default:
                    // any other state is bad
                    setState(ClientSWmSessionState.IDLE, true);
                    break;
            }
            dispatch();
            return true;
        }
        catch (Exception e) {
            throw new InternalException(e);
        }
        finally {
            sendAndStateLock.unlock();
        }
    }
    @Override
    public <E> E getState(Class<E> stateType) {
        return null;
    }



    @Override
    public void onTimer(String timerName) {

    }

    @Override
    public void sendDiameterEAPRequest(SWmDiameterEAPRequest request) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
        this.handleEvent(new Event(Event.Type.SEND_DER, request, null));
    }

    @Override
    public void sendAbortSessionAnswer(SWmAbortSessionAnswer answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
        this.handleEvent(new Event(Event.Type.SEND_ASA, null, answer));
    }

    protected void deliverAbortSessionRequest(SWmAbortSessionRequest request) {
        try {
            listener.doAbortSessionRequest(this, request);
        }
        catch (Exception e) {
            logger.debug("Failure delivering ASR", e);
        }
    }
}
