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

import static java.lang.String.format;
import static org.jboss.netty.buffer.ChannelBuffers.copiedBuffer;
import static org.jboss.netty.util.CharsetUtil.UTF_8;
import static org.kaazing.k3po.lang.RegionInfo.newSequential;

import java.nio.charset.Charset;

import org.jboss.netty.buffer.ChannelBuffer;
import org.kaazing.k3po.driver.behavior.ScriptProgressException;
import org.kaazing.k3po.lang.RegionInfo;

public class ReadExactTextDecoder extends MessageDecoder {

    private final ChannelBuffer expected;
    private final Charset charset;

    public ReadExactTextDecoder(RegionInfo regionInfo, String expected, Charset charset) {
        super(regionInfo);
        this.expected = copiedBuffer(expected, charset);
        this.charset = charset;
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
        return expected.toString(UTF_8);
    }

    // unit tests
    ReadExactTextDecoder(String expected, Charset charset) {
        this(newSequential(0, 0), expected, charset);
    }

}
