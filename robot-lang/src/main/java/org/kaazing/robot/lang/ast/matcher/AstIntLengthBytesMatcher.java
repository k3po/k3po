/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.lang.ast.matcher;

public class AstIntLengthBytesMatcher extends AstFixedLengthBytesMatcher {

    @Deprecated
    public AstIntLengthBytesMatcher() {
        super(Integer.SIZE / Byte.SIZE);
    }

    public AstIntLengthBytesMatcher(String captureName) {
        super(Integer.SIZE / Byte.SIZE, captureName);
    }

    @Override
    public String toString() {
        String captureName = getCaptureName();
        if (captureName == null) {
            return "int";

        }
        return String.format("(int:%s)", captureName);
    }

    @Override
    public <R, P> R accept(Visitor<R, P> visitor, P parameter) throws Exception {

        return visitor.visit(this, parameter);
    }
}
