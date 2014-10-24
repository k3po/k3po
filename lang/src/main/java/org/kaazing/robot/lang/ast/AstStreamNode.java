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

package org.kaazing.robot.lang.ast;

import static org.kaazing.robot.lang.ast.util.AstUtil.equivalent;

import java.util.LinkedList;
import java.util.List;

public abstract class AstStreamNode extends AstNode {

    private List<AstStreamableNode> streamables;

    public List<AstStreamableNode> getStreamables() {
        if (streamables == null) {
            streamables = new LinkedList<AstStreamableNode>();
        }
        return streamables;
    }

    @Override
    protected int hashTo() {
        int hashCode = getClass().hashCode();

        if (streamables != null) {
            hashCode <<= 4;
            hashCode ^= streamables.hashCode();
        }

        return hashCode;
    }

    protected boolean equalTo(AstStreamNode that) {
        return equivalent(this.streamables, that.streamables);
    }

    @Override
    protected void describe(StringBuilder buf) {
        describeLine(buf);
        if (streamables != null) {
            for (AstRegion streamable : streamables) {
                streamable.describe(buf);
            }
        }
    }

    protected void describeLine(StringBuilder sb) {
        super.describe(sb);
    }
}
