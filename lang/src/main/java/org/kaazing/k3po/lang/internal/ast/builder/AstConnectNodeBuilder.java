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

import org.kaazing.k3po.lang.internal.ast.AstConnectNode;
import org.kaazing.k3po.lang.internal.ast.AstScriptNode;
import org.kaazing.k3po.lang.internal.ast.value.AstLocation;

public final class AstConnectNodeBuilder extends AbstractAstConnectNodeBuilder<AstConnectNode> {

    public AstConnectNodeBuilder() {
        this(new AstConnectNode());
    }

    public AstConnectNodeBuilder setLocation(AstLocation location) {
        node.setLocation(location);
        return this;
    }

    public AstConnectNodeBuilder setBarrier(String barrier) {
        node.setBarrier(barrier);
        return this;
    }

    @Override
    public AstOpenedNodeBuilder.StreamNested<AstConnectNodeBuilder> addOpenedEvent() {
        return new AstOpenedNodeBuilder.StreamNested<AstConnectNodeBuilder>(this);
    }

    @Override
    public AstBoundNodeBuilder.StreamNested<AstConnectNodeBuilder> addBoundEvent() {
        return new AstBoundNodeBuilder.StreamNested<AstConnectNodeBuilder>(this);
    }

    @Override
    public AstConnectedNodeBuilder.StreamNested<AstConnectNodeBuilder> addConnectedEvent() {
        return new AstConnectedNodeBuilder.StreamNested<AstConnectNodeBuilder>(this);
    }

    @Override
    public AstReadNodeBuilder.StreamNested<AstConnectNodeBuilder> addReadEvent() {
        return new AstReadNodeBuilder.StreamNested<AstConnectNodeBuilder>(this);
    }

    @Override
    public AstDisconnectedNodeBuilder.StreamNested<AstConnectNodeBuilder> addDisconnectedEvent() {
        return new AstDisconnectedNodeBuilder.StreamNested<AstConnectNodeBuilder>(this);
    }

    @Override
    public AstUnboundNodeBuilder.StreamNested<AstConnectNodeBuilder> addUnboundEvent() {
        return new AstUnboundNodeBuilder.StreamNested<AstConnectNodeBuilder>(this);
    }

    @Override
    public AstClosedNodeBuilder.StreamNested<AstConnectNodeBuilder> addClosedEvent() {
        return new AstClosedNodeBuilder.StreamNested<AstConnectNodeBuilder>(this);
    }

    @Override
    public AstWriteNodeBuilder.StreamNested<AstConnectNodeBuilder> addWriteCommand() {
        return new AstWriteNodeBuilder.StreamNested<AstConnectNodeBuilder>(this);
    }

    @Override
    public AstDisconnectNodeBuilder.StreamNested<AstConnectNodeBuilder> addDisconnectCommand() {
        return new AstDisconnectNodeBuilder.StreamNested<AstConnectNodeBuilder>(this);
    }

    @Override
    public AstUnbindNodeBuilder.StreamNested<AstConnectNodeBuilder> addUnbindCommand() {
        return new AstUnbindNodeBuilder.StreamNested<AstConnectNodeBuilder>(this);
    }

    @Override
    public AstCloseNodeBuilder.StreamNested<AstConnectNodeBuilder> addCloseCommand() {
        return new AstCloseNodeBuilder.StreamNested<AstConnectNodeBuilder>(this);
    }

    @Override
    public AstReadAwaitNodeBuilder.StreamNested<AstConnectNodeBuilder> addReadAwaitBarrier() {
        return new AstReadAwaitNodeBuilder.StreamNested<AstConnectNodeBuilder>(this);
    }

    @Override
    public AstReadNotifyNodeBuilder.StreamNested<AstConnectNodeBuilder> addReadNotifyBarrier() {
        return new AstReadNotifyNodeBuilder.StreamNested<AstConnectNodeBuilder>(this);
    }

    @Override
    public AstWriteAwaitNodeBuilder.StreamNested<AstConnectNodeBuilder> addWriteAwaitBarrier() {
        return new AstWriteAwaitNodeBuilder.StreamNested<AstConnectNodeBuilder>(this);
    }

    @Override
    public AstWriteNotifyNodeBuilder.StreamNested<AstConnectNodeBuilder> addWriteNotifyBarrier() {
        return new AstWriteNotifyNodeBuilder.StreamNested<AstConnectNodeBuilder>(this);
    }

    @Override
    public AstReadOptionNodeBuilder.StreamNested<AstConnectNodeBuilder> addReadOption() {
        return new AstReadOptionNodeBuilder.StreamNested<AstConnectNodeBuilder>(this);
    }

    @Override
    public AstWriteOptionNodeBuilder.StreamNested<AstConnectNodeBuilder> addWriteOption() {
        return new AstWriteOptionNodeBuilder.StreamNested<AstConnectNodeBuilder>(this);
    }

