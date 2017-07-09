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

import org.kaazing.k3po.lang.internal.ast.AstBoundNode;
import org.kaazing.k3po.lang.internal.ast.AstCloseNode;
import org.kaazing.k3po.lang.internal.ast.AstClosedNode;
import org.kaazing.k3po.lang.internal.ast.AstConnectedNode;
import org.kaazing.k3po.lang.internal.ast.AstDisconnectNode;
import org.kaazing.k3po.lang.internal.ast.AstDisconnectedNode;
import org.kaazing.k3po.lang.internal.ast.AstOpenedNode;
import org.kaazing.k3po.lang.internal.ast.AstReadAbortNode;
import org.kaazing.k3po.lang.internal.ast.AstReadAbortedNode;
import org.kaazing.k3po.lang.internal.ast.AstReadAwaitNode;
import org.kaazing.k3po.lang.internal.ast.AstReadClosedNode;
import org.kaazing.k3po.lang.internal.ast.AstReadConfigNode;
import org.kaazing.k3po.lang.internal.ast.AstReadNotifyNode;
import org.kaazing.k3po.lang.internal.ast.AstReadOptionNode;
import org.kaazing.k3po.lang.internal.ast.AstReadValueNode;
import org.kaazing.k3po.lang.internal.ast.AstStreamNode;
import org.kaazing.k3po.lang.internal.ast.AstUnbindNode;
import org.kaazing.k3po.lang.internal.ast.AstUnboundNode;
import org.kaazing.k3po.lang.internal.ast.AstWriteAbortNode;
import org.kaazing.k3po.lang.internal.ast.AstWriteAbortedNode;
import org.kaazing.k3po.lang.internal.ast.AstWriteAwaitNode;
import org.kaazing.k3po.lang.internal.ast.AstWriteCloseNode;
import org.kaazing.k3po.lang.internal.ast.AstWriteConfigNode;
import org.kaazing.k3po.lang.internal.ast.AstWriteFlushNode;
import org.kaazing.k3po.lang.internal.ast.AstWriteNotifyNode;
import org.kaazing.k3po.lang.internal.ast.AstWriteOptionNode;
import org.kaazing.k3po.lang.internal.ast.AstWriteValueNode;

public abstract class AbstractAstStreamNodeBuilder<T extends AstStreamNode, R> extends AbstractAstNodeBuilder<T, R> {

    public AbstractAstStreamNodeBuilder(T node, R result) {
        super(node, result);
    }

    public abstract AbstractAstStreamableNodeBuilder<AstOpenedNode, ? extends AbstractAstStreamNodeBuilder<T, R>>
            addOpenedEvent();

    public abstract AbstractAstStreamableNodeBuilder<AstBoundNode, ? extends AbstractAstStreamNodeBuilder<T, R>> addBoundEvent();

    public abstract AbstractAstStreamableNodeBuilder<AstConnectedNode, ? extends AbstractAstStreamNodeBuilder<T, R>>
            addConnectedEvent();

    public abstract AbstractAstStreamableNodeBuilder<AstReadValueNode, ? extends AbstractAstStreamNodeBuilder<T, R>>
            addReadEvent();

    public abstract AbstractAstStreamableNodeBuilder<AstDisconnectedNode, ? extends AbstractAstStreamNodeBuilder<T, R>>
            addDisconnectedEvent();

    public abstract AbstractAstStreamableNodeBuilder<AstUnboundNode, ? extends AbstractAstStreamNodeBuilder<T, R>>
            addUnboundEvent();

    public abstract AbstractAstStreamableNodeBuilder<AstClosedNode, ? extends AbstractAstStreamNodeBuilder<T, R>>
            addClosedEvent();

    public abstract AbstractAstStreamableNodeBuilder<AstWriteValueNode, ? extends AbstractAstStreamNodeBuilder<T, R>>
            addWriteCommand();

    public abstract AbstractAstStreamableNodeBuilder<AstDisconnectNode, ? extends AbstractAstStreamNodeBuilder<T, R>>
            addDisconnectCommand();

    public abstract AbstractAstStreamableNodeBuilder<AstUnbindNode, ? extends AbstractAstStreamNodeBuilder<T, R>>
            addUnbindCommand();

    public abstract AbstractAstStreamableNodeBuilder<AstCloseNode, ? extends AbstractAstStreamNodeBuilder<T, R>>
            addCloseCommand();

    public abstract AbstractAstStreamableNodeBuilder<AstWriteAbortNode, ? extends AbstractAstStreamNodeBuilder<T, R>>
            addWriteAbortCommand();

    public abstract AbstractAstStreamableNodeBuilder<AstReadAbortedNode, ? extends AbstractAstStreamNodeBuilder<T, R>>
            addReadAbortedEvent();

    public abstract AbstractAstStreamableNodeBuilder<AstReadAbortNode, ? extends AbstractAstStreamNodeBuilder<T, R>>
            addReadAbortCommand();

    public abstract AbstractAstStreamableNodeBuilder<AstWriteAbortedNode, ? extends AbstractAstStreamNodeBuilder<T, R>>
            addWriteAbortedEvent();

    public abstract AbstractAstStreamableNodeBuilder<AstReadAwaitNode, ? extends AbstractAstStreamNodeBuilder<T, R>>
            addReadAwaitBarrier();

    public abstract AbstractAstStreamableNodeBuilder<AstReadNotifyNode, ? extends AbstractAstStreamNodeBuilder<T, R>>
            addReadNotifyBarrier();

    public abstract AbstractAstStreamableNodeBuilder<AstWriteAwaitNode, ? extends AbstractAstStreamNodeBuilder<T, R>>
            addWriteAwaitBarrier();

    public abstract AbstractAstStreamableNodeBuilder<AstWriteNotifyNode, ? extends AbstractAstStreamNodeBuilder<T, R>>
            addWriteNotifyBarrier();

    public abstract AbstractAstStreamableNodeBuilder<AstReadConfigNode, ? extends AbstractAstStreamNodeBuilder<T, R>>
        addReadConfigEvent();

    public abstract AbstractAstStreamableNodeBuilder<AstWriteConfigNode, ? extends AbstractAstStreamNodeBuilder<T, R>>
        addWriteConfigCommand();

    public abstract AbstractAstStreamableNodeBuilder<AstWriteFlushNode, ? extends AbstractAstStreamNodeBuilder<T, R>>
        addFlushCommand();

    public abstract AbstractAstStreamableNodeBuilder<AstReadClosedNode, ? extends AbstractAstStreamNodeBuilder<T, R>>
        addReadCloseCommand();

    public abstract AbstractAstStreamableNodeBuilder<AstWriteCloseNode, ? extends AbstractAstStreamNodeBuilder<T, R>>
        addWriteCloseCommand();

    public abstract AbstractAstStreamableNodeBuilder<AstReadOptionNode, ? extends AbstractAstStreamNodeBuilder<T, R>>
        addReadOption();

    public abstract AbstractAstStreamableNodeBuilder<AstWriteOptionNode, ? extends AbstractAstStreamNodeBuilder<T, R>>
        addWriteOption();
}
