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

import static org.jboss.netty.buffer.ChannelBuffers.copiedBuffer;
import static org.kaazing.k3po.lang.internal.RegionInfo.newSequential;

import org.jboss.netty.buffer.ChannelBuffer;
import org.kaazing.k3po.driver.internal.behavior.ScriptProgressException;
import org.kaazing.k3po.driver.internal.util.Utils;
import org.kaazing.k3po.lang.internal.RegionInfo;

public class ReadExactBytesDecoder extends MessageDecoder {

    private final ChannelBuffer expected;

    public ReadExactBytesDecoder(RegionInfo regionInfo, byte[] expected) {
        super(regionInfo);
        this.expected = copiedBuffer(expected);
    }

    @Override
    protected Object decodeBuffer(ChannelBuffer buffer) throws Exception {

        if (buffer.readableBytes() < expected.readableBytes()) {
            return null;
        }

        ChannelBuffer observed = buffer.readSlice(expected.readableBytes());
        if (!observed.equals(expected)) {
            throw new ScriptProgressException(getRegionInfo(), Utils.format(observed));
        }

        return buffer;
    }

    @Override
    public String toString() {
        return Utils.format(expected.array());
    }

    // unit tests
    ReadExactBytesDecoder(byte[] expected) {
        this(newSequential(0, 0), expected);
    }

}
