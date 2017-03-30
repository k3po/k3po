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
package org.kaazing.k3po.lang.types;

import static java.util.Objects.requireNonNull;

public final class TypeInfo<T> implements Comparable<TypeInfo<T>> {

    private final Class<T> type;
    private final String name;

    public TypeInfo(
        String name,
        Class<T> type)
    {
        this.type = requireNonNull(type);
        this.name = requireNonNull(name);
    }

    public String getName() {
        return name;
    }

    public Class<T> getType() {
        return type;
    }

    @Override
    public int compareTo( TypeInfo<T> that) {
        // collide on name
        return this.name.compareTo(that.name);
    }

    @Override
    public int hashCode() {
        // collide on name
        return name.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof TypeInfo)) {
            return false;
        }

        TypeInfo<?> that = (TypeInfo<?>)o;
        // collide on name
        return this.name.equals(that.name);
    }

    @Override
    public String toString() {
        return String.format("%s[%s %s]", getClass().getSimpleName(), name, type.getSimpleName());
    }
}
