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

import org.kaazing.k3po.lang.ast.AstReadAwaitNode;
import org.kaazing.k3po.lang.ast.AstStreamNode;

public class AstReadAwaitNodeBuilder extends AbstractAstStreamableNodeBuilder<AstReadAwaitNode, AstReadAwaitNode> {

    public AstReadAwaitNodeBuilder() {
        this(new AstReadAwaitNode());
    }

    public AstReadAwaitNodeBuilder setBarrierName(String barrierName) {
        node.setBarrierName(barrierName);
        return this;
    }

    @Override
    public AstReadAwaitNode done() {
        return result;
    }

    private AstReadAwaitNodeBuilder(AstReadAwaitNode node) {
        super(node, node);
    }

    public static class StreamNested<R extends AbstractAstNodeBuilder<? extends AstStreamNode, ?>> extends
            AbstractAstStreamableNodeBuilder<AstReadAwaitNode, R> {

        public StreamNested(R builder) {
            super(new AstReadAwaitNode(), builder);
        }

        public StreamNested<R> setBarrierName(String barrierName) {
            node.setBarrierName(barrierName);
            return this;
        }

        @Override
        public R done() {
            AstStreamNode streamNode = result.node;
            streamNode.getStreamables().add(node);
            return result;
        }
    }
}
