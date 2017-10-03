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

import static java.util.Objects.requireNonNull;
import static org.jboss.netty.buffer.ChannelBuffers.EMPTY_BUFFER;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.kaazing.k3po.driver.internal.behavior.ScriptProgressException;
import org.kaazing.k3po.lang.internal.RegionInfo;

public abstract class MessageDecoder {

    private final RegionInfo regionInfo;
    private ChannelBuffer cumulation;

    protected MessageDecoder(RegionInfo regionInfo) {
        this.regionInfo = requireNonNull(regionInfo);
    }

    public ChannelBuffer decodeLast(ChannelBuffer buffer) throws Exception {
        return decode0(buffer, true);
    }

    // Returns the ChannelBuffer that should be passed on to the next
    // handler the pipeline, or null if more data is needed by the
    // decoder.
    public ChannelBuffer decode(ChannelBuffer buffer) throws Exception {
        return decode0(buffer, false);
    }

    public RegionInfo getRegionInfo() {
        return regionInfo;
    }

    private ChannelBuffer decode0(ChannelBuffer buffer, boolean isLast) throws Exception {
        try {
            // If we don't have a cumulation buffer yet, create it
            if (cumulation == null) {
                cumulation = ChannelBuffers.dynamicBuffer(buffer.order(), 256);
            }

            // Write the input bytes in the cumulation buffer
            cumulation.writeBytes(buffer);

            Object decoded;
            if (isLast) {
                decoded = decodeBufferLast(cumulation);
            } else {
                decoded = decodeBuffer(cumulation);
            }

            if (decoded == null) {
                // Not enough data yet, keeping accumulating more (unless last)
                return null;
            }

            ChannelBuffer remaining = EMPTY_BUFFER;

            if (cumulation.readable()) {
                // The decoder did not consume all of our accumulated bytes; create
                // the ChannelBuffer to pass on.
                remaining = cumulation.readBytes(cumulation.readableBytes());
            }

            // Let the VM know we're done with the cumulation buffer
            cumulation = null;

            return remaining;
        }
        catch (ScriptProgressException e) {
            // clean up on failure to prevent side-effects when re-using decoder
            cumulation = null;
            throw e;
        }
    }

    protected ChannelBuffer createCumulationBuffer(ChannelHandlerContext ctx) {
        return ChannelBuffers.dynamicBuffer();
    }

    protected Object decodeBufferLast(ChannelBuffer buffer) throws Exception {
        // by default, no distinct behavior between last and non-last
        return decodeBuffer(buffer);
    }

    protected abstract Object decodeBuffer(ChannelBuffer buffer) throws Exception;
}
