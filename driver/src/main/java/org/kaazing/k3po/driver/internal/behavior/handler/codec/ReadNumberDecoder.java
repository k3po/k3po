/**
 * Copyright 2007-2015, Kaazing Corporation. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kaazing.k3po.driver.internal.behavior.handler.codec;

import static org.kaazing.k3po.lang.internal.RegionInfo.newSequential;

import org.jboss.netty.buffer.ChannelBuffer;
import org.kaazing.k3po.driver.internal.behavior.ScriptProgressException;
import org.kaazing.k3po.lang.internal.RegionInfo;

public class ReadNumberDecoder extends MessageDecoder {

    private final Number expected;

    public ReadNumberDecoder(RegionInfo regionInfo, Number expected) {
        super(regionInfo);
        this.expected = expected;
    }

    @Override
    protected Object decodeBuffer(ChannelBuffer buffer) throws Exception {

        if (expected instanceof Short)
        {
            return decodeBufferAsShort(buffer);
        }
        else if (expected instanceof Integer)
        {
            return decodeBufferAsInteger(buffer);
        }
        else if (expected instanceof Long)
        {
            return decodeBufferAsLong(buffer);
        }

        throw new ScriptProgressException(getRegionInfo(), String.format("Unsupported type: %s", expected.getClass().getName()));
    }

    private Object decodeBufferAsShort(ChannelBuffer buffer) throws Exception {

        if (buffer.readableBytes() < Short.BYTES) {
            return null;
        }

        short observed = buffer.readShort();
        if (observed != expected.shortValue()) {
            throw new ScriptProgressException(getRegionInfo(), Short.toString(observed));
        }

        return buffer;
    }

    private Object decodeBufferAsInteger(ChannelBuffer buffer) throws Exception {

        if (buffer.readableBytes() < Integer.BYTES) {
            return null;
        }

        int observed = buffer.readInt();
        if (observed != expected.intValue()) {
            throw new ScriptProgressException(getRegionInfo(), Integer.toString(observed));
        }

        return buffer;
    }

    private Object decodeBufferAsLong(ChannelBuffer buffer) throws Exception {

        if (buffer.readableBytes() < Long.BYTES) {
            return null;
        }

        long observed = buffer.readLong();
        if (observed != expected.longValue()) {
            throw new ScriptProgressException(getRegionInfo(), Long.toString(observed));
        }

        return buffer;
    }

    @Override
    public String toString() {
        return expected.toString();
    }

    // unit tests
    ReadNumberDecoder(Number expected) {
        this(newSequential(0, 0), expected);
    }
}
