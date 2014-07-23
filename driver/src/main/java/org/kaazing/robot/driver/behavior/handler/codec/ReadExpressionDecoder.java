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

package org.kaazing.robot.driver.behavior.handler.codec;

import java.util.Arrays;

import javax.el.ValueExpression;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.logging.InternalLoggerFactory;

import org.kaazing.robot.lang.el.ExpressionContext;
import org.kaazing.robot.driver.util.Utils;

public class ReadExpressionDecoder extends MessageDecoder {

    private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(ReadExpressionDecoder.class);

    private final ValueExpression expression;
    private final ExpressionContext environment;

    public ReadExpressionDecoder(ValueExpression expression, ExpressionContext environment) {
        this.expression = expression;
        this.environment = environment;
    }

    @Override
    protected Object decodeBuffer(ChannelBuffer buffer) throws Exception {

        byte[] expected = (byte[]) expression.getValue(environment);

        if (buffer.readableBytes() < expected.length) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Not enough bytes ready to read. expecting " + expected.length + " ready to read "
                        + buffer.readableBytes());
            }
            return null;
        }

        byte[] observed = new byte[expected.length];
        buffer.readBytes(observed);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Read " + observed.length + " bytes " + Utils.byteArrayToString(observed));
        }
        if (!Arrays.equals(observed, expected)) {
            LOGGER.error("observed bytes do not match expected bytes");
            if (LOGGER.isInfoEnabled()) {
                LOGGER.error("\texpected: " + Utils.byteArrayToString(expected));
                LOGGER.error("\tobserved: " + Utils.byteArrayToString(observed));
            }
            // Use a mismatch exception subclass, include the expression?
            throw new MessageMismatchException("Expression mismatch", expected, observed);
        }

        return buffer;
    }
}
