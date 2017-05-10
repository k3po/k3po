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

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import org.kaazing.k3po.lang.internal.ast.value.AstValue;
import org.kaazing.k3po.lang.types.StructuredTypeInfo;

public class AstWriteConfigNode extends AstCommandNode {

    private StructuredTypeInfo type;
    private Collection<AstValue<?>> values;
    private Map<String, AstValue<?>> valuesByName;

    public AstWriteConfigNode() {
        this.valuesByName = new LinkedHashMap<>();
        this.values = new LinkedList<>();
    }

    public void setType(StructuredTypeInfo type) {
        this.type = type;
    }

    public StructuredTypeInfo getType() {
        return type;
    }

    public void setValue(String name, AstValue<?> value) {
        valuesByName.put(name, value);
    }

    public AstValue<?> getValue(String name) {
        return valuesByName.get(name);
    }

    public void addValue(AstValue<?> value) {
        values.add(value);
    }

    public Collection<AstValue<?>> getValues() {
        return values;
    }

    public AstValue<?> getValue() {

        if (valuesByName.isEmpty()) {
            switch (values.size()) {
            case 0:
                return null;
            case 1:
                return values.iterator().next();
            }
        }

        throw new IllegalStateException("Multiple values available, yet assuming only one value");
    }

    @Override
    public <R, P> R accept(Visitor<R, P> visitor, P parameter) {
        return visitor.visit(this, parameter);
    }

    @Override
    protected int hashTo() {
        int hashCode = getClass().hashCode();

        if (type != null) {
            hashCode <<= 4;
            hashCode ^= type.hashCode();
        }
        if (valuesByName != null) {
            hashCode <<= 4;
            hashCode ^= valuesByName.hashCode();
        }

        return hashCode;
    }

    @Override
    protected boolean equalTo(AstRegion that) {
        return that instanceof AstWriteConfigNode && equalTo((AstWriteConfigNode) that);
    }

    protected boolean equalTo(AstWriteConfigNode that) {
        return equivalent(this.type, that.type) &&
                equivalent(this.values, that.values) &&
                equivalent(this.valuesByName, that.valuesByName);
    }

    @Override
    protected void describe(StringBuilder buf) {
        super.describe(buf);
        buf.append("write ").append(type);
        for (Map.Entry<String, AstValue<?>> entry : valuesByName.entrySet()) {
            String name = entry.getKey();
            AstValue<?> value = entry.getValue();
            buf.append(' ').append(name).append('=').append(value);
        }
        for (AstValue<?> value : values) {
            buf.append(' ').append(value);
        }
        buf.append('\n');
    }
}
