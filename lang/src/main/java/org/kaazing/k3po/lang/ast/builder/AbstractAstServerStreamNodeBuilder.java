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
