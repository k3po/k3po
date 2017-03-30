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

import java.util.ArrayList;
import java.util.List;

import org.kaazing.k3po.lang.internal.ast.value.AstValue;

public class AstWriteValueNode extends AstCommandNode {

    private List<AstValue<?>> values;

    public List<AstValue<?>> getValues() {
        return values;
    }

    public void setValues(List<AstValue<?>> values) {
        this.values = values;
    }

    public void addValue(AstValue<?> value) {
        if (values == null) {
            values = new ArrayList<>();
        }
        values.add(value);
    }

    @Override
    public <R, P> R accept(Visitor<R, P> visitor, P parameter) {
        return visitor.visit(this, parameter);
    }

    @Override
    protected int hashTo() {
        int hashCode = getClass().hashCode();

        if (values != null) {
            hashCode <<= 4;
            hashCode ^= values.hashCode();
        }

        return hashCode;
    }

    @Override
    protected boolean equalTo(AstRegion that) {
        return that instanceof AstWriteValueNode && equalTo((AstWriteValueNode) that);
    }

    protected boolean equalTo(AstWriteValueNode that) {
        return equivalent(this.values, that.values);
    }

    @Override
    protected void describe(StringBuilder buf) {
        super.describe(buf);
        buf.append("write");
        for (AstValue<?> val : values) {
            buf.append(" " + val);
        }
        buf.append("\n");
    }
}
