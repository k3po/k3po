/*
 * Copyright (c) 2014 "Kaazing Corporation," (www.kaazing.com)
 *
 * This file is part of Robot.
 *
 * Robot is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.kaazing.k3po.lang.ast;

import static org.kaazing.k3po.lang.ast.util.AstUtil.equivalent;

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
    protected int hashTo() {
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
    protected boolean equalTo(AstRegion that) {
        return that instanceof AstAcceptNode && equalTo((AstAcceptNode) that);
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
    protected void describe(StringBuilder buf) {
        super.describe(buf);

        if (acceptables != null) {
            for (AstAcceptableNode acceptable : acceptables) {
                acceptable.describe(buf);
            }
        }
    }

    @Override
    protected void describeLine(StringBuilder sb) {
        super.describeLine(sb);
        sb.append("accept ");
        sb.append(location);

        if (acceptName != null) {
            sb.append(" as ");
            sb.append(acceptName);
        }

        sb.append('\n');
    }

}
