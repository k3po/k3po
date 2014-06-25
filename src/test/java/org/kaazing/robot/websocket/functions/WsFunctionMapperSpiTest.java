/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
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
