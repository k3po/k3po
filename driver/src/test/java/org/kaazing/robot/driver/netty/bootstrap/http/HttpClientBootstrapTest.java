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

package org.kaazing.robot.driver.netty.bootstrap.http;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.jboss.netty.buffer.ChannelBuffers.copiedBuffer;
import static org.jboss.netty.channel.Channels.pipeline;
import static org.junit.Assert.assertEquals;
import static org.kaazing.robot.driver.netty.channel.ChannelAddressFactory.newChannelAddressFactory;
import static org.kaazing.robot.driver.netty.channel.http.HttpChannels.completeContent;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
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
import org.jboss.netty.handler.codec.http.DefaultHttpHeaders;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpHeaders.Names;
import org.jboss.netty.handler.codec.http.HttpHeaders.Values;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.rules.DisableOnDebug;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;
import org.junit.runner.RunWith;
import org.kaazing.robot.driver.netty.bootstrap.ClientBootstrapRule;
import org.kaazing.robot.driver.netty.channel.ChannelAddress;
import org.kaazing.robot.driver.netty.channel.ChannelAddressFactory;
import org.kaazing.robot.driver.netty.channel.http.HttpContentCompleteEvent;
import org.kaazing.robot.driver.netty.channel.http.SimpleHttpChannelHandler;
import org.mockito.InOrder;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

@RunWith(Theories.class)
@SuppressWarnings("restriction")
public class HttpClientBootstrapTest {

    private static enum ContentStrategy { CHUNKED, BUFFERED, EXPLICIT }

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
System.out.println(strategy);
        SimpleHttpChannelHandler client = new SimpleHttpChannelHandler() {

            @Override
            public void handleUpstream(ChannelHandlerContext ctx, ChannelEvent e)
                    throws Exception {
                System.out.println(e);
                super.handleUpstream(ctx, e);
            }

            @Override
            public void handleDownstream(ChannelHandlerContext ctx,
                    ChannelEvent e) throws Exception {
                System.out.println(e);
                super.handleDownstream(ctx, e);
            }

        };
        SimpleHttpChannelHandler clientSpy = spy(client);

        bootstrap.setPipeline(pipeline(clientSpy));

        ChannelAddressFactory channelAddressFactory = newChannelAddressFactory();
        ChannelAddress channelAddress = channelAddressFactory.newChannelAddress(URI.create("http://localhost:8000/path"));

        final AtomicReference<String> messageRef = new AtomicReference<String>();
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
        HttpChannelConfig channelConfig = (HttpChannelConfig) channel.getConfig();
        switch (strategy) {
        case BUFFERED: {
            channelConfig.setMaximumBufferedContentLength(8192);
            break;
        }
        case CHUNKED: {
            HttpHeaders writeHeaders = new DefaultHttpHeaders();
            writeHeaders.set(Names.TRANSFER_ENCODING, Values.CHUNKED);
            channelConfig.setWriteHeaders(writeHeaders);
            break;
        }
        case EXPLICIT: {
            HttpHeaders writeHeaders = new DefaultHttpHeaders();
            writeHeaders.set(Names.CONTENT_LENGTH, 12);
            channelConfig.setWriteHeaders(writeHeaders);
            break;
        }
        }
        channel.write(copiedBuffer("Hello, world", UTF_8));
        completeContent(channel);

        channel.getCloseFuture().syncUninterruptibly();

        String message;
        do {
            Thread.sleep(1L);
        } while ((message = messageRef.get()) == null);

        assertEquals("Hello, world", message);

        verify(clientSpy, times(9)).handleUpstream(any(ChannelHandlerContext.class), any(ChannelEvent.class));
        verify(clientSpy, times(3)).handleDownstream(any(ChannelHandlerContext.class), any(ChannelEvent.class));

        InOrder childConnect = inOrder(clientSpy);
        childConnect.verify(clientSpy).channelOpen(any(ChannelHandlerContext.class), any(ChannelStateEvent.class));
        childConnect.verify(clientSpy).connectRequested(any(ChannelHandlerContext.class), any(ChannelStateEvent.class));
        childConnect.verify(clientSpy).channelBound(any(ChannelHandlerContext.class), any(ChannelStateEvent.class));
        childConnect.verify(clientSpy).channelConnected(any(ChannelHandlerContext.class), any(ChannelStateEvent.class));

        InOrder childRead = inOrder(clientSpy);
        childRead.verify(clientSpy).messageReceived(any(ChannelHandlerContext.class), any(MessageEvent.class));
        childRead.verify(clientSpy).contentComplete(any(ChannelHandlerContext.class), any(HttpContentCompleteEvent.class));

        InOrder childWrite = inOrder(clientSpy);
        childWrite.verify(clientSpy).writeRequested(any(ChannelHandlerContext.class), any(MessageEvent.class));
        childWrite.verify(clientSpy).completeContentRequested(any(ChannelHandlerContext.class), any(HttpContentCompleteEvent.class));
        // asynchronous
        verify(clientSpy).writeComplete(any(ChannelHandlerContext.class), any(WriteCompletionEvent.class));

        InOrder childClose = inOrder(clientSpy);
        childClose.verify(clientSpy).channelDisconnected(any(ChannelHandlerContext.class), any(ChannelStateEvent.class));
        childClose.verify(clientSpy).channelUnbound(any(ChannelHandlerContext.class), any(ChannelStateEvent.class));
        childClose.verify(clientSpy).channelClosed(any(ChannelHandlerContext.class), any(ChannelStateEvent.class));

        verifyNoMoreInteractions(clientSpy);
    }
}
