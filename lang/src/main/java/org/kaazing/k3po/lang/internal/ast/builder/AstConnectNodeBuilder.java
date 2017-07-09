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

import java.net.URI;

import org.kaazing.k3po.lang.internal.ast.AstConnectNode;
import org.kaazing.k3po.lang.internal.ast.AstScriptNode;
import org.kaazing.k3po.lang.internal.ast.value.AstValue;
import org.kaazing.k3po.lang.types.TypeInfo;

public final class AstConnectNodeBuilder extends AbstractAstConnectNodeBuilder<AstConnectNode> {

    public AstConnectNodeBuilder() {
        this(new AstConnectNode());
    }

    public AstConnectNodeBuilder setLocation(AstValue<URI> location) {
        node.setLocation(location);
        return this;
    }

    public AstConnectNodeBuilder setAwaitName(String barrier) {
        node.setAwaitName(barrier);
        return this;
    }

    public <T> AstConnectNodeBuilder setOption(TypeInfo<T> option, AstValue<T> optionValue) {
        node.getOptions().put(option.getName(), optionValue);
        return this;
    }

    public AstConnectNodeBuilder setOption(String optionName, AstValue<?> optionValue) {
        node.getOptions().put(optionName, optionValue);
        return this;
    }

    @Override
    public AstOpenedNodeBuilder.StreamNested<AstConnectNodeBuilder> addOpenedEvent() {
        return new AstOpenedNodeBuilder.StreamNested<>(this);
    }

    @Override
    public AstBoundNodeBuilder.StreamNested<AstConnectNodeBuilder> addBoundEvent() {
        return new AstBoundNodeBuilder.StreamNested<>(this);
    }

    @Override
    public AstConnectedNodeBuilder.StreamNested<AstConnectNodeBuilder> addConnectedEvent() {
        return new AstConnectedNodeBuilder.StreamNested<>(this);
    }

    @Override
    public AstReadNodeBuilder.StreamNested<AstConnectNodeBuilder> addReadEvent() {
        return new AstReadNodeBuilder.StreamNested<>(this);
    }

    @Override
    public AstDisconnectedNodeBuilder.StreamNested<AstConnectNodeBuilder> addDisconnectedEvent() {
        return new AstDisconnectedNodeBuilder.StreamNested<>(this);
    }

    @Override
    public AstUnboundNodeBuilder.StreamNested<AstConnectNodeBuilder> addUnboundEvent() {
        return new AstUnboundNodeBuilder.StreamNested<>(this);
    }

    @Override
    public AstClosedNodeBuilder.StreamNested<AstConnectNodeBuilder> addClosedEvent() {
        return new AstClosedNodeBuilder.StreamNested<>(this);
    }

    @Override
    public AstWriteNodeBuilder.StreamNested<AstConnectNodeBuilder> addWriteCommand() {
        return new AstWriteNodeBuilder.StreamNested<>(this);
    }

    @Override
    public AstDisconnectNodeBuilder.StreamNested<AstConnectNodeBuilder> addDisconnectCommand() {
        return new AstDisconnectNodeBuilder.StreamNested<>(this);
    }

    @Override
    public AstUnbindNodeBuilder.StreamNested<AstConnectNodeBuilder> addUnbindCommand() {
        return new AstUnbindNodeBuilder.StreamNested<>(this);
    }

    @Override
    public AstCloseNodeBuilder.StreamNested<AstConnectNodeBuilder> addCloseCommand() {
        return new AstCloseNodeBuilder.StreamNested<>(this);
    }

    @Override
    public AstWriteAbortNodeBuilder.StreamNested<AstConnectNodeBuilder> addWriteAbortCommand() {
        return new AstWriteAbortNodeBuilder.StreamNested<>(this);
    }

    @Override
    public AstReadAbortedNodeBuilder.StreamNested<AstConnectNodeBuilder> addReadAbortedEvent() {
        return new AstReadAbortedNodeBuilder.StreamNested<>(this);
    }

    @Override
    public AstReadAbortNodeBuilder.StreamNested<AstConnectNodeBuilder> addReadAbortCommand() {
        return new AstReadAbortNodeBuilder.StreamNested<>(this);
    }

    @Override
    public AstWriteAbortedNodeBuilder.StreamNested<AstConnectNodeBuilder> addWriteAbortedEvent() {
        return new AstWriteAbortedNodeBuilder.StreamNested<>(this);
    }

