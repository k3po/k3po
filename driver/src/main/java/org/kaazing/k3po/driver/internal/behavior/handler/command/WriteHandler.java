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
package org.kaazing.k3po.driver.internal.behavior.handler.command;

import static org.jboss.netty.buffer.ChannelBuffers.copiedBuffer;
import static org.jboss.netty.buffer.ChannelBuffers.wrappedBuffer;
import static org.jboss.netty.channel.Channels.write;
import static org.kaazing.k3po.driver.internal.behavior.handler.codec.Masker.IDENTITY_MASKER;

import java.util.List;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBufferFactory;
import org.jboss.netty.channel.ChannelConfig;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.kaazing.k3po.driver.internal.behavior.handler.codec.Masker;
import org.kaazing.k3po.driver.internal.behavior.handler.codec.MessageEncoder;

public class WriteHandler extends AbstractCommandHandler {

    private final List<MessageEncoder> encoders;
    private final Masker masker;

    public WriteHandler(List<MessageEncoder> encoders, Masker masker) {
        if (encoders == null) {
            throw new NullPointerException("encoders");
        } else if (encoders.size() == 0) {
            throw new IllegalArgumentException("must have at least one encoder");
        }
        this.encoders = encoders;
        this.masker = masker;
    }

    @Override
    protected void invokeCommand(ChannelHandlerContext ctx) throws Exception {
        ChannelConfig config = ctx.getChannel().getConfig();
        ChannelBufferFactory bufferFactory = config.getBufferFactory();
        ChannelBuffer[] buffers = new ChannelBuffer[encoders.size()];
        int idx = 0;
        for (MessageEncoder encoder : encoders) {
            buffers[idx] = encoder.encode(bufferFactory);
            idx++;
        }

        if (masker == IDENTITY_MASKER) {
            // avoid unnecessary copy when masking disabled
            ChannelBuffer bytes = wrappedBuffer(buffers);
            write(ctx, getHandlerFuture(), bytes);
        }
        else {
            ChannelBuffer bytes = copiedBuffer(buffers);
            ChannelBuffer maskedBytes = masker.applyMask(bytes);
            write(ctx, getHandlerFuture(), maskedBytes);
        }
    }

    @Override
    protected StringBuilder describe(StringBuilder sb) {
        sb.append("write ");
        for (MessageEncoder encoder : encoders) {
            sb.append(encoder).append(' ');
        }
        sb.setLength(sb.length() - 1);
        return sb;
    }

}
