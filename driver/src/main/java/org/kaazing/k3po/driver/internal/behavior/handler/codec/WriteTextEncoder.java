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

import java.nio.charset.Charset;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBufferFactory;

public class WriteTextEncoder implements MessageEncoder {

    private final String text;
    private final Charset charset;

    public WriteTextEncoder(String text, Charset charset) {
        this.text = text;
        this.charset = charset;
    }

    @Override
    public ChannelBuffer encode(ChannelBufferFactory bufferFactory) {
        return copiedBuffer(bufferFactory.getDefaultOrder(), text, charset);
    }

    @Override
    public String toString() {
        return String.format("\"%s\"", text);
    }
}
