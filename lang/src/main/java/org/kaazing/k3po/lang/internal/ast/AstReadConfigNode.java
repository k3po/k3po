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
import java.util.LinkedHashSet;
import java.util.Map;

import org.kaazing.k3po.lang.internal.ast.matcher.AstValueMatcher;

public class AstReadConfigNode extends AstEventNode {

    private String type;
    private Map<String, AstValueMatcher> matchersByName;
    private Collection<AstValueMatcher> matchers;

    public AstReadConfigNode() {
        this.matchersByName = new LinkedHashMap<>();
        this.matchers = new LinkedHashSet<>();
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public AstValueMatcher getMatcher() {

        if (matchersByName.isEmpty()) {
            switch (matchers.size()) {
            case 0:
                return null;
            case 1:
                return matchers.iterator().next();
            }
        }

        throw new IllegalStateException("Multiple values available, yet assuming only one value");
    }

    public Collection<AstValueMatcher> getMatchers() {
        return matchers;
    }

    public AstValueMatcher getMatcher(String name) {
        return matchersByName.get(name);
    }

    public void setMatcher(String name, AstValueMatcher matcher) {
        matchersByName.put(name, matcher);
    }

    public void addMatcher(AstValueMatcher matcher) {
        matchers.add(matcher);
    }

    public void addMatchers(Collection<AstValueMatcher> matchers) {
        this.matchers.addAll(matchers);
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
        if (matchers != null) {
            hashCode <<= 4;
            hashCode ^= matchers.hashCode();
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
                equivalent(this.matchers, that.matchers) &&
                equivalent(this.matchersByName, that.matchersByName);
    }

    @Override
    protected void describe(StringBuilder buf) {
        super.describe(buf);
        buf.append("read ").append(type);
        for (Map.Entry<String, AstValueMatcher> entry : matchersByName.entrySet()) {
            String name = entry.getKey();
            AstValueMatcher matcher = entry.getValue();
            buf.append(' ').append(name).append('=').append(matcher);
        }
        for (AstValueMatcher value : matchers) {
            buf.append(' ').append(value);
        }
        buf.append('\n');
    }
}
