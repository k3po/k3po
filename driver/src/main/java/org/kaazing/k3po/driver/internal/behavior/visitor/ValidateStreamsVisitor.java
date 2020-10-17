/**
 * Copyright 2007-2015, Kaazing Corporation. All rights reserved.
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

import org.kaazing.k3po.lang.internal.ast.AstAcceptNode;
import org.kaazing.k3po.lang.internal.ast.AstAcceptableNode;
import org.kaazing.k3po.lang.internal.ast.AstAcceptedNode;
import org.kaazing.k3po.lang.internal.ast.AstBoundNode;
import org.kaazing.k3po.lang.internal.ast.AstChildClosedNode;
import org.kaazing.k3po.lang.internal.ast.AstChildOpenedNode;
import org.kaazing.k3po.lang.internal.ast.AstCloseNode;
import org.kaazing.k3po.lang.internal.ast.AstClosedNode;
import org.kaazing.k3po.lang.internal.ast.AstCommandNode;
import org.kaazing.k3po.lang.internal.ast.AstConnectAbortNode;
import org.kaazing.k3po.lang.internal.ast.AstConnectAbortedNode;
import org.kaazing.k3po.lang.internal.ast.AstConnectNode;
import org.kaazing.k3po.lang.internal.ast.AstConnectedNode;
import org.kaazing.k3po.lang.internal.ast.AstDisconnectNode;
import org.kaazing.k3po.lang.internal.ast.AstDisconnectedNode;
import org.kaazing.k3po.lang.internal.ast.AstEventNode;
import org.kaazing.k3po.lang.internal.ast.AstNode;
import org.kaazing.k3po.lang.internal.ast.AstOpenedNode;
import org.kaazing.k3po.lang.internal.ast.AstPropertyNode;
import org.kaazing.k3po.lang.internal.ast.AstReadAbortNode;
import org.kaazing.k3po.lang.internal.ast.AstReadAbortedNode;
import org.kaazing.k3po.lang.internal.ast.AstReadAdviseNode;
import org.kaazing.k3po.lang.internal.ast.AstReadAdvisedNode;
import org.kaazing.k3po.lang.internal.ast.AstReadAwaitNode;
import org.kaazing.k3po.lang.internal.ast.AstReadClosedNode;
import org.kaazing.k3po.lang.internal.ast.AstReadConfigNode;
import org.kaazing.k3po.lang.internal.ast.AstReadNotifyNode;
import org.kaazing.k3po.lang.internal.ast.AstReadOptionNode;
import org.kaazing.k3po.lang.internal.ast.AstReadValueNode;
import org.kaazing.k3po.lang.internal.ast.AstRejectedNode;
import org.kaazing.k3po.lang.internal.ast.AstScriptNode;
import org.kaazing.k3po.lang.internal.ast.AstStreamNode;
import org.kaazing.k3po.lang.internal.ast.AstStreamableNode;
import org.kaazing.k3po.lang.internal.ast.AstUnbindNode;
import org.kaazing.k3po.lang.internal.ast.AstUnboundNode;
import org.kaazing.k3po.lang.internal.ast.AstWriteAbortNode;
import org.kaazing.k3po.lang.internal.ast.AstWriteAbortedNode;
import org.kaazing.k3po.lang.internal.ast.AstWriteAdviseNode;
import org.kaazing.k3po.lang.internal.ast.AstWriteAdvisedNode;
import org.kaazing.k3po.lang.internal.ast.AstWriteAwaitNode;
import org.kaazing.k3po.lang.internal.ast.AstWriteCloseNode;
import org.kaazing.k3po.lang.internal.ast.AstWriteConfigNode;
import org.kaazing.k3po.lang.internal.ast.AstWriteFlushNode;
import org.kaazing.k3po.lang.internal.ast.AstWriteNotifyNode;
import org.kaazing.k3po.lang.internal.ast.AstWriteOptionNode;
import org.kaazing.k3po.lang.internal.ast.AstWriteValueNode;

// Note: this is no longer injecting, just validating, as injection is now generalized
public class ValidateStreamsVisitor implements AstNode.Visitor<AstScriptNode, ValidateStreamsVisitor.State> {

    public enum StreamState {
        // @formatter:off
        OPEN,
        CLOSED,
        // @formatter:on
    }

    public static final class State {
        private StreamState readState;
        private StreamState writeState;

        public State() {
            readState = StreamState.OPEN;
            writeState = StreamState.OPEN;
        }
    }

    @Override
    public AstScriptNode visit(AstScriptNode script, State state) {
        for (AstStreamNode stream : script.getStreams()) {
            stream.accept(this, state);
        }

        return null;
    }

    @Override
    public AstScriptNode visit(AstPropertyNode propertyNode, State state) {
        return null;
    }

    @Override
    public AstScriptNode visit(AstAcceptNode acceptNode, State state) {

        for (AstStreamableNode streamable : acceptNode.getStreamables()) {
            streamable.accept(this, state);
        }

        for (AstAcceptableNode acceptable : acceptNode.getAcceptables()) {
            state.readState = StreamState.OPEN;
            state.writeState = StreamState.OPEN;
            acceptable.accept(this, state);

        }

        return null;
    }

    @Override
    public AstScriptNode visit(AstConnectNode connectNode, State state) {

        state.writeState = StreamState.OPEN;
        state.readState = StreamState.OPEN;

        for (AstStreamableNode streamable : connectNode.getStreamables()) {
            streamable.accept(this, state);
        }

        return null;
    }

    @Override
    public AstScriptNode visit(AstConnectAbortNode node, State state) {

        switch (state.writeState) {
        case OPEN:
            state.readState = StreamState.CLOSED;
            break;
        default:
            throw new IllegalStateException(unexpectedInWriteState(node, state));
        }
        return null;
    }

    @Override
    public AstScriptNode visit(AstConnectAbortedNode node, State state) {

        switch (state.writeState) {
        case OPEN:
            state.readState = StreamState.CLOSED;
            break;
        default:
            throw new IllegalStateException(unexpectedInWriteState(node, state));
        }
        return null;
    }

    @Override
    public AstScriptNode visit(AstReadConfigNode node, State state) {

        switch (state.readState) {
        case OPEN:
        case CLOSED:
            break;
        default:
            throw new IllegalStateException(String.format("Unexpected read config event (%s) while reading in state %s", node
                    .toString().trim(), state.readState));
        }
        return null;
    }

    @Override
    public AstScriptNode visit(AstWriteConfigNode node, State state) {

        switch (state.writeState) {
        case OPEN:
        case CLOSED:
            break;
        default:
            throw new IllegalStateException(String.format("Unexpected write config command (%s) while writing in state %s", node
                    .toString().trim(), state.writeState));
        }
        return null;
    }

    @Override
    public AstScriptNode visit(AstReadAdviseNode node, State state) {

        switch (state.readState) {
        case OPEN:
        case CLOSED:
            break;
        default:
            throw new IllegalStateException(String.format("Unexpected read advise command (%s) while writing in state %s", node
                    .toString().trim(), state.writeState));
        }
        return null;
    }

    @Override
    public AstScriptNode visit(AstReadAdvisedNode node, State state) {

        switch (state.readState) {
        case OPEN:
        case CLOSED:
            break;
        default:
            throw new IllegalStateException(String.format("Unexpected read advised event (%s) while writing in state %s", node
                    .toString().trim(), state.writeState));
        }
        return null;
    }

    @Override
    public AstScriptNode visit(AstWriteAdviseNode node, State state) {

        switch (state.writeState) {
        case OPEN:
        case CLOSED:
            break;
        default:
            throw new IllegalStateException(String.format("Unexpected write advise command (%s) while writing in state %s", node
                    .toString().trim(), state.writeState));
        }
        return null;
    }

    @Override
    public AstScriptNode visit(AstWriteAdvisedNode node, State state) {

        switch (state.writeState) {
        case OPEN:
        case CLOSED:
            break;
        default:
            throw new IllegalStateException(String.format("Unexpected write advised event (%s) while writing in state %s", node
                    .toString().trim(), state.writeState));
        }
        return null;
    }

    @Override
    public AstScriptNode visit(AstReadClosedNode node, State state) {

        switch (state.readState) {
        case OPEN:
            state.readState = StreamState.CLOSED;
            break;
        default:
            throw new IllegalStateException(unexpectedInReadState(node, state));
        }
        return null;
    }

    @Override
    public AstScriptNode visit(AstWriteCloseNode node, State state) {

        switch (state.writeState) {
        case OPEN:
            state.writeState = StreamState.CLOSED;
            break;
        default:
            throw new IllegalStateException(unexpectedInWriteState(node, state));
        }
        return null;
    }

    @Override
    public AstScriptNode visit(AstWriteAbortNode node, State state) {
        switch (state.readState) {
        case OPEN:
        case CLOSED:
            state.writeState = StreamState.CLOSED;
            break;
        default:
            throw new IllegalStateException(unexpectedInReadState(node, state));
        }
        return null;
    }

    @Override
    public AstScriptNode visit(AstReadAbortNode node, State state) {
        switch (state.readState) {
        case OPEN:
        case CLOSED:
            state.readState = StreamState.CLOSED;
            break;
        default:
            throw new IllegalStateException(unexpectedInReadState(node, state));
        }
        return null;
    }

    @Override
    public AstScriptNode visit(AstReadAbortedNode node, State state) {

        switch (state.writeState) {
        case OPEN:
        case CLOSED:
            state.readState = StreamState.CLOSED;
            break;
        default:
            throw new IllegalStateException(unexpectedInWriteState(node, state));
        }
        return null;
    }

    @Override
    public AstScriptNode visit(AstWriteAbortedNode node, State state) {

        switch (state.writeState) {
        case OPEN:
        case CLOSED:
            state.writeState = StreamState.CLOSED;
            break;
        default:
            throw new IllegalStateException(unexpectedInWriteState(node, state));
        }
        return null;
    }

    @Override
    public AstScriptNode visit(AstReadValueNode node, State state) {

        switch (state.readState) {
        case OPEN:
            break;
        default:
            throw new IllegalStateException(unexpectedInReadState(node, state));
        }
        return null;
    }

    @Override
    public AstScriptNode visit(AstWriteValueNode node, State state) {

        switch (state.writeState) {
        case OPEN:
            break;
        default:
            throw new IllegalStateException(unexpectedInWriteState(node, state));
        }
        return null;
    }

    @Override
    public AstScriptNode visit(AstWriteFlushNode node, State state) {

        switch (state.writeState) {
        case OPEN:
            break;
        default:
            throw new IllegalStateException(unexpectedInWriteState(node, state));
        }
        return null;
    }

    @Override
    public AstScriptNode visit(AstAcceptedNode node, State state) {

        for (AstStreamableNode streamable : node.getStreamables()) {
            streamable.accept(this, state);
        }

        return null;
    }

    @Override
    public AstScriptNode visit(AstRejectedNode node, State state) {

        for (AstStreamableNode streamable : node.getStreamables()) {
            streamable.accept(this, state);
        }

        return null;
    }

    @Override
    public AstScriptNode visit(AstDisconnectNode node, State state) {
        return null;
    }

    @Override
    public AstScriptNode visit(AstUnbindNode node, State state) {
        return null;
    }

    @Override
    public AstScriptNode visit(AstCloseNode node, State state) {
        return null;
    }

    @Override
    public AstScriptNode visit(AstChildOpenedNode node, State state) {
        return null;
    }

    @Override
    public AstScriptNode visit(AstChildClosedNode node, State state) {
        return null;
    }

    @Override
    public AstScriptNode visit(AstOpenedNode node, State state) {
        return null;
    }

    @Override
    public AstScriptNode visit(AstBoundNode node, State state) {
        return null;
    }

    @Override
    public AstScriptNode visit(AstConnectedNode node, State state) {
        return null;
    }

    @Override
    public AstScriptNode visit(AstDisconnectedNode node, State state) {
        return null;
    }

    @Override
    public AstScriptNode visit(AstUnboundNode node, State state) {
        return null;
    }

    @Override
    public AstScriptNode visit(AstClosedNode node, State state) {
        switch (state.readState) {
        case OPEN:
            state.readState = StreamState.CLOSED;
            break;
        case CLOSED:
            break;
        default:
            throw new IllegalStateException(unexpectedInReadState(node, state));
        }

        switch (state.writeState) {
        case OPEN:
            state.writeState = StreamState.CLOSED;
            break;
        case CLOSED:
            break;
        default:
            throw new IllegalStateException(unexpectedInWriteState(node, state));
        }

        return null;
    }

    @Override
    public AstScriptNode visit(AstReadAwaitNode node, State state) {
        return null;
    }

    @Override
    public AstScriptNode visit(AstWriteAwaitNode node, State state) {
        return null;
    }

    @Override
    public AstScriptNode visit(AstReadNotifyNode node, State state) {
        return null;
    }

    @Override
    public AstScriptNode visit(AstWriteNotifyNode node, State state) {
        return null;
    }

    @Override
    public AstScriptNode visit(AstReadOptionNode node, State state) {
        return null;
    }

    @Override
    public AstScriptNode visit(AstWriteOptionNode node, State state) {
        return null;
    }

    private String unexpectedInReadState(AstNode node, State state) {
        return String.format("Unexpected %s while reading in state %s", description(node), state.readState);
    }

    private String unexpectedInWriteState(AstNode node, State state) {
        return String.format("Unexpected %s while writing in state %s", description(node), state.writeState);
    }

    private String description(AstNode node) {
        String description = node.toString().trim();

        if (node instanceof AstEventNode) {
            description = String.format("event (%s)", description);
        }
        else if (node instanceof AstCommandNode) {
            description = String.format("command (%s)", description);
        }

        return description;
    }
}
