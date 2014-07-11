/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.lang.ast;

import static org.kaazing.robot.lang.ast.util.AstUtil.equivalent;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;

public class AstConnectNode extends AstStreamNode {

    private URI location;
    private Map<String, Object> options;

    public URI getLocation() {
        return location;
    }

    public void setLocation(URI location) {
        this.location = location;
    }

    public Map<String, Object> getOptions() {
        if (options == null) {
            options = new LinkedHashMap<String, Object>();
        }

        return options;
    }

    @Override
    public int hashCode() {
        int hashCode = super.hashTo();

        if (location != null) {
            hashCode <<= 4;
            hashCode ^= location.hashCode();
        }

        if (options != null) {
            hashCode <<= 4;
            hashCode ^= options.hashCode();
        }

        return hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        return (this == obj) || ((obj instanceof AstConnectNode) && equalTo((AstConnectNode) obj));
    }

    protected boolean equalTo(AstConnectNode that) {
        return super.equalTo(that) && equivalent(this.location, that.location) && equivalent(this.options, that.options);
    }

    @Override
    public <R, P> R accept(Visitor<R, P> visitor, P parameter) throws Exception {

        return visitor.visit(this, parameter);
    }

    @Override
    protected void formatNodeLine(StringBuilder sb) {
        super.formatNodeLine(sb);
        sb.append(String.format("connect %s\n", location));
    }
}
