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

import org.jboss.netty.buffer.ChannelBuffer;
import org.kaazing.k3po.lang.RegionInfo;
import org.kaazing.k3po.lang.el.ExpressionContext;
import org.kaazing.k3po.lang.parser.ScriptParseException;

public abstract class ReadFixedLengthBytesDecoder<T> extends MessageDecoder {

    private final int length;
    private final ExpressionContext environment;
    private final String captureName;

    public ReadFixedLengthBytesDecoder(RegionInfo regionInfo, int length) {
        this(regionInfo, length, null, null);
    }

    public ReadFixedLengthBytesDecoder(RegionInfo regionInfo, int length, ExpressionContext environment, String captureName) {
        super(regionInfo);
        this.length = length;
        this.environment = environment;
        this.captureName = captureName;
    }

    protected abstract T readBuffer(ChannelBuffer buffer);


    public int getLength() {
        return length;
    }

    @Override
    protected Object decodeBufferLast(ChannelBuffer buffer) throws Exception {

        if (buffer.readableBytes() < length) {
            throw new ScriptParseException("Not enough bytes");
        }

        if (captureName == null) {
            buffer.readSlice(length);
        } else {
            T value = readBuffer(buffer);
            environment.getELResolver().setValue(environment, null, captureName, value);
        }
        return buffer;
    }

    @Override
    protected Object decodeBuffer(ChannelBuffer buffer) throws Exception {

        if (buffer.readableBytes() < length) {
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

    @Override
    public String toString() {
        return String.format("%d bytes", length);
    }
}
