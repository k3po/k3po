/*
 * Copyright (c) 2014 "Kaazing Corporation," (www.kaazing.com)
 *
 * This file is part of Robot.
 *
 * Robot is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.kaazing.robot.driver.netty.bootstrap.tcp;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.jboss.netty.buffer.ChannelBuffers.copiedBuffer;
import static org.jboss.netty.channel.Channels.pipeline;
import static org.junit.Assert.assertEquals;
import static org.kaazing.robot.driver.netty.channel.ChannelAddressFactory.newChannelAddressFactory;
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
import org.kaazing.robot.driver.netty.bootstrap.ClientBootstrapRule;
import org.kaazing.robot.driver.netty.channel.ChannelAddress;
import org.kaazing.robot.driver.netty.channel.ChannelAddressFactory;
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

        Future<String> readFuture = executor.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                
                try (ServerSocket server = new ServerSocket(8000)) {
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
            }
        });

        Channel channel = bootstrap.connect(channelAddress).syncUninterruptibly().getChannel();
        channel.write(copiedBuffer("Hello, world", UTF_8));
        channel.getCloseFuture().syncUninterruptibly();

        String message;
        do {
            Thread.sleep(1L);
        } while ((message = readFuture.get()) == null);

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
