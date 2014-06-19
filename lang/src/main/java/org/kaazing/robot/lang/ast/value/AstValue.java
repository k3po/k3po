/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.lang.ast.value;

public abstract class AstValue {
    public abstract <R, P> R accept(Visitor<R, P> visitor, P parameter) throws Exception;

    public interface Visitor<R, P> {

        R visit(AstExpressionValue value, P parameter) throws Exception;

        R visit(AstLiteralTextValue value, P parameter) throws Exception;

        R visit(AstLiteralBytesValue value, P parameter) throws Exception;
    }
}
