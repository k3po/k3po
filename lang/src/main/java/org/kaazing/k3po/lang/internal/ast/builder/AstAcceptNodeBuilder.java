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

package org.kaazing.k3po.lang.internal.ast.builder;

import org.kaazing.k3po.lang.internal.ast.AstAcceptNode;
import org.kaazing.k3po.lang.internal.ast.AstScriptNode;
import org.kaazing.k3po.lang.internal.ast.value.AstLocation;

public final class AstAcceptNodeBuilder extends AbstractAstAcceptNodeBuilder<AstAcceptNode> {

    public AstAcceptNodeBuilder() {
        this(new AstAcceptNode());
    }

    public AstAcceptNodeBuilder setLocation(AstLocation location) {
        node.setLocation(location);
        return this;
    }

    public AstAcceptNodeBuilder setAcceptName(String acceptName) {
        node.setAcceptName(acceptName);
        return this;
    }

    @Override
    public AstOpenedNodeBuilder.StreamNested<AstAcceptNodeBuilder> addOpenedEvent() {
        return new AstOpenedNodeBuilder.StreamNested<AstAcceptNodeBuilder>(this);
    }

    @Override
    public AstBoundNodeBuilder.StreamNested<AstAcceptNodeBuilder> addBoundEvent() {
        return new AstBoundNodeBuilder.StreamNested<AstAcceptNodeBuilder>(this);
    }

    @Override
    public AstChildOpenedNodeBuilder.StreamNested<AstAcceptNodeBuilder> addChildOpenedEvent() {
        return new AstChildOpenedNodeBuilder.StreamNested<AstAcceptNodeBuilder>(this);
    }

    @Override
    public AstChildClosedNodeBuilder.StreamNested<AstAcceptNodeBuilder> addChildClosedEvent() {
        return new AstChildClosedNodeBuilder.StreamNested<AstAcceptNodeBuilder>(this);
    }

    @Override
    public AstUnboundNodeBuilder.StreamNested<AstAcceptNodeBuilder> addUnboundEvent() {
        return new AstUnboundNodeBuilder.StreamNested<AstAcceptNodeBuilder>(this);
    }

    @Override
    public AstClosedNodeBuilder.StreamNested<AstAcceptNodeBuilder> addClosedEvent() {
        return new AstClosedNodeBuilder.StreamNested<AstAcceptNodeBuilder>(this);
    }

    @Override
    public AstReadAwaitNodeBuilder.StreamNested<AstAcceptNodeBuilder> addReadAwaitBarrier() {
        return new AstReadAwaitNodeBuilder.StreamNested<AstAcceptNodeBuilder>(this);
    }

    @Override
    public AstReadNotifyNodeBuilder.StreamNested<AstAcceptNodeBuilder> addReadNotifyBarrier() {
        return new AstReadNotifyNodeBuilder.StreamNested<AstAcceptNodeBuilder>(this);
    }

    @Override
    public AstWriteAwaitNodeBuilder.StreamNested<AstAcceptNodeBuilder> addWriteAwaitBarrier() {
        return new AstWriteAwaitNodeBuilder.StreamNested<AstAcceptNodeBuilder>(this);
    }

    @Override
    public AstWriteNotifyNodeBuilder.StreamNested<AstAcceptNodeBuilder> addWriteNotifyBarrier() {
        return new AstWriteNotifyNodeBuilder.StreamNested<AstAcceptNodeBuilder>(this);
    }

    @Override
    public AstAcceptNode done() {
        return result;
    }

    private AstAcceptNodeBuilder(AstAcceptNode node) {
        super(node, node);
    }

    public static final class ScriptNested<R extends AbstractAstNodeBuilder<? extends AstScriptNode, ?>> extends
            AbstractAstAcceptNodeBuilder<R> {

        public ScriptNested(R builder) {
            super(new AstAcceptNode(), builder);
        }

        public ScriptNested<R> setLocation(AstLocation location) {
            node.setLocation(location);
            return this;
        }

        public ScriptNested<R> setTransport(AstLocation transport) {
            node.getOptions().put("transport", transport);
            return this;
        }

        public ScriptNested<R> setAcceptName(String acceptName) {
            node.setAcceptName(acceptName);
            return this;
        }

        public AstAcceptableNodeBuilder.AcceptNested<ScriptNested<R>> addAcceptedStream() {
            return new AstAcceptableNodeBuilder.AcceptNested<ScriptNested<R>>(this);
        }

        @Override
        public AstOpenedNodeBuilder.StreamNested<ScriptNested<R>> addOpenedEvent() {
            return new AstOpenedNodeBuilder.StreamNested<ScriptNested<R>>(this);
        }

        @Override
        public AstBoundNodeBuilder.StreamNested<ScriptNested<R>> addBoundEvent() {
            return new AstBoundNodeBuilder.StreamNested<ScriptNested<R>>(this);
        }

        @Override
        public AstChildOpenedNodeBuilder.StreamNested<ScriptNested<R>> addChildOpenedEvent() {
            return new AstChildOpenedNodeBuilder.StreamNested<ScriptNested<R>>(this);
        }

        @Override
        public AstChildClosedNodeBuilder.StreamNested<ScriptNested<R>> addChildClosedEvent() {
            return new AstChildClosedNodeBuilder.StreamNested<ScriptNested<R>>(this);
        }

        @Override
        public AstUnboundNodeBuilder.StreamNested<ScriptNested<R>> addUnboundEvent() {
            return new AstUnboundNodeBuilder.StreamNested<ScriptNested<R>>(this);
        }

        @Override
        public AstClosedNodeBuilder.StreamNested<ScriptNested<R>> addClosedEvent() {
            return new AstClosedNodeBuilder.StreamNested<ScriptNested<R>>(this);
        }

        @Override
        public AstReadAwaitNodeBuilder.StreamNested<ScriptNested<R>> addReadAwaitBarrier() {
            return new AstReadAwaitNodeBuilder.StreamNested<ScriptNested<R>>(this);
        }

        @Override
        public AstReadNotifyNodeBuilder.StreamNested<ScriptNested<R>> addReadNotifyBarrier() {
            return new AstReadNotifyNodeBuilder.StreamNested<ScriptNested<R>>(this);
        }

        @Override
        public AstWriteAwaitNodeBuilder.StreamNested<ScriptNested<R>> addWriteAwaitBarrier() {
            return new AstWriteAwaitNodeBuilder.StreamNested<ScriptNested<R>>(this);
        }

        @Override
        public AstWriteNotifyNodeBuilder.StreamNested<ScriptNested<R>> addWriteNotifyBarrier() {
            return new AstWriteNotifyNodeBuilder.StreamNested<ScriptNested<R>>(this);
        }

        @Override
        public R done() {
            AstScriptNode scriptNode = result.node;
            scriptNode.getStreams().add(node);
            return result;
        }
    }

}
