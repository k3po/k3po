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

import static java.lang.String.format;
import static org.jboss.netty.buffer.ChannelBuffers.copiedBuffer;
import static org.jboss.netty.util.CharsetUtil.UTF_8;
import static org.kaazing.k3po.lang.internal.RegionInfo.newSequential;

import java.nio.charset.Charset;

import org.jboss.netty.buffer.ChannelBuffer;
import org.kaazing.k3po.driver.internal.behavior.ScriptProgressException;
import org.kaazing.k3po.lang.internal.RegionInfo;

public class ReadExactTextDecoder extends MessageDecoder {

    private final ChannelBuffer expected;
    private final Charset charset;

    public ReadExactTextDecoder(RegionInfo regionInfo, String expected, Charset charset) {
        super(regionInfo);
        this.expected = copiedBuffer(expected, charset);
        this.charset = charset;
    }

    protected Object decodeBufferLast(ChannelBuffer buffer) throws Exception {

        if (buffer.readableBytes() < expected.readableBytes()) {
            String observedText = buffer.toString(charset);
            throw new ScriptProgressException(getRegionInfo(), format("\"%s\"", observedText));
        }

        return super.decodeBufferLast(buffer);
    }

    @Override
    protected Object decodeBuffer(ChannelBuffer buffer) throws Exception {

        if (buffer.readableBytes() < expected.readableBytes()) {
            // TODO: compare readable bytes aggressively to fail fast?
            return null;
        }

        ChannelBuffer observed = buffer.readSlice(expected.readableBytes());
        if (!observed.equals(expected)) {
            String observedText = observed.toString(charset);
            // TODO: general escaping strategy (AstRegion in exception, plus Formatter?)
            observedText = observedText.replace("\r", "\\r");
            observedText = observedText.replace("\n", "\\n");
            observedText = observedText.replace("\t", "\\t");
            throw new ScriptProgressException(getRegionInfo(), format("\"%s\"", observedText));
        }

        return buffer;
    }

    @Override
    public String toString() {
        // note: assumes charset UTF-8
        return String.format("\"%s\"", expected.toString(UTF_8));
    }

    // unit tests
    ReadExactTextDecoder(String expected, Charset charset) {
        this(newSequential(0, 0), expected, charset);
    }

}