    @Override
    public AstReadAwaitNodeBuilder.StreamNested<AstConnectNodeBuilder> addReadAwaitBarrier() {
        return new AstReadAwaitNodeBuilder.StreamNested<>(this);
    }

    @Override
    public AstReadNotifyNodeBuilder.StreamNested<AstConnectNodeBuilder> addReadNotifyBarrier() {
        return new AstReadNotifyNodeBuilder.StreamNested<>(this);
    }

    @Override
    public AstWriteAwaitNodeBuilder.StreamNested<AstConnectNodeBuilder> addWriteAwaitBarrier() {
        return new AstWriteAwaitNodeBuilder.StreamNested<>(this);
    }

    @Override
    public AstWriteNotifyNodeBuilder.StreamNested<AstConnectNodeBuilder> addWriteNotifyBarrier() {
        return new AstWriteNotifyNodeBuilder.StreamNested<>(this);
    }

    @Override
    public AstReadOptionNodeBuilder.StreamNested<AstConnectNodeBuilder> addReadOption() {
        return new AstReadOptionNodeBuilder.StreamNested<>(this);
    }

    @Override
    public AstWriteOptionNodeBuilder.StreamNested<AstConnectNodeBuilder> addWriteOption() {
        return new AstWriteOptionNodeBuilder.StreamNested<>(this);
    }

    @Override
    public AstReadConfigNodeBuilder.StreamNested<AstConnectNodeBuilder> addReadConfigEvent() {
        return new AstReadConfigNodeBuilder.StreamNested<>(this);
    }

    @Override
    public AstWriteConfigNodeBuilder.StreamNested<AstConnectNodeBuilder> addWriteConfigCommand() {
        return new AstWriteConfigNodeBuilder.StreamNested<>(this);
    }

    @Override
    public AstFlushNodeBuilder.StreamNested<AstConnectNodeBuilder> addFlushCommand() {
        return new AstFlushNodeBuilder.StreamNested<>(this);
    }

    @Override
    public AstReadClosedNodeBuilder.StreamNested<AstConnectNodeBuilder> addReadCloseCommand() {
        return new AstReadClosedNodeBuilder.StreamNested<>(this);
    }

    @Override
    public AstWriteCloseNodeBuilder.StreamNested<AstConnectNodeBuilder> addWriteCloseCommand() {
        return new AstWriteCloseNodeBuilder.StreamNested<>(this);
    }

    @Override
    public AstConnectNode done() {
        return result;
    }

    private AstConnectNodeBuilder(AstConnectNode node) {
        super(node, node);
    }

