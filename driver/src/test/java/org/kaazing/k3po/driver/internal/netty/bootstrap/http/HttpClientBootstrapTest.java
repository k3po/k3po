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
package org.kaazing.k3po.driver.internal.netty.bootstrap.http;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.jboss.netty.buffer.ChannelBuffers.copiedBuffer;
import static org.jboss.netty.channel.Channels.pipeline;
import static org.junit.Assert.assertEquals;
import static org.kaazing.k3po.driver.internal.netty.channel.ChannelAddressFactory.newChannelAddressFactory;
import static org.kaazing.k3po.driver.internal.netty.channel.Channels.shutdownOutput;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.EnumSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.WriteCompletionEvent;
import org.jboss.netty.channel.socket.nio.NioSocketChannel;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpHeaders.Names;
import org.jboss.netty.handler.codec.http.HttpHeaders.Values;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.rules.DisableOnDebug;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;
import org.junit.runner.RunWith;
import org.kaazing.k3po.driver.internal.netty.bootstrap.ClientBootstrapRule;
import org.kaazing.k3po.driver.internal.netty.channel.ChannelAddress;
import org.kaazing.k3po.driver.internal.netty.channel.ChannelAddressFactory;
import org.kaazing.k3po.driver.internal.netty.channel.ShutdownInputEvent;
import org.kaazing.k3po.driver.internal.netty.channel.ShutdownOutputEvent;
import org.kaazing.k3po.driver.internal.netty.channel.SimpleChannelHandler;
import org.mockito.InOrder;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

@RunWith(Theories.class)
@SuppressWarnings("restriction")
public class HttpClientBootstrapTest {

    private enum ContentStrategy {
        CHUNKED, BUFFERED, EXPLICIT
    }

    @DataPoints
    public static final Set<ContentStrategy> CONTENT_STRATEGIES = EnumSet.allOf(ContentStrategy.class);

    @Rule
    public final ClientBootstrapRule bootstrap = new ClientBootstrapRule("http");

    @Rule
    public final TestRule timeout = new DisableOnDebug(new Timeout(5, SECONDS));

    private HttpServer httpServer;

    @Before
    public void startHttpServer() throws IOException {
        httpServer = HttpServer.create(new InetSocketAddress("localhost", 8000), 10);
        httpServer.start();
    }

    @After
    public void stopHttpServer() throws InterruptedException {
        // stop immediately
        httpServer.stop(0);
    }

