/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package com.kaazing.el.util;

import static java.lang.String.format;
import static org.junit.Assert.assertTrue;

import javax.el.ExpressionFactory;
import javax.el.ValueExpression;

import org.junit.Before;
import org.junit.Test;

public class ELExpressionTest {
    protected ExpressionFactory factory;
    protected javax.el.ELContext ctx;

    @Before
    public void setUp()
        throws Exception {

        factory = ExpressionFactory.newInstance();
        ctx = new ExpressionContext();
    }

    @Test
    public void shouldParseELExpression()
        throws Exception {

        String script = "${1 + 2 + 3 > 7}";

        ValueExpression expr = factory.createValueExpression(ctx, script, boolean.class);
        Boolean value = (Boolean) expr.getValue(ctx);

        assertTrue(format("Expected '%s' to be false, got %s", expr.getExpressionString(), value), value.equals(false));
    }

    @Test
    public void shouldUseTestFunctionMapper()
        throws Exception {

        String script = "${test:add(7, 3)}";
        ValueExpression expr = factory.createValueExpression(ctx, script, int.class);
        Integer value = (Integer) expr.getValue(ctx);

        assertTrue(format("Expected '%s' to result in 10, got %d", expr.getExpressionString(), value), value.equals(10));
    }
}
