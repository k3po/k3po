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

import java.util.Objects;

import org.kaazing.k3po.lang.types.TypeInfo;

public class AstWriteOptionNode extends AstOptionNode {

    private TypeInfo<?> optionType;

    public void setOptionType(TypeInfo<?> optionType) {
        this.optionType = optionType;
    }

    public TypeInfo<?> getOptionType() {
        return optionType;
    }

    @Override
    public <R, P> R accept(Visitor<R, P> visitor, P parameter) {
        return visitor.visit(this, parameter);
    }


    @Override
    protected int hashTo() {
        int hash = super.hashCode();

        if (optionType != null) {
            hash <<= 4;
            hash &= optionType.hashCode();
        }

        return hash;
    }

    @Override
    protected final boolean equalTo(AstOptionNode that) {
        return that instanceof AstWriteOptionNode &&
                equalTo((AstWriteOptionNode) that);
    }

    protected boolean equalTo(AstWriteOptionNode that) {
        return super.equalTo(that) &&
                Objects.equals(this.optionType, that.optionType);
    }

    @Override
    protected void describe(StringBuilder buf) {
        super.describe(buf);
        buf.append(format("write option %s %s\n", getOptionName(), getOptionValue()));
    }
}
