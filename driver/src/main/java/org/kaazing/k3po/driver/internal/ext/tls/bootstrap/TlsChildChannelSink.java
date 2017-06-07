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
package org.kaazing.k3po.driver.internal.ext.tls.bootstrap;

import static java.util.Objects.requireNonNull;
import static org.jboss.netty.channel.Channels.future;
import static org.kaazing.k3po.driver.internal.channel.Channels.chainFutures;
import static org.kaazing.k3po.driver.internal.channel.Channels.chainWriteCompletes;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.MessageEvent;
import org.kaazing.k3po.driver.internal.netty.bootstrap.channel.AbstractChannelSink;
import org.kaazing.k3po.driver.internal.netty.channel.AbortEvent;
import org.kaazing.k3po.driver.internal.netty.channel.FlushEvent;
import org.kaazing.k3po.driver.internal.netty.channel.ShutdownOutputEvent;

public class TlsChildChannelSink extends AbstractChannelSink {

    private final Channel transport;

    public TlsChildChannelSink(Channel transport) {
        this.transport = requireNonNull(transport);
    }

    @Override
    public ChannelFuture execute(ChannelPipeline httpPipeline, Runnable task) {
        ChannelPipeline pipeline = transport.getPipeline();
        ChannelFuture future = pipeline.execute(task);
        Channel tlsChannel = pipeline.getChannel();
        ChannelFuture tlsFuture = future(tlsChannel);
        chainFutures(future, tlsFuture);
        return tlsFuture;
    }

    @Override
    protected void setInterestOpsRequested(ChannelPipeline pipeline, ChannelStateEvent evt) throws Exception {
    }

    @Override
    protected void writeRequested(ChannelPipeline httpPipeline, MessageEvent e) throws Exception {
        ChannelFuture tlsFuture = e.getFuture();
        ChannelBuffer tlsContent = (ChannelBuffer) e.getMessage();
        int tlsReadableBytes = tlsContent.readableBytes();

        ChannelFuture future = transport.write(tlsContent);
        chainWriteCompletes(future, tlsFuture, tlsReadableBytes);
    }

    @Override
    protected void flushRequested(ChannelPipeline pipeline, FlushEvent evt) throws Exception {
        TlsChildChannel tlsChildChannel = (TlsChildChannel) pipeline.getChannel();
        ChannelFuture tlsFuture = evt.getFuture();
        flushRequested(tlsChildChannel, tlsFuture);
    }

    @Override
    protected void abortRequested(ChannelPipeline pipeline, final AbortEvent evt) throws Exception {
        ChannelFuture disconnect = transport.disconnect();
        chainFutures(disconnect, evt.getFuture());
    }

    @Override
    protected void shutdownOutputRequested(ChannelPipeline pipeline, ShutdownOutputEvent evt) throws Exception {
        TlsChildChannel httpChildChannel = (TlsChildChannel) pipeline.getChannel();
        ChannelFuture httpFuture = evt.getFuture();
        // TODO: shutdown response output is identical to close semantics (if request fully read already)
        closeRequested(httpChildChannel, httpFuture);
    }

    @Override
    protected void closeRequested(ChannelPipeline pipeline, ChannelStateEvent evt) throws Exception {
        TlsChildChannel tlsChildChannel = (TlsChildChannel) pipeline.getChannel();
        ChannelFuture tlsFuture = evt.getFuture();
        closeRequested(tlsChildChannel, tlsFuture);
    }

    private void closeRequested(final TlsChildChannel tlsChildChannel, ChannelFuture tlsFuture) {
        if (!tlsChildChannel.isOpen()) {
            tlsFuture.setSuccess();
        }
        else
        {
            ChannelFuture tlsCloseFuture = tlsChildChannel.getCloseFuture();
            if (tlsFuture != tlsCloseFuture) {
                chainFutures(tlsCloseFuture, tlsFuture);
            }

            ChannelFuture tlsFlushed = future(tlsChildChannel);
            flushRequested(tlsChildChannel, tlsFlushed);

            // setClosed() chained asynchronously after transport.close() completes
            transport.close();
        }
    }

    private void flushRequested(TlsChildChannel tlsChildChannel, ChannelFuture tlsFuture) {
        tlsFuture.setSuccess();
    }
}
