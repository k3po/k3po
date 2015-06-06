/*
 * Copyright 2014, Kaazing Corporation. All rights reserved.
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

import java.net.URI;

import org.kaazing.k3po.lang.internal.ast.AstRegion;
import org.kaazing.k3po.lang.internal.ast.util.AstUtil;

public class AstUriLiteralValue extends AstLocation {

    private final URI uri;

    public AstUriLiteralValue(URI value) {
        if (value == null) {
            throw new IllegalArgumentException("value cannot be null");
        }
        uri = value;
    }

    public URI getValue() {
        return uri;
    }

    @Override
    public <R, P> R accept(LocationVisitor<R, P> visitor, P parameter) throws Exception {
        return visitor.visit(this, parameter);
    }

    @Override
    protected int hashTo() {
        return uri.hashCode();
    }

    @Override
    protected boolean equalTo(AstRegion that) {
        return (that instanceof AstUriLiteralValue) && equalTo((AstUriLiteralValue) that);
    }

    protected boolean equalTo(AstUriLiteralValue that) {
        return AstUtil.equivalent(this.uri, that.uri);
    }


}
