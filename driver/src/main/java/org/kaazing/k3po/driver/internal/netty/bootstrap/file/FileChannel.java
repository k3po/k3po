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
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelSink;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.UpstreamMessageEvent;
import org.kaazing.k3po.driver.internal.netty.bootstrap.channel.AbstractChannel;
import org.kaazing.k3po.driver.internal.netty.channel.ChannelAddress;
import org.kaazing.k3po.driver.internal.netty.channel.file.FileChannelAddress;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URI;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel.MapMode;

import static java.nio.channels.FileChannel.MapMode.READ_ONLY;
import static java.nio.channels.FileChannel.MapMode.READ_WRITE;
import static org.jboss.netty.channel.Channels.fireChannelOpen;

public final class FileChannel extends AbstractChannel<FileChannelConfig> {

    // Note that read position and write position are independent in the script. We are keeping
    // two buffers(view on the same MappedByteBuffer) to achieve this and also don't have to
    // manage positions explicitly
    private ChannelBuffer readBuffer;
    private ChannelBuffer writeBuffer;

    FileChannel(ChannelFactory factory, ChannelPipeline pipeline, ChannelSink sink) {
        super(null, factory, pipeline, sink, new DefaultFileChannelConfig());

        fireChannelOpen(this);
    }

    @Override
    protected void setBound() {
        super.setBound();
    }

    @Override
    protected void setConnected() {
        super.setConnected();
    }

    @Override
    protected boolean setClosed() {
        // Do we need to call MappedByteBuffer#force() ?
        // Also unmap the memory mapper buffer without waiting for GC ??
        return super.setClosed();
    }

    @Override
    protected void setLocalAddress(ChannelAddress localAddress) {
        super.setLocalAddress(localAddress);
    }

    @Override
    public String toString() {
        ChannelAddress localAddress = this.getLocalAddress();
        return localAddress != null ? localAddress.toString() : super.toString();
    }

    void write(ChannelBuffer channelBuffer) {
        writeBuffer.writeBytes(channelBuffer);
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

    void fireMessageReceived(FileChannel fileChannel, ChannelAddress fileAddress) {
        MessageEvent msg = new UpstreamMessageEvent(fileChannel, fileChannel.readBuffer, fileAddress);
        fileChannel.getPipeline().sendUpstream(msg);
    }

    void mapFile() throws IOException {
        FileChannelAddress address = (FileChannelAddress) getLocalAddress();
        MappedByteBuffer mappedBuffer = mapFile(address.getLocation(), address.mode(), address.size());
        readBuffer = ChannelBuffers.wrappedBuffer(mappedBuffer);
        setReadOffset(0);
        writeBuffer = ChannelBuffers.wrappedBuffer(mappedBuffer);
        setWriteOffset(0);
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
                // parent directory must exist to create file
                File parentDir = location.getParentFile();
                parentDir.mkdirs();
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

}
