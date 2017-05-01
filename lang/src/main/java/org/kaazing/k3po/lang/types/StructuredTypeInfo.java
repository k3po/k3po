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

import java.util.List;
import java.util.Objects;

public final class StructuredTypeInfo {

    private final String qualifiedName;
    private final String name;
    private final List<TypeInfo<?>> namedFields;
    private final int anonymousFields;

    public StructuredTypeInfo(
        String scope,
        String name,
        List<TypeInfo<?>> namedFields,
        int anonymousFields)
    {
        this.qualifiedName = String.format("%s:%s", scope, name);
        this.name = name;
        this.namedFields = namedFields;
        this.anonymousFields = anonymousFields;
    }

    public String getQualifiedName() {
        return qualifiedName;
    }

    public String getName() {
        return name;
    }

    public List<TypeInfo<?>> getNamedFields() {
        return namedFields;
    }

    public int getAnonymousFields() {
        return anonymousFields;
    }

    @Override
    public int hashCode() {
        int hashCode = getClass().hashCode();

        if (name != null) {
            hashCode <<= 4;
            hashCode ^= name.hashCode();
        }

        if (namedFields != null) {
            hashCode <<= 4;
            hashCode ^= namedFields.hashCode();
        }

        hashCode <<= 4;
        hashCode ^= anonymousFields;

        return hashCode;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof TypeInfo)) {
            return false;
        }

        StructuredTypeInfo that = (StructuredTypeInfo)o;
        return this.anonymousFields == that.anonymousFields &&
                Objects.equals(this.name, that.name) &&
                Objects.equals(this.namedFields, that.namedFields);
    }

    @Override
    public String toString() {
        return String.format("%s[%s %s %d]", getClass().getSimpleName(), qualifiedName, namedFields, anonymousFields);
    }
}
