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

import java.lang.reflect.Method;

import javax.el.FunctionMapper;
import javax.el.ValueExpression;
import javax.el.VariableMapper;

import de.odysseus.el.util.SimpleContext;

public class ExpressionContext extends SimpleContext {

    protected FunctionMapper functionMapper;
    protected VariableMapper variableMapper;

    public ExpressionContext() {
        super();
        this.functionMapper = org.kaazing.k3po.lang.el.FunctionMapper.newFunctionMapper();
        this.variableMapper = new org.kaazing.k3po.lang.internal.el.VariableMapper();
    }

    @Override
    public FunctionMapper getFunctionMapper() {
        return functionMapper;
    }

    @Override
    public VariableMapper getVariableMapper() {
        return variableMapper;
    }

    @Override
    public void setFunction(String prefix, String localName, Method method) {
        throw new IllegalArgumentException("setFunction not supported");
    }

    @Override
    public ValueExpression setVariable(String name, ValueExpression expr) {
        return variableMapper.setVariable(name, expr);
    }
}
