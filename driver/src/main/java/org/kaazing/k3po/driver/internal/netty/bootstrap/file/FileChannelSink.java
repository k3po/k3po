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
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.UpstreamMessageEvent;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.logging.InternalLoggerFactory;
import org.kaazing.k3po.driver.internal.netty.bootstrap.channel.AbstractChannelSink;
import org.kaazing.k3po.driver.internal.netty.channel.ChannelAddress;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URI;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel.MapMode;
import java.util.Map;

import static java.nio.channels.FileChannel.MapMode.READ_ONLY;
import static java.nio.channels.FileChannel.MapMode.READ_WRITE;
import static org.jboss.netty.channel.Channels.fireChannelBound;

public class FileChannelSink extends AbstractChannelSink {

    private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(FileChannelSink.class);

    // Note that read position and write position are independent in the script. We are keeping
    // two buffers(view on the same MappedByteBuffer) to achieve this and also don't have to
    // manage positions explicitly
    private ChannelBuffer readBuffer;
    private ChannelBuffer writeBuffer;

    @Override
    protected void connectRequested(ChannelPipeline pipeline, ChannelStateEvent evt) throws Exception {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("connectRequested pipeline = " + pipeline + " evt = " + evt);
        }

        ChannelFuture connectFuture = evt.getFuture();
        ChannelAddress fileAddress = (ChannelAddress) evt.getValue();
        Map<String, Object> options = fileAddress.getOptions();
        String mode = (String) options.get("mode");
        if (mode == null) {
            mode = "rw";
        }
        Long size = (Long) options.get("size");
        if (size == null) {
            size = (long) 0;
        }
        FileChannel fileChannel = (FileChannel) evt.getChannel();

        try {
            MappedByteBuffer mappedBuffer = mapFile(fileAddress.getLocation(), mode, size);
            readBuffer = ChannelBuffers.wrappedBuffer(mappedBuffer);
            setReadOffset(0);
            writeBuffer = ChannelBuffers.wrappedBuffer(mappedBuffer);
            setWriteOffset(0);

            if (!fileChannel.isBound()) {
                fileChannel.setLocalAddress(fileAddress);
                fileChannel.setBound();
                fireChannelBound(fileChannel, fileAddress);
            }

            connectFuture.setSuccess();
        } catch (Throwable t) {
            connectFuture.setFailure(t);
        }

        Channels.fireChannelConnected(fileChannel, fileAddress);

        // Send a read event using memory mapped buffer contents
        fireMessageReceived(fileChannel, fileAddress);
    }

    @Override
    protected void writeRequested(ChannelPipeline pipeline, MessageEvent evt) throws Exception {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("writeRequested pipeline = " + pipeline + " evt = " + evt);
        }
        ChannelBuffer channelBuffer = (ChannelBuffer) evt.getMessage();
        writeBuffer.writeBytes(channelBuffer);
        ChannelFuture writeFuture = evt.getFuture();
        writeFuture.setSuccess();
    }

    @Override
    protected void closeRequested(ChannelPipeline pipeline, ChannelStateEvent evt) throws Exception {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("closeRequested pipeline = " + pipeline + " evt = " + evt);
        }        final FileChannel fileChannel = (FileChannel) evt.getChannel();
        fileChannel.setClosed();

        ChannelFuture closeFuture = evt.getFuture();
        closeFuture.setSuccess();
    }


    private static MappedByteBuffer mapFile(URI fileAddress, String mode, long size) throws IOException {
        if (fileAddress.isOpaque()) {
            // robot scripts specify relative file uri as opaque !
            URI currentDir = new File(".").toURI();
            fileAddress = currentDir.resolve(fileAddress.getSchemeSpecificPart());
        }
        File location = new File(fileAddress);

        if (!location.exists()) {
            if (mode.equals("r")) {
                String msg = String.format("File = %s doesn't exist, cannot be opened in read only mode", location);
                throw new IllegalArgumentException(msg);
            }
            if (size == 0) {
                String msg = String.format("File = %s is newly created, need size to be specified", location);
                throw new IllegalArgumentException(msg);
            }
        }

        MapMode mapMode;
        switch (mode) {
            case "r":
                mapMode = READ_ONLY;
                break;
            case "rw":
                mapMode = READ_WRITE;
                break;
            default:
                throw new IllegalArgumentException(String.format("Unknown mode = %s for file = %s", mode, location));
        }

        try (RandomAccessFile file = new RandomAccessFile(location, mode);
             java.nio.channels.FileChannel channel = file.getChannel()) {

            if (size == 0) {
                size = channel.size();
            }

            return channel.map(mapMode, 0, size);
        }
    }

    public void setWriteOffset(int offset) {
        writeBuffer.writerIndex(offset);
    }

    public void setReadOffset(int offset) {
        readBuffer.readerIndex(offset);
    }

    public void fireMessageReceived(ChannelHandlerContext ctx) {
        Channels.fireMessageReceived(ctx, readBuffer, ctx.getChannel().getRemoteAddress());
    }

    private void fireMessageReceived(FileChannel fileChannel, ChannelAddress fileAddress) {
        MessageEvent msg = new UpstreamMessageEvent(fileChannel, readBuffer, fileAddress);
        fileChannel.getPipeline().sendUpstream(msg);
    }

}
