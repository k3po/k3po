/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package com.kaazing.robot.behavior.handler.command.http;

import static java.lang.Boolean.TRUE;
import static org.jboss.netty.buffer.ChannelBuffers.copiedBuffer;
import static org.jboss.netty.buffer.ChannelBuffers.wrappedBuffer;
import static org.jboss.netty.channel.ChannelState.CONNECTED;
import static org.jboss.netty.channel.ChannelState.OPEN;
import static org.jboss.netty.channel.Channels.pipeline;
import static org.jboss.netty.util.CharsetUtil.UTF_8;
import static org.junit.Assert.assertFalse;

import java.nio.channels.NotYetConnectedException;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelDownstreamHandler;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelException;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ChannelUpstreamHandler;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.WriteCompletionEvent;
import org.jboss.netty.channel.local.DefaultLocalClientChannelFactory;
import org.jboss.netty.handler.codec.http.HttpMessage;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.kaazing.netty.jmock.Expectations;
import com.kaazing.netty.jmock.Mockery;
import com.kaazing.robot.behavior.handler.ExecutionHandler;
import com.kaazing.robot.behavior.handler.codec.MessageMismatchException;
import com.kaazing.robot.behavior.handler.codec.http.HttpMessageContributingEncoder;
import com.kaazing.robot.behavior.handler.prepare.PreparationEvent;

public class WriteHttpHandlerTest {

    private Mockery context;
    private ChannelUpstreamHandler upstream;
    private ChannelDownstreamHandler downstream;
    private ChannelPipeline pipeline;
    private ChannelFactory channelFactory;
    private WriteHttpHandler handler;
    private boolean blockChannelOpen = true;
    private HttpMessageContributingEncoder encoder;
    private ExecutionHandler execution;
    private HttpMessage message;

    @Before
    public void setUp() throws Exception {
        context = new Mockery() {
            {
                setThrowFirstErrorOnAssertIsSatisfied(true);
            }
        };

        upstream = context.mock(ChannelUpstreamHandler.class);
        downstream = context.mock(ChannelDownstreamHandler.class);
        encoder = context.mock(HttpMessageContributingEncoder.class);
        message = context.mock(HttpMessage.class);
        handler = new WriteHttpHandler(message, encoder);

        execution = new ExecutionHandler();

        pipeline = pipeline(new SimpleChannelHandler() {
            @Override
            public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
                // block implicit channel open?
                if (!blockChannelOpen) {
                    ctx.sendUpstream(e);
                }
            }
        }, execution, handler, new SimpleChannelHandler() {
            @Override
            public void handleUpstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
                upstream.handleUpstream(ctx, e);
                super.handleUpstream(ctx, e);
            }

            @Override
            public void handleDownstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
                downstream.handleDownstream(ctx, e);
                super.handleDownstream(ctx, e);
            }

            @Override
            public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
                // prevent console error message
            }
        });

        channelFactory = new DefaultLocalClientChannelFactory();
    }

    @Ignore("not implemented")
    @Test(timeout = 2000)
    public void shouldPropagateDownstreamHttpMessage() throws Exception {
        final HttpMessage message = context.mock(HttpMessage.class);
        // TODO
    }

}
