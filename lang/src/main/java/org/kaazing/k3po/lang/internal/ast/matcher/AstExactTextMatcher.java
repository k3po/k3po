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

import org.kaazing.k3po.lang.internal.ast.AstRegion;

public class AstExactTextMatcher extends AstValueMatcher {

    private final String value;

    public AstExactTextMatcher(String value) {
        if (value == null) {
            throw new NullPointerException("value");
        }
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public <R, P> R accept(Visitor<R, P> visitor, P parameter) {
        return visitor.visit(this, parameter);
    }

    @Override
    protected int hashTo() {
        return value.hashCode();
    }

    @Override
    protected boolean equalTo(AstRegion that) {
        return (that instanceof AstExactTextMatcher) && equalTo((AstExactTextMatcher) that);
    }

    protected boolean equalTo(AstExactTextMatcher that) {
        return equivalent(this.value, that.value);
    }

    @Override
    protected void describe(StringBuilder buf) {
        buf.append(format("\"%s\"", value));
    }
}
