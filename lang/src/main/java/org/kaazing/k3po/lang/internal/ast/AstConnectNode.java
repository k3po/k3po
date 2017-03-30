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
import java.util.Map;

import org.kaazing.k3po.lang.internal.ast.value.AstValue;

public class AstConnectNode extends AstStreamNode {

    private Map<String, Object> options;
    private String awaitName;

    private AstValue<URI> location;

    public AstConnectNode() {
    }

    public AstConnectNode(AstConnectNode connectNode) {
        this.regionInfo = connectNode.regionInfo;
        this.location = connectNode.location;
        this.awaitName = connectNode.awaitName;
        this.options = connectNode.options;
    }

    public String getAwaitName() {
        return awaitName;
    }

    public void setAwaitName(String awaitName) {
        this.awaitName = awaitName;
    }

    public AstValue<URI> getLocation() {
        return location;
    }

    public void setLocation(AstValue<URI> location) {
        this.location = location;
    }

    public Map<String, Object> getOptions() {
        if (options == null) {
            options = new LinkedHashMap<>();
        }

        return options;
    }

    @Override
    protected int hashTo() {
        int hashCode = getClass().hashCode();

        if (location != null) {
            hashCode <<= 4;
            hashCode ^= location.hashCode();
        }

        if (options != null) {
            hashCode <<= 4;
            hashCode ^= options.hashCode();
        }

        if (awaitName != null) {
            hashCode <<= 4;
            hashCode ^= awaitName.hashCode();
        }

        return hashCode;
    }

    @Override
    public <R, P> R accept(Visitor<R, P> visitor, P parameter) {
        return visitor.visit(this, parameter);
    }

    @Override
    protected boolean equalTo(AstRegion that) {
        return that instanceof AstConnectNode && equalTo((AstConnectNode) that);
    }

    protected boolean equalTo(AstConnectNode that) {
        return super.equalTo(that) && equivalent(this.location, that.location) && equivalent(this.options, that.options);
    }

    @Override
    protected void describeLine(StringBuilder sb) {
        super.describeLine(sb);
        sb.append("connect ");
        if (awaitName != null) {
            sb.append("await ").append(awaitName).append("\n        ");
        }
        sb.append(location).append('\n');
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
