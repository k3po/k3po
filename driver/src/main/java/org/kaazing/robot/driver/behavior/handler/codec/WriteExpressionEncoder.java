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

import static org.jboss.netty.buffer.ChannelBuffers.buffer;
import static org.jboss.netty.buffer.ChannelBuffers.wrappedBuffer;
import static org.kaazing.robot.driver.util.Utils.byteArrayToString;

import javax.el.ValueExpression;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.logging.InternalLoggerFactory;
import org.kaazing.robot.lang.el.ExpressionContext;

public class WriteExpressionEncoder implements MessageEncoder {

    private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(WriteExpressionEncoder.class);

    private final ExpressionContext context;
    private final ValueExpression expression;

    public WriteExpressionEncoder(ValueExpression expression, ExpressionContext context) {
        this.context = context;
        this.expression = expression;
    }

    @Override
    public ChannelBuffer encode() {

        final boolean isDebugEnabled = LOGGER.isDebugEnabled();
        final byte[] value = (byte[]) expression.getValue(context);
        final ChannelBuffer result;

        if (value != null) {
            if (isDebugEnabled) {
                LOGGER.debug("Encoding expression results. " + value.length + " bytes. Bytes: "
                        + byteArrayToString(value));
            }
            result = wrappedBuffer(value);
        } else {
            if (isDebugEnabled) {
                LOGGER.debug("Value of expression is null. Encoding as a 0 length buffer");
            }
            result = buffer(0);
        }
        return result;
    }

}
