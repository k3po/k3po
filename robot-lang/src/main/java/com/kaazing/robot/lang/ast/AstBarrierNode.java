/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package com.kaazing.robot.lang.ast;

import static com.kaazing.robot.lang.ast.util.AstUtil.equivalent;

public abstract class AstBarrierNode extends AstStreamableNode {

    private String barrierName;

    public String getBarrierName() {
        return barrierName;
    }

    public void setBarrierName(String barrierName) {
        this.barrierName = barrierName;
    }

    protected int hashTo() {
        int hashCode = super.hashTo();

        if (barrierName != null) {
            hashCode <<= 4;
            hashCode ^= barrierName.hashCode();
        }

        return hashCode;
    }

    protected boolean equalTo(AstBarrierNode that) {
        return equivalent(this.barrierName, that.barrierName);
    }
}
