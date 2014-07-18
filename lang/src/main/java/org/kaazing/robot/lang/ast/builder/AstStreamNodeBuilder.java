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

package org.kaazing.robot.lang.ast.builder;

import org.kaazing.robot.lang.ast.AstBoundNode;
import org.kaazing.robot.lang.ast.AstCloseNode;
import org.kaazing.robot.lang.ast.AstClosedNode;
import org.kaazing.robot.lang.ast.AstConnectedNode;
import org.kaazing.robot.lang.ast.AstDisconnectNode;
import org.kaazing.robot.lang.ast.AstDisconnectedNode;
import org.kaazing.robot.lang.ast.AstOpenedNode;
import org.kaazing.robot.lang.ast.AstReadValueNode;
import org.kaazing.robot.lang.ast.AstScriptNode;
import org.kaazing.robot.lang.ast.AstStreamNode;
import org.kaazing.robot.lang.ast.AstUnbindNode;
import org.kaazing.robot.lang.ast.AstUnboundNode;
import org.kaazing.robot.lang.ast.AstWriteValueNode;

public abstract class AstStreamNodeBuilder<T extends AstStreamNode> extends
        AbstractAstStreamNodeBuilder<T, AstScriptNodeBuilder> {

    public AstStreamNodeBuilder(T node, AstScriptNodeBuilder builder) {
        super(node, builder);
    }

    @Override
    public int line() {
        return result.line();
    }

    @Override
    public int line(int line) {
        return result.line(line);
    }

    public abstract AbstractAstStreamableNodeBuilder<AstOpenedNode, ? extends AstStreamNodeBuilder<T>> addOpenedEvent();

    public abstract AbstractAstStreamableNodeBuilder<AstBoundNode, ? extends AstStreamNodeBuilder<T>> addBoundEvent();

    public abstract AbstractAstStreamableNodeBuilder<AstConnectedNode, ? extends AstStreamNodeBuilder<T>> addConnectedEvent();

    public abstract AbstractAstStreamableNodeBuilder<AstReadValueNode, ? extends AstStreamNodeBuilder<T>> addReadEvent();

    public abstract AbstractAstStreamableNodeBuilder<AstDisconnectedNode, ? extends AstStreamNodeBuilder<T>>
            addDisconnectedEvent();

    public abstract AbstractAstStreamableNodeBuilder<AstUnboundNode, ? extends AstStreamNodeBuilder<T>> addUnboundEvent();

    public abstract AbstractAstStreamableNodeBuilder<AstClosedNode, ? extends AstStreamNodeBuilder<T>> addClosedEvent();

    public abstract AbstractAstStreamableNodeBuilder<AstWriteValueNode, ? extends AstStreamNodeBuilder<T>> addWriteCommand();

    public abstract AbstractAstStreamableNodeBuilder<AstDisconnectNode, ? extends AstStreamNodeBuilder<T>>
            addDisconnectCommand();

    public abstract AbstractAstStreamableNodeBuilder<AstUnbindNode, ? extends AstStreamNodeBuilder<T>> addUnbindCommand();

    public abstract AbstractAstStreamableNodeBuilder<AstCloseNode, ? extends AstStreamNodeBuilder<T>> addCloseCommand();

    @Override
    public final AstScriptNodeBuilder done() {
        AstScriptNode scriptNode = result.node;
        scriptNode.getStreams().add(node);
        return result;
    }
}
