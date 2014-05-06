/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.lang.el;

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
        this.functionMapper = org.kaazing.el.FunctionMapper.newFunctionMapper();
        this.variableMapper = new org.kaazing.el.VariableMapper();
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