    public static final class ScriptNested<R extends AbstractAstNodeBuilder<? extends AstScriptNode, ?>> extends
            AbstractAstConnectNodeBuilder<R> {

        public ScriptNested(R builder) {
            super(new AstConnectNode(), builder);
        }

        public ScriptNested<R> setLocation(AstValue<URI> location) {
            node.setLocation(location);
            return this;
        }

        public ScriptNested<R> setAwaitName(String barrier) {
            node.setAwaitName(barrier);
            return this;
        }

        public <T> ScriptNested<R> setOption(TypeInfo<T> option, AstValue<T> optionValue) {
            node.getOptions().put(option.getName(), optionValue);
            return this;
        }

        public <T> ScriptNested<R> setOption(String optionName, AstValue<?> optionValue) {
            node.getOptions().put(optionName, optionValue);
            return this;
        }

        @Override
        public AstOpenedNodeBuilder.StreamNested<ScriptNested<R>> addOpenedEvent() {
            return new AstOpenedNodeBuilder.StreamNested<>(this);
        }

        @Override
        public AstBoundNodeBuilder.StreamNested<ScriptNested<R>> addBoundEvent() {
            return new AstBoundNodeBuilder.StreamNested<>(this);
        }

        @Override
        public AstConnectedNodeBuilder.StreamNested<ScriptNested<R>> addConnectedEvent() {
            return new AstConnectedNodeBuilder.StreamNested<>(this);
        }

        @Override
        public AstReadNodeBuilder.StreamNested<ScriptNested<R>> addReadEvent() {
            return new AstReadNodeBuilder.StreamNested<>(this);
        }

        @Override
        public AstDisconnectedNodeBuilder.StreamNested<ScriptNested<R>> addDisconnectedEvent() {
            return new AstDisconnectedNodeBuilder.StreamNested<>(this);
        }

        @Override
        public AstUnboundNodeBuilder.StreamNested<ScriptNested<R>> addUnboundEvent() {
            return new AstUnboundNodeBuilder.StreamNested<>(this);
        }

        @Override
        public AstClosedNodeBuilder.StreamNested<ScriptNested<R>> addClosedEvent() {
            return new AstClosedNodeBuilder.StreamNested<>(this);
        }

        @Override
        public AstWriteNodeBuilder.StreamNested<ScriptNested<R>> addWriteCommand() {
            return new AstWriteNodeBuilder.StreamNested<>(this);
        }

        @Override
        public AstDisconnectNodeBuilder.StreamNested<ScriptNested<R>> addDisconnectCommand() {
            return new AstDisconnectNodeBuilder.StreamNested<>(this);
        }

        @Override
        public AstUnbindNodeBuilder.StreamNested<ScriptNested<R>> addUnbindCommand() {
            return new AstUnbindNodeBuilder.StreamNested<>(this);
        }

        @Override
        public AstCloseNodeBuilder.StreamNested<ScriptNested<R>> addCloseCommand() {
            return new AstCloseNodeBuilder.StreamNested<>(this);
        }

        @Override
        public AstWriteAbortNodeBuilder.StreamNested<ScriptNested<R>> addWriteAbortCommand() {
            return new AstWriteAbortNodeBuilder.StreamNested<>(this);
        }

        @Override
        public AstReadAbortedNodeBuilder.StreamNested<ScriptNested<R>> addReadAbortedEvent() {
            return new AstReadAbortedNodeBuilder.StreamNested<>(this);
        }

        @Override
        public AstReadAbortNodeBuilder.StreamNested<ScriptNested<R>> addReadAbortCommand() {
            return new AstReadAbortNodeBuilder.StreamNested<>(this);
        }

        @Override
        public AstWriteAbortedNodeBuilder.StreamNested<ScriptNested<R>> addWriteAbortedEvent() {
            return new AstWriteAbortedNodeBuilder.StreamNested<>(this);
        }

        @Override
        public AstReadAwaitNodeBuilder.StreamNested<ScriptNested<R>> addReadAwaitBarrier() {
            return new AstReadAwaitNodeBuilder.StreamNested<>(this);
        }

        @Override
        public AstReadNotifyNodeBuilder.StreamNested<ScriptNested<R>> addReadNotifyBarrier() {
            return new AstReadNotifyNodeBuilder.StreamNested<>(this);
        }

        @Override
        public AstWriteAwaitNodeBuilder.StreamNested<ScriptNested<R>> addWriteAwaitBarrier() {
            return new AstWriteAwaitNodeBuilder.StreamNested<>(this);
        }

        @Override
        public AstWriteNotifyNodeBuilder.StreamNested<ScriptNested<R>> addWriteNotifyBarrier() {
            return new AstWriteNotifyNodeBuilder.StreamNested<>(this);
        }

        @Override
        public AstReadConfigNodeBuilder.StreamNested<ScriptNested<R>> addReadConfigEvent() {
            return new AstReadConfigNodeBuilder.StreamNested<>(this);
        }

        @Override
        public AstWriteConfigNodeBuilder.StreamNested<ScriptNested<R>> addWriteConfigCommand() {
            return new AstWriteConfigNodeBuilder.StreamNested<>(this);
        }

        @Override
        public AstFlushNodeBuilder.StreamNested<ScriptNested<R>> addFlushCommand() {
            return new AstFlushNodeBuilder.StreamNested<>(this);
        }

        @Override
        public AstReadClosedNodeBuilder.StreamNested<ScriptNested<R>> addReadCloseCommand() {
            return new AstReadClosedNodeBuilder.StreamNested<>(this);
        }

        @Override
        public AstWriteCloseNodeBuilder.StreamNested<ScriptNested<R>> addWriteCloseCommand() {
            return new AstWriteCloseNodeBuilder.StreamNested<>(this);
        }

        @Override
        public AstReadOptionNodeBuilder.StreamNested<ScriptNested<R>> addReadOption() {
            return new AstReadOptionNodeBuilder.StreamNested<>(this);
        }

        @Override
        public AstWriteOptionNodeBuilder.StreamNested<ScriptNested<R>> addWriteOption() {
            return new AstWriteOptionNodeBuilder.StreamNested<>(this);
        }

        @Override
        public R done() {
            AstScriptNode scriptNode = result.node;
            scriptNode.getStreams().add(node);
            return result;
        }

    }

}
