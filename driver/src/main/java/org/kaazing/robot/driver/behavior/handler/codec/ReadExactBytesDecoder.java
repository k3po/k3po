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

import static org.jboss.netty.buffer.ChannelBuffers.copiedBuffer;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.logging.InternalLoggerFactory;

public class ReadExactBytesDecoder extends MessageDecoder {

    private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(ReadExactBytesDecoder.class);

    private final ChannelBuffer expected;

    public ReadExactBytesDecoder(byte[] expected) {
        this.expected = copiedBuffer(expected);
    }

    @Override
    protected Object decodeBuffer(ChannelBuffer buffer) throws Exception {

        if (buffer.readableBytes() < expected.readableBytes()) {
            LOGGER.debug("not enough bytes are ready to read. Expecting " + expected.readableBytes()
                    + " bytes. Read to read is " + buffer.readableBytes());
            return null;
        }

        ChannelBuffer observed = buffer.readSlice(expected.readableBytes());
        if (!observed.equals(expected)) {
            throw new MessageMismatchException("Exact bytes mismatch", expected, observed);
        }

        return buffer;
    }
}
