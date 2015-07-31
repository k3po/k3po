/*
 * Copyright 2014, Kaazing Corporation. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kaazing.k3po.driver.internal.behavior.visitor;

import java.util.List;

import org.kaazing.k3po.driver.internal.behavior.visitor.InjectEventsVisitor.State;
import org.kaazing.k3po.lang.internal.ast.AstAcceptNode;
import org.kaazing.k3po.lang.internal.ast.AstAcceptableNode;
import org.kaazing.k3po.lang.internal.ast.AstBoundNode;
import org.kaazing.k3po.lang.internal.ast.AstChildClosedNode;
import org.kaazing.k3po.lang.internal.ast.AstChildOpenedNode;
import org.kaazing.k3po.lang.internal.ast.AstCloseNode;
import org.kaazing.k3po.lang.internal.ast.AstClosedNode;
import org.kaazing.k3po.lang.internal.ast.AstConnectNode;
import org.kaazing.k3po.lang.internal.ast.AstConnectedNode;
import org.kaazing.k3po.lang.internal.ast.AstDisconnectNode;
import org.kaazing.k3po.lang.internal.ast.AstDisconnectedNode;
import org.kaazing.k3po.lang.internal.ast.AstNode;
import org.kaazing.k3po.lang.internal.ast.AstOpenedNode;
import org.kaazing.k3po.lang.internal.ast.AstPropertyNode;
import org.kaazing.k3po.lang.internal.ast.AstReadAwaitNode;
import org.kaazing.k3po.lang.internal.ast.AstReadClosedNode;
import org.kaazing.k3po.lang.internal.ast.AstReadConfigNode;
import org.kaazing.k3po.lang.internal.ast.AstReadNotifyNode;
import org.kaazing.k3po.lang.internal.ast.AstReadOptionNode;
import org.kaazing.k3po.lang.internal.ast.AstReadValueNode;
import org.kaazing.k3po.lang.internal.ast.AstScriptNode;
import org.kaazing.k3po.lang.internal.ast.AstStreamNode;
import org.kaazing.k3po.lang.internal.ast.AstStreamableNode;
import org.kaazing.k3po.lang.internal.ast.AstUnbindNode;
import org.kaazing.k3po.lang.internal.ast.AstUnboundNode;
import org.kaazing.k3po.lang.internal.ast.AstWriteAwaitNode;
import org.kaazing.k3po.lang.internal.ast.AstWriteCloseNode;
import org.kaazing.k3po.lang.internal.ast.AstWriteConfigNode;
import org.kaazing.k3po.lang.internal.ast.AstWriteFlushNode;
import org.kaazing.k3po.lang.internal.ast.AstWriteNotifyNode;
import org.kaazing.k3po.lang.internal.ast.AstWriteOptionNode;
import org.kaazing.k3po.lang.internal.ast.AstWriteValueNode;

public class InjectEventsVisitor implements AstNode.Visitor<AstScriptNode, State> {

    public static enum ConnectivityState {
        NONE, OPENED, BOUND, CONNECTED, DISCONNECTED, UNBOUND, CLOSED
    }

    public static final class State {
        private List<AstStreamNode> streams;
        private List<AstStreamableNode> streamables;
        private ConnectivityState connectivityState;
    }

    @Override
    public AstScriptNode visit(AstScriptNode script, State state) throws Exception {

        AstScriptNode newScript = new AstScriptNode();
        newScript.setRegionInfo(script.getRegionInfo());
        newScript.getProperties().addAll(script.getProperties());

        state.streams = newScript.getStreams();

        for (AstStreamNode stream : script.getStreams()) {
            stream.accept(this, state);
        }

        return newScript;
    }

    @Override
    public AstScriptNode visit(AstPropertyNode propertyNode, State state) throws Exception {
        return null;
    }

    @Override
    public AstScriptNode visit(AstAcceptNode acceptNode, State state) throws Exception {

        state.connectivityState = ConnectivityState.NONE;

        AstAcceptNode newAcceptNode = new AstAcceptNode(acceptNode);

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
        newAcceptableNode.setRegionInfo(acceptableNode.getRegionInfo());
        newAcceptableNode.setAcceptName(acceptableNode.getAcceptName());

        state.streamables = newAcceptableNode.getStreamables();
        for (AstStreamableNode streamable : acceptableNode.getStreamables()) {
            streamable.accept(this, state);
        }

        state.streams.add(newAcceptableNode);

        return null;
    }

    @Override
    public AstScriptNode visit(AstConnectNode connectNode, State state) throws Exception {

        state.connectivityState = ConnectivityState.NONE;

        AstConnectNode newConnectNode = new AstConnectNode(connectNode);

        state.streamables = newConnectNode.getStreamables();
        for (AstStreamableNode streamable : connectNode.getStreamables()) {
            streamable.accept(this, state);
        }

        state.streams.add(newConnectNode);

        return null;
    }

    @Override
    public AstScriptNode visit(AstReadAwaitNode node, State state) throws Exception {
        state.streamables.add(node);
        return null;
    }

    @Override
    public AstScriptNode visit(AstWriteAwaitNode node, State state) throws Exception {
        state.streamables.add(node);
        return null;
    }

    @Override
    public AstScriptNode visit(AstReadNotifyNode node, State state) throws Exception {
        state.streamables.add(node);
        return null;
    }

    @Override
    public AstScriptNode visit(AstWriteNotifyNode node, State state) throws Exception {
        state.streamables.add(node);
        return null;
    }

    @Override
    public AstScriptNode visit(AstWriteValueNode node, State state) throws Exception {

        switch (state.connectivityState) {
        case CONNECTED:
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
            state.streamables.add(node);
            break;

        default:
            throw new IllegalStateException("Unexpected close before connected");
        }

        return null;
    }

    @Override
    public AstScriptNode visit(AstChildOpenedNode childOpenedNode, State state) throws Exception {
        state.streamables.add(childOpenedNode);
        return null;
    }

    @Override
    public AstScriptNode visit(AstChildClosedNode childClosedNode, State state) throws Exception {
        state.streamables.add(childClosedNode);
        return null;
    }

    @Override
    public AstScriptNode visit(AstOpenedNode openedNode, State state) throws Exception {

        switch (state.connectivityState) {
        case NONE:
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
            openedNode.setRegionInfo(boundNode.getRegionInfo());
            openedNode.accept(this, state);
            break;
        default:
            break;
        }

        // The above switch might have changed the connectivity state, so
        // we switch on it again
        switch (state.connectivityState) {
        case OPENED:
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
            boundNode.setRegionInfo(connectedNode.getRegionInfo());
            boundNode.accept(this, state);
            break;
        default:
            break;
        }

        // The above switch might have changed the connectivity state, so
        // we switch on it again
        switch (state.connectivityState) {
        case BOUND:
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
            disconnectedNode.setRegionInfo(unboundNode.getRegionInfo());
            disconnectedNode.accept(this, state);
            break;
        default:
            break;
        }

        switch (state.connectivityState) {
        case DISCONNECTED:
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
            unboundNode.setRegionInfo(closedNode.getRegionInfo());
            unboundNode.accept(this, state);
            break;
        default:
            break;
        }

        switch (state.connectivityState) {
        case UNBOUND:
            state.streamables.add(closedNode);
            state.connectivityState = ConnectivityState.CLOSED;
            break;

        default:
            throw new IllegalStateException("Unexpected event: closed");
        }

        return null;
    }

    @Override
    public AstScriptNode visit(AstReadConfigNode node, State state) throws Exception {

        switch (state.connectivityState) {
        case CONNECTED:
            break;
        default:
            throw new IllegalStateException(String.format("Unexpected \"%s\" before connected", node));
        }
        state.streamables.add(node);

        return null;
    }

    @Override
    public AstScriptNode visit(AstWriteConfigNode node, State state) throws Exception {

        switch (state.connectivityState) {
        case CONNECTED:
            break;
        default:
            throw new IllegalStateException(String.format("Unexpected \"%s\" before connected", node));
        }
        state.streamables.add(node);

        return null;
    }

    @Override
    public AstScriptNode visit(AstReadClosedNode node, State state) throws Exception {

        switch (state.connectivityState) {
        case CONNECTED:
            break;
        default:
            throw new IllegalStateException(String.format("Unexpected \"%s\" before connected", node));
        }
        state.streamables.add(node);

        return null;
    }

    @Override
    public AstScriptNode visit(AstWriteCloseNode node, State state) throws Exception {

        switch (state.connectivityState) {
        case CONNECTED:
            break;
        default:
            throw new IllegalStateException(String.format("Unexpected \"%s\" before connected", node));
        }
        state.streamables.add(node);

        return null;
    }

    @Override
    public AstScriptNode visit(AstWriteFlushNode node, State state) throws Exception {

        switch (state.connectivityState) {
        case CONNECTED:
            break;
        default:
            throw new IllegalStateException(String.format("Unexpected \"%s\" before connected", node));
        }
        state.streamables.add(node);

        return null;
    }

    @Override
    public AstScriptNode visit(AstReadOptionNode node, State state) throws Exception {
        state.streamables.add(node);
        return null;
    }

    @Override
    public AstScriptNode visit(AstWriteOptionNode node, State state) throws Exception {
        state.streamables.add(node);
        return null;
    }

}
