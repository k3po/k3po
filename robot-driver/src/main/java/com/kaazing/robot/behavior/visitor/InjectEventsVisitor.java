/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package com.kaazing.robot.behavior.visitor;

import static java.lang.String.format;

import java.util.List;

import com.kaazing.robot.behavior.visitor.InjectEventsVisitor.State;
import com.kaazing.robot.lang.LocationInfo;
import com.kaazing.robot.lang.ast.AstAcceptNode;
import com.kaazing.robot.lang.ast.AstAcceptableNode;
import com.kaazing.robot.lang.ast.AstBoundNode;
import com.kaazing.robot.lang.ast.AstChildClosedNode;
import com.kaazing.robot.lang.ast.AstChildOpenedNode;
import com.kaazing.robot.lang.ast.AstCloseHttpRequestNode;
import com.kaazing.robot.lang.ast.AstCloseHttpResponseNode;
import com.kaazing.robot.lang.ast.AstCloseNode;
import com.kaazing.robot.lang.ast.AstClosedNode;
import com.kaazing.robot.lang.ast.AstConnectNode;
import com.kaazing.robot.lang.ast.AstConnectedNode;
import com.kaazing.robot.lang.ast.AstDisconnectNode;
import com.kaazing.robot.lang.ast.AstDisconnectedNode;
import com.kaazing.robot.lang.ast.AstEndOfHttpHeadersNode;
import com.kaazing.robot.lang.ast.AstNode;
import com.kaazing.robot.lang.ast.AstOpenedNode;
import com.kaazing.robot.lang.ast.AstReadAwaitNode;
import com.kaazing.robot.lang.ast.AstReadHttpHeaderNode;
import com.kaazing.robot.lang.ast.AstReadHttpMethodNode;
import com.kaazing.robot.lang.ast.AstReadHttpParameterNode;
import com.kaazing.robot.lang.ast.AstReadHttpStatusNode;
import com.kaazing.robot.lang.ast.AstReadHttpVersionNode;
import com.kaazing.robot.lang.ast.AstReadNotifyNode;
import com.kaazing.robot.lang.ast.AstReadValueNode;
import com.kaazing.robot.lang.ast.AstScriptNode;
import com.kaazing.robot.lang.ast.AstStreamNode;
import com.kaazing.robot.lang.ast.AstStreamableNode;
import com.kaazing.robot.lang.ast.AstUnbindNode;
import com.kaazing.robot.lang.ast.AstUnboundNode;
import com.kaazing.robot.lang.ast.AstWriteAwaitNode;
import com.kaazing.robot.lang.ast.AstWriteHttpContentLengthNode;
import com.kaazing.robot.lang.ast.AstWriteHttpHeaderNode;
import com.kaazing.robot.lang.ast.AstWriteHttpMethodNode;
import com.kaazing.robot.lang.ast.AstWriteHttpParameterNode;
import com.kaazing.robot.lang.ast.AstWriteHttpStatusNode;
import com.kaazing.robot.lang.ast.AstWriteHttpVersionNode;
import com.kaazing.robot.lang.ast.AstWriteNotifyNode;
import com.kaazing.robot.lang.ast.AstWriteValueNode;

public class InjectEventsVisitor implements AstNode.Visitor<AstScriptNode, State> {

    public static enum ConnectivityState {
        NONE, OPENED, BOUND, CONNECTED, DISCONNECTED, UNBOUND, CLOSED
    }

    public static final class State {
        private List<AstStreamNode> streams;
        private List<AstStreamableNode> streamables;
        private ConnectivityState connectivityState;
        private LocationInfo            lastLocationInfo;
        private boolean isStraightTcp;

        /**
         * Check and make sure you have a valid connectivity state after
         * visiting.
         *
         * @throws Exception
         */
        public void finish() throws Exception {

            if (isStraightTcp && connectivityState != null && !connectivityState.equals(ConnectivityState.CLOSED)) {
                throw new IllegalStateException(format("Unexpected %s without subsequent closed",
                        connectivityState.name().toLowerCase()));
            }
        }
    }

    @Override
    public AstScriptNode visit(AstScriptNode script, State state) throws Exception {

        AstScriptNode newScript = new AstScriptNode();
        newScript.setLocationInfo(script.getLocationInfo());

        state.streams = newScript.getStreams();

        for (AstStreamNode stream : script.getStreams()) {
            stream.accept(this, state);
        }

        return newScript;
    }

