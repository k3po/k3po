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

import javax.el.ELContext;
import javax.el.ValueExpression;

import org.kaazing.k3po.driver.internal.behavior.visitor.GenerateConfigurationVisitor;
import org.kaazing.k3po.lang.internal.ast.value.AstLocation;
import org.kaazing.k3po.lang.internal.ast.value.AstLocationExpression;
import org.kaazing.k3po.lang.internal.ast.value.AstLocationLiteral;

/**
 * The class is used to defer the evaluation of location such as
 * accept/connect uri in {@link GenerateConfigurationVisitor}. In scenarios when
 * accept/connect takes expression value which only gets resolved during the
 * script execution, it is necessary to defer the resolution of accept/connect
 * uri.
 *
 */
public class LocationResolver {

    private static final LocationVisitorImpl VISITOR = new LocationVisitorImpl();

    private final AstLocation location;
    private final ELContext environment;

    private URI evaluatedURI;

    public LocationResolver(AstLocation location, ELContext environment) {
        this.location = location;
        this.environment = environment;
    }

    public URI resolve() throws Exception {
        if (evaluatedURI == null) {
            evaluatedURI = location.accept(VISITOR, environment);
        }
        return evaluatedURI;
    }

    private static class LocationVisitorImpl implements AstLocation.Visitor<URI, ELContext> {

        @Override
        public URI visit(AstLocationLiteral value, ELContext parameter) throws Exception {
            return value.getValue();
        }

        @Override
        public URI visit(AstLocationExpression value, ELContext environment) throws Exception {
            Object location;

            // TODO: Remove when JUEL sync bug is fixed https://github.com/k3po/k3po/issues/147
            synchronized (environment) {
                ValueExpression expression = value.getValue();
                location = expression.getValue(environment);
            }

            if (location == null) {
                throw new NullPointerException("Location expression result is null");
            }

            return (URI) location;
        }

    }
}
