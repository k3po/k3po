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
package org.kaazing.k3po.lang.internal.ast.builder;

import org.kaazing.k3po.lang.internal.ast.AstAcceptNode;
import org.kaazing.k3po.lang.internal.ast.AstRejectedNode;
import org.kaazing.k3po.lang.internal.ast.AstScriptNode;

public final class AstRejectedNodeBuilder extends AbstractAstRejectedNodeBuilder<AstRejectedNode> {

    public AstRejectedNodeBuilder() {
        this(new AstRejectedNode());
    }

    public AstRejectedNodeBuilder setAcceptName(String acceptName) {
        node.setAcceptName(acceptName);
        return this;
    }

    @Override
    public AstReadAwaitNodeBuilder.RejectedNested<AstRejectedNodeBuilder> addReadAwaitBarrier() {
        return new AstReadAwaitNodeBuilder.RejectedNested<>(this);
    }

    @Override
    public AstReadNotifyNodeBuilder.RejectedNested<AstRejectedNodeBuilder> addReadNotifyBarrier() {
        return new AstReadNotifyNodeBuilder.RejectedNested<>(this);
    }

    @Override
    public AstReadConfigNodeBuilder.RejectedNested<AstRejectedNodeBuilder> addReadConfigEvent() {
        return new AstReadConfigNodeBuilder.RejectedNested<>(this);
    }

    @Override
    public AstRejectedNode done() {
        return result;
    }

    private AstRejectedNodeBuilder(AstRejectedNode node) {
        super(node, node);
    }

    public static final class ScriptNested<R extends AbstractAstNodeBuilder<? extends AstScriptNode, ?>> extends
            AbstractAstRejectedNodeBuilder<R> {

        public ScriptNested(R builder) {
            super(builder);
        }

        public ScriptNested<R> setAcceptName(String acceptName) {
            node.setAcceptName(acceptName);
            return this;
        }

        @Override
        public AstReadAwaitNodeBuilder.RejectedNested<ScriptNested<R>> addReadAwaitBarrier() {
            return new AstReadAwaitNodeBuilder.RejectedNested<>(this);
        }

        @Override
        public AstReadNotifyNodeBuilder.RejectedNested<ScriptNested<R>> addReadNotifyBarrier() {
            return new AstReadNotifyNodeBuilder.RejectedNested<>(this);
        }

        @Override
        public AstReadConfigNodeBuilder.RejectedNested<ScriptNested<R>> addReadConfigEvent() {
            return new AstReadConfigNodeBuilder.RejectedNested<>(this);
        }

        @Override
        public R done() {
            AstScriptNode scriptNode = result.node;
            scriptNode.getStreams().add(node);
            return result;
        }
    }

