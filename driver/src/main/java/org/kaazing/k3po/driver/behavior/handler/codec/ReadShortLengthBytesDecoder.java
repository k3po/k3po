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

public class ReadShortLengthBytesDecoder extends ReadFixedLengthBytesDecoder<Short> {

    public ReadShortLengthBytesDecoder(RegionInfo regionInfo, ExpressionContext environment, String captureName) {
        super(regionInfo, Short.SIZE / Byte.SIZE, environment, captureName);
    }

    // Read the data into a Short
    @Override
    public Short readBuffer(ChannelBuffer buffer) {
        return buffer.readShort();
    }

    // unit tests
    ReadShortLengthBytesDecoder(ExpressionContext environment, String captureName) {
        this(newSequential(0, 0), environment, captureName);
    }
}
