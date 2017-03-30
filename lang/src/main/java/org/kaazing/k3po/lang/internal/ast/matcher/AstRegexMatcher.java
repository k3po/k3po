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

import static org.kaazing.k3po.lang.internal.ast.util.AstUtil.equivalent;

import org.kaazing.k3po.lang.internal.ast.AstRegion;
import org.kaazing.k3po.lang.internal.el.ExpressionContext;
import org.kaazing.k3po.lang.internal.regex.NamedGroupPattern;

public class AstRegexMatcher extends AstValueMatcher {

    private final NamedGroupPattern pattern;
    private final ExpressionContext environment;

    public AstRegexMatcher(NamedGroupPattern pattern, ExpressionContext environment) {
        if (pattern == null) {
            throw new NullPointerException("pattern");
        }
        this.pattern = pattern;
        this.environment = environment;
    }

    public NamedGroupPattern getValue() {
        return pattern;
    }

    @Override
    protected int hashTo() {
        return pattern.hashCode();
    }

    @Override
    protected boolean equalTo(AstRegion that) {
        return that instanceof AstRegexMatcher && equalTo((AstRegexMatcher) that);
    }

    protected boolean equalTo(AstRegexMatcher that) {
        return equivalent(this.pattern, that.pattern);
    }

    @Override
    public <R, P> R accept(Visitor<R, P> visitor, P parameter) {

        return visitor.visit(this, parameter);
    }

    @Override
    protected void describe(StringBuilder buf) {
        buf.append('/').append(pattern.toString()).append('/');
    }

    public ExpressionContext getEnvironment() {
        return environment;
    }
}
