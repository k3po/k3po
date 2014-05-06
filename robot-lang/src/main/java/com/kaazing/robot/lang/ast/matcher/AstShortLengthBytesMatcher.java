/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package com.kaazing.robot.lang.ast.matcher;

public class AstShortLengthBytesMatcher extends AstFixedLengthBytesMatcher {

    @Deprecated
    public AstShortLengthBytesMatcher() {
        super(Short.SIZE / Byte.SIZE);
    }

    public AstShortLengthBytesMatcher(String captureName) {
        super(Short.SIZE / Byte.SIZE, captureName);
    }

    @Override
    public String toString() {
        String captureName = getCaptureName();
        if (captureName == null) {
            return "short";

        }
        return String.format("(short:%s)", captureName);
    }

    @Override
    public <R, P> R accept(Visitor<R, P> visitor, P parameter) throws Exception {

        return visitor.visit(this, parameter);
    }
}