    public static final class AcceptNested<R extends AbstractAstNodeBuilder<? extends AstAcceptNode, ?>> extends
            AbstractAstAcceptedNodeBuilder<R> {

        public AcceptNested(R builder) {
            super(builder);
        }

        public AcceptNested<R> setAcceptName(String acceptName) {
            node.setAcceptName(acceptName);
            return this;
        }

        @Override
        public AstOpenedNodeBuilder.StreamNested<AcceptNested<R>> addOpenedEvent() {
            return new AstOpenedNodeBuilder.StreamNested<>(this);
        }

        @Override
        public AstBoundNodeBuilder.StreamNested<AcceptNested<R>> addBoundEvent() {
            return new AstBoundNodeBuilder.StreamNested<>(this);
        }

        @Override
        public AstConnectedNodeBuilder.StreamNested<AcceptNested<R>> addConnectedEvent() {
            return new AstConnectedNodeBuilder.StreamNested<>(this);
        }

        @Override
        public AstReadNodeBuilder.StreamNested<AcceptNested<R>> addReadEvent() {
            return new AstReadNodeBuilder.StreamNested<>(this);
        }

        @Override
        public AstDisconnectedNodeBuilder.StreamNested<AcceptNested<R>> addDisconnectedEvent() {
            return new AstDisconnectedNodeBuilder.StreamNested<>(this);
        }

        @Override
        public AstUnboundNodeBuilder.StreamNested<AcceptNested<R>> addUnboundEvent() {
            return new AstUnboundNodeBuilder.StreamNested<>(this);
        }

        @Override
        public AstClosedNodeBuilder.StreamNested<AcceptNested<R>> addClosedEvent() {
            return new AstClosedNodeBuilder.StreamNested<>(this);
        }

        @Override
        public AstWriteNodeBuilder.StreamNested<AcceptNested<R>> addWriteCommand() {
            return new AstWriteNodeBuilder.StreamNested<>(this);
        }

        @Override
        public AstDisconnectNodeBuilder.StreamNested<AcceptNested<R>> addDisconnectCommand() {
            return new AstDisconnectNodeBuilder.StreamNested<>(this);
        }

        @Override
        public AstUnbindNodeBuilder.StreamNested<AcceptNested<R>> addUnbindCommand() {
            return new AstUnbindNodeBuilder.StreamNested<>(this);
        }

        @Override
        public AstCloseNodeBuilder.StreamNested<AcceptNested<R>> addCloseCommand() {
            return new AstCloseNodeBuilder.StreamNested<>(this);
        }

        @Override
        public AstWriteAbortNodeBuilder.StreamNested<AcceptNested<R>> addWriteAbortCommand() {
            return new AstWriteAbortNodeBuilder.StreamNested<>(this);
        }

        @Override
        public AstReadAbortedNodeBuilder.StreamNested<AcceptNested<R>> addReadAbortedEvent() {
            return new AstReadAbortedNodeBuilder.StreamNested<>(this);
        }

        @Override
        public AstReadAbortNodeBuilder.StreamNested<AcceptNested<R>> addReadAbortCommand() {
            return new AstReadAbortNodeBuilder.StreamNested<>(this);
        }

        @Override
        public AstWriteAbortedNodeBuilder.StreamNested<AcceptNested<R>> addWriteAbortedEvent() {
            return new AstWriteAbortedNodeBuilder.StreamNested<>(this);
        }

        @Override
        public AstReadAwaitNodeBuilder.StreamNested<AcceptNested<R>> addReadAwaitBarrier() {
            return new AstReadAwaitNodeBuilder.StreamNested<>(this);
        }

        @Override
        public AstReadNotifyNodeBuilder.StreamNested<AcceptNested<R>> addReadNotifyBarrier() {
            return new AstReadNotifyNodeBuilder.StreamNested<>(this);
        }

        @Override
        public AstWriteAwaitNodeBuilder.StreamNested<AcceptNested<R>> addWriteAwaitBarrier() {
            return new AstWriteAwaitNodeBuilder.StreamNested<>(this);
        }

        @Override
        public AstWriteNotifyNodeBuilder.StreamNested<AcceptNested<R>> addWriteNotifyBarrier() {
            return new AstWriteNotifyNodeBuilder.StreamNested<>(this);
        }

        @Override
        public R done() {
            AstAcceptNode acceptNode = result.node;
            acceptNode.getAcceptables().add(node);
            return result;
        }

        @Override
        public AstReadOptionNodeBuilder.StreamNested<AcceptNested<R>> addReadOption() {
            return new AstReadOptionNodeBuilder.StreamNested<>(this);
        }

        @Override
        public AstWriteOptionNodeBuilder.StreamNested<AcceptNested<R>> addWriteOption() {
            return new AstWriteOptionNodeBuilder.StreamNested<>(this);
        }

        @Override
        public AstReadConfigNodeBuilder.StreamNested<AcceptNested<R>> addReadConfigEvent() {
            return new AstReadConfigNodeBuilder.StreamNested<>(this);
        }

        @Override
        public AstWriteConfigNodeBuilder.StreamNested<AcceptNested<R>> addWriteConfigCommand() {
            return new AstWriteConfigNodeBuilder.StreamNested<>(this);
        }

        @Override
        public AstFlushNodeBuilder.StreamNested<AcceptNested<R>> addFlushCommand() {
            return new AstFlushNodeBuilder.StreamNested<>(this);
        }

        @Override
        public AstReadClosedNodeBuilder.StreamNested<AcceptNested<R>> addReadCloseCommand() {
            return new AstReadClosedNodeBuilder.StreamNested<>(this);
        }

        @Override
        public AstWriteCloseNodeBuilder.StreamNested<AcceptNested<R>> addWriteCloseCommand() {
            return new AstWriteCloseNodeBuilder.StreamNested<>(this);
        }
    }
}
