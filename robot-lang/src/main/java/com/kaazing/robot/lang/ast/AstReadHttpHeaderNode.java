/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package com.kaazing.robot.lang.ast;

import static com.kaazing.robot.lang.ast.util.AstUtil.equivalent;

import com.kaazing.robot.lang.ast.matcher.AstValueMatcher;
import com.kaazing.robot.lang.ast.value.AstLiteralTextValue;

public class AstReadHttpHeaderNode extends AstEventNode {

    private AstLiteralTextValue name;
    private AstValueMatcher value;

    @Override
    public <R, P> R accept(Visitor<R, P> visitor, P parameter) throws Exception {
        return visitor.visit(this, parameter);
    }

    @Override
    public int hashCode() {
        int hashCode = super.hashTo();

        if (name != null) {
            hashCode <<= 4;
            hashCode ^= name.hashCode();
        }
        if (value != null) {
            hashCode <<= 4;
            hashCode ^= value.hashCode();
        }

        return hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        return (this == obj) || ((obj instanceof AstReadHttpHeaderNode) && equals((AstReadHttpHeaderNode) obj));
    }

    protected boolean equals(AstReadHttpHeaderNode that) {
        return super.equalTo(that) && equivalent(this.name, that.name) && equivalent(this.value, that.value);
    }

    @Override
    protected void formatNode(StringBuilder sb) {
        super.formatNode(sb);
        sb.append(String.format("read header %s %s\n", name, value));
    }

    public AstValueMatcher getValue() {
        return value;
    }

    public void setValue(AstValueMatcher value) {
        this.value = value;
    }

    public AstLiteralTextValue getName() {
        return name;
    }

    public void setName(AstLiteralTextValue name) {
        this.name = name;
    }
}
