/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package com.kaazing.robot.lang.ast;

import static com.kaazing.robot.lang.ast.util.AstUtil.equivalent;

import com.kaazing.robot.lang.ast.matcher.AstValueMatcher;
import com.kaazing.robot.lang.ast.value.AstLiteralTextValue;

public class AstReadHttpParameterNode extends AstEventNode {

    private AstLiteralTextValue key;
    private AstValueMatcher value;

    @Override
    public <R, P> R accept(Visitor<R, P> visitor, P parameter) throws Exception {
        return visitor.visit(this, parameter);
    }

    @Override
    public int hashCode() {
        int hashCode = super.hashTo();

        if (key != null) {
            hashCode <<= 4;
            hashCode ^= key.hashCode();
        }
        if (value != null) {
            hashCode <<= 4;
            hashCode ^= value.hashCode();
        }

        return hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        return (this == obj) || ((obj instanceof AstReadHttpParameterNode) && equals((AstReadHttpParameterNode) obj));
    }

    protected boolean equals(AstReadHttpParameterNode that) {
        return super.equalTo(that) && equivalent(this.key, that.key)
                && equivalent(this.value, that.value);
    }

    @Override
    protected void formatNode(StringBuilder sb) {
        super.formatNode(sb);
        sb.append(String.format("read parameter %s %s\n", key, value));
    }

    public AstLiteralTextValue getKey() {
        return key;
    }

    public void setKey(AstLiteralTextValue key) {
        this.key = key;
    }

    public AstValueMatcher getValue() {
        return value;
    }

    public void setValue(AstValueMatcher value) {
        this.value = value;
    }
}