    @Override
    public AstReadConfigNodeBuilder.StreamNested<AstConnectNodeBuilder> addReadConfigEvent() {
        return new AstReadConfigNodeBuilder.StreamNested<AstConnectNodeBuilder>(this);
    }

    @Override
    public AstWriteConfigNodeBuilder.StreamNested<AstConnectNodeBuilder> addWriteConfigCommand() {
        return new AstWriteConfigNodeBuilder.StreamNested<AstConnectNodeBuilder>(this);
    }

    @Override
    public AstFlushNodeBuilder.StreamNested<AstConnectNodeBuilder> addFlushCommand() {
        return new AstFlushNodeBuilder.StreamNested<AstConnectNodeBuilder>(this);
    }

    @Override
    public AstReadClosedNodeBuilder.StreamNested<AstConnectNodeBuilder> addReadCloseCommand() {
        return new AstReadClosedNodeBuilder.StreamNested<AstConnectNodeBuilder>(this);
    }

    @Override
    public AstWriteCloseNodeBuilder.StreamNested<AstConnectNodeBuilder> addWriteCloseCommand() {
        return new AstWriteCloseNodeBuilder.StreamNested<AstConnectNodeBuilder>(this);
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

        public ScriptNested<R> setLocation(AstLocation location) {
            node.setLocation(location);
            return this;
        }

        public ScriptNested<R> setTransport(AstLocation transport) {
            node.getOptions().put("transport", transport);
            return this;
        }

        public ScriptNested<R> setBarrier(String barrier) {
            node.setBarrier(barrier);
            return this;
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
        public AstConnectedNodeBuilder.StreamNested<ScriptNested<R>> addConnectedEvent() {
            return new AstConnectedNodeBuilder.StreamNested<ScriptNested<R>>(this);
        }

        @Override
        public AstReadNodeBuilder.StreamNested<ScriptNested<R>> addReadEvent() {
            return new AstReadNodeBuilder.StreamNested<ScriptNested<R>>(this);
        }

        @Override
        public AstDisconnectedNodeBuilder.StreamNested<ScriptNested<R>> addDisconnectedEvent() {
            return new AstDisconnectedNodeBuilder.StreamNested<ScriptNested<R>>(this);
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
        public AstWriteNodeBuilder.StreamNested<ScriptNested<R>> addWriteCommand() {
            return new AstWriteNodeBuilder.StreamNested<ScriptNested<R>>(this);
        }

        @Override
        public AstDisconnectNodeBuilder.StreamNested<ScriptNested<R>> addDisconnectCommand() {
            return new AstDisconnectNodeBuilder.StreamNested<ScriptNested<R>>(this);
        }

        @Override
        public AstUnbindNodeBuilder.StreamNested<ScriptNested<R>> addUnbindCommand() {
            return new AstUnbindNodeBuilder.StreamNested<ScriptNested<R>>(this);
        }

        @Override
        public AstCloseNodeBuilder.StreamNested<ScriptNested<R>> addCloseCommand() {
            return new AstCloseNodeBuilder.StreamNested<ScriptNested<R>>(this);
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
        public AstReadConfigNodeBuilder.StreamNested<ScriptNested<R>> addReadConfigEvent() {
            return new AstReadConfigNodeBuilder.StreamNested<ScriptNested<R>>(this);
        }

        @Override
        public AstWriteConfigNodeBuilder.StreamNested<ScriptNested<R>> addWriteConfigCommand() {
            return new AstWriteConfigNodeBuilder.StreamNested<ScriptNested<R>>(this);
        }

        @Override
        public AstFlushNodeBuilder.StreamNested<ScriptNested<R>> addFlushCommand() {
            return new AstFlushNodeBuilder.StreamNested<ScriptNested<R>>(this);
        }

        @Override
        public AstReadClosedNodeBuilder.StreamNested<ScriptNested<R>> addReadCloseCommand() {
            return new AstReadClosedNodeBuilder.StreamNested<ScriptNested<R>>(this);
        }

        @Override
        public AstWriteCloseNodeBuilder.StreamNested<ScriptNested<R>> addWriteCloseCommand() {
            return new AstWriteCloseNodeBuilder.StreamNested<ScriptNested<R>>(this);
        }

        @Override
        public AstReadOptionNodeBuilder.StreamNested<ScriptNested<R>> addReadOption() {
            return new AstReadOptionNodeBuilder.StreamNested<ScriptNested<R>>(this);
        }

        @Override
        public AstWriteOptionNodeBuilder.StreamNested<ScriptNested<R>> addWriteOption() {
            return new AstWriteOptionNodeBuilder.StreamNested<ScriptNested<R>>(this);
        }

        @Override
        public R done() {
            AstScriptNode scriptNode = result.node;
            scriptNode.getStreams().add(node);
            return result;
        }

    }

}
