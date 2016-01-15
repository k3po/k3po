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

import static java.util.Collections.singletonList;
import static org.jboss.netty.buffer.ChannelBuffers.copiedBuffer;
import static org.jboss.netty.channel.Channels.future;
import static org.jboss.netty.channel.Channels.pipeline;
import static org.jboss.netty.util.CharsetUtil.UTF_8;
import static org.junit.Assert.assertEquals;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelUpstreamHandler;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.local.DefaultLocalClientChannelFactory;
import org.jboss.netty.channel.local.DefaultLocalServerChannelFactory;
import org.jboss.netty.channel.local.LocalAddress;
import org.jmock.Mockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kaazing.k3po.driver.internal.control.AbortMessage;
import org.kaazing.k3po.driver.internal.control.PrepareMessage;
import org.kaazing.k3po.driver.internal.control.StartMessage;
import org.kaazing.k3po.driver.internal.jmock.Expectations;

public class ControlDecoderTest {

    private Mockery context;
    private ServerBootstrap server;
    private ChannelUpstreamHandler handler;
    private ClientBootstrap client;

    @Before
    public void setUp() throws Exception {

        context = new Mockery();

        handler = context.mock(ChannelUpstreamHandler.class);

        server = new ServerBootstrap(new DefaultLocalServerChannelFactory());
        server.setPipeline(pipeline(new ControlDecoder(), new SimpleChannelHandler() {
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

        String path = "org/kaazing/robot/driver/control/handler/testScript.rpt";

        final PrepareMessage expected = new PrepareMessage();
        expected.setNames(singletonList(path));

        context.checking(new Expectations() {
            {
                oneOf(handler).handleUpstream(with(any(ChannelHandlerContext.class)), with(message(expected)));
            }
        });

        // @formatter:off
        ChannelBuffer buffer = copiedBuffer("PREPARE\n" +
                                            "name:" + path
                                            + "\n" + "\n", UTF_8);
        // @formatter:on

        ChannelFuture future = client.connect(new LocalAddress("test")).sync();
        Channel channel = future.getChannel();
        channel.write(buffer).sync();
        channel.close().sync();

        assertEquals(0, buffer.readableBytes());
        context.assertIsSatisfied();
    }

    @Test
    public void shouldDecodeAbortMessage() throws Exception {

        final AbortMessage expected = new AbortMessage();

        context.checking(new Expectations() {
            {
                oneOf(handler).handleUpstream(with(any(ChannelHandlerContext.class)), with(message(expected)));
            }
        });

        // @formatter:off
        ChannelBuffer buffer = copiedBuffer("ABORT\n" +
                                            "\n", UTF_8);
        // @formatter:on

        ChannelFuture future = client.connect(new LocalAddress("test")).sync();
        Channel channel = future.getChannel();
        channel.write(buffer).sync();
        channel.close().sync();

        assertEquals(0, buffer.readableBytes());
        context.assertIsSatisfied();
    }

    @Test
    public void shouldDecodeMultipleMessages() throws Exception {

        // @formatter:off
        String path = "org/kaazing/robot/driver/control/handler/emptyScript.rpt";
        ChannelBuffer buffer1 = copiedBuffer("PREPARE\n" +
                                             "name:" + path +
                                             "\n" + "\n", UTF_8);
        // @formatter:on

        // @formatter:off
        ChannelBuffer buffer2 = copiedBuffer("START\n" +
                                             "\n", UTF_8);
        // @formatter:on

        ChannelFuture future = client.connect(new LocalAddress("test")).sync();
        Channel channel = future.getChannel();

        final PrepareMessage expectedPrepare = new PrepareMessage();
        expectedPrepare.setNames(singletonList(path));

        context.checking(new Expectations() {
            {
                oneOf(handler).handleUpstream(with(any(ChannelHandlerContext.class)), with(message(expectedPrepare)));
            }
        });

        channel.write(buffer1).sync();

        final StartMessage expectedStart = new StartMessage();

        context.checking(new Expectations() {
            {
                oneOf(handler).handleUpstream(with(any(ChannelHandlerContext.class)), with(message(expectedStart)));
            }
        });

        channel.write(buffer2).sync();

        channel.close().sync();

        assertEquals(0, buffer2.readableBytes());

        context.assertIsSatisfied();
    }

}
