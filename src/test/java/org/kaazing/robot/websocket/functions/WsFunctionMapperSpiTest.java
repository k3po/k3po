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

package org.kaazing.robot.websocket.functions;

import static org.jboss.netty.util.CharsetUtil.UTF_8;
import static org.junit.Assert.assertArrayEquals;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;

import org.junit.Before;
import org.junit.Test;
import org.kaazing.robot.lang.el.ExpressionContext;

public class WsFunctionMapperSpiTest {

    private ExpressionFactory factory;
    private ELContext ctx;

    @Before
    public void setUp() throws Exception {

        factory = ExpressionFactory.newInstance();
        ctx = new ExpressionContext();
    }

    @Test
    public void shouldComputeHandshakeHash() throws Exception {

        String expressionText = "${ws:computeHashAsBase64(string:asBytes('dGhlIHNhbXBsZSBub25jZQ=='))}";
        ValueExpression expression = factory.createValueExpression(ctx, expressionText, byte[].class);
        byte[] computedHash = (byte[]) expression.getValue(ctx);

        assertArrayEquals("Inconsistent handshake hash", "s3pPLMBiTxaQ9kYGzzhZRbK+xOo=".getBytes(UTF_8), computedHash);
    }
}
