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

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.logging.InternalLoggerFactory;

import org.kaazing.robot.lang.el.ExpressionContext;

public abstract class ReadFixedLengthBytesDecoder<T> extends MessageDecoder {

    private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(ReadFixedLengthBytesDecoder.class);

    private final int length;
    private final ExpressionContext environment;
    private final String captureName;

    public ReadFixedLengthBytesDecoder(int length) {
        this.length = length;
        environment = null;
        captureName = null;
    }

    public ReadFixedLengthBytesDecoder(int length, ExpressionContext environment, String captureName) {
        this.length = length;
        this.environment = environment;
        this.captureName = captureName;
    }

    protected abstract T readBuffer(ChannelBuffer buffer);


    public int getLength() {
        return length;
    }

    @Override
    protected Object decodeBuffer(ChannelBuffer buffer) throws Exception {

        if (buffer.readableBytes() < length) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("not enough bytes are ready to read. Expecting " + length + " bytes. Read to read is "
                        + buffer.readableBytes());
            }
            return null;
        }
        if (captureName == null) {
            buffer.readSlice(length);
        } else {
            T value = readBuffer(buffer);
            environment.getELResolver().setValue(environment, null, captureName, value);
        }
        return buffer;
    }
}
