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

import org.kaazing.k3po.lang.ast.AstCloseNode;
import org.kaazing.k3po.lang.ast.AstStreamNode;

public class AstCloseNodeBuilder extends AbstractAstStreamableNodeBuilder<AstCloseNode, AstCloseNode> {

    public AstCloseNodeBuilder() {
        this(new AstCloseNode());
    }

    @Override
    public AstCloseNode done() {
        return result;
    }

    private AstCloseNodeBuilder(AstCloseNode node) {
        super(node, node);
    }

    public static class StreamNested<R extends AbstractAstNodeBuilder<? extends AstStreamNode, ?>> extends
            AbstractAstStreamableNodeBuilder<AstCloseNode, R> {

        public StreamNested(R builder) {
            super(new AstCloseNode(), builder);
        }

        @Override
        public R done() {
            AstStreamNode streamNode = result.node;
            streamNode.getStreamables().add(node);
            return result;
        }

    }
}
