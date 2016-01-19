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
package org.kaazing.k3po.driver.internal.resolver;

import java.util.HashMap;
import java.util.Map;

import javax.el.ELContext;

import org.kaazing.k3po.lang.internal.ast.value.AstExpressionValue;
import org.kaazing.k3po.lang.internal.ast.value.AstLocationExpression;
import org.kaazing.k3po.lang.internal.ast.value.AstLocationLiteral;

/**
 * The class is used to defer the evaluation of accept and connect options.
 */
public final class OptionsResolver {

    private final Map<String, Object> options;
    private final ELContext environment;

    private Map<String, Object> resolved;

    public OptionsResolver(Map<String, Object> options, ELContext environment) {
        this.options = options;
        this.environment = environment;
    }

    public Map<String, Object> resolve() {

        if (resolved == null) {
            resolved = new HashMap<String, Object>();
            for (String name : options.keySet()) {
                Object value = options.get(name);
                if (value instanceof AstExpressionValue) {
                    AstExpressionValue expressionValue = (AstExpressionValue) value;
                    value = expressionValue.getValue().getValue(environment);
                }
                else if (value instanceof AstLocationExpression) {
                    AstLocationExpression location = (AstLocationExpression) value;
                    value = location.getValue().getValue(environment);
                }
                else if (value instanceof AstLocationLiteral) {
                    AstLocationLiteral location = (AstLocationLiteral) value;
                    value = location.getValue();
                }
                resolved.put(name, value);
            }
        }

        return resolved;
    }
}
