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
package org.kaazing.k3po.lang.internal.ast.value;

import static java.lang.String.format;
import static org.kaazing.k3po.lang.internal.ast.util.AstUtil.equivalent;
import static org.kaazing.k3po.lang.internal.el.ExpressionFactoryUtils.synchronizedValue;

import javax.el.ValueExpression;

import org.kaazing.k3po.lang.internal.ast.AstRegion;
import org.kaazing.k3po.lang.internal.el.ExpressionContext;

public final class AstExpressionValue<T> extends AstValue<T> {

    private final ValueExpression expression;
    private final ExpressionContext environment;

    public AstExpressionValue(ValueExpression expression, ExpressionContext environment) {
        if (expression == null) {
            throw new NullPointerException("expression");
        }
        this.expression = expression;
        this.environment = environment;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T getValue() {
        return (T) synchronizedValue(expression, environment, expression.getExpectedType());
    }

    public <R> R getValue(Class<R> expectedType) {
        return synchronizedValue(expression, environment, expectedType);
    }

    public ValueExpression getExpression() {
        return expression;
    }

    public ExpressionContext getEnvironment() {
        return environment;
    }

    @Override
    public <R, P> R accept(Visitor<R, P> visitor, P parameter) {
        return visitor.visit(this, parameter);
    }

    @Override
    protected int hashTo() {
        return expression.hashCode();
    }

    @Override
    protected boolean equalTo(AstRegion that) {
        return (that instanceof AstExpressionValue) && equalTo((AstExpressionValue<?>) that);
    }

    protected boolean equalTo(AstExpressionValue<?> that) {
        return equivalent(this.expression, that.expression);
    }

    @Override
    protected void describe(StringBuilder buf) {
        buf.append(format("(%s)%s", expression.getExpectedType().getSimpleName(), expression.getExpressionString()));
    }
}
