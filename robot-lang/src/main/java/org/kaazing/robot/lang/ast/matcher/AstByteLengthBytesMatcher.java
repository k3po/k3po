/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.lang.ast.matcher;

public class AstByteLengthBytesMatcher extends AstFixedLengthBytesMatcher {

    @Deprecated
    public AstByteLengthBytesMatcher() {
        super(Byte.SIZE / Byte.SIZE);
    }

    public AstByteLengthBytesMatcher(String captureName) {
        super(Byte.SIZE / Byte.SIZE, captureName);
    }

    @Override
    public String toString() {
        String captureName = getCaptureName();
        if (captureName == null) {
            return "byte";

        }
        return String.format("(byte:%s)", captureName);
    }

    @Override
    public <R, P> R accept(Visitor<R, P> visitor, P parameter) throws Exception {

        return visitor.visit(this, parameter);
    }
}
