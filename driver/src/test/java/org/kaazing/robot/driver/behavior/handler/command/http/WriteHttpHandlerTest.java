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

package org.kaazing.robot.driver.behavior.handler.command.http;

/**
 * TODO Robot HTTP Support not implemented
 *
 */

//import static org.jboss.netty.buffer.ChannelBuffers.copiedBuffer;
//import static org.jboss.netty.buffer.ChannelBuffers.wrappedBuffer;
//import static org.jboss.netty.channel.Channels.pipeline;
//import static org.junit.Assert.assertFalse;
//
//import org.jboss.netty.channel.ChannelDownstreamHandler;
//import org.jboss.netty.channel.ChannelEvent;
//import org.jboss.netty.channel.ChannelFactory;
//import org.jboss.netty.channel.ChannelHandlerContext;
//import org.jboss.netty.channel.ChannelPipeline;
//import org.jboss.netty.channel.ChannelStateEvent;
//import org.jboss.netty.channel.ChannelUpstreamHandler;
//import org.jboss.netty.channel.ExceptionEvent;
//import org.jboss.netty.channel.SimpleChannelHandler;
//import org.jboss.netty.channel.local.DefaultLocalClientChannelFactory;
//import org.jboss.netty.handler.codec.http.HttpMessage;
//import org.junit.Before;
//import org.junit.Ignore;
//import org.junit.Test;
//
//import org.kaazing.robot.driver.jmock.Mockery;
//import org.kaazing.robot.driver.behavior.handler.ExecutionHandler;
//import org.kaazing.robot.driver.behavior.handler.codec.http.HttpMessageContributingEncoder;

public class WriteHttpHandlerTest {
//
//    private Mockery context;
//    private ChannelUpstreamHandler upstream;
//    private ChannelDownstreamHandler downstream;
//    private ChannelPipeline pipeline;
//    private ChannelFactory channelFactory;
//    private WriteHttpHandler handler;
//    private boolean blockChannelOpen = true;
//    private HttpMessageContributingEncoder encoder;
//    private ExecutionHandler execution;
//    private HttpMessage message;
//
//    @Before
//    public void setUp() throws Exception {
//        context = new Mockery() {
//            {
//                setThrowFirstErrorOnAssertIsSatisfied(true);
//            }
//        };
//
//        upstream = context.mock(ChannelUpstreamHandler.class);
//        downstream = context.mock(ChannelDownstreamHandler.class);
//        encoder = context.mock(HttpMessageContributingEncoder.class);
//        message = context.mock(HttpMessage.class);
//        handler = new WriteHttpHandler(message, encoder);
//
//        execution = new ExecutionHandler();
//
//        pipeline = pipeline(new SimpleChannelHandler() {
//            @Override
//            public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
//                // block implicit channel open?
//                if (!blockChannelOpen) {
//                    ctx.sendUpstream(e);
//                }
//            }
//        }, execution, handler, new SimpleChannelHandler() {
//            @Override
//            public void handleUpstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
//                upstream.handleUpstream(ctx, e);
//                super.handleUpstream(ctx, e);
//            }
//
//            @Override
//            public void handleDownstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
//                downstream.handleDownstream(ctx, e);
//                super.handleDownstream(ctx, e);
//            }
//
//            @Override
//            public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
//                // prevent console error message
//            }
//        });
//
//        channelFactory = new DefaultLocalClientChannelFactory();
//    }
//
//    @Ignore("not implemented")
//    @Test(timeout = 2000)
//    public void shouldPropagateDownstreamHttpMessage() throws Exception {
//        final HttpMessage message = context.mock(HttpMessage.class);
//        // TODO
//    }

}
