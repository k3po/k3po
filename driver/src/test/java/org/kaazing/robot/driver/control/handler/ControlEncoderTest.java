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

import java.nio.file.Paths;

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
import org.kaazing.robot.driver.jmock.Expectations;
import org.kaazing.robot.driver.control.ErrorMessage;
import org.kaazing.robot.driver.control.FinishedMessage;
import org.kaazing.robot.driver.control.PreparedMessage;
import org.kaazing.robot.driver.control.StartedMessage;

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

        String path = Paths.get("").toAbsolutePath().toString()
                + "/src/test/scripts/org/kaazing/robot/driver/control/handler/testScript.rpt";
        // @formatter:off
        final ChannelBuffer expected = copiedBuffer("PREPARED\n" +
                                                    "name:" + path + "\n" +
                                                    "\n", UTF_8);
        // @formatter:on

        context.checking(new Expectations() {
            {
                oneOf(handler).handleDownstream(with(any(ChannelHandlerContext.class)), with(message(expected)));
            }
        });

        PreparedMessage preparedMessage = new PreparedMessage();
        preparedMessage.setName(path);

        ChannelFuture future = client.connect(new LocalAddress("test")).sync();
        Channel channel = future.getChannel();
        channel.write(preparedMessage).sync();
        channel.close().sync();

        context.assertIsSatisfied();
    }

    @Test
    public void shouldEncodeStartedMessage() throws Exception {

        // @formatter:off
        String path = Paths.get("").toAbsolutePath().toString()
                + "/src/test/scripts/org/kaazing/robot/driver/control/handler/testScript.rpt";
        final ChannelBuffer expected = copiedBuffer("STARTED\n" +
                                                    "name:" + path + "\n" +
                                                    "\n", UTF_8);
        // @formatter:on

        context.checking(new Expectations() {
            {
                oneOf(handler).handleDownstream(with(any(ChannelHandlerContext.class)), with(message(expected)));
            }
        });

        StartedMessage startedMessage = new StartedMessage();
        startedMessage.setName(path);

        ChannelFuture future = client.connect(new LocalAddress("test")).sync();
        Channel channel = future.getChannel();
        channel.write(startedMessage).sync();
        channel.close().sync();

        context.assertIsSatisfied();
    }

    @Test
    public void shouldEncodeErrorMessage() throws Exception {

        // @formatter:off
        String path = Paths.get("").toAbsolutePath().toString()
                + "/src/test/scripts/org/kaazing/robot/driver/control/handler/testScript.rpt";
        final ChannelBuffer expected = copiedBuffer("ERROR\n" +
                                                    "name:" + path + "\n" +
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
        errorMessage.setName(path);
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
        String path = Paths.get("").toAbsolutePath().toString()
                + "/src/test/scripts/org/kaazing/robot/driver/control/handler/testScript.rpt";
        final ChannelBuffer expected = copiedBuffer("FINISHED\n" +
                                                    "name:" + path + "\n" +
                                                    "content-length:52\n" +
                                                    "\n" +
                                                    "connect tcp://localhost:8000\n" +
                                                    "connected\n" +
                                                    "close\n" +
                                                    "closed\n" +
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

        FinishedMessage finishedMessage = new FinishedMessage();
        finishedMessage.setName(path);
        finishedMessage.setExpectedScript("connect tcp://localhost:8000\n" + "connected\n" + "close\n" + "closed\n");
        finishedMessage.setObservedScript("connect tcp://localhost:8000\n" + "connected\n" + "close\n" + "closed\n");

        ChannelFuture future = client.connect(new LocalAddress("test")).sync();
        Channel channel = future.getChannel();
        channel.write(finishedMessage).sync();
        channel.close().sync();

        context.assertIsSatisfied();
    }
}
