/*
 * Copyright (c) 2014 "Kaazing Corporation," (www.kaazing.com)
 *
 * This file is part of Robot.
 *
 * Robot is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.kaazing.robot.driver.behavior.visitor;

import java.net.URI;
import java.util.List;

import org.kaazing.robot.lang.LocationInfo;
import org.kaazing.robot.lang.ast.AstAcceptNode;
import org.kaazing.robot.lang.ast.AstAcceptableNode;
import org.kaazing.robot.lang.ast.AstBoundNode;
import org.kaazing.robot.lang.ast.AstChildClosedNode;
import org.kaazing.robot.lang.ast.AstChildOpenedNode;
import org.kaazing.robot.lang.ast.AstCloseNode;
import org.kaazing.robot.lang.ast.AstClosedNode;
import org.kaazing.robot.lang.ast.AstConnectNode;
import org.kaazing.robot.lang.ast.AstConnectedNode;
import org.kaazing.robot.lang.ast.AstDisconnectNode;
import org.kaazing.robot.lang.ast.AstDisconnectedNode;
import org.kaazing.robot.lang.ast.AstFlushNode;
import org.kaazing.robot.lang.ast.AstNode;
import org.kaazing.robot.lang.ast.AstOpenedNode;
import org.kaazing.robot.lang.ast.AstReadAwaitNode;
import org.kaazing.robot.lang.ast.AstReadClosedNode;
import org.kaazing.robot.lang.ast.AstReadNotifyNode;
import org.kaazing.robot.lang.ast.AstReadOptionNode;
import org.kaazing.robot.lang.ast.AstReadValueNode;
import org.kaazing.robot.lang.ast.AstScriptNode;
import org.kaazing.robot.lang.ast.AstStreamNode;
import org.kaazing.robot.lang.ast.AstStreamableNode;
import org.kaazing.robot.lang.ast.AstUnbindNode;
import org.kaazing.robot.lang.ast.AstUnboundNode;
import org.kaazing.robot.lang.ast.AstWriteAwaitNode;
import org.kaazing.robot.lang.ast.AstWriteCloseNode;
import org.kaazing.robot.lang.ast.AstWriteNotifyNode;
import org.kaazing.robot.lang.ast.AstWriteOptionNode;
import org.kaazing.robot.lang.ast.AstWriteValueNode;
import org.kaazing.robot.lang.http.ast.AstReadHttpHeaderNode;
import org.kaazing.robot.lang.http.ast.AstReadHttpMethodNode;
import org.kaazing.robot.lang.http.ast.AstReadHttpParameterNode;
import org.kaazing.robot.lang.http.ast.AstReadHttpStatusNode;
import org.kaazing.robot.lang.http.ast.AstReadHttpVersionNode;
import org.kaazing.robot.lang.http.ast.AstWriteHttpContentLengthNode;
import org.kaazing.robot.lang.http.ast.AstWriteHttpHeaderNode;
import org.kaazing.robot.lang.http.ast.AstWriteHttpMethodNode;
import org.kaazing.robot.lang.http.ast.AstWriteHttpParameterNode;
import org.kaazing.robot.lang.http.ast.AstWriteHttpStatusNode;
import org.kaazing.robot.lang.http.ast.AstWriteHttpVersionNode;

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
        private LocationInfo lastLocationInfo;
        private StreamState readState;
        private StreamState writeState;
        public List<AstAcceptableNode> acceptables;

        public State() {
            readState = StreamState.OPEN;
            writeState = StreamState.OPEN;
        }
    }

    @Override
    public AstScriptNode visit(AstAcceptNode acceptNode, State state) throws Exception {

        AstAcceptNode newAcceptNode = new AstAcceptNode();
        newAcceptNode.setLocationInfo(acceptNode.getLocationInfo());
        newAcceptNode.setAcceptName(acceptNode.getAcceptName());
        newAcceptNode.setLocation(acceptNode.getLocation());
        newAcceptNode.setEndLocation(acceptNode.getEndLocation());
        state.lastLocationInfo = acceptNode.getLocationInfo();

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

            if (state.readState != StreamState.CLOSED) {
                throw new IllegalStateException(String.format("Http read was left in state: %s", state.readState));
            }

            if (state.writeState != StreamState.CLOSED) {
                throw new IllegalStateException(String.format("Http write was left in state: %s", state.writeState));
            }
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
        newConnectNode.setLocationInfo(connectNode.getLocationInfo());
        newConnectNode.setLocation(connectNode.getLocation());
        newConnectNode.setEndLocation(connectNode.getEndLocation());
        state.lastLocationInfo = connectNode.getLocationInfo();

        state.streamables = newConnectNode.getStreamables();
        for (AstStreamableNode streamable : connectNode.getStreamables()) {
            streamable.accept(this, state);
        }

        if (state.readState != StreamState.CLOSED) {
            throw new IllegalStateException(String.format("Http read was left in state: %s", state.readState));
        }

        if (state.writeState != StreamState.CLOSED) {
            throw new IllegalStateException(String.format("Http write was left in state: %s", state.writeState));
        }

        state.lastLocationInfo = connectNode.getLocationInfo();
        state.streams.add(newConnectNode);

        return null;
    }

    @Override
    public AstScriptNode visit(AstReadHttpHeaderNode node, State state) throws Exception {

        switch (state.readState) {
        case REQUEST:
        case RESPONSE:
            break;
        default:
            throw new IllegalStateException(String.format("Unexpected http event (%s) while reading in state %s", node
                    .toString().trim(), state.readState));
        }
        state.lastLocationInfo = node.getLocationInfo();
        state.streamables.add(node);
        return null;
    }

    @Override
    public AstScriptNode visit(AstWriteHttpHeaderNode node, State state) throws Exception {

        switch (state.writeState) {
        case REQUEST:
        case RESPONSE:
            break;
        default:
            throw new IllegalStateException(String.format("Unexpected http command (%s) while writing in state %s", node
                    .toString().trim(), state.writeState));
        }
        state.lastLocationInfo = node.getLocationInfo();
        state.streamables.add(node);
        return null;
    }

    @Override
    public AstScriptNode visit(AstWriteHttpContentLengthNode node, State state) throws Exception {

        switch (state.writeState) {
        case REQUEST:
        case RESPONSE:
            break;
        default:
            throw new IllegalStateException(unexpectedWriteEvent(node, state));
        }
        state.lastLocationInfo = node.getLocationInfo();
        state.streamables.add(node);
        return null;
    }

    @Override
    public AstScriptNode visit(AstReadHttpMethodNode node, State state) throws Exception {

        switch (state.readState) {
        case REQUEST:
            break;
        default:
            throw new IllegalStateException(unexpectedReadEvent(node, state));
        }
        state.lastLocationInfo = node.getLocationInfo();
        state.streamables.add(node);
        return null;
    }

    @Override
    public AstScriptNode visit(AstWriteHttpMethodNode node, State state) throws Exception {

        switch (state.writeState) {
        case REQUEST:
            break;
        default:
            throw new IllegalStateException(unexpectedWriteEvent(node, state));
        }
        state.lastLocationInfo = node.getLocationInfo();
        state.streamables.add(node);
        return null;
    }

    @Override
    public AstScriptNode visit(AstReadHttpParameterNode node, State state) throws Exception {

        switch (state.readState) {
        case REQUEST:
        case RESPONSE:
            break;
        default:
            throw new IllegalStateException(unexpectedReadEvent(node, state));
        }
        state.lastLocationInfo = node.getLocationInfo();
        state.streamables.add(node);
        return null;
    }

    @Override
    public AstScriptNode visit(AstWriteHttpParameterNode node, State state) throws Exception {

        switch (state.writeState) {
        case REQUEST:
        case RESPONSE:
            break;
        default:
            throw new IllegalStateException(unexpectedWriteEvent(node, state));
        }
        state.lastLocationInfo = node.getLocationInfo();
        state.streamables.add(node);
        return null;
    }

    @Override
    public AstScriptNode visit(AstReadHttpVersionNode node, State state) throws Exception {

        switch (state.readState) {
        case REQUEST:
        case RESPONSE:
            break;
        default:
            throw new IllegalStateException(unexpectedReadEvent(node, state));
        }
        state.lastLocationInfo = node.getLocationInfo();
        state.streamables.add(node);
        return null;
    }

    @Override
    public AstScriptNode visit(AstWriteHttpVersionNode node, State state) throws Exception {

        switch (state.writeState) {
        case REQUEST:
        case RESPONSE:
            break;
        default:
            throw new IllegalStateException(unexpectedWriteEvent(node, state));
        }
        state.lastLocationInfo = node.getLocationInfo();
        state.streamables.add(node);
        return null;
    }

    @Override
    public AstScriptNode visit(AstReadHttpStatusNode node, State state) throws Exception {

        switch (state.readState) {
        case RESPONSE:
            break;
        default:
            throw new IllegalStateException(unexpectedReadEvent(node, state));
        }
        state.lastLocationInfo = node.getLocationInfo();
        state.streamables.add(node);
        return null;
    }

    @Override
    public AstScriptNode visit(AstWriteHttpStatusNode node, State state) throws Exception {

        switch (state.writeState) {
        case RESPONSE:
            break;
        default:
            throw new IllegalStateException(unexpectedWriteEvent(node, state));
        }
        state.lastLocationInfo = node.getLocationInfo();
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
        state.lastLocationInfo = state.lastLocationInfo;
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
        state.lastLocationInfo = node.getLocationInfo();
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
        state.lastLocationInfo = node.getLocationInfo();
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
        state.lastLocationInfo = node.getLocationInfo();
        state.streamables.add(node);
        return null;
    }

    @Override
    public AstScriptNode visit(AstFlushNode node, State state) throws Exception {

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
        state.lastLocationInfo = node.getLocationInfo();
        state.streamables.add(node);
        return null;
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
    public AstScriptNode visit(AstAcceptableNode acceptableNode, State state) throws Exception {
        AstAcceptableNode newAcceptableNode = new AstAcceptableNode();
        newAcceptableNode.setLocationInfo(acceptableNode.getLocationInfo());
        newAcceptableNode.setAcceptName(acceptableNode.getAcceptName());
        newAcceptableNode.setEndLocation(acceptableNode.getEndLocation());
        state.lastLocationInfo = acceptableNode.getLocationInfo();

        state.streamables = newAcceptableNode.getStreamables();
        for (AstStreamableNode streamable : acceptableNode.getStreamables()) {
            streamable.accept(this, state);
        }
        state.lastLocationInfo = acceptableNode.getLocationInfo();

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
        state.lastLocationInfo = node.getLocationInfo();
        state.streamables.add(node);
        return null;
    }

    @Override
    public AstScriptNode visit(AstUnbindNode node, State state) throws Exception {
        state.lastLocationInfo = node.getLocationInfo();
        state.streamables.add(node);
        return null;
    }

    @Override
    public AstScriptNode visit(AstCloseNode node, State state) throws Exception {
        state.lastLocationInfo = node.getLocationInfo();
        state.streamables.add(node);
        return null;
    }

    @Override
    public AstScriptNode visit(AstChildOpenedNode node, State state) throws Exception {
        state.lastLocationInfo = node.getLocationInfo();
        state.streamables.add(node);
        return null;
    }

    @Override
    public AstScriptNode visit(AstChildClosedNode node, State state) throws Exception {
        state.lastLocationInfo = node.getLocationInfo();
        state.streamables.add(node);
        return null;
    }

    @Override
    public AstScriptNode visit(AstOpenedNode node, State state) throws Exception {
        state.lastLocationInfo = node.getLocationInfo();
        state.streamables.add(node);
        return null;
    }

    @Override
    public AstScriptNode visit(AstBoundNode node, State state) throws Exception {
        state.lastLocationInfo = node.getLocationInfo();
        state.streamables.add(node);
        return null;
    }

    @Override
    public AstScriptNode visit(AstConnectedNode node, State state) throws Exception {
        state.lastLocationInfo = node.getLocationInfo();
        state.streamables.add(node);
        return null;
    }

    @Override
    public AstScriptNode visit(AstDisconnectedNode node, State state) throws Exception {
        state.lastLocationInfo = node.getLocationInfo();
        state.streamables.add(node);
        return null;
    }

    @Override
    public AstScriptNode visit(AstUnboundNode node, State state) throws Exception {
        state.lastLocationInfo = node.getLocationInfo();
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

        state.lastLocationInfo = node.getLocationInfo();
        state.streamables.add(node);
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
    public AstScriptNode visit(AstReadOptionNode node, State state) throws Exception {
        state.lastLocationInfo = node.getLocationInfo();
        state.streamables.add(node);
        return null;
    }

    @Override
    public AstScriptNode visit(AstWriteOptionNode node, State state) throws Exception {
        state.lastLocationInfo = node.getLocationInfo();
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
