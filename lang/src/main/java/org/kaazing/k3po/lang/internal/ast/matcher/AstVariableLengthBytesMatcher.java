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
package org.kaazing.k3po.lang.internal.ast.matcher;

import static java.lang.String.format;
import static org.kaazing.k3po.lang.internal.ast.util.AstUtil.equivalent;

import javax.el.ValueExpression;

import org.kaazing.k3po.lang.internal.ast.AstRegion;
import org.kaazing.k3po.lang.internal.el.ExpressionContext;

public class AstVariableLengthBytesMatcher extends AstValueMatcher {

    private final ValueExpression length;
    private final String captureName;
    private final ExpressionContext environment;

    public AstVariableLengthBytesMatcher(ValueExpression length, ExpressionContext environment) {
        this(length, null, environment);
    }

    public AstVariableLengthBytesMatcher(ValueExpression length, String captureName, ExpressionContext environment) {
        this.length = length;
        this.captureName = captureName;
        this.environment = environment;
    }

    public ValueExpression getLength() {
        return length;
    }

    public String getCaptureName() {
        return captureName;
    }

    @Override
    protected int hashTo() {
        int hashCode = getClass().hashCode();

        if (length != null) {
            hashCode <<= 4;
            hashCode ^= length.hashCode();
        }

        if (captureName != null) {
            hashCode <<= 4;
            hashCode ^= captureName.hashCode();
        }

        return hashCode;
    }

    @Override
    protected boolean equalTo(AstRegion that) {
        return that instanceof AstVariableLengthBytesMatcher && equalTo((AstVariableLengthBytesMatcher) that);
    }

    protected boolean equalTo(AstVariableLengthBytesMatcher that) {
        return equivalent(this.length, that.length) && equivalent(this.captureName, that.captureName);
    }

    @Override
    public <R, P> R accept(Visitor<R, P> visitor, P parameter) {
        return visitor.visit(this, parameter);
    }

    @Override
    protected void describe(StringBuilder buf) {
        if (captureName != null) {
            buf.append(format("([0..%s]:%s)", length.getExpressionString(), captureName));
        } else {
            buf.append(format("[0..%s]", length.getExpressionString()));
        }
    }

    public ExpressionContext getEnvironment() {
        return environment;
    }
}
