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

import org.kaazing.robot.driver.behavior.visitor.InjectHttpEventsVisitor.State;
import org.kaazing.robot.lang.LocationInfo;
import org.kaazing.robot.lang.ast.AstAcceptNode;
import org.kaazing.robot.lang.ast.AstAcceptableNode;
import org.kaazing.robot.lang.ast.AstBoundNode;
import org.kaazing.robot.lang.ast.AstChildClosedNode;
import org.kaazing.robot.lang.ast.AstChildOpenedNode;
import org.kaazing.robot.lang.ast.AstCloseHttpRequestNode;
import org.kaazing.robot.lang.ast.AstCloseHttpResponseNode;
import org.kaazing.robot.lang.ast.AstCloseNode;
import org.kaazing.robot.lang.ast.AstClosedNode;
import org.kaazing.robot.lang.ast.AstConnectNode;
import org.kaazing.robot.lang.ast.AstConnectedNode;
import org.kaazing.robot.lang.ast.AstDisconnectNode;
import org.kaazing.robot.lang.ast.AstDisconnectedNode;
import org.kaazing.robot.lang.ast.AstEndOfHttpHeadersNode;
import org.kaazing.robot.lang.ast.AstNode;
import org.kaazing.robot.lang.ast.AstOpenedNode;
import org.kaazing.robot.lang.ast.AstReadAwaitNode;
import org.kaazing.robot.lang.ast.AstReadHttpHeaderNode;
import org.kaazing.robot.lang.ast.AstReadHttpMethodNode;
import org.kaazing.robot.lang.ast.AstReadHttpParameterNode;
import org.kaazing.robot.lang.ast.AstReadHttpStatusNode;
import org.kaazing.robot.lang.ast.AstReadHttpVersionNode;
import org.kaazing.robot.lang.ast.AstReadNotifyNode;
import org.kaazing.robot.lang.ast.AstReadOptionNode;
import org.kaazing.robot.lang.ast.AstReadValueNode;
import org.kaazing.robot.lang.ast.AstScriptNode;
import org.kaazing.robot.lang.ast.AstStreamNode;
import org.kaazing.robot.lang.ast.AstStreamableNode;
import org.kaazing.robot.lang.ast.AstUnbindNode;
import org.kaazing.robot.lang.ast.AstUnboundNode;
import org.kaazing.robot.lang.ast.AstWriteAwaitNode;
import org.kaazing.robot.lang.ast.AstWriteHttpContentLengthNode;
import org.kaazing.robot.lang.ast.AstWriteHttpHeaderNode;
import org.kaazing.robot.lang.ast.AstWriteHttpMethodNode;
import org.kaazing.robot.lang.ast.AstWriteHttpParameterNode;
import org.kaazing.robot.lang.ast.AstWriteHttpStatusNode;
import org.kaazing.robot.lang.ast.AstWriteHttpVersionNode;
import org.kaazing.robot.lang.ast.AstWriteNotifyNode;
import org.kaazing.robot.lang.ast.AstWriteOptionNode;
import org.kaazing.robot.lang.ast.AstWriteValueNode;

public class InjectHttpEventsVisitor implements AstNode.Visitor<AstScriptNode, State> {

    public static enum HttpState {
        // @formatter:off
        NOT_HTTP("Not Http"),
        END_OF_HTTP("End of http"),
        READ_REQUEST_HEADERS("Read Request Headers"),
        READ_REQUEST_CONTENT("Read Request Content"),
        WRITE_REQUEST_HEADERS("Write Request Headers"),
        WRITE_REQUEST_CONTENT("Write Request Content"),
        READ_RESPONSE_HEADERS("Read Response Headers"),
        READ_RESPONSE_CONTENT("Read Response Content"),
        WRITE_RESPONSE_HEADERS("Write Response Headers"),
        WRITE_RESPONSE_CONTENT("Write Response Content"),
        HTTP_CLOSED("CLOSED");
        // @formatter:on

