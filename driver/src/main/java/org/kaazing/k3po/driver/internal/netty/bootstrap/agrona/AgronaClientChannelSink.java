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
package org.kaazing.k3po.driver.internal.netty.bootstrap.agrona;

import static org.jboss.netty.channel.Channels.fireChannelBound;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.MessageEvent;
import org.kaazing.k3po.driver.internal.netty.bootstrap.channel.AbstractChannelSink;
import org.kaazing.k3po.driver.internal.netty.channel.ChannelAddress;
import org.kaazing.k3po.driver.internal.netty.channel.FlushEvent;
import org.kaazing.k3po.driver.internal.netty.channel.ShutdownOutputEvent;
import org.kaazing.k3po.driver.internal.netty.channel.agrona.AgronaChannelAddress;

public class AgronaClientChannelSink extends AbstractChannelSink {

    @Override
    protected void connectRequested(ChannelPipeline pipeline, ChannelStateEvent evt) throws Exception {
        AgronaClientChannel channel = (AgronaClientChannel) evt.getChannel();
        AgronaChannelAddress remoteAddress = (AgronaChannelAddress) evt.getValue();

        if (!channel.isBound()) {
            ChannelAddress localAddress = remoteAddress;
            channel.setLocalAddress(localAddress);
            channel.setBound();
            fireChannelBound(channel, localAddress);
        }

        ChannelFuture future = evt.getFuture();
        channel.boss.connect(channel, remoteAddress, future);
    }

    @Override
    protected void writeRequested(ChannelPipeline pipeline, MessageEvent evt) throws Exception {
        ChannelBuffer channelBuffer = (ChannelBuffer) evt.getMessage();
        AgronaChannel channel = (AgronaChannel) evt.getChannel();
        ChannelFuture future = evt.getFuture();
        channel.worker.write(channel, channelBuffer, future);
    }

    @Override
    protected void flushRequested(ChannelPipeline pipeline, FlushEvent evt) throws Exception {
        AgronaChannel channel = (AgronaChannel) evt.getChannel();
        ChannelFuture future = evt.getFuture();
        channel.worker.flush(channel, future);
    }

    @Override
    protected void shutdownOutputRequested(ChannelPipeline pipeline, ShutdownOutputEvent evt) throws Exception {
        AgronaChannel channel = (AgronaChannel) evt.getChannel();
        ChannelFuture future = evt.getFuture();
        channel.worker.shutdownOutput(channel, future);
    }

    @Override
    protected void closeRequested(ChannelPipeline pipeline, ChannelStateEvent evt) throws Exception {
        AgronaChannel channel = (AgronaChannel) evt.getChannel();
        ChannelFuture future = evt.getFuture();
        channel.worker.close(channel, future);
    }

}