    @Theory
    public void shouldConnectEchoThenClose(final ContentStrategy strategy) throws Exception {

        SimpleChannelHandler client = new SimpleChannelHandler();
        SimpleChannelHandler clientSpy = spy(client);

        bootstrap.setPipeline(pipeline(clientSpy));

        ChannelAddressFactory channelAddressFactory = newChannelAddressFactory();
        ChannelAddress channelAddress = channelAddressFactory.newChannelAddress(URI.create("http://localhost:8000/path"));

        final AtomicReference<String> messageRef = new AtomicReference<>();
        HttpContext httpContext = httpServer.createContext("/path");
        httpContext.setHandler(new HttpHandler() {

            @Override
            public void handle(HttpExchange exchange) throws IOException {
                DataInputStream input = new DataInputStream(exchange.getRequestBody());
                byte[] buf = new byte[12];
                input.readFully(buf);
                input.close();

                messageRef.set(new String(buf, UTF_8));

                exchange.sendResponseHeaders(200, buf.length);
                OutputStream output = exchange.getResponseBody();
                output.write(buf);
                output.close();
            }
        });

        Channel channel = bootstrap.connect(channelAddress).syncUninterruptibly().getChannel();
        HttpChannelConfig config = (HttpChannelConfig) channel.getConfig();
        switch (strategy) {
        case BUFFERED: {
            config.setMaximumBufferedContentLength(8192);
            break;
        }
        case CHUNKED: {
            HttpHeaders writeHeaders = config.getWriteHeaders();
            writeHeaders.set(Names.TRANSFER_ENCODING, Values.CHUNKED);
            break;
        }
        case EXPLICIT: {
            HttpHeaders writeHeaders = config.getWriteHeaders();
            writeHeaders.set(Names.CONTENT_LENGTH, 12);
            break;
        }
        }
        channel.write(copiedBuffer("Hello, world", UTF_8)).syncUninterruptibly();
        shutdownOutput(channel).syncUninterruptibly();
        channel.getCloseFuture().syncUninterruptibly();

        String message;
        do {
            Thread.sleep(1L);
        } while ((message = messageRef.get()) == null);

        assertEquals("Hello, world", message);

        bootstrap.shutdown();

        verify(clientSpy, times(10)).handleUpstream(any(ChannelHandlerContext.class), any(ChannelEvent.class));
        verify(clientSpy, times(5)).handleDownstream(any(ChannelHandlerContext.class), any(ChannelEvent.class));

        InOrder childConnect = inOrder(clientSpy);
        childConnect.verify(clientSpy).channelOpen(any(ChannelHandlerContext.class), any(ChannelStateEvent.class));
        childConnect.verify(clientSpy).setInterestOpsRequested(any(ChannelHandlerContext.class), any(ChannelStateEvent.class));
        childConnect.verify(clientSpy).connectRequested(any(ChannelHandlerContext.class), any(ChannelStateEvent.class));
        childConnect.verify(clientSpy).channelBound(any(ChannelHandlerContext.class), any(ChannelStateEvent.class));
        childConnect.verify(clientSpy).channelConnected(any(ChannelHandlerContext.class), any(ChannelStateEvent.class));

        InOrder childWrite = inOrder(clientSpy);
        childWrite.verify(clientSpy).writeRequested(any(ChannelHandlerContext.class), any(MessageEvent.class));
        childWrite.verify(clientSpy).shutdownOutputRequested(any(ChannelHandlerContext.class), any(ShutdownOutputEvent.class));
        // asynchronous
        verify(clientSpy, times(2)).setInterestOpsRequested(any(ChannelHandlerContext.class), any(ChannelStateEvent.class));
        verify(clientSpy).writeComplete(any(ChannelHandlerContext.class), any(WriteCompletionEvent.class));

        InOrder childRead = inOrder(clientSpy);
        childRead.verify(clientSpy).channelInterestChanged(any(ChannelHandlerContext.class), any(ChannelStateEvent.class));
        childRead.verify(clientSpy).messageReceived(any(ChannelHandlerContext.class), any(MessageEvent.class));
        childRead.verify(clientSpy).inputShutdown(any(ChannelHandlerContext.class), any(ShutdownInputEvent.class));

        InOrder childClose = inOrder(clientSpy);
        childClose.verify(clientSpy).channelDisconnected(any(ChannelHandlerContext.class), any(ChannelStateEvent.class));
        childClose.verify(clientSpy).channelUnbound(any(ChannelHandlerContext.class), any(ChannelStateEvent.class));
        childClose.verify(clientSpy).channelClosed(any(ChannelHandlerContext.class), any(ChannelStateEvent.class));

        verifyNoMoreInteractions(clientSpy);
    }

    @Test
    public void shouldPropagateTransportOptions() throws Exception {

        SimpleChannelHandler client = new SimpleChannelHandler();
        SimpleChannelHandler clientSpy = spy(client);

        bootstrap.setPipeline(pipeline(clientSpy));

        ChannelAddressFactory channelAddressFactory = newChannelAddressFactory();
        ChannelAddress channelAddress = channelAddressFactory.newChannelAddress(URI.create("http://localhost:8000/path"));

        httpServer.createContext("/path");

        bootstrap.setOption("writeBufferLowWaterMark", 123);

        HttpClientChannel channel = (HttpClientChannel) bootstrap.connect(channelAddress).syncUninterruptibly().getChannel();

        assertEquals(123, channel.getConfig().getTransportOptions().get("writeBufferLowWaterMark"));
        HttpClientChannelSink sink = (HttpClientChannelSink) channel.getPipeline().getSink();
        Field field = sink.getClass().getDeclaredField("transport");
        field.setAccessible(true);
        NioSocketChannel transport = (NioSocketChannel) field.get(sink);
        assertEquals(123, transport.getConfig().getWriteBufferLowWaterMark());
        bootstrap.shutdown();
    }
}
