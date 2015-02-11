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

package org.kaazing.k3po.driver.behavior.visitor;

import java.net.URI;
import java.util.List;

import org.kaazing.k3po.lang.ast.AstAcceptNode;
import org.kaazing.k3po.lang.ast.AstAcceptableNode;
import org.kaazing.k3po.lang.ast.AstBoundNode;
import org.kaazing.k3po.lang.ast.AstChildClosedNode;
import org.kaazing.k3po.lang.ast.AstChildOpenedNode;
import org.kaazing.k3po.lang.ast.AstCloseNode;
import org.kaazing.k3po.lang.ast.AstClosedNode;
import org.kaazing.k3po.lang.ast.AstConnectNode;
import org.kaazing.k3po.lang.ast.AstConnectedNode;
import org.kaazing.k3po.lang.ast.AstDisconnectNode;
import org.kaazing.k3po.lang.ast.AstDisconnectedNode;
import org.kaazing.k3po.lang.ast.AstWriteFlushNode;
import org.kaazing.k3po.lang.ast.AstNode;
import org.kaazing.k3po.lang.ast.AstOpenedNode;
import org.kaazing.k3po.lang.ast.AstPropertyNode;
import org.kaazing.k3po.lang.ast.AstReadAwaitNode;
import org.kaazing.k3po.lang.ast.AstReadClosedNode;
import org.kaazing.k3po.lang.ast.AstReadConfigNode;
import org.kaazing.k3po.lang.ast.AstReadNotifyNode;
import org.kaazing.k3po.lang.ast.AstReadOptionNode;
import org.kaazing.k3po.lang.ast.AstReadValueNode;
import org.kaazing.k3po.lang.ast.AstScriptNode;
import org.kaazing.k3po.lang.ast.AstStreamNode;
import org.kaazing.k3po.lang.ast.AstStreamableNode;
import org.kaazing.k3po.lang.ast.AstUnbindNode;
import org.kaazing.k3po.lang.ast.AstUnboundNode;
import org.kaazing.k3po.lang.ast.AstWriteAwaitNode;
import org.kaazing.k3po.lang.ast.AstWriteCloseNode;
import org.kaazing.k3po.lang.ast.AstWriteConfigNode;
import org.kaazing.k3po.lang.ast.AstWriteNotifyNode;
import org.kaazing.k3po.lang.ast.AstWriteOptionNode;
import org.kaazing.k3po.lang.ast.AstWriteValueNode;

// Note: this is no longer injecting, just validating, as injection is now generalized
public class InjectHttpStreamsVisitor implements AstNode.Visitor<AstScriptNode, InjectHttpStreamsVisitor.State> {

    // READ_OPEN  -> [READ_START  -> READ_HEADERS_COMPLETE  -> READ_CONTENT_COMPLETE]  -> READ_CLOSED
    // WRITE_OPEN -> [WRITE_START -> WRITE_HEADERS_COMPLETE -> WRITE_CONTENT_COMPLETE] -> WRITE_CLOSED
    public static enum StreamState {
        // @formatter:off
        OPEN,
        REQUEST,
        RESPONSE,
        HEADERS_COMPLETE,
        CONTENT_COMPLETE,
        CLOSED,
        // @formatter:on
    }

    // TODO: validate content strategies
    public static enum HttpContentType {
        // @formatter:off
        NONE("none"),
        ONE_ZERO_ONE("101 Upgrade"),
        CONNECTION_CLOSE("connection: close"),
        CHUNKED ("transfer-encoding: chunked"),
        CONTENT_LENGTH ("content-length");
        // @formatter:on
        private HttpContentType(final String readableState) {
            this.readableState = readableState;
        }

        private final String readableState;

        @Override
        public String toString() {
            return readableState;
        }
    }

    public static final class State {
        private List<AstStreamNode> streams;
        private List<AstStreamableNode> streamables;
        private StreamState readState;
        private StreamState writeState;
        public List<AstAcceptableNode> acceptables;

        public State() {
            readState = StreamState.OPEN;
            writeState = StreamState.OPEN;
        }
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

        AstAcceptNode newAcceptNode = new AstAcceptNode();
        newAcceptNode.setRegionInfo(acceptNode.getRegionInfo());
        newAcceptNode.setAcceptName(acceptNode.getAcceptName());
        newAcceptNode.setLocation(acceptNode.getLocation());