    @Override
    public AstScriptNode visit(AstAcceptNode acceptNode, State state) throws Exception {

        state.connectivityState = ConnectivityState.NONE;

        AstAcceptNode newAcceptNode = new AstAcceptNode();
        newAcceptNode.setLocationInfo(acceptNode.getLocationInfo());
        newAcceptNode.setAcceptName(acceptNode.getAcceptName());
        newAcceptNode.setLocation(acceptNode.getLocation());
        state.lastLocationInfo = acceptNode.getLocationInfo();

        if (acceptNode.getLocation() != null && acceptNode.getLocation().getScheme().equalsIgnoreCase("tcp")) {
            state.isStraightTcp = true;
        }

        state.streamables = newAcceptNode.getStreamables();
        for (AstStreamableNode streamable : acceptNode.getStreamables()) {
            streamable.accept(this, state);
        }

        for (AstAcceptableNode acceptable : acceptNode.getAcceptables()) {
            acceptable.accept(this, state);
        }

        state.streams.add(newAcceptNode);

        return null;
    }

    @Override
    public AstScriptNode visit(AstAcceptableNode acceptableNode, State state) throws Exception {

        state.connectivityState = ConnectivityState.NONE;

        AstAcceptableNode newAcceptableNode = new AstAcceptableNode();
        newAcceptableNode.setLocationInfo(acceptableNode.getLocationInfo());
        newAcceptableNode.setAcceptName(acceptableNode.getAcceptName());
        state.lastLocationInfo = acceptableNode.getLocationInfo();

        state.streamables = newAcceptableNode.getStreamables();
        for (AstStreamableNode streamable : acceptableNode.getStreamables()) {
            streamable.accept(this, state);
        }
        state.lastLocationInfo = acceptableNode.getLocationInfo();

        state.streams.add(newAcceptableNode);

        return null;
    }

    @Override
    public AstScriptNode visit(AstConnectNode connectNode, State state) throws Exception {

        if (state.connectivityState != null
                && !(state.connectivityState.equals(ConnectivityState.NONE) || state.connectivityState
                        .equals(ConnectivityState.CLOSED))) {
            throw new IllegalStateException("Unexpected connect before closed");
        }

        state.connectivityState = ConnectivityState.NONE;

        if (connectNode.getLocation() != null && connectNode.getLocation().getScheme().equalsIgnoreCase("tcp")) {
            state.isStraightTcp = true;
        }

        AstConnectNode newConnectNode = new AstConnectNode();
        newConnectNode.setLocationInfo(connectNode.getLocationInfo());
        newConnectNode.setLocation(connectNode.getLocation());
        state.lastLocationInfo = connectNode.getLocationInfo();

        state.streamables = newConnectNode.getStreamables();
        for (AstStreamableNode streamable : connectNode.getStreamables()) {
            streamable.accept(this, state);
        }

        state.lastLocationInfo = connectNode.getLocationInfo();
        state.streams.add(newConnectNode);

        return null;
    }

    @Override
    public AstScriptNode visit(AstReadAwaitNode node, State state) throws Exception {
        state.lastLocationInfo = node.getLocationInfo();
        state.streamables.add(node);
        return null;
    }

    @Override
    public AstScriptNode visit(AstWriteAwaitNode node, State state) throws Exception {
        state.lastLocationInfo = node.getLocationInfo();
        state.streamables.add(node);
        return null;
    }

    @Override
    public AstScriptNode visit(AstReadNotifyNode node, State state) throws Exception {
        state.lastLocationInfo = node.getLocationInfo();
        state.streamables.add(node);
        return null;
    }

    @Override
    public AstScriptNode visit(AstWriteNotifyNode node, State state) throws Exception {
        state.lastLocationInfo = node.getLocationInfo();
        state.streamables.add(node);
        return null;
    }

    @Override
    public AstScriptNode visit(AstWriteValueNode node, State state) throws Exception {

        switch (state.connectivityState) {
        case CONNECTED:
            state.lastLocationInfo = node.getLocationInfo();
            state.streamables.add(node);
            break;

        default:
            throw new IllegalStateException("Unexpected write before connected");
        }

        return null;
    }

