package org.jdiameter.common.impl.app.swm;

import org.jdiameter.api.Answer;
import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.Message;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.Request;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppRequestEvent;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.app.StateChangeListener;
import org.jdiameter.api.swm.ClientSWmSession;
import org.jdiameter.api.swm.ClientSWmSessionListener;
import org.jdiameter.api.swm.ServerSWmSession;
import org.jdiameter.api.swm.ServerSWmSessionListener;
import org.jdiameter.api.swm.events.SWmAbortSessionAnswer;
import org.jdiameter.api.swm.events.SWmAbortSessionRequest;
import org.jdiameter.api.swm.events.SWmDiameterEAPAnswer;
import org.jdiameter.api.swm.events.SWmDiameterEAPRequest;
import org.jdiameter.common.api.app.swm.IClientSWmSessionContext;
import org.jdiameter.common.api.app.swm.ISWmMessageFactory;
import org.jdiameter.common.api.app.swm.ISWmSessionFactory;
import org.jdiameter.common.api.app.swm.IServerSWmSessionContext;

import java.util.concurrent.ScheduledFuture;

public class SWmSessionFactoryImpl implements ISWmSessionFactory, ClientSWmSessionListener, ServerSWmSessionListener, StateChangeListener<AppSession>,
        ISWmMessageFactory, IServerSWmSessionContext, IClientSWmSessionContext {

    @Override
    public void doDiameterEAPAnswer(ClientSWmSession session, SWmDiameterEAPRequest request, SWmDiameterEAPAnswer answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {

    }

    @Override
    public void doAbortSessionRequest(ClientSWmSession session, SWmAbortSessionRequest request) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {

    }

    @Override
    public void doDiameterEAPRequest(ServerSWmSession session, SWmDiameterEAPRequest request) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {

    }

    @Override
    public void doAbortSessionAnswer(ServerSWmSession session, SWmAbortSessionRequest request, SWmAbortSessionAnswer answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {

    }

    @Override
    public void doOtherEvent(AppSession session, AppRequestEvent request, AppAnswerEvent answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {

    }

    @Override
    public AppSession getNewSession(String sessionId, Class<? extends AppSession> aClass, ApplicationId applicationId, Object[] args) {
        return null;
    }

    @Override
    public AppSession getSession(String sessionId, Class<? extends AppSession> aClass) {
        return null;
    }

    @Override
    public SWmAbortSessionRequest createAbortSessionRequest(Request request) {
        return null;
    }

    @Override
    public SWmAbortSessionAnswer createAbortSessionAnswer(Answer answer) {
        return null;
    }

    @Override
    public SWmDiameterEAPRequest createDiameterEAPRequest(Request request) {
        return null;
    }

    @Override
    public SWmDiameterEAPAnswer createDiameterEAPAnswer(Answer answer) {
        return null;
    }

    @Override
    public long[] getApplicationIds() {
        return new long[0];
    }

    @Override
    public ClientSWmSessionListener getClientSessionListener() {
        return null;
    }

    @Override
    public void setClientSessionListener(ClientSWmSessionListener clientSessionListener) {

    }

    @Override
    public ServerSWmSessionListener getServerSessionListener() {
        return null;
    }

    @Override
    public void setServerSessionListener(ServerSWmSessionListener serverSessionListener) {

    }

    @Override
    public IServerSWmSessionContext getServerContextListener() {
        return null;
    }

    @Override
    public void setServerContextListener(IServerSWmSessionContext serverContextListener) {

    }

    @Override
    public IClientSWmSessionContext getClientContextListener() {
        return null;
    }

    @Override
    public void setClientContextListener(IClientSWmSessionContext clientContextListener) {

    }

    @Override
    public ISWmMessageFactory getMessageFactory() {
        return null;
    }

    @Override
    public void setMessageFactory(ISWmMessageFactory messageFactory) {

    }

    @Override
    public StateChangeListener<AppSession> getStateListener() {
        return null;
    }

    @Override
    public void setStateListener(StateChangeListener<AppSession> stateListener) {

    }

    @Override
    public void stateChanged(Enum oldState, Enum newState) {

    }

    @Override
    public void stateChanged(AppSession source, Enum oldState, Enum newState) {

    }

    @Override
    public void grantAccessOnDeliverFailure(ClientSWmSession clientCCASessionImpl, Message request) {

    }

    @Override
    public void denyAccessOnDeliverFailure(ClientSWmSession clientCCASessionImpl, Message request) {

    }

    @Override
    public void grantAccessOnFailureMessage(ClientSWmSession clientCCASessionImpl) {

    }

    @Override
    public void denyAccessOnFailureMessage(ClientSWmSession clientCCASessionImpl) {

    }

    @Override
    public void indicateServiceError(ClientSWmSession clientCCASessionImpl) {

    }

    @Override
    public void sessionSupervisionTimerExpired(ServerSWmSession session) {

    }

    @Override
    public void sessionSupervisionTimerStarted(ServerSWmSession session, ScheduledFuture future) {

    }

    @Override
    public void sessionSupervisionTimerReStarted(ServerSWmSession session, ScheduledFuture future) {

    }

    @Override
    public void sessionSupervisionTimerStopped(ServerSWmSession session, ScheduledFuture future) {

    }
}
