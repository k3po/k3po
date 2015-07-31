/*
 * Copyright 2014, Kaazing Corporation. All rights reserved.
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

import static java.lang.String.format;
import static org.kaazing.k3po.lang.internal.ast.util.AstUtil.equivalent;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.el.ELContext;

import org.kaazing.k3po.lang.internal.ast.value.AstLocation;

public class AstConnectNode extends AstStreamNode {

    private Map<String, Object> options;
    private String barrier;

    private AstLocation location;
    private ELContext environment;

    public AstConnectNode() {
    }

    public AstConnectNode(AstConnectNode connectNode) {
        this.regionInfo = connectNode.regionInfo;
        this.location = connectNode.location;
        this.environment = connectNode.environment;
        this.barrier = connectNode.barrier;
        this.options = connectNode.options;
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

        if (environment != null) {
            hashCode <<= 4;
            hashCode ^= environment.hashCode();
        }

        if (options != null) {
            hashCode <<= 4;
            hashCode ^= options.hashCode();
        }

        return hashCode;
    }

    @Override
    public <R, P> R accept(Visitor<R, P> visitor, P parameter) throws Exception {
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
        if (barrier == null) {
            sb.append(format("connect %s\n", location));
        } else {
            sb.append(format("connect await %s\nconnect %s\n", barrier, location));
        }
    }

    public String getBarrier() {
        return barrier;
    }

    public void setBarrier(String barrier) {
        this.barrier = barrier;
    }

    public AstLocation getLocation() {
        return location;
    }

    public void setLocation(AstLocation location) {
        this.location = location;
    }

    public ELContext getEnvironment() {
        return environment;
    }

    public void setEnvironment(ELContext expressionContext) {
        this.environment = expressionContext;
    }

}
