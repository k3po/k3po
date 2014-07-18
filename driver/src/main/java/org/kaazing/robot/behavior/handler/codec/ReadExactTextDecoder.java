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

package org.kaazing.robot.behavior.handler.codec;

import static org.jboss.netty.buffer.ChannelBuffers.copiedBuffer;
import static org.jboss.netty.util.CharsetUtil.UTF_8;

import java.nio.charset.Charset;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.logging.InternalLoggerFactory;

public class ReadExactTextDecoder extends MessageDecoder {

    private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(ReadExactTextDecoder.class);

        private final ChannelBuffer expected;

    public ReadExactTextDecoder(String expected, Charset charset) {
        this.expected = copiedBuffer(expected, charset);
    }

    @Override
    protected Object decodeBuffer(ChannelBuffer buffer) throws Exception {

        if (buffer.readableBytes() < expected.readableBytes()) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("not enough bytes are ready to read. Expecting " + expected.readableBytes()
                        + " bytes. Read to read is " + buffer.readableBytes());
            }
            return null;
        }

        ChannelBuffer observed = buffer.readSlice(expected.readableBytes());
        if (!observed.equals(expected)) {
            LOGGER.error("observed bytes do not match expected bytes");
            if (LOGGER.isInfoEnabled()) {
                LOGGER.error("\texpected: " + expected.toString(UTF_8));
                LOGGER.error("\tobserved: " + observed.toString(UTF_8));
            }
            // Use a mismatch exception subclass, include the charset
            // expected?
            throw new MessageMismatchException("Exact text mismatch", expected, observed);
        }

        return buffer;
    }
}
