/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package com.kaazing.el;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.el.ValueExpression;

public class VariableMapper
    extends javax.el.VariableMapper {

    protected final ConcurrentMap<String, ValueExpression> variables;

    public VariableMapper() {
        variables = new ConcurrentHashMap<String, ValueExpression>();
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
