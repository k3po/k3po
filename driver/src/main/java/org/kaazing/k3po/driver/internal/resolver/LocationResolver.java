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

package org.kaazing.k3po.driver.internal.resolver;

import java.net.URI;

import org.kaazing.k3po.lang.internal.ast.value.AstExpressionValue;
import org.kaazing.k3po.lang.internal.ast.value.AstLiteralBytesValue;
import org.kaazing.k3po.lang.internal.ast.value.AstLiteralTextValue;
import org.kaazing.k3po.lang.internal.ast.value.AstLocation;
import org.kaazing.k3po.lang.internal.ast.value.AstLocationLiteral;
import org.kaazing.k3po.lang.internal.ast.value.AstValue;
import org.kaazing.k3po.lang.internal.el.ExpressionContext;

public class LocationResolver {

    private final AstValue location;
    private final ExpressionContext environment;

    private URI value;

    public LocationResolver(AstValue location, ExpressionContext environment) {
        this.location = location;
        this.environment = environment;
    }

    public URI resolve() throws Exception {
        if (value == null) {
            value = location.accept(new URIVisitor(), environment);
        }
        return value;
    }

    private static class URIVisitor implements AstLocation.LocationVisitor<URI, ExpressionContext> {

        @Override
        public URI visit(AstExpressionValue value, ExpressionContext environment) throws Exception {
            Object uriLiteralObj;

            synchronized (environment) {
                uriLiteralObj = value.getValue().getValue(environment);
            }

            if (uriLiteralObj == null) {
                throw new NullPointerException("Location expression result is null");
            }

            String uriLiteral = uriLiteralObj.toString();
            return URI.create(uriLiteral);
        }

        @Override
        public URI visit(AstLocationLiteral value, ExpressionContext parameter) throws Exception {
            return value.getValue();
        }

        @Override
        public URI visit(AstLiteralTextValue value, ExpressionContext parameter) throws Exception {
            return null;
        }

        @Override
        public URI visit(AstLiteralBytesValue value, ExpressionContext parameter) throws Exception {
            return null;
        }
    }
}
