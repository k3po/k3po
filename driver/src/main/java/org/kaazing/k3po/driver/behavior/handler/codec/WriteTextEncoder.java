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

import static org.jboss.netty.buffer.ChannelBuffers.copiedBuffer;

import java.nio.charset.Charset;

import org.jboss.netty.buffer.ChannelBuffer;

public class WriteTextEncoder implements MessageEncoder {

    private final String text;
    private final Charset charset;

    public WriteTextEncoder(String text, Charset charset) {
        this.text = text;
        this.charset = charset;
    }

    @Override
    public ChannelBuffer encode() {
        return copiedBuffer(text, charset);
    }

    @Override
    public String toString() {
        return text;
    }
}