        state.streamables = newAcceptNode.getStreamables();
        state.streams.add(newAcceptNode);

        for (AstStreamableNode streamable : acceptNode.getStreamables()) {
            streamable.accept(this, state);
        }

        state.acceptables = newAcceptNode.getAcceptables();
        for (AstAcceptableNode acceptable : acceptNode.getAcceptables()) {
            URI location = acceptNode.getLocation();
            if (location != null && "http".equals(location.getScheme())) {
                state.readState = StreamState.REQUEST;
                state.writeState = StreamState.RESPONSE;
            }
            else {
                state.readState = StreamState.OPEN;
                state.writeState = StreamState.OPEN;
            }

            acceptable.accept(this, state);

        }
        state.acceptables = null;

        return null;
    }

    @Override
    public AstScriptNode visit(AstConnectNode connectNode, State state) throws Exception {

        URI location = connectNode.getLocation();
        if (location != null && "http".equals(location.getScheme())) {
            state.writeState = StreamState.REQUEST;
            state.readState = StreamState.RESPONSE;
        }
        else {
            state.writeState = StreamState.OPEN;
            state.readState = StreamState.OPEN;
        }

        AstConnectNode newConnectNode = new AstConnectNode();
        newConnectNode.setRegionInfo(connectNode.getRegionInfo());
        newConnectNode.setLocation(connectNode.getLocation());

        state.streamables = newConnectNode.getStreamables();
        for (AstStreamableNode streamable : connectNode.getStreamables()) {
            streamable.accept(this, state);
        }

        state.streams.add(newConnectNode);

        return null;
    }

    @Override
    public AstScriptNode visit(AstReadConfigNode node, State state) throws Exception {

        switch (state.readState) {
        case REQUEST:
        case RESPONSE:
        case OPEN:
            break;
        default:
            throw new IllegalStateException(String.format("Unexpected read config event (%s) while reading in state %s", node
                    .toString().trim(), state.readState));
        }
        state.streamables.add(node);
        return null;
    }

    @Override
    public AstScriptNode visit(AstWriteConfigNode node, State state) throws Exception {

        switch (state.writeState) {
        case REQUEST:
        case RESPONSE:
        case OPEN:
            break;
        default:
            throw new IllegalStateException(String.format("Unexpected write config command (%s) while writing in state %s", node
                    .toString().trim(), state.writeState));
        }
        state.streamables.add(node);
        return null;
    }

    @Override
    public AstScriptNode visit(AstReadClosedNode node, State state) throws Exception {

        switch (state.readState) {
        case REQUEST:
        case RESPONSE:
        case HEADERS_COMPLETE:
        case CONTENT_COMPLETE:
            state.readState = StreamState.CLOSED;
            break;
        default:
            throw new IllegalStateException(unexpectedReadEvent(node, state));
        }
        state.streamables.add(node);
        return null;
    }

    @Override
    public AstScriptNode visit(AstWriteCloseNode node, State state) throws Exception {

        switch (state.writeState) {
        case REQUEST:
        case RESPONSE:
        case HEADERS_COMPLETE:
        case CONTENT_COMPLETE:
            state.writeState = StreamState.CLOSED;
            break;
        default:
            throw new IllegalStateException(unexpectedWriteEvent(node, state));
        }
        state.streamables.add(node);
        return null;
    }

    @Override
    public AstScriptNode visit(AstReadValueNode node, State state) throws Exception {

        switch (state.readState) {
        case OPEN:
            break;
        case REQUEST:
        case RESPONSE:
            // TODO: -> OPEN for Upgrade / 101 Switching Protocols
            state.readState = StreamState.HEADERS_COMPLETE;
            break;
        case HEADERS_COMPLETE:
            break;
        default:
            throw new IllegalStateException(unexpectedReadEvent(node, state));
        }
        state.streamables.add(node);
        return null;
    }

    @Override
    public AstScriptNode visit(AstWriteValueNode node, State state) throws Exception {

        switch (state.writeState) {
        case OPEN:
            break;
        case REQUEST:
        case RESPONSE:
            // TODO: -> OPEN for Upgrade / 101 Switching Protocols
            state.writeState = StreamState.HEADERS_COMPLETE;
            break;
        case HEADERS_COMPLETE:
            break;
        default:
            throw new IllegalStateException(unexpectedWriteEvent(node, state));
        }
        state.streamables.add(node);
        return null;
    }

