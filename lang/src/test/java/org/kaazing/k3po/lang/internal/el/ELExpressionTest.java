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

package org.kaazing.k3po.lang.internal.el;

import static java.lang.String.format;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import javax.el.ExpressionFactory;
import javax.el.ValueExpression;

import org.junit.Before;
import org.junit.Test;

public class ELExpressionTest {
    protected ExpressionFactory factory;
    protected javax.el.ELContext ctx;

    @Before
    public void setUp() throws Exception {

        factory = ExpressionFactory.newInstance();
        ctx = new ExpressionContext();
    }

    @Test
    public void shouldParseELExpression() throws Exception {

        String script = "${1 + 2 + 3 > 7}";

        ValueExpression expr = factory.createValueExpression(ctx, script, boolean.class);
        Boolean value = (Boolean) expr.getValue(ctx);

        assertFalse(format("Expected '%s' to be false, got %s", expr.getExpressionString(), value), value);
    }

    @Test
    public void shouldUseCustomFunctionMapper() throws Exception {

        String script = "${custom:add2(7, 3)}";
        ValueExpression expr = factory.createValueExpression(ctx, script, int.class);
        Integer value = (Integer) expr.getValue(ctx);

        assertEquals(format("Expected '%s' to result in 14, got %d", expr.getExpressionString(), value),
                Integer.valueOf(14), value);
    }

    @Test
    public void shouldUseTestFunctionMapper() throws Exception {

        String script = "${test:add(7, 3)}";
        ValueExpression expr = factory.createValueExpression(ctx, script, int.class);
        Integer value = (Integer) expr.getValue(ctx);

        assertEquals(format("Expected '%s' to result in 10, got %d", expr.getExpressionString(), value),
                Integer.valueOf(10), value);
    }
}
