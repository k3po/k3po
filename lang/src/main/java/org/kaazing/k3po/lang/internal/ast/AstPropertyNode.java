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

import static java.lang.String.format;
import static org.kaazing.k3po.lang.internal.ast.util.AstUtil.equivalent;

import javax.el.ELResolver;

import org.kaazing.k3po.lang.internal.ast.value.AstValue;
import org.kaazing.k3po.lang.internal.el.ExpressionContext;

public class AstPropertyNode extends AstNode {

    private String propertyName;
    private AstValue<?> propertyValue;
    private ExpressionContext environment;

    @Override
    public <R, P> R accept(Visitor<R, P> visitor, P parameter) {
        return visitor.visit(this, parameter);
    }

    public void setEnvironment(ExpressionContext environment) {
        this.environment = environment;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public AstValue<?> getPropertyValue() {
        return propertyValue;
    }

    public void setPropertyValue(AstValue<?> propertyValue) {
        this.propertyValue = propertyValue;
    }

    public Object resolve() {
        Object value = propertyValue.getValue();
        ELResolver resolver = environment.getELResolver();
        // ELResolver.setValue is not thread-safe
        synchronized (environment) {
            resolver.setValue(environment, null, propertyName, value);
        }
        return value;
    }

    protected int hashTo() {
        int hashCode = getClass().hashCode();

        if (propertyName != null) {
            hashCode <<= 4;
            hashCode ^= propertyName.hashCode();
        }

        if (propertyValue != null) {
            hashCode <<= 4;
            hashCode ^= propertyValue.hashCode();
        }

        return hashCode;
    }

    @Override
    protected boolean equalTo(AstRegion that) {
        return that instanceof AstPropertyNode && equalTo((AstPropertyNode) that);
    }

    protected boolean equalTo(AstPropertyNode that) {
        return equivalent(this.propertyName, that.propertyName) && equivalent(this.propertyValue, that.propertyValue);
    }

    @Override
    protected void describe(StringBuilder buf) {
        super.describe(buf);
        buf.append(format("property %s %s\n", getPropertyName(), getPropertyValue()));
    }

    public ExpressionContext getExpressionContext() {
        return environment;
    }
}
