/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.lang.ast;

import static java.lang.String.format;

public class AstWriteNotifyNode extends AstBarrierNode {

    @Override
    public <R, P> R accept(Visitor<R, P> visitor, P parameter) throws Exception {
        return visitor.visit(this, parameter);
    }

    @Override
    public int hashCode() {
        return hashTo();
    }

    @Override
    public boolean equals(Object obj) {
        return (this == obj) || ((obj instanceof AstWriteNotifyNode) && equalTo((AstWriteNotifyNode) obj));
    }

    @Override
    protected void formatNode(StringBuilder sb) {
        super.formatNode(sb);
        sb.append(format("write notify %s\n", getBarrierName()));
    }
}
