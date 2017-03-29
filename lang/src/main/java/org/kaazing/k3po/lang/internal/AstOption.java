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
package org.kaazing.k3po.lang.internal;

import static org.kaazing.k3po.lang.internal.ast.util.AstUtil.equivalent;

public final class AstOption<T> {

    private final Class<T> type;
    private final String name;

    public AstOption(
        String name,
        Class<T> type)
    {
        this.type = type;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Class<T> getType() {
        return type;
    }

    protected int hashTo() {
        int hashCode = getClass().hashCode();

        if (name != null) {
            hashCode <<= 4;
            hashCode ^= name.hashCode();
        }

        if (type != null) {
            hashCode <<= 4;
            hashCode ^= type.hashCode();
        }

        return hashCode;
    }

    protected boolean equalTo(AstOption<?> that) {
        return equivalent(this.name, that.name) &&
                equivalent(this.type, that.type);
    }
}
