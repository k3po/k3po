/*
 * Copyright 2014, Kaazing Corporation. All rights reserved.
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

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.logging.InternalLoggerFactory;
import org.kaazing.k3po.driver.internal.netty.bootstrap.file.FileChannel;

import static org.jboss.netty.channel.Channels.fireMessageReceived;

public class ReadOptionOffsetHandler extends AbstractCommandHandler {

    private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(ReadOptionOffsetHandler.class);
    private final int offset;

    public ReadOptionOffsetHandler(int offset) {
        this.offset = offset;
    }

    @Override
    protected void handleUpstream1(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
        // Do not propagate remaining data to next handler(s) as the offset change make them invalid
        // This handler would fire message received event below to the next handlers
        if (!(e instanceof MessageEvent)) {
            super.handleUpstream1(ctx, e);
        }
    }

    @Override
    protected void invokeCommand(ChannelHandlerContext ctx) throws Exception {
        FileChannel channel = (FileChannel) ctx.getChannel();
        ChannelBuffer buffer = channel.readBuffer;

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(String.format("Adjusting the file %s channel for read option offset %d", channel, offset));
        }
        try {
            buffer.readerIndex(offset);
            getHandlerFuture().setSuccess();
        } catch (Throwable t) {
            getHandlerFuture().setFailure(t);
        }

        fireMessageReceived(ctx, buffer, ctx.getChannel().getRemoteAddress());
    }

    @Override
    public String toString() {
        return "read option offset " + offset;
    }

}
