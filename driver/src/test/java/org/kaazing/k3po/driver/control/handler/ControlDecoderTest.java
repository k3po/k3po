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

package org.kaazing.k3po.driver.control.handler;

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
import org.kaazing.k3po.driver.control.AbortMessage;
import org.kaazing.k3po.driver.control.PrepareMessage;
import org.kaazing.k3po.driver.control.StartMessage;
import org.kaazing.k3po.driver.control.handler.ControlDecoder;
import org.kaazing.k3po.driver.jmock.Expectations;

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