    @Override
    public AstScriptNode visit(AstWriteFlushNode node, State state) throws Exception {

        switch (state.writeState) {
        case OPEN:
            break;
        case REQUEST:
        case RESPONSE:
            // TODO: -> OPEN for Upgrade / 101 Switching Protocols
            state.writeState = StreamState.HEADERS_COMPLETE;
            break;
        case HEADERS_COMPLETE:
            break;
        default:
            throw new IllegalStateException(unexpectedWriteEvent(node, state));
        }
        state.streamables.add(node);
        return null;
    }

    @Override
    public AstScriptNode visit(AstAcceptableNode acceptableNode, State state) throws Exception {
        AstAcceptableNode newAcceptableNode = new AstAcceptableNode();
        newAcceptableNode.setRegionInfo(acceptableNode.getRegionInfo());
        newAcceptableNode.setAcceptName(acceptableNode.getAcceptName());

        state.streamables = newAcceptableNode.getStreamables();
        for (AstStreamableNode streamable : acceptableNode.getStreamables()) {
            streamable.accept(this, state);
        }

        if (state.acceptables != null) {
            state.acceptables.add(newAcceptableNode);
        }
        else {
            state.streams.add(newAcceptableNode);
        }

        return null;
    }

    @Override
    public AstScriptNode visit(AstDisconnectNode node, State state) throws Exception {
        state.streamables.add(node);
        return null;
    }

    @Override
    public AstScriptNode visit(AstUnbindNode node, State state) throws Exception {
        state.streamables.add(node);
        return null;
    }

    @Override
    public AstScriptNode visit(AstCloseNode node, State state) throws Exception {
        state.streamables.add(node);
        return null;
    }

    @Override
    public AstScriptNode visit(AstChildOpenedNode node, State state) throws Exception {
        state.streamables.add(node);
        return null;
    }

    @Override
    public AstScriptNode visit(AstChildClosedNode node, State state) throws Exception {
        state.streamables.add(node);
        return null;
    }

    @Override
    public AstScriptNode visit(AstOpenedNode node, State state) throws Exception {
        state.streamables.add(node);
        return null;
    }

    @Override
    public AstScriptNode visit(AstBoundNode node, State state) throws Exception {
        state.streamables.add(node);
        return null;
    }

    @Override
    public AstScriptNode visit(AstConnectedNode node, State state) throws Exception {
        state.streamables.add(node);
        return null;
    }

    @Override
    public AstScriptNode visit(AstDisconnectedNode node, State state) throws Exception {
        state.streamables.add(node);
        return null;
    }

    @Override
    public AstScriptNode visit(AstUnboundNode node, State state) throws Exception {
        state.streamables.add(node);
        return null;
    }

    @Override
    public AstScriptNode visit(AstClosedNode node, State state) throws Exception {
        switch (state.readState) {
        case OPEN:
        case REQUEST:
        case RESPONSE:
        case HEADERS_COMPLETE:
        case CONTENT_COMPLETE:
            state.readState = StreamState.CLOSED;
            break;
        case CLOSED:
            break;
        default:
            throw new IllegalStateException(unexpectedReadEvent(node, state));
        }

        switch (state.writeState) {
        case OPEN:
        case REQUEST:
        case RESPONSE:
        case HEADERS_COMPLETE:
        case CONTENT_COMPLETE:
            state.writeState = StreamState.CLOSED;
            break;
        case CLOSED:
            break;
        default:
            throw new IllegalStateException(unexpectedWriteEvent(node, state));
        }

        state.streamables.add(node);
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
    public AstScriptNode visit(AstReadOptionNode node, State state) throws Exception {
        state.streamables.add(node);
        return null;
    }

    @Override
    public AstScriptNode visit(AstWriteOptionNode node, State state) throws Exception {
        state.streamables.add(node);
        return null;
    }

    private String unexpectedReadEvent(AstNode node, State state) {
        return String.format("Unexpected http event (%s) while reading in state %s", node
                .toString().trim(), state.readState);
    }

    private String unexpectedWriteEvent(AstNode node, State state) {
        return String.format("Unexpected http command (%s) while writing in state %s", node
                .toString().trim(), state.writeState);
    }

}
