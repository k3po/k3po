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
import static org.jboss.netty.buffer.ChannelBuffers.copiedBuffer;
import static org.jboss.netty.channel.Channels.pipeline;
import static org.junit.Assert.assertEquals;
import static org.kaazing.k3po.driver.internal.netty.channel.ChannelAddressFactory.newChannelAddressFactory;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import java.io.DataInputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.WriteCompletionEvent;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.DisableOnDebug;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;
import org.kaazing.k3po.driver.internal.netty.bootstrap.ClientBootstrapRule;
import org.kaazing.k3po.driver.internal.netty.channel.ChannelAddress;
import org.kaazing.k3po.driver.internal.netty.channel.ChannelAddressFactory;
import org.mockito.InOrder;

public class TcpClientBootstrapTest {

    @Rule
    public final ClientBootstrapRule bootstrap = new ClientBootstrapRule("tcp");

    @Rule
    public final TestRule timeout = new DisableOnDebug(new Timeout(5, SECONDS));

    private ExecutorService executor;

    @Before
    public void createExecutor() {
        executor = Executors.newSingleThreadExecutor();
    }

    @After
    public void destroyExecutor() throws InterruptedException {
        executor.shutdown();
        executor.awaitTermination(1, SECONDS);
    }

    @Test
    public void shouldConnectEchoThenClose() throws Exception {

        SimpleChannelHandler client = new SimpleChannelHandler();
        SimpleChannelHandler clientSpy = spy(client);

        bootstrap.setPipeline(pipeline(clientSpy));

        ChannelAddressFactory channelAddressFactory = newChannelAddressFactory();
        ChannelAddress channelAddress = channelAddressFactory.newChannelAddress(URI.create("tcp://localhost:8000"));

        String message;
        try (ServerSocket server = new ServerSocket(8000)) {

            Future<String> readFuture = executor.submit(new Callable<String>() {
                @Override
                public String call() throws Exception {

                    Socket child = server.accept();

                    DataInputStream input = new DataInputStream(child.getInputStream());
                    byte[] buf = new byte[12];
                    input.readFully(buf);
                    child.shutdownInput();

                    OutputStream output = child.getOutputStream();
                    output.write(buf);
                    child.shutdownOutput();

                    child.close();

                    return new String(buf, UTF_8);
                }
            });

            Channel channel = bootstrap.connect(channelAddress).syncUninterruptibly().getChannel();
            channel.write(copiedBuffer("Hello, world", UTF_8));
            channel.getCloseFuture().syncUninterruptibly();

            do {
                Thread.sleep(1L);
            } while ((message = readFuture.get()) == null);
        }

        assertEquals("Hello, world", message);

        verify(clientSpy, times(8)).handleUpstream(any(ChannelHandlerContext.class), any(ChannelEvent.class));
        verify(clientSpy, times(2)).handleDownstream(any(ChannelHandlerContext.class), any(ChannelEvent.class));

        InOrder childConnect = inOrder(clientSpy);
        childConnect.verify(clientSpy).channelOpen(any(ChannelHandlerContext.class), any(ChannelStateEvent.class));
        childConnect.verify(clientSpy).connectRequested(any(ChannelHandlerContext.class), any(ChannelStateEvent.class));
        childConnect.verify(clientSpy).channelBound(any(ChannelHandlerContext.class), any(ChannelStateEvent.class));
        childConnect.verify(clientSpy).channelConnected(any(ChannelHandlerContext.class), any(ChannelStateEvent.class));

        InOrder childReadWrite = inOrder(clientSpy);
        childReadWrite.verify(clientSpy).writeRequested(any(ChannelHandlerContext.class), any(MessageEvent.class));
        childReadWrite.verify(clientSpy).writeComplete(any(ChannelHandlerContext.class), any(WriteCompletionEvent.class));
        childReadWrite.verify(clientSpy).messageReceived(any(ChannelHandlerContext.class), any(MessageEvent.class));

        InOrder childClose = inOrder(clientSpy);
        childClose.verify(clientSpy).channelDisconnected(any(ChannelHandlerContext.class), any(ChannelStateEvent.class));
        childClose.verify(clientSpy).channelUnbound(any(ChannelHandlerContext.class), any(ChannelStateEvent.class));
        childClose.verify(clientSpy).channelClosed(any(ChannelHandlerContext.class), any(ChannelStateEvent.class));

        verifyNoMoreInteractions(clientSpy);
    }
}
