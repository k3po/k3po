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
import static org.jboss.netty.channel.Channels.pipeline;
import static org.jboss.netty.util.CharsetUtil.UTF_8;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelDownstreamHandler;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.local.DefaultLocalClientChannelFactory;
import org.jboss.netty.channel.local.DefaultLocalServerChannelFactory;
import org.jboss.netty.channel.local.LocalAddress;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.jmock.Mockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kaazing.robot.driver.control.ErrorMessage;
import org.kaazing.robot.driver.control.PreparedMessage;
import org.kaazing.robot.driver.control.StartedMessage;
import org.kaazing.robot.driver.jmock.Expectations;

import com.fasterxml.jackson.databind.ObjectMapper;

public class HttpControlResponseEncoderTest {
    
    private Mockery context;
    private ChannelDownstreamHandler handler;
    private ServerBootstrap server;
    private ClientBootstrap client;
    private ObjectMapper mapper;

    @Before
    public void setUp() throws Exception {

        mapper = new ObjectMapper();
        
        context = new Mockery();

        handler = context.mock(ChannelDownstreamHandler.class);

        server = new ServerBootstrap(new DefaultLocalServerChannelFactory());
        server.setPipeline(pipeline(new SimpleChannelHandler()));
        server.bind(new LocalAddress("test"));

        client = new ClientBootstrap(new DefaultLocalClientChannelFactory());
        client.setPipeline(pipeline(new SimpleChannelHandler() {
            @Override
            public void writeRequested(ChannelHandlerContext ctx, MessageEvent e) throws Exception {

                handler.handleDownstream(ctx, e);
                super.writeRequested(ctx, e);
            }
        }, new HttpControlResponseEncoder()));
    }

    @After
    public void tearDown() throws Exception {

        client.releaseExternalResources();
        server.releaseExternalResources();
    }

    @Test
    public void shouldEncodePreparedMessage() throws Exception {

        // @formatter:off
        PreparedMessage preparedMessage = new PreparedMessage();
        preparedMessage.setName("test");
        
        String contentString = mapper.writeValueAsString(preparedMessage);
        
        ChannelBuffer content = copiedBuffer(contentString, UTF_8);
        // @formatter:on
        final DefaultHttpResponse expected = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        expected.headers().add(HttpHeaders.Names.CONTENT_TYPE, "text/html");
        expected.headers().add(HttpHeaders.Names.CONTENT_LENGTH, String.format("%d", content.readableBytes()));
        expected.setContent(content);

        context.checking(new Expectations() {
            {
                oneOf(handler).handleDownstream(with(any(ChannelHandlerContext.class)), with(response(expected)));
            }
        });

        ChannelFuture future = client.connect(new LocalAddress("test")).sync();
        Channel channel = future.getChannel();
        channel.write(preparedMessage).sync();
        channel.close().sync();

        context.assertIsSatisfied();
    }

    @Test
    public void shouldEncodeStartedMessage() throws Exception {

        // @formatter:off

        StartedMessage startedMessage = new StartedMessage();
        startedMessage.setName("test");
        
        String contentString = mapper.writeValueAsString(startedMessage);
        final ChannelBuffer content = copiedBuffer(contentString, UTF_8);
        // @formatter:on
        final DefaultHttpResponse expected = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        expected.headers().add(HttpHeaders.Names.CONTENT_TYPE, "text/html");
        expected.headers().add(HttpHeaders.Names.CONTENT_LENGTH, String.format("%d", content.readableBytes()));
        expected.setContent(content);

        context.checking(new Expectations() {
            {
                oneOf(handler).handleDownstream(with(any(ChannelHandlerContext.class)), with(response(expected)));
            }
        });

        ChannelFuture future = client.connect(new LocalAddress("test")).sync();
        Channel channel = future.getChannel();
        channel.write(startedMessage).sync();
        channel.close().sync();

        context.assertIsSatisfied();
    }

    @Test
    public void shouldEncodeErrorMessage() throws Exception {

        // @formatter:off
        ErrorMessage errorMessage = new ErrorMessage();
        errorMessage.setName("test");
        errorMessage.setSummary("unexpected");
        errorMessage.setDescription("This was an unexpected error.");
        
        String contentString = mapper.writeValueAsString(errorMessage);
        
        final ChannelBuffer content = copiedBuffer(contentString, UTF_8);
        // @formatter:on
        final DefaultHttpResponse expected = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        expected.headers().add(HttpHeaders.Names.CONTENT_TYPE, "text/html");
        expected.headers().add(HttpHeaders.Names.CONTENT_LENGTH, String.format("%d", content.readableBytes()));
        expected.setContent(content);

        context.checking(new Expectations() {
            {
                oneOf(handler).handleDownstream(with(any(ChannelHandlerContext.class)), with(response(expected)));
            }
        });

        ChannelFuture future = client.connect(new LocalAddress("test")).sync();
        Channel channel = future.getChannel();
        channel.write(errorMessage).sync();
        channel.close().sync();

        context.assertIsSatisfied();
    }
}
