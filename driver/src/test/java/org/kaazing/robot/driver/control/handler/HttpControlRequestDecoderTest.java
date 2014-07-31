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

package org.kaazing.robot.driver.control.handler;

import static org.jboss.netty.buffer.ChannelBuffers.copiedBuffer;
import static org.jboss.netty.channel.Channels.future;
import static org.jboss.netty.channel.Channels.pipeline;
import static org.jboss.netty.util.CharsetUtil.UTF_8;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelUpstreamHandler;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.local.DefaultLocalClientChannelFactory;
import org.jboss.netty.channel.local.DefaultLocalServerChannelFactory;
import org.jboss.netty.channel.local.LocalAddress;
import org.jboss.netty.handler.codec.http.DefaultHttpRequest;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.jmock.Mockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kaazing.robot.driver.jmock.Expectations;
import org.kaazing.robot.driver.control.AbortMessage;
import org.kaazing.robot.driver.control.PrepareMessage;
import org.kaazing.robot.driver.control.StartMessage;

public class HttpControlRequestDecoderTest {

    private Mockery context;
    private ServerBootstrap server;
    private ChannelUpstreamHandler handler;
    private ClientBootstrap client;

    @Before
    public void setUp() throws Exception {

        context = new Mockery();

        handler = context.mock(ChannelUpstreamHandler.class);

        server = new ServerBootstrap(new DefaultLocalServerChannelFactory());
        server.setPipeline(pipeline(new HttpControlRequestDecoder(), new SimpleChannelHandler() {
            @Override
            public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
                ChannelFuture decodedFuture = future(ctx.getChannel());
                decodedFuture.setSuccess();

                handler.handleUpstream(ctx, e);
                super.messageReceived(ctx, e);
            }
        }));
        server.bind(new LocalAddress("test"));

        client = new ClientBootstrap(new DefaultLocalClientChannelFactory());
        client.setPipeline(pipeline(new SimpleChannelHandler()));
    }

    @After
    public void tearDown() throws Exception {
        client.releaseExternalResources();
        server.releaseExternalResources();
    }

    @Test
    public void shouldDecodePrepareMessage() throws Exception {

        String content = "name:test\n\n";

        final DefaultHttpRequest prepareRequest = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, "/PREPARE");
        prepareRequest.setContent(copiedBuffer(content, UTF_8));
        prepareRequest.headers().add(HttpHeaders.Names.CONTENT_LENGTH, content.length());

        final PrepareMessage expectedPrepare = new PrepareMessage();
        expectedPrepare.setName("test");

        context.checking(new Expectations() {
            {
                oneOf(handler).handleUpstream(with(any(ChannelHandlerContext.class)), with(message(expectedPrepare)));
            }
        });

        ChannelFuture future = client.connect(new LocalAddress("test")).sync();
        Channel channel = future.getChannel();
        channel.write(prepareRequest).sync();
        channel.close().sync();

        context.assertIsSatisfied();
    }

    @Test
    public void shouldDecodeAbortMessage() throws Exception {

        String content = "name:test\n\n";

        final DefaultHttpRequest abortRequest = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, "/ABORT");
        abortRequest.setContent(copiedBuffer(content, UTF_8));
        abortRequest.headers().add(HttpHeaders.Names.CONTENT_LENGTH, content.length());

        final AbortMessage expectedAbort = new AbortMessage();
        expectedAbort.setName("test");

        context.checking(new Expectations() {
            {
                oneOf(handler).handleUpstream(with(any(ChannelHandlerContext.class)), with(message(expectedAbort)));
            }
        });

        ChannelFuture future = client.connect(new LocalAddress("test")).sync();
        Channel channel = future.getChannel();
        channel.write(abortRequest).sync();
        channel.close().sync();

        context.assertIsSatisfied();
    }

    @Test
    public void shouldDecodeStartMessage() throws Exception {

        String content = "name:test\n\n";

        final DefaultHttpRequest startRequest = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, "/START");
        startRequest.setContent(copiedBuffer(content, UTF_8));
        startRequest.headers().add(HttpHeaders.Names.CONTENT_LENGTH, content.length());

        final StartMessage expectedStart = new StartMessage();
        expectedStart.setName("test");

        context.checking(new Expectations() {
            {
                oneOf(handler).handleUpstream(with(any(ChannelHandlerContext.class)), with(message(expectedStart)));
            }
        });

        ChannelFuture future = client.connect(new LocalAddress("test")).sync();
        Channel channel = future.getChannel();
        channel.write(startRequest).sync();
        channel.close().sync();

        context.assertIsSatisfied();
    }

    @Test
    public void shouldDecodeMultipleMessages() throws Exception {

        String content = "name:test\n\n";

        final DefaultHttpRequest prepareRequest = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, "/PREPARE");
        prepareRequest.setContent(copiedBuffer(content, UTF_8));
        prepareRequest.headers().add(HttpHeaders.Names.CONTENT_LENGTH, content.length());

        final PrepareMessage expectedPrepare = new PrepareMessage();
        expectedPrepare.setName("test");

        context.checking(new Expectations() {
            {
                oneOf(handler).handleUpstream(with(any(ChannelHandlerContext.class)), with(message(expectedPrepare)));
            }
        });

        ChannelFuture future = client.connect(new LocalAddress("test")).sync();
        Channel channel = future.getChannel();

        channel.write(prepareRequest).sync();

        final DefaultHttpRequest startRequest = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, "/START");
        startRequest.setContent(copiedBuffer(content, UTF_8));
        startRequest.headers().add(HttpHeaders.Names.CONTENT_LENGTH, content.length());

        final StartMessage expectedStart = new StartMessage();
        expectedStart.setName("test");

        context.checking(new Expectations() {
            {
                oneOf(handler).handleUpstream(with(any(ChannelHandlerContext.class)), with(message(expectedStart)));
            }
        });

        channel.write(startRequest).sync();

        channel.close().sync();
        context.assertIsSatisfied();
    }
}
