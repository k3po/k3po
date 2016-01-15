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

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.kaazing.k3po.lang.internal.ast.value.AstValue;

public class AstWriteConfigNode extends AstCommandNode {

    private String type;
    private Map<String, AstValue> namesByName;
    private Map<String, AstValue> valuesByName;

    public AstWriteConfigNode() {
        this.namesByName = new LinkedHashMap<>();
        this.valuesByName = new LinkedHashMap<>();
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setName(String name, AstValue value) {
        namesByName.put(name, value);
    }

    public AstValue getName(String name) {
        return namesByName.get(name);
    }

    public void setValue(String name, AstValue value) {
        valuesByName.put(name, value);
    }

    public AstValue getValue(String name) {
        return valuesByName.get(name);
    }

    public void addValue(AstValue value) {
        String name = format("value#%d", valuesByName.size());
        valuesByName.put(name, value);
    }

    public Collection<AstValue> getValues() {
        return valuesByName.values();
    }

    public AstValue getValue() {
        switch (valuesByName.size()) {
        case 0:
            return null;
        case 1:
            return valuesByName.values().iterator().next();
        default:
            throw new IllegalStateException("Multiple values available, yet assuming only one value");
        }
    }

    @Override
    public <R, P> R accept(Visitor<R, P> visitor, P parameter) throws Exception {
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
                equivalent(this.valuesByName, that.valuesByName);
    }

    @Override
    protected void describe(StringBuilder buf) {
        super.describe(buf);
        buf.append("write ").append(type);
        for (AstValue name : namesByName.values()) {
            buf.append(' ').append(name);
        }
        for (AstValue value : valuesByName.values()) {
            buf.append(' ').append(value);
        }
        buf.append('\n');
    }
}
