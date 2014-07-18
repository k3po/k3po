/*
 * Copyright (c) 2014 "Kaazing Corporation," (www.kaazing.com)
 *
 * This file is part of Robot.
 *
 * Robot is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.kaazing.robot.lang.el;

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
    public void shouldUseCustomFunctionMapper()
        throws Exception {

        String script = "${custom:add2(7, 3)}";
        ValueExpression expr = factory.createValueExpression(ctx, script, int.class);
        Integer value = (Integer) expr.getValue(ctx);

        assertTrue(format("Expected '%s' to result in 14, got %d", expr.getExpressionString(), value), value.equals(14));
    }
}
