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
package org.kaazing.k3po.driver.internal.netty.bootstrap.tcp;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.jboss.netty.channel.Channels.close;
import static org.jboss.netty.channel.Channels.future;
import static org.jboss.netty.channel.Channels.pipeline;
import static org.jboss.netty.channel.Channels.write;
import static org.junit.Assert.assertEquals;
import static org.kaazing.k3po.driver.internal.netty.channel.ChannelAddressFactory.newChannelAddressFactory;
import static org.kaazing.net.URLFactory.createURL;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import java.io.DataInputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ChildChannelStateEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.WriteCompletionEvent;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.DisableOnDebug;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;
import org.kaazing.k3po.driver.internal.netty.bootstrap.ServerBootstrapRule;
import org.kaazing.k3po.driver.internal.netty.channel.ChannelAddress;
import org.kaazing.k3po.driver.internal.netty.channel.ChannelAddressFactory;
import org.mockito.InOrder;

public class TcpServerBootstrapTest {

    @Rule
    public final ServerBootstrapRule server = new ServerBootstrapRule("tcp");

    @Rule
    public final TestRule timeout = new DisableOnDebug(new Timeout(5, SECONDS));

    @Test
    public void shouldAcceptEchoThenClose() throws Exception {

        ChannelHandler echoHandler = new SimpleChannelHandler() {
            @Override
            public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
                Channel channel = ctx.getChannel();
                ChannelBuffer message = (ChannelBuffer) e.getMessage();

                write(ctx, future(channel), message);
                close(ctx, future(channel));
            }
        };

        final ChannelGroup childChannels = new DefaultChannelGroup();
        SimpleChannelHandler parent = new SimpleChannelHandler() {

            @Override
            public void childChannelOpen(ChannelHandlerContext ctx,
                    ChildChannelStateEvent e) throws Exception {
                childChannels.add(e.getChildChannel());
                super.childChannelOpen(ctx, e);
            }

        };
        SimpleChannelHandler parentSpy = spy(parent);

        SimpleChannelHandler child = new SimpleChannelHandler();
        SimpleChannelHandler childSpy = spy(child);

        server.setParentHandler(parentSpy);
        server.setPipeline(pipeline(childSpy, echoHandler));

        ChannelAddressFactory channelAddressFactory = newChannelAddressFactory();
        ChannelAddress channelAddress = channelAddressFactory.newChannelAddress(URI.create("tcp://localhost:8000"));
        Channel binding = server.bind(channelAddress).syncUninterruptibly().getChannel();

        URL location = createURL("tcp://localhost:8000");
        URLConnection connection = location.openConnection();
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.connect();

        Writer output = new OutputStreamWriter(connection.getOutputStream(), UTF_8);
        output.write("Hello, world");
        output.close();

        DataInputStream input = new DataInputStream(connection.getInputStream());
        byte[] buf = new byte[12];
        input.readFully(buf);
        input.close();

        // wait for child channels to close
        for (Channel childChannel : childChannels) {
            ChannelFuture childCloseFuture = childChannel.getCloseFuture();
            childCloseFuture.syncUninterruptibly();
        }

        binding.close().syncUninterruptibly();
        server.shutdown();

        assertEquals("Hello, world", new String(buf, UTF_8));

        verify(parentSpy, times(6)).handleUpstream(any(ChannelHandlerContext.class), any(ChannelEvent.class));
        verify(parentSpy, times(2)).handleDownstream(any(ChannelHandlerContext.class), any(ChannelEvent.class));

        InOrder parentBind = inOrder(parentSpy);
        parentBind.verify(parentSpy).channelOpen(any(ChannelHandlerContext.class), any(ChannelStateEvent.class));
        parentBind.verify(parentSpy).bindRequested(any(ChannelHandlerContext.class), any(ChannelStateEvent.class));
        parentBind.verify(parentSpy).channelBound(any(ChannelHandlerContext.class), any(ChannelStateEvent.class));

        InOrder parentChild = inOrder(parentSpy);
        parentChild.verify(parentSpy).childChannelOpen(any(ChannelHandlerContext.class), any(ChildChannelStateEvent.class));
        parentChild.verify(parentSpy).childChannelClosed(any(ChannelHandlerContext.class), any(ChildChannelStateEvent.class));

        InOrder parentClose = inOrder(parentSpy);
        parentClose.verify(parentSpy).closeRequested(any(ChannelHandlerContext.class), any(ChannelStateEvent.class));
        parentClose.verify(parentSpy).channelUnbound(any(ChannelHandlerContext.class), any(ChannelStateEvent.class));
        parentClose.verify(parentSpy).channelClosed(any(ChannelHandlerContext.class), any(ChannelStateEvent.class));

        verify(childSpy, times(8)).handleUpstream(any(ChannelHandlerContext.class), any(ChannelStateEvent.class));
        verify(childSpy, times(2)).handleDownstream(any(ChannelHandlerContext.class), any(ChannelStateEvent.class));

        InOrder childConnect = inOrder(childSpy);
        childConnect.verify(childSpy).channelOpen(any(ChannelHandlerContext.class), any(ChannelStateEvent.class));
        childConnect.verify(childSpy).channelBound(any(ChannelHandlerContext.class), any(ChannelStateEvent.class));
        childConnect.verify(childSpy).channelConnected(any(ChannelHandlerContext.class), any(ChannelStateEvent.class));

        InOrder childReadWrite = inOrder(childSpy);
        childReadWrite.verify(childSpy).messageReceived(any(ChannelHandlerContext.class), any(MessageEvent.class));
        childReadWrite.verify(childSpy).writeRequested(any(ChannelHandlerContext.class), any(MessageEvent.class));
        childReadWrite.verify(childSpy).writeComplete(any(ChannelHandlerContext.class), any(WriteCompletionEvent.class));

        InOrder childClose = inOrder(childSpy);
        childClose.verify(childSpy).closeRequested(any(ChannelHandlerContext.class), any(ChannelStateEvent.class));
        childClose.verify(childSpy).channelDisconnected(any(ChannelHandlerContext.class), any(ChannelStateEvent.class));
        childClose.verify(childSpy).channelUnbound(any(ChannelHandlerContext.class), any(ChannelStateEvent.class));
        childClose.verify(childSpy).channelClosed(any(ChannelHandlerContext.class), any(ChannelStateEvent.class));

        verifyNoMoreInteractions(parentSpy, childSpy);
    }
}
