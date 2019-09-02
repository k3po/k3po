/**
 * Copyright 2007-2015, Kaazing Corporation. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kaazing.k3po.lang.internal.ast;

import static org.kaazing.k3po.lang.internal.ast.util.AstUtil.equivalent;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.kaazing.k3po.lang.internal.ast.value.AstValue;

public class AstAcceptNode extends AstStreamNode {

    private String acceptName;
    private String notifyName;
    private Map<String, Object> options;
    private List<AstAcceptableNode> acceptables;

    private AstValue<URI> location;

    public AstAcceptNode() {
    }

    public AstAcceptNode(AstAcceptNode acceptNode) {
        this.regionInfo = acceptNode.regionInfo;
        this.location = acceptNode.location;
        this.acceptName = acceptNode.acceptName;
        this.notifyName = acceptNode.notifyName;
        this.options = acceptNode.options;
    }

    public AstValue<URI> getLocation() {
        return location;
    }

    public void setLocation(AstValue<URI> location) {
        this.location = location;
    }

    public String getAcceptName() {
        return acceptName;
    }

    public void setAcceptName(String acceptName) {
        this.acceptName = acceptName;
    }

    public String getNotifyName() {
        return notifyName;
    }

    public void setNotifyName(String notifyName) {
        this.notifyName = notifyName;
    }

    public Map<String, Object> getOptions() {
        if (options == null) {
            options = new LinkedHashMap<>();
        }

        return options;
    }

    public List<AstAcceptableNode> getAcceptables() {
        if (acceptables == null) {
            acceptables = new LinkedList<>();
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

        if (notifyName != null) {
            hashCode <<= 4;
            hashCode ^= notifyName.hashCode();
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
        return super.equalTo(that) && equivalent(this.location, that.location)
                && equivalent(this.options, that.options)
                && equivalent(this.acceptName, that.acceptName) && equivalent(this.acceptables, that.acceptables);
 }

    @Override
    public <R, P> R accept(Visitor<R, P> visitor, P parameter) {

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
        sb.append("accept ").append(location);

        if (acceptName != null) {
            sb.append(" as ").append(acceptName);
        }

        sb.append('\n');

        if (options != null) {
            for (Map.Entry<String, Object> entry : options.entrySet()) {
                sb.append("        option ")
                  .append(entry.getKey())
                  .append(" ")
                  .append(entry.getValue())
                  .append('\n');
            }
        }
    }

}
