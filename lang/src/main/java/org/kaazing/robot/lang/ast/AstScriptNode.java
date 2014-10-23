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

public class AstScriptNode extends AstNode {

    private List<AstPropertyNode> properties;
    private List<AstStreamNode> streams;

    public List<AstPropertyNode> getProperties() {
        if (properties == null) {
            properties = new LinkedList<AstPropertyNode>();
        }

        return properties;
    }

    public List<AstStreamNode> getStreams() {
        if (streams == null) {
            streams = new LinkedList<AstStreamNode>();
        }

        return streams;
    }

    @Override
    public <R, P> R accept(Visitor<R, P> visitor, P parameter) throws Exception {

        return visitor.visit(this, parameter);
    }

    @Override
    public int hashCode() {
        int hashCode = super.hashTo();

        if (streams != null) {
            hashCode <<= 4;
            hashCode ^= streams.hashCode();
        }

        return hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        return (this == obj) || ((obj instanceof AstScriptNode) && equalTo((AstScriptNode) obj));
    }

    protected boolean equalTo(AstScriptNode that) {
        return super.equalTo(that) && equivalent(this.streams, that.streams);
    }

    @Override
    protected void formatNode(StringBuilder sb) {
        if (properties != null) {
            for (AstPropertyNode property : properties) {
                property.formatNode(sb);
            }
        }
        if (streams != null) {
            for (AstStreamNode stream : streams) {
                stream.formatNode(sb);
            }
        }
    }
}
