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

import org.kaazing.k3po.lang.internal.ast.matcher.AstValueMatcher;
import org.kaazing.k3po.lang.internal.ast.value.AstValue;

public class AstReadConfigNode extends AstEventNode {

    private String type;
    private Map<String, AstValue> valuesByName;
    private Map<String, AstValueMatcher> matchersByName;

    public AstReadConfigNode() {
        this.valuesByName = new LinkedHashMap<>();
        this.matchersByName = new LinkedHashMap<>();
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setValue(String name, AstValue value) {
        valuesByName.put(name, value);
    }

    public AstValue getValue(String name) {
        return valuesByName.get(name);
    }

    public Collection<AstValueMatcher> getMatchers() {
        return matchersByName.values();
    }

    public AstValueMatcher getMatcher(String name) {
        return matchersByName.get(name);
    }

    public void setMatcher(String name, AstValueMatcher matcher) {
        matchersByName.put(name, matcher);
    }

    public void addMatcher(AstValueMatcher matcher) {
        matchersByName.put(format("matcher#%d", matchersByName.size()), matcher);
    }

    public void addMatchers(Collection<AstValueMatcher> matchers) {
        for (AstValueMatcher matcher : matchers) {
            matchersByName.put(format("matcher#%d", matchersByName.size()), matcher);
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
        if (matchersByName != null) {
            hashCode <<= 4;
            hashCode ^= matchersByName.hashCode();
        }

        return hashCode;
    }

    @Override
    protected boolean equalTo(AstRegion that) {
        return that instanceof AstReadConfigNode && equalTo((AstReadConfigNode) that);
    }

    protected boolean equalTo(AstReadConfigNode that) {
        return equivalent(this.type, that.type) &&
                equivalent(this.valuesByName, that.valuesByName) &&
                equivalent(this.matchersByName, that.matchersByName);
    }

    @Override
    protected void describe(StringBuilder buf) {
        super.describe(buf);
        buf.append("read ").append(type);
        for (AstValue value : valuesByName.values()) {
            buf.append(' ').append(value);
        }
        for (AstValueMatcher matcher : matchersByName.values()) {
            buf.append(' ').append(matcher);
        }
        buf.append('\n');
    }
}
