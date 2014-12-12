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

package org.kaazing.k3po.driver.behavior.handler.codec;

import static org.kaazing.k3po.lang.RegionInfo.newSequential;

import org.jboss.netty.buffer.ChannelBuffer;
import org.kaazing.k3po.lang.RegionInfo;
import org.kaazing.k3po.lang.el.ExpressionContext;

public class ReadByteArrayBytesDecoder extends ReadFixedLengthBytesDecoder<byte[]> {

    public ReadByteArrayBytesDecoder(RegionInfo regionInfo, int length) {
        super(regionInfo, length);
    }

    public ReadByteArrayBytesDecoder(RegionInfo regionInfo, int length, ExpressionContext environment, String captureName) {
        super(regionInfo, length, environment, captureName);
    }

    // Read the data into an array of bytes
    @Override
    public byte[] readBuffer(final ChannelBuffer buffer) {
        int len = getLength();
        byte[] matched = new byte[len];
        buffer.readBytes(matched, 0, len);
        return matched;
    }

    // unit tests
    ReadByteArrayBytesDecoder(int length) {
        this(newSequential(0, 0), length);
    }

    // unit tests
    ReadByteArrayBytesDecoder(int length, ExpressionContext environment, String captureName) {
        this(newSequential(0, 0), length, environment, captureName);
    }
}
