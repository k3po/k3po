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
package org.kaazing.k3po.driver.internal.netty.bootstrap.file;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.logging.InternalLoggerFactory;
import org.kaazing.k3po.driver.internal.netty.bootstrap.channel.AbstractChannelSink;
import org.kaazing.k3po.driver.internal.netty.channel.ChannelAddress;

import static org.jboss.netty.channel.Channels.fireChannelBound;

public class FileChannelSink extends AbstractChannelSink {

    private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(FileChannelSink.class);

    @Override
    protected void connectRequested(ChannelPipeline pipeline, ChannelStateEvent evt) throws Exception {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("connectRequested pipeline = " + pipeline + " evt = " + evt);
        }

        FileChannel fileChannel = (FileChannel) evt.getChannel();
        ChannelAddress fileAddress = (ChannelAddress) evt.getValue();

        if (!fileChannel.isBound()) {
            fileChannel.setLocalAddress(fileAddress);
            fileChannel.setBound();
            fireChannelBound(fileChannel, fileAddress);
        }

        ChannelFuture connectFuture = evt.getFuture();
        try {
            fileChannel.mapFile();
            connectFuture.setSuccess();
        } catch (Throwable t) {
            connectFuture.setFailure(t);
        }

        fileChannel.setConnected();
        Channels.fireChannelConnected(fileChannel, fileAddress);

        // Send a read event using memory mapped buffer contents so that reads in the
        // scripts can be matched
        fileChannel.fireMessageReceived(fileChannel, fileAddress);
    }

    @Override
    protected void writeRequested(ChannelPipeline pipeline, MessageEvent evt) throws Exception {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("writeRequested pipeline = " + pipeline + " evt = " + evt);
        }
        ChannelBuffer channelBuffer = (ChannelBuffer) evt.getMessage();
        FileChannel fileChannel = (FileChannel) evt.getChannel();
        ChannelFuture writeFuture = evt.getFuture();
        try {
            fileChannel.write(channelBuffer);
            writeFuture.setSuccess();
        } catch (Throwable t) {
            writeFuture.setFailure(t);
        }
    }

    @Override
    protected void closeRequested(ChannelPipeline pipeline, ChannelStateEvent evt) throws Exception {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("closeRequested pipeline = " + pipeline + " evt = " + evt);
        }
        FileChannel fileChannel = (FileChannel) evt.getChannel();
        ChannelFuture closeFuture = evt.getFuture();
        fileChannel.setClosed();
        closeFuture.setSuccess();
    }

}
