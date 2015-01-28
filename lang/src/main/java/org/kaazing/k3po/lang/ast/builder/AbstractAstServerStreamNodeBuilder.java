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

package org.kaazing.k3po.lang.ast.builder;

import org.kaazing.k3po.lang.ast.AstBoundNode;
import org.kaazing.k3po.lang.ast.AstChildClosedNode;
import org.kaazing.k3po.lang.ast.AstChildOpenedNode;
import org.kaazing.k3po.lang.ast.AstClosedNode;
import org.kaazing.k3po.lang.ast.AstOpenedNode;
import org.kaazing.k3po.lang.ast.AstReadAwaitNode;
import org.kaazing.k3po.lang.ast.AstReadNotifyNode;
import org.kaazing.k3po.lang.ast.AstStreamNode;
import org.kaazing.k3po.lang.ast.AstUnboundNode;
import org.kaazing.k3po.lang.ast.AstWriteAwaitNode;
import org.kaazing.k3po.lang.ast.AstWriteNotifyNode;

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
