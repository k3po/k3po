/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package com.kaazing.robot.lang.ast;

import static com.kaazing.robot.lang.ast.util.AstUtil.equivalent;

import java.util.LinkedList;
import java.util.List;

public class AstScriptNode extends AstNode {

    private List<AstStreamNode> streams;

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
        if (streams != null) {
            for (AstStreamNode stream : streams) {
                stream.formatNode(sb);
            }
        }
    }
}
