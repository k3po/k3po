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
package org.kaazing.k3po.driver.internal.control.handler;

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
import org.jmock.Mockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kaazing.k3po.driver.internal.control.ErrorMessage;
import org.kaazing.k3po.driver.internal.control.FinishedMessage;
import org.kaazing.k3po.driver.internal.control.PreparedMessage;
import org.kaazing.k3po.driver.internal.control.StartedMessage;
import org.kaazing.k3po.driver.internal.jmock.Expectations;

public class ControlEncoderTest {

    private Mockery context;
    private ChannelDownstreamHandler handler;
    private ServerBootstrap server;
    private ClientBootstrap client;

    @Before
    public void setUp() throws Exception {

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
        }, new ControlEncoder()));
    }

    @After
    public void tearDown() throws Exception {

        client.releaseExternalResources();
        server.releaseExternalResources();
    }

    @Test
    public void shouldEncodePreparedMessage() throws Exception {

        // @formatter:off
        final ChannelBuffer expected = copiedBuffer("PREPARED\n" +
                                                    "content-length:52\n" +
                                                    "\n" +
                                                    "connect tcp://localhost:8000\n" +
                                                    "connected\n" +
                                                    "close\n" +
                                                    "closed\n", UTF_8);
        // @formatter:on

        context.checking(new Expectations() {
            {
                oneOf(handler).handleDownstream(with(any(ChannelHandlerContext.class)), with(message(expected)));
            }
        });

        // @formatter:off
        PreparedMessage preparedMessage = new PreparedMessage();
        preparedMessage.setScript("connect tcp://localhost:8000\n" +
                                  "connected\n" +
                                  "close\n" +
                                  "closed\n");
        // @formatter:on

        ChannelFuture future = client.connect(new LocalAddress("test")).sync();
        Channel channel = future.getChannel();
        channel.write(preparedMessage).sync();
        channel.close().sync();

        context.assertIsSatisfied();
    }

    @Test
    public void shouldEncodeStartedMessage() throws Exception {

        // @formatter:off
        final ChannelBuffer expected = copiedBuffer("STARTED\n" +
                                                    "\n", UTF_8);
        // @formatter:on

        context.checking(new Expectations() {
            {
                oneOf(handler).handleDownstream(with(any(ChannelHandlerContext.class)), with(message(expected)));
            }
        });

        StartedMessage startedMessage = new StartedMessage();

        ChannelFuture future = client.connect(new LocalAddress("test")).sync();
        Channel channel = future.getChannel();
        channel.write(startedMessage).sync();
        channel.close().sync();

        context.assertIsSatisfied();
    }

    @Test
    public void shouldEncodeErrorMessage() throws Exception {

        // @formatter:off
        final ChannelBuffer expected = copiedBuffer("ERROR\n" +
                                                    "summary:unexpected\n" +
                                                    "content-length:29\n" +
                                                    "\n" +
                                                    "This was an unexpected error.", UTF_8);
        // @formatter:on

        context.checking(new Expectations() {
            {
                oneOf(handler).handleDownstream(with(any(ChannelHandlerContext.class)), with(message(expected)));
            }
        });

        ErrorMessage errorMessage = new ErrorMessage();
        errorMessage.setSummary("unexpected");
        errorMessage.setDescription("This was an unexpected error.");

        ChannelFuture future = client.connect(new LocalAddress("test")).sync();
        Channel channel = future.getChannel();
        channel.write(errorMessage).sync();
        channel.close().sync();

        context.assertIsSatisfied();
    }

    @Test
    public void shouldEncodeFinishedMessage() throws Exception {

        // @formatter:off
        final ChannelBuffer expected = copiedBuffer("FINISHED\n" +
                                                    "content-length:52\n" +
                                                    "\n" +
                                                    "connect tcp://localhost:8000\n" +
                                                    "connected\n" +
                                                    "close\n" +
                                                    "closed\n", UTF_8);
        // @formatter:on

        context.checking(new Expectations() {
            {
                oneOf(handler).handleDownstream(with(any(ChannelHandlerContext.class)), with(message(expected)));
            }
        });

        // @formatter:off
        FinishedMessage finishedMessage = new FinishedMessage();
        finishedMessage.setScript("connect tcp://localhost:8000\n" +
                                  "connected\n" +
                                  "close\n" +
                                  "closed\n");
        // @formatter:on

        ChannelFuture future = client.connect(new LocalAddress("test")).sync();
        Channel channel = future.getChannel();
        channel.write(finishedMessage).sync();
        channel.close().sync();

        context.assertIsSatisfied();
    }
}