    @Override
    public AstScriptNode visit(AstDisconnectNode node, State state) throws Exception {

        switch (state.connectivityState) {
        case CONNECTED:
                state.lastLocationInfo = node.getLocationInfo();
            state.streamables.add(node);
            break;

        default:
            throw new IllegalStateException("Unexpected disconnect before connected");
        }

        return null;
    }

    @Override
    public AstScriptNode visit(AstUnbindNode node, State state) throws Exception {

        switch (state.connectivityState) {
        case DISCONNECTED:
                state.lastLocationInfo = node.getLocationInfo();
            state.streamables.add(node);
            break;

        default:
            throw new IllegalStateException("Unexpected unbind before disconnected");
        }

        return null;
    }

    @Override
    public AstScriptNode visit(AstCloseNode node, State state) throws Exception {

        switch (state.connectivityState) {
        case CONNECTED:
                state.lastLocationInfo = node.getLocationInfo();
            state.streamables.add(node);
            break;

        default:
            throw new IllegalStateException("Unexpected close before connected");
        }

        return null;
    }

    @Override
    public AstScriptNode visit(AstChildOpenedNode childOpenedNode, State state) throws Exception {
        state.lastLocationInfo = childOpenedNode.getLocationInfo();
        state.streamables.add(childOpenedNode);
        return null;
    }

    @Override
    public AstScriptNode visit(AstChildClosedNode childClosedNode, State state) throws Exception {
        state.lastLocationInfo = childClosedNode.getLocationInfo();
        state.streamables.add(childClosedNode);
        return null;
    }

    @Override
    public AstScriptNode visit(AstOpenedNode openedNode, State state) throws Exception {

        switch (state.connectivityState) {
        case NONE:
                state.lastLocationInfo = openedNode.getLocationInfo();
                state.connectivityState = ConnectivityState.OPENED;
                state.streamables.add(openedNode);
                break;

        default:
            throw new IllegalStateException("Unexpected event: opened");
        }

        return null;
    }

    @Override
    public AstScriptNode visit(AstBoundNode boundNode, State state) throws Exception {

        switch (state.connectivityState) {
        case NONE:
                AstOpenedNode openedNode = new AstOpenedNode();
                openedNode.setLocationInfo(state.lastLocationInfo);
                openedNode.accept(this, state);
            break;
        }

        // The above switch might have changed the connectivity state, so
        // we switch on it again
        switch (state.connectivityState) {
        case OPENED:
                state.lastLocationInfo = boundNode.getLocationInfo();
                state.streamables.add(boundNode);
                state.connectivityState = ConnectivityState.BOUND;
            break;

        default:
            throw new IllegalStateException("Unexpected event: bound");
        }

        return null;
    }

    @Override
    public AstScriptNode visit(AstConnectedNode connectedNode, State state) throws Exception {

        switch (state.connectivityState) {
            case NONE:
            case OPENED:
                AstBoundNode boundNode = new AstBoundNode();
                boundNode.setLocationInfo(state.lastLocationInfo);
                boundNode.accept(this, state);
                break;
        }

        // The above switch might have changed the connectivity state, so
        // we switch on it again
        switch (state.connectivityState) {
            case BOUND:
                state.lastLocationInfo = connectedNode.getLocationInfo();
                state.streamables.add(connectedNode);
                state.connectivityState = ConnectivityState.CONNECTED;
                break;

        default:
            throw new IllegalStateException("Unexpected event: connected");
        }

        return null;
    }

    @Override
    public AstScriptNode visit(AstReadValueNode node, State state) throws Exception {

        switch (state.connectivityState) {
            case CONNECTED:
                state.lastLocationInfo = node.getLocationInfo();
                state.streamables.add(node);
            break;

        default:
            throw new IllegalStateException("Unexpected read before connected");
        }

        return null;
    }

    @Override
    public AstScriptNode visit(AstDisconnectedNode disconnectedNode, State state) throws Exception {

        switch (state.connectivityState) {
        case CONNECTED:
                state.lastLocationInfo = disconnectedNode.getLocationInfo();
                state.streamables.add(disconnectedNode);
                state.connectivityState = ConnectivityState.DISCONNECTED;
                break;

        default:
            throw new IllegalStateException("Unexpected event: disconnected");
        }

        return null;
    }

