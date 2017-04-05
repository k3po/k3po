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
import org.kaazing.k3po.lang.internal.el.ExpressionContext;

public class AstFixedLengthBytesMatcher extends AstValueMatcher {

    private final int length;
    private final String captureName;
    private final ExpressionContext environment;

    public AstFixedLengthBytesMatcher(int length) {
        this(length, null, null);
    }

    public AstFixedLengthBytesMatcher(int length, String captureName, ExpressionContext environment) {
        this.length = length;
        this.captureName = captureName;
        this.environment = environment;
    }

    public int getLength() {
        return length;
    }

    public String getCaptureName() {
        return captureName;
    }

    @Override
    protected int hashTo() {
        int hashCode = getClass().hashCode();

        hashCode <<= 4;
        hashCode ^= length;

        if (captureName != null) {
            hashCode <<= 4;
            hashCode ^= captureName.hashCode();
        }

        return hashCode;
    }

    @Override
    public <R, P> R accept(Visitor<R, P> visitor, P parameter) {
        return visitor.visit(this, parameter);
    }

    @Override
    protected boolean equalTo(AstRegion that) {
        return that instanceof AstFixedLengthBytesMatcher && equalTo((AstFixedLengthBytesMatcher) that);
    }

    protected boolean equalTo(AstFixedLengthBytesMatcher that) {
        return equivalent(this.length, that.length) && equivalent(this.captureName, that.captureName);
    }

    @Override
    protected void describe(StringBuilder buf) {
        if (captureName != null) {
            buf.append(format("([0..%d}]:%s)", length, captureName));
        }
        else {
            buf.append(format("[0..%d}]", length));
        }
    }

    public ExpressionContext getEnvironment() {
        return environment;
    }
}
