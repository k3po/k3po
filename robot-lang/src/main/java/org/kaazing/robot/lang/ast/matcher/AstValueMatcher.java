/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.lang.ast.matcher;

public abstract class AstValueMatcher {

    public abstract <R, P> R accept(Visitor<R, P> visitor, P parameter) throws Exception;

    public interface Visitor<R, P> {

        R visit(AstExpressionMatcher matcher, P parameter) throws Exception;

        R visit(AstFixedLengthBytesMatcher matcher, P parameter) throws Exception;

        R visit(AstRegexMatcher matcher, P parameter) throws Exception;

        R visit(AstExactTextMatcher matcher, P parameter) throws Exception;

        R visit(AstExactBytesMatcher matcher, P parameter) throws Exception;

        R visit(AstVariableLengthBytesMatcher matcher, P parameter) throws Exception;

        R visit(AstByteLengthBytesMatcher matcher, P parameter) throws Exception;

        R visit(AstShortLengthBytesMatcher matcher, P parameter) throws Exception;

        R visit(AstIntLengthBytesMatcher matcher, P parameter) throws Exception;

        R visit(AstLongLengthBytesMatcher matcher, P parameter) throws Exception;

    }
}
