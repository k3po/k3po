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

import org.kaazing.k3po.lang.internal.ast.value.AstValue;

public abstract class AstOptionNode extends AstStreamableNode {

    private String optionName;
    private AstValue<?> optionValue;

    public String getOptionName() {
        return optionName;
    }

    public void setOptionName(String optionName) {
        this.optionName = optionName;
    }

    public AstValue<?> getOptionValue() {
        return optionValue;
    }

    public void setOptionValue(AstValue<?> optionValue) {
        this.optionValue = optionValue;
    }

    protected int hashTo() {
        int hashCode = getClass().hashCode();

        if (optionName != null) {
            hashCode <<= 4;
            hashCode ^= optionName.hashCode();
        }

        if (optionValue != null) {
            hashCode <<= 4;
            hashCode ^= optionValue.hashCode();
        }

        return hashCode;
    }

    @Override
    protected final boolean equalTo(AstRegion that) {
        return that instanceof AstOptionNode &&
                equalTo((AstOptionNode) that);
    }

    protected boolean equalTo(AstOptionNode that) {
        return equivalent(this.optionName, that.optionName) && equivalent(this.optionValue, that.optionValue);
    }
}
