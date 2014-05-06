/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.lang.ast;

import static org.kaazing.robot.lang.ast.util.AstUtil.equivalent;

import org.kaazing.robot.lang.ast.value.AstValue;

public class AstWriteHttpVersionNode extends AstCommandNode {

    private AstValue version;

    public AstValue getVersion() {
        return version;
    }

    public void setVersion(AstValue version) {
        this.version = version;
    }

    @Override
    public <R, P> R accept(Visitor<R, P> visitor, P parameter) throws Exception {
        return visitor.visit(this, parameter);
    }

    @Override
    public int hashCode() {
        int hashCode = super.hashTo();

        if (version != null) {
            hashCode <<= 4;
            hashCode ^= version.hashCode();
        }

        return hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        return (this == obj) || ((obj instanceof AstWriteHttpVersionNode) && equals((AstWriteHttpVersionNode) obj));
    }

    protected boolean equals(AstWriteHttpVersionNode that) {
        return super.equalTo(that) && equivalent(this.version, that.version);
    }

    @Override
    protected void formatNode(StringBuilder sb) {
        super.formatNode(sb);
        sb.append(String.format("write version %s\n", version));
    }

}
