/*
 * Copyright (c) 2014 "Kaazing Corporation," (www.kaazing.com)
 *
 * This file is part of Robot.
 *
 * Robot is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.kaazing.k3po.lang.ast;

import static java.lang.String.format;
import static org.kaazing.k3po.lang.ast.util.AstUtil.equivalent;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.kaazing.k3po.lang.ast.value.AstValue;

public class AstWriteConfigNode extends AstCommandNode {

    private String type;
    private Map<String, AstValue> namesByName;
    private Map<String, AstValue> valuesByName;

    public AstWriteConfigNode() {
        this.namesByName = new LinkedHashMap<String, AstValue>();
        this.valuesByName = new LinkedHashMap<String, AstValue>();
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