        private HttpState(final String readableState) {
            this.readableState = readableState;
        }

        private final String readableState;

        @Override
        public String toString() {
            return readableState;
        }
    }

    public static enum HttpContentType {
        // @formatter:off
        NONE("none"),
        ONE_ZERO_ONE("101 Upgrade"),
        CONNECTION_CLOSE("connection: close"),
        CHUNKED ("transfer-encoding: chuncked"),
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
        private HttpState httpState;
        private HttpContentType contentType;

        public void finish() throws Exception {

            if (httpState != null && httpState != HttpState.NOT_HTTP && httpState != HttpState.HTTP_CLOSED) {
                throw new IllegalStateException(String.format("Http request response was left in state: %s, "
                        + "and was not closed properly with either a close response, or a closed in the case of a 101",
                        httpState));
            }
        }
    }

    @Override
    public AstScriptNode visit(AstAcceptNode acceptNode, State state) throws Exception {

        URI uri = acceptNode.getLocation();
        if (uri != null && uri.getScheme().equalsIgnoreCase("http")) {
            state.httpState = HttpState.READ_REQUEST_HEADERS;
            state.contentType = HttpContentType.NONE;
        } else {
            state.httpState = HttpState.NOT_HTTP;
        }
        AstAcceptNode newAcceptNode = new AstAcceptNode();
        newAcceptNode.setLocationInfo(acceptNode.getLocationInfo());
        newAcceptNode.setAcceptName(acceptNode.getAcceptName());
        newAcceptNode.setLocation(acceptNode.getLocation());
        state.lastLocationInfo = acceptNode.getLocationInfo();

        state.streamables = newAcceptNode.getStreamables();
        state.streams.add(newAcceptNode);

        for (AstStreamableNode streamable : acceptNode.getStreamables()) {
            streamable.accept(this, state);
        }

        for (AstAcceptableNode acceptable : acceptNode.getAcceptables()) {
            acceptable.accept(this, state);
        }

        return null;
    }

