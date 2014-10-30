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

import static java.lang.String.format;
import static org.kaazing.robot.lang.RegionInfo.newSequential;

import javax.el.ValueExpression;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.logging.InternalLoggerFactory;
import org.kaazing.robot.lang.RegionInfo;
import org.kaazing.robot.lang.el.ExpressionContext;

public class ReadVariableLengthBytesDecoder extends MessageDecoder {

    private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(ReadVariableLengthBytesDecoder.class);

    private final ValueExpression length;
    private final ExpressionContext environment;
    private final String captureName;

    public ReadVariableLengthBytesDecoder(RegionInfo regionInfo, ValueExpression length, ExpressionContext environment) {
        this(regionInfo, length, environment, null);
    }

    public ReadVariableLengthBytesDecoder(RegionInfo regionInfo, ValueExpression length, ExpressionContext environment, String captureName) {
        super(regionInfo);
        this.length = length;
        this.environment = environment;
        this.captureName = captureName;
    }

    @Override
    public String toString() {
        return format("%s bytes", length);
    }

    @Override
    protected Object decodeBuffer(ChannelBuffer buffer) throws Exception {

        int resolvedLength = (Integer) length.getValue(environment);

        if (buffer.readableBytes() < resolvedLength) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("not enough bytes are ready to read. Expecting " + resolvedLength + " bytes. Read to read is "
                        + buffer.readableBytes());
            }
            return null;
        }
        if (captureName == null) {
            buffer.readSlice(resolvedLength);
        } else {
            byte[] bytes = new byte[resolvedLength];
            buffer.readBytes(bytes, 0, resolvedLength);
            environment.getELResolver().setValue(environment, null, captureName, bytes);
        }
        return buffer;
    }

    // unit tests
    ReadVariableLengthBytesDecoder(ValueExpression length, ExpressionContext environment) {
        this(newSequential(0, 0), length, environment);
    }

    // unit tests
    ReadVariableLengthBytesDecoder(ValueExpression length, ExpressionContext environment, String captureName) {
        this(newSequential(0, 0), length, environment, captureName);
    }
}
