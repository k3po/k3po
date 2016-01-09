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
import org.kaazing.k3po.lang.internal.ast.AstChildClosedNode;
import org.kaazing.k3po.lang.internal.ast.AstChildOpenedNode;
import org.kaazing.k3po.lang.internal.ast.AstClosedNode;
import org.kaazing.k3po.lang.internal.ast.AstOpenedNode;
import org.kaazing.k3po.lang.internal.ast.AstReadAwaitNode;
import org.kaazing.k3po.lang.internal.ast.AstReadNotifyNode;
import org.kaazing.k3po.lang.internal.ast.AstStreamNode;
import org.kaazing.k3po.lang.internal.ast.AstUnboundNode;
import org.kaazing.k3po.lang.internal.ast.AstWriteAwaitNode;
import org.kaazing.k3po.lang.internal.ast.AstWriteNotifyNode;

public abstract class AbstractAstServerStreamNodeBuilder<T extends AstStreamNode, R> extends AbstractAstNodeBuilder<T, R> {

    public AbstractAstServerStreamNodeBuilder(T node, R result) {
        super(node, result);
    }

    public abstract AbstractAstNodeBuilder<AstOpenedNode, ? extends AbstractAstNodeBuilder<T, ?>> addOpenedEvent();

    public abstract AbstractAstNodeBuilder<AstBoundNode, ? extends AbstractAstNodeBuilder<T, ?>> addBoundEvent();

    public abstract AbstractAstNodeBuilder<AstChildOpenedNode, ? extends AbstractAstNodeBuilder<T, ?>> addChildOpenedEvent();

    public abstract AbstractAstNodeBuilder<AstChildClosedNode, ? extends AbstractAstNodeBuilder<T, ?>> addChildClosedEvent();

    public abstract AbstractAstNodeBuilder<AstUnboundNode, ? extends AbstractAstNodeBuilder<T, ?>> addUnboundEvent();

    public abstract AbstractAstNodeBuilder<AstClosedNode, ? extends AbstractAstNodeBuilder<T, ?>> addClosedEvent();

    public abstract AbstractAstNodeBuilder<AstReadAwaitNode, ? extends AbstractAstNodeBuilder<T, R>> addReadAwaitBarrier();

    public abstract AbstractAstNodeBuilder<AstReadNotifyNode, ? extends AbstractAstNodeBuilder<T, R>> addReadNotifyBarrier();

    public abstract AbstractAstNodeBuilder<AstWriteAwaitNode, ? extends AbstractAstNodeBuilder<T, R>> addWriteAwaitBarrier();

    public abstract AbstractAstNodeBuilder<AstWriteNotifyNode, ? extends AbstractAstNodeBuilder<T, R>> addWriteNotifyBarrier();
}