    @Override
    public AstScriptNode visit(AstConnectNode connectNode, State state) throws Exception {

        URI uri = connectNode.getLocation();
        if (uri != null && uri.getScheme().equalsIgnoreCase("http")) {
            state.httpState = HttpState.WRITE_REQUEST_HEADERS;
            state.contentType = HttpContentType.NONE;
        } else {
            state.httpState = HttpState.NOT_HTTP;
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
    public AstScriptNode visit(AstReadHttpHeaderNode node, State state) throws Exception {

        switch (state.httpState) {
        case NOT_HTTP:
            break;
        case READ_REQUEST_HEADERS:
        case READ_RESPONSE_HEADERS:
            break;
        default:
            throw new IllegalStateException(String.format("Unexpected http event (%s) while in http state %s", node
                    .toString().trim(), state.httpState));
        }
        state.lastLocationInfo = node.getLocationInfo();
        state.streamables.add(node);
        return null;
    }

    @Override
    public AstScriptNode visit(AstWriteHttpHeaderNode node, State state) throws Exception {

        switch (state.httpState) {
        case WRITE_REQUEST_HEADERS:
        case WRITE_RESPONSE_HEADERS:
            break;
        default:
            throw new IllegalStateException(String.format("Unexpected http command (%s) while in http state %s", node
                    .toString().trim(), state.httpState));
        }

        if ("\"Content-Length\"".equalsIgnoreCase(node.getName().toString())) {
            throw new IllegalStateException(String.format(
                    "Explicitly setting the content length via: %s, is not allowed,"
                            + "use \"write header content-length\" to dynamically calculate content length instead",
                    node));
        }
        if ("\"transfer-encoding\"".equalsIgnoreCase(node.getName().toString())
                && "\"chunked\"".equalsIgnoreCase(node.getValue().toString())) {
            switch (state.contentType) {
            case NONE:
                state.contentType = HttpContentType.CHUNKED;
                break;
            default:
                throw new IllegalStateException(String.format(
                        "Can not set transfer-encoding: chunked when %s has already been set", state.contentType));
            }
        }

        if ("\"connection\"".equalsIgnoreCase(node.getName().toString())
                && "\"close\"".equalsIgnoreCase(node.getValue().toString())) {
            switch (state.contentType) {
            case NONE:
                state.contentType = HttpContentType.CONNECTION_CLOSE;
                break;
            default:
                throw new IllegalStateException(String.format(
                        "Can not set connection: close when %s has already been set", state.contentType));
            }
        }
        state.lastLocationInfo = node.getLocationInfo();
        state.streamables.add(node);
        return null;
    }

    @Override
    public AstScriptNode visit(AstWriteHttpContentLengthNode node, State state) throws Exception {

        switch (state.httpState) {
        case WRITE_REQUEST_HEADERS:
        case WRITE_RESPONSE_HEADERS:
            break;
        default:
            throw new IllegalStateException(String.format("Unexpected http command (%s) while in http state %s", node
                    .toString().trim(), state.httpState));
        }

        switch (state.contentType) {
        case NONE:
            state.contentType = HttpContentType.CONTENT_LENGTH;
            break;
        default:
            throw new IllegalStateException(String.format("Can not set content-length when %s has already been set",
                    state.contentType));
        }
        state.lastLocationInfo = node.getLocationInfo();
        state.streamables.add(node);
        return null;
    }

    @Override
    public AstScriptNode visit(AstReadHttpMethodNode node, State state) throws Exception {

        switch (state.httpState) {
        case READ_REQUEST_HEADERS:
            break;
        default:
            throw new IllegalStateException(String.format("Unexpected http event (%s) while in http state %s", node
                    .toString().trim(), state.httpState));
        }
        state.lastLocationInfo = node.getLocationInfo();
        state.streamables.add(node);
        return null;
    }

    @Override
    public AstScriptNode visit(AstWriteHttpMethodNode node, State state) throws Exception {

        switch (state.httpState) {
        case WRITE_REQUEST_HEADERS:
            break;
        default:
            throw new IllegalStateException(String.format("Unexpected http command (%s) while in http state %s", node
                    .toString().trim(), state.httpState));
        }
        state.lastLocationInfo = node.getLocationInfo();
        state.streamables.add(node);
        return null;
    }

    @Override
    public AstScriptNode visit(AstReadHttpParameterNode node, State state) throws Exception {

        switch (state.httpState) {
        case READ_REQUEST_HEADERS:
            break;
        default:
            throw new IllegalStateException(String.format("Unexpected http event (%s) while in http state %s", node
                    .toString().trim(), state.httpState));
        }
        state.lastLocationInfo = node.getLocationInfo();
        state.streamables.add(node);
        return null;
    }

    @Override
    public AstScriptNode visit(AstWriteHttpParameterNode node, State state) throws Exception {

        switch (state.httpState) {
        case WRITE_REQUEST_HEADERS:
            break;
        default:
            throw new IllegalStateException(String.format("Unexpected http command (%s) while in http state %s", node
                    .toString().trim(), state.httpState));
        }
        state.lastLocationInfo = node.getLocationInfo();
        state.streamables.add(node);
        return null;
    }

    @Override
    public AstScriptNode visit(AstReadHttpVersionNode node, State state) throws Exception {

        switch (state.httpState) {
        case READ_REQUEST_HEADERS:
            break;
        default:
            throw new IllegalStateException(String.format("Unexpected http event (%s) while in http state %s", node
                    .toString().trim(), state.httpState));
        }
        state.lastLocationInfo = node.getLocationInfo();
        state.streamables.add(node);
        return null;
    }

    @Override
    public AstScriptNode visit(AstWriteHttpVersionNode node, State state) throws Exception {

        switch (state.httpState) {
        case WRITE_REQUEST_HEADERS:
            break;
        default:
            throw new IllegalStateException(String.format("Unexpected http command (%s) while in http state %s", node
                    .toString().trim(), state.httpState));
        }
        state.lastLocationInfo = node.getLocationInfo();
        state.streamables.add(node);
        return null;
    }

    @Override
    public AstScriptNode visit(AstReadHttpStatusNode node, State state) throws Exception {

        switch (state.httpState) {
        case READ_RESPONSE_HEADERS:
            break;
        default:
            throw new IllegalStateException(String.format("Unexpected http event (%s) while in http state %s", node
                    .toString().trim(), state.httpState));
        }
        state.lastLocationInfo = node.getLocationInfo();
        state.streamables.add(node);
        if ("\"101\"".equalsIgnoreCase(node.getCode().toString())) {
            switch (state.contentType) {
            case NONE:
                state.contentType = HttpContentType.ONE_ZERO_ONE;
                break;
            default:
                throw new IllegalStateException(String.format(
                        "Can not set upgrade to websocket when %s has already been set", state.contentType));
            }
        }
        return null;
    }

    @Override
    public AstScriptNode visit(AstWriteHttpStatusNode node, State state) throws Exception {

        switch (state.httpState) {
        case WRITE_RESPONSE_HEADERS:
            break;
        default:
            throw new IllegalStateException(String.format("Unexpected http command (%s) while in http state %s", node
                    .toString().trim(), state.httpState));
        }

        if ("\"101\"".equalsIgnoreCase(node.getCode().toString())) {
            switch (state.contentType) {
            case NONE:
                state.contentType = HttpContentType.ONE_ZERO_ONE;
                break;
            default:
                throw new IllegalStateException(String.format(
                        "Can not set upgrade to websocket when %s has already been set", state.contentType));
            }
        }
        state.lastLocationInfo = node.getLocationInfo();
        state.streamables.add(node);
        return null;
    }

    @Override
    public AstScriptNode visit(AstCloseHttpRequestNode node, State state) throws Exception {

        switch (state.httpState) {
        case READ_REQUEST_HEADERS:
        case WRITE_REQUEST_HEADERS:
            AstEndOfHttpHeadersNode endOfHeadersNode = new AstEndOfHttpHeadersNode();
            endOfHeadersNode.setLocationInfo(node.getLocationInfo());
            endOfHeadersNode.accept(this, state);
            node.accept(this, state);
            return null;
        case READ_REQUEST_CONTENT:
            state.httpState = HttpState.WRITE_RESPONSE_HEADERS;
            break;
        case WRITE_REQUEST_CONTENT:
            state.contentType = HttpContentType.NONE;
            state.httpState = HttpState.READ_RESPONSE_HEADERS;
            break;
        default:
            throw new IllegalStateException(String.format("Unexpected http command (%s) while in http state %s", node
                    .toString().trim(), state.httpState));
        }
        state.lastLocationInfo = state.lastLocationInfo;
        state.streamables.add(node);
        return null;
    }

    @Override
    public AstScriptNode visit(AstCloseHttpResponseNode node, State state) throws Exception {

        switch (state.httpState) {
        case WRITE_RESPONSE_HEADERS:
        case READ_RESPONSE_HEADERS:
            AstEndOfHttpHeadersNode endOfHeadersNode = new AstEndOfHttpHeadersNode();
            endOfHeadersNode.setLocationInfo(node.getLocationInfo());
            endOfHeadersNode.accept(this, state);
            node.accept(this, state);
            return null;
        case WRITE_RESPONSE_CONTENT:
        case READ_RESPONSE_CONTENT:
            switch (state.contentType) {
            case ONE_ZERO_ONE:
                state.httpState = HttpState.END_OF_HTTP;
                break;
            default:
                state.lastLocationInfo = node.getLocationInfo();
                state.streamables.add(node);
                state.httpState = HttpState.HTTP_CLOSED;
                AstClosedNode closedNode = new AstClosedNode();
                closedNode.setLocationInfo(node.getLocationInfo());
                closedNode.accept(this, state);
                return null;
            }
            break;
        default:
            throw new IllegalStateException(String.format("Unexpected http command (%s) while in http state %s", node
                    .toString().trim(), state.httpState));
        }
        state.lastLocationInfo = node.getLocationInfo();
        state.streamables.add(node);
        return null;
    }

    @Override
    public AstScriptNode visit(AstEndOfHttpHeadersNode node, State state) throws Exception {

        switch (state.httpState) {
        case WRITE_RESPONSE_HEADERS:
            state.httpState = HttpState.WRITE_RESPONSE_CONTENT;
            break;
        case WRITE_REQUEST_HEADERS:
            state.httpState = HttpState.WRITE_REQUEST_CONTENT;
            break;
        case READ_RESPONSE_HEADERS:
            state.httpState = HttpState.READ_RESPONSE_CONTENT;
            break;
        case READ_REQUEST_HEADERS:
            state.httpState = HttpState.READ_REQUEST_CONTENT;
            break;
        default:
            throw new IllegalStateException(String.format("Unexpected http command (%s) while in http state %s", node
                    .toString().trim(), state.httpState));
        }
        state.lastLocationInfo = node.getLocationInfo();
        state.streamables.add(node);
        return null;
    }

    @Override
    public AstScriptNode visit(AstReadValueNode node, State state) throws Exception {

        switch (state.httpState) {
        case NOT_HTTP:
        case READ_REQUEST_CONTENT:
        case READ_RESPONSE_CONTENT:
            break;
        case READ_REQUEST_HEADERS:
        case READ_RESPONSE_HEADERS:
            AstEndOfHttpHeadersNode endOfHeadersNode = new AstEndOfHttpHeadersNode();
            endOfHeadersNode.setLocationInfo(node.getLocationInfo());
            endOfHeadersNode.accept(this, state);
            break;
        case END_OF_HTTP:
            // NOOP assume correct because 101 would be set by client
            break;
        default:
            throw new IllegalStateException(String.format("Unexpected http event (%s) while in http state %s", node
                    .toString().trim(), state.httpState));
        }
        state.lastLocationInfo = node.getLocationInfo();
        state.streamables.add(node);
        return null;
    }

    @Override
    public AstScriptNode visit(AstWriteValueNode node, State state) throws Exception {

        switch (state.httpState) {
        case NOT_HTTP:
            break;
        case END_OF_HTTP:
            switch (state.contentType) {
            case ONE_ZERO_ONE:
                // NOOP
                break;
            default:
                throw new IllegalStateException(
                        "Cannot write to tcp after http request/response when the response did not have a 101 status");
            }
            break;
        case WRITE_REQUEST_CONTENT:
        case WRITE_RESPONSE_CONTENT:
            switch (state.contentType) {
            case NONE:
                throw new IllegalStateException("Cannot write content when none of the following has been specified:"
                        + "Content-Length, Transfer-Encoding: chunked, Connection: close");
            default:
                // NOOP
            }
            break;
        case WRITE_REQUEST_HEADERS:
        case WRITE_RESPONSE_HEADERS:
            AstEndOfHttpHeadersNode endOfHeadersNode = new AstEndOfHttpHeadersNode();
            endOfHeadersNode.setLocationInfo(node.getLocationInfo());
            endOfHeadersNode.accept(this, state);
            node.accept(this, state);
            return null;
        default:
            throw new IllegalStateException(String.format("Unexpected http command (%s) while in http state %s", node
                    .toString().trim(), state.httpState));
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
        switch (state.httpState) {
        case NOT_HTTP:
            break;
        case END_OF_HTTP:
            state.httpState = HttpState.HTTP_CLOSED;
            break;
        default:
            throw new IllegalStateException(String.format("Unexpected closed event (%s) while in http state %s", node
                    .toString().trim(), state.httpState));
        }
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

}