    @Override
    public AstScriptNode visit(AstUnboundNode unboundNode, State state) throws Exception {

        switch (state.connectivityState) {
        case CONNECTED:
                AstDisconnectedNode disconnectedNode = new AstDisconnectedNode();
                disconnectedNode.setLocationInfo(state.lastLocationInfo);
                disconnectedNode.accept(this, state);
            break;
        }

        switch (state.connectivityState) {
        case DISCONNECTED:
                state.lastLocationInfo = unboundNode.getLocationInfo();
                state.streamables.add(unboundNode);
                state.connectivityState = ConnectivityState.UNBOUND;
            break;

        default:
            throw new IllegalStateException("Unexpected event: unbound");
        }

        return null;
    }

    @Override
    public AstScriptNode visit(AstClosedNode closedNode, State state) throws Exception {

        switch (state.connectivityState) {
        case CONNECTED:
        case DISCONNECTED:
                AstUnboundNode unboundNode = new AstUnboundNode();
                unboundNode.setLocationInfo(state.lastLocationInfo);
                unboundNode.accept(this, state);
            break;
        }

        switch (state.connectivityState) {
        case UNBOUND:
                state.lastLocationInfo = closedNode.getLocationInfo();
                state.streamables.add(closedNode);
                state.connectivityState = ConnectivityState.CLOSED;
            break;

        default:
            throw new IllegalStateException("Unexpected event: closed");
        }

        return null;
    }

    @Override
    public AstScriptNode visit(AstReadHttpHeaderNode node, State state) throws Exception {
        inTcpConnectedState(node, state);
        return null;
    }

    @Override
    public AstScriptNode visit(AstWriteHttpHeaderNode node, State state) throws Exception {
        inTcpConnectedState(node, state);
        return null;
    }

    @Override
    public AstScriptNode visit(AstWriteHttpContentLengthNode node, State state) throws Exception {
        inTcpConnectedState(node, state);
        return null;
    }

    @Override
    public AstScriptNode visit(AstReadHttpMethodNode node, State state) throws Exception {
        inTcpConnectedState(node, state);
        return null;
    }

    @Override
    public AstScriptNode visit(AstWriteHttpMethodNode node, State state) throws Exception {
        inTcpConnectedState(node, state);
        return null;
    }

    @Override
    public AstScriptNode visit(AstReadHttpParameterNode node, State state) throws Exception {
        inTcpConnectedState(node, state);
        return null;
    }

    @Override
    public AstScriptNode visit(AstWriteHttpParameterNode node, State state) throws Exception {
        inTcpConnectedState(node, state);
        return null;
    }

    @Override
    public AstScriptNode visit(AstReadHttpVersionNode node, State state) throws Exception {
        inTcpConnectedState(node, state);
        return null;
    }

    @Override
    public AstScriptNode visit(AstWriteHttpVersionNode node, State state) throws Exception {
        inTcpConnectedState(node, state);
        return null;
    }

    @Override
    public AstScriptNode visit(AstReadHttpStatusNode node, State state) throws Exception {
        inTcpConnectedState(node, state);
        return null;
    }

    @Override
    public AstScriptNode visit(AstWriteHttpStatusNode node, State state) throws Exception {
        inTcpConnectedState(node, state);
        return null;
    }

    @Override
    public AstScriptNode visit(AstCloseHttpRequestNode node, State state) throws Exception {
        inTcpConnectedState(node, state);
        return null;
    }

    @Override
    public AstScriptNode visit(AstCloseHttpResponseNode node, State state) throws Exception {
        inTcpConnectedState(node, state);
        return null;
    }

    @Override
    public AstScriptNode visit(AstEndOfHttpHeadersNode node, State state) throws Exception {
        inTcpConnectedState(node, state);
        return null;
    }

    /**
     * Guarantees that it is in the connected state, if not it must throw an Exception
     * @return
     * @throws Exception
     */
    private void inTcpConnectedState(AstStreamableNode node, State state) throws Exception {
        switch (state.connectivityState) {
        case CONNECTED:
            break;
        default:
            throw new IllegalStateException(String.format("Unexpected \"%s\" before connected", node));
        }
        state.lastLocationInfo = node.getLocationInfo();
        state.streamables.add(node);
    }

}
