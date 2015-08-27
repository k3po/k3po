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

package org.kaazing.k3po.driver.internal.netty.bootstrap.file;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.UpstreamMessageEvent;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.logging.InternalLoggerFactory;
import org.kaazing.k3po.driver.internal.netty.bootstrap.channel.AbstractChannelSink;
import org.kaazing.k3po.driver.internal.netty.channel.ChannelAddress;
import uk.co.real_logic.agrona.IoUtil;
import uk.co.real_logic.agrona.concurrent.UnsafeBuffer;

import java.io.File;
import java.nio.MappedByteBuffer;

import static org.jboss.netty.buffer.ChannelBuffers.copiedBuffer;
import static org.jboss.netty.channel.Channels.fireChannelBound;

public class FileChannelSink extends AbstractChannelSink {

    private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(FileChannelSink.class);
    private UnsafeBuffer unsafeBuffer;
    private int writeOffset;

    @Override
    protected void connectRequested(ChannelPipeline pipeline, ChannelStateEvent evt) throws Exception {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("connectRequested pipeline = " + pipeline + " evt = " + evt);
        }

        try {

            final ChannelAddress fileAddress = (ChannelAddress) evt.getValue();
            final FileChannel fileChannel = (FileChannel) evt.getChannel();

            File file = new File(fileAddress.getLocation());
            MappedByteBuffer buf = IoUtil.mapExistingFile(file, fileAddress.toString());
            unsafeBuffer = new UnsafeBuffer(buf);

            if (!fileChannel.isBound()) {
                fileChannel.setLocalAddress(fileAddress);
                fileChannel.setBound();
                fireChannelBound(fileChannel, fileAddress);
            }


            ChannelFuture connectFuture = evt.getFuture();
            connectFuture.setSuccess();

            Channels.fireChannelConnected(fileChannel, fileAddress);

            // Send a read event using memory mapped buffer contents
            ChannelBuffer channelBuffer = ChannelBuffers.wrappedBuffer(buf);
            MessageEvent msg = new UpstreamMessageEvent(fileChannel, channelBuffer, fileAddress);
            fileChannel.getPipeline().sendUpstream(msg);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    @Override
    protected void writeRequested(ChannelPipeline pipeline, MessageEvent evt) throws Exception {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("writeRequested pipeline = " + pipeline + " evt = " + evt);
        }
        ChannelBuffer channelBuffer = (ChannelBuffer) evt.getMessage();
        while (channelBuffer.readable()) {
            unsafeBuffer.putByte(writeOffset++, channelBuffer.readByte());
        }

        ChannelFuture writeFuture = evt.getFuture();
        writeFuture.setSuccess();
    }

//    @Override
//    protected void bindRequested(ChannelPipeline pipeline, ChannelStateEvent evt) throws Exception {
//        System.out.println("bindRequested pipeline = " + pipeline + " evt = " + evt);
//        ChannelFuture bindFuture = evt.getFuture();
//        bindFuture.setSuccess();
//        Channels.fireChannelBound(evt.getChannel(), null);
//    }
//
//    @Override
//    protected void unbindRequested(ChannelPipeline pipeline, ChannelStateEvent evt) throws Exception {
//        System.out.println("unbindRequested pipeline = " + pipeline + " evt = " + evt);
//    }

    @Override
    protected void closeRequested(ChannelPipeline pipeline, ChannelStateEvent evt) throws Exception {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("closeRequested pipeline = " + pipeline + " evt = " + evt);
        }        final FileChannel fileChannel = (FileChannel) evt.getChannel();
        fileChannel.setClosed();

        ChannelFuture closeFuture = evt.getFuture();
        closeFuture.setSuccess();
    }

}
