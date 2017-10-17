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
package org.kaazing.k3po.lang.internal.ast.value;

import org.kaazing.k3po.lang.internal.ast.AstRegion;

public abstract class AstValue<T> extends AstRegion {

    public abstract <R, P> R accept(Visitor<R, P> visitor, P parameter);

    public interface Visitor<R, P> {

        R visit(AstExpressionValue<?> value, P parameter);

        R visit(AstLiteralTextValue value, P parameter);

        R visit(AstLiteralBytesValue value, P parameter);

        R visit(AstLiteralByteValue value, P parameter);

        R visit(AstLiteralShortValue value, P parameter);

        R visit(AstLiteralIntegerValue value, P parameter);

        R visit(AstLiteralLongValue value, P parameter);

        R visit(AstLiteralURIValue value, P parameter);
    }

    public abstract T getValue();
}
