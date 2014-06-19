/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.lang.ast.matcher;

public class AstLongLengthBytesMatcher extends AstFixedLengthBytesMatcher {

    @Deprecated
    public AstLongLengthBytesMatcher() {
        super(Long.SIZE / Byte.SIZE);
    }

    public AstLongLengthBytesMatcher(String captureName) {
        super(Long.SIZE / Byte.SIZE, captureName);
    }

    @Override
    public String toString() {
        String captureName = getCaptureName();
        if (captureName == null) {
            return "long";

        }
        return String.format("(long:%s)", captureName);
    }

    @Override
    public <R, P> R accept(Visitor<R, P> visitor, P parameter) throws Exception {

        return visitor.visit(this, parameter);
    }
}
