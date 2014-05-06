/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package com.kaazing.robot.lang.ast;

import static com.kaazing.robot.lang.ast.util.AstUtil.equivalent;

import java.util.LinkedList;
import java.util.List;

import com.kaazing.robot.lang.LocationInfo;

public abstract class AstStreamNode extends AstNode {

    private List<AstStreamableNode> streamables;
    private LocationInfo endLocation;

    public List<AstStreamableNode> getStreamables() {
        if (streamables == null) {
            streamables = new LinkedList<AstStreamableNode>();
        }

        return streamables;
    }

    public LocationInfo getEndLocation() {
        return endLocation;
    }

    public void setEndLocation(LocationInfo info) {
        endLocation = info;
    }

    @Override
    protected int hashTo() {
        int hashCode = super.hashTo();

        if (streamables != null) {
            hashCode <<= 4;
            hashCode ^= streamables.hashCode();
        }

        return hashCode;
    }

    protected boolean equalTo(AstStreamNode that) {
        return super.equalTo(that) && equivalent(this.streamables, that.streamables);
    }

    @Override
    protected void formatNode(StringBuilder sb) {
        formatNodeLine(sb);
        if (streamables != null) {
            for (AstStreamableNode streamable : streamables) {
                streamable.formatNode(sb);
            }
        }
    }

    protected void formatNodeLine(StringBuilder sb) {
        super.formatNode(sb);
    }
}
