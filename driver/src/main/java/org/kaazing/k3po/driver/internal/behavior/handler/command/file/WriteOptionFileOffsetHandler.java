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
package org.kaazing.k3po.driver.internal.behavior.handler.command.file;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.logging.InternalLoggerFactory;
import org.kaazing.k3po.driver.internal.behavior.handler.command.AbstractCommandHandler;
import org.kaazing.k3po.driver.internal.netty.bootstrap.file.FileChannel;

public class WriteOptionFileOffsetHandler extends AbstractCommandHandler {

    private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(WriteOptionFileOffsetHandler.class);
    private final int offset;

    public WriteOptionFileOffsetHandler(int offset) {
        this.offset = offset;
    }

    @Override
    protected void invokeCommand(ChannelHandlerContext ctx) throws Exception {
        FileChannel channel = (FileChannel) ctx.getChannel();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(String.format("Setting write option offset %d for channel %s", offset, channel));
        }
        try {
            channel.setWriteOffset(offset);
            getHandlerFuture().setSuccess();
        } catch (Throwable t) {
            getHandlerFuture().setFailure(t);
        }
    }

    @Override
    protected StringBuilder describe(StringBuilder sb) {
        return sb.append("write option offset " + offset);
    }

}
