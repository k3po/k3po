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
package org.kaazing.k3po.lang.internal.el;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.el.ValueExpression;

public class VariableMapper extends javax.el.VariableMapper {

    protected final ConcurrentMap<String, ValueExpression> variables;

    public VariableMapper() {
        variables = new ConcurrentHashMap<>();
    }

    @Override
    public ValueExpression resolveVariable(String name) {
        return variables.get(name);
    }

    @Override
    public ValueExpression setVariable(String name,
                                       ValueExpression expr) {
        return expr == null ? variables.remove(name) : variables.put(name, expr);
    }
}
