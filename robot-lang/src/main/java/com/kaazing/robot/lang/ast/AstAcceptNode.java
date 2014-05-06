/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package com.kaazing.robot.lang.ast;

import static com.kaazing.robot.lang.ast.util.AstUtil.equivalent;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class AstAcceptNode extends AstStreamNode {

    private URI location;
    private Map<String, Object> options;
    private String acceptName;
    private List<AstAcceptableNode> acceptables;

    public URI getLocation() {
        return location;
    }

    public void setLocation(URI location) {
        this.location = location;
    }

    public String getAcceptName() {
        return acceptName;
    }

    public void setAcceptName(String acceptName) {
        this.acceptName = acceptName;
    }

    public Map<String, Object> getOptions() {
        if (options == null) {
            options = new LinkedHashMap<String, Object>();
        }

        return options;
    }

    public List<AstAcceptableNode> getAcceptables() {
        if (acceptables == null) {
            acceptables = new LinkedList<AstAcceptableNode>();
        }

        return acceptables;
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

        if (acceptName != null) {
            hashCode <<= 4;
            hashCode ^= acceptName.hashCode();
        }

        if (acceptables != null) {
            hashCode <<= 4;
            hashCode ^= acceptables.hashCode();
        }

        return hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        return (this == obj) || ((obj instanceof AstAcceptNode) && equalTo((AstAcceptNode) obj));
    }

    protected boolean equalTo(AstAcceptNode that) {
        return super.equalTo(that) && equivalent(this.location, that.location) && equivalent(this.options, that.options)
                && equivalent(this.acceptName, that.acceptName) && equivalent(this.acceptables, that.acceptables);
    }

    @Override
    public <R, P> R accept(Visitor<R, P> visitor, P parameter) throws Exception {

        return visitor.visit(this, parameter);
    }

    @Override
    protected void formatNode(StringBuilder sb) {
        super.formatNode(sb);

        if (acceptables != null) {
            for (AstAcceptableNode acceptable : acceptables) {
                acceptable.formatNode(sb);
            }
        }
    }

    @Override
    protected void formatNodeLine(StringBuilder sb) {
        super.formatNodeLine(sb);
        sb.append("accept ");
        sb.append(location);

        if (acceptName != null) {
            sb.append(" as ");
            sb.append(acceptName);
        }

        sb.append('\n');
    }

}
