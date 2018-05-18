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
package org.kaazing.k3po.driver.internal.behavior.handler.event;

import static java.util.Collections.singletonList;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.jboss.netty.buffer.ChannelBuffers.copiedBuffer;
import static org.jboss.netty.channel.Channels.fireMessageReceived;
import static org.jboss.netty.util.CharsetUtil.UTF_8;
import static org.junit.Assert.assertFalse;
import static org.kaazing.k3po.lang.internal.RegionInfo.newSequential;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelConfig;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ChannelUpstreamHandler;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.local.DefaultLocalClientChannelFactory;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.jmock.lib.concurrent.Synchroniser;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.DisableOnDebug;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;
import org.kaazing.k3po.driver.internal.behavior.handler.codec.Masker;
import org.kaazing.k3po.driver.internal.behavior.handler.codec.ReadExactTextDecoder;
import org.kaazing.k3po.driver.internal.behavior.handler.prepare.PreparationEvent;
import org.kaazing.k3po.driver.internal.jmock.Expectations;
import org.kaazing.k3po.lang.internal.RegionInfo;

public class ReadHandlerBoundaryTest {

    @Rule
    public final JUnitRuleMockery context = new JUnitRuleMockery(){
        {
            setThreadingPolicy(new Synchroniser());
        }
    };

    @Rule
    public final TestRule timeout = new DisableOnDebug(new Timeout(1, SECONDS));

    @Test
    public void shouldConsumeMessageEventWithBoundary() throws Exception {

        final ChannelUpstreamHandler upstream = context.mock(ChannelUpstreamHandler.class);

        final RegionInfo regionInfo = newSequential(0, 12);
        final ReadHandler handler = new ReadHandler(singletonList(new ReadExactTextDecoder(regionInfo, "Hello, world", UTF_8)),
                                                    Masker.IDENTITY_MASKER) {
            @Override
            protected boolean isMessageAligned(ChannelConfig config) {
                return true;
            }
        };
        handler.setRegionInfo(regionInfo);

        final ChannelPipeline pipeline = Channels.pipeline(
                new SimpleChannelHandler() {
                    @Override
                    public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
                        // skip implicit channel open?
                    }
                },
                handler,
                new SimpleChannelHandler() {
                    @Override
                    public void handleUpstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
                        upstream.handleUpstream(ctx, e);
                        super.handleUpstream(ctx, e);
                    }

                    @Override
                    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
                        // prevent console error message
                    }
                });

        final ChannelFactory channelFactory = new DefaultLocalClientChannelFactory();

        context.checking(new Expectations() {
            {
                oneOf(upstream).handleUpstream(with(any(ChannelHandlerContext.class)), with(any(PreparationEvent.class)));
            }
        });

        Channel channel = channelFactory.newChannel(pipeline);
        ChannelFuture handlerFuture = handler.getHandlerFuture();

        ChannelBuffer text = copiedBuffer("Hello, world", UTF_8);
        text.markWriterIndex();

        fireMessageReceived(channel, text);

        handlerFuture.sync();
    }

    @Test
    public void shouldConsumeMessageEventWithBoundaryWhenFragmented() throws Exception {

        final ChannelUpstreamHandler upstream = context.mock(ChannelUpstreamHandler.class);

        final RegionInfo regionInfo = newSequential(0, 12);
        final ReadHandler handler = new ReadHandler(singletonList(new ReadExactTextDecoder(regionInfo, "Hello, world", UTF_8)),
                                                    Masker.IDENTITY_MASKER) {
            @Override
            protected boolean isMessageAligned(ChannelConfig config) {
                return true;
            }
        };
        handler.setRegionInfo(regionInfo);

        final ChannelPipeline pipeline = Channels.pipeline(
                new SimpleChannelHandler() {
                    @Override
                    public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
                        // skip implicit channel open?
                    }
                },
                handler,
                new SimpleChannelHandler() {
                    @Override
                    public void handleUpstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
                        upstream.handleUpstream(ctx, e);
                        super.handleUpstream(ctx, e);
                    }

                    @Override
                    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
                        // prevent console error message
                    }
                });

        final ChannelFactory channelFactory = new DefaultLocalClientChannelFactory();

        context.checking(new Expectations() {
            {
                oneOf(upstream).handleUpstream(with(any(ChannelHandlerContext.class)), with(any(PreparationEvent.class)));
            }
        });

        Channel channel = channelFactory.newChannel(pipeline);
        ChannelFuture handlerFuture = handler.getHandlerFuture();

        ChannelBuffer text = copiedBuffer("Hello, ", UTF_8);
        ChannelBuffer last = copiedBuffer("world", UTF_8);
        last.markWriterIndex();

        fireMessageReceived(channel, text);
        fireMessageReceived(channel, last);

        handlerFuture.sync();
    }

    @Test
    public void shouldConsumeMessageEventWithMissingBoundary() throws Exception {

        final ChannelUpstreamHandler upstream = context.mock(ChannelUpstreamHandler.class);

        final RegionInfo regionInfo = newSequential(0, 12);
        final ReadHandler handler = new ReadHandler(singletonList(new ReadExactTextDecoder(regionInfo, "Hello, world", UTF_8)),
                                                    Masker.IDENTITY_MASKER) {
            @Override
            protected boolean isMessageAligned(ChannelConfig config) {
                return true;
            }
        };
        handler.setRegionInfo(regionInfo);

        final ChannelPipeline pipeline = Channels.pipeline(
                new SimpleChannelHandler() {
                    @Override
                    public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
                        // skip implicit channel open?
                    }
                },
                handler,
                new SimpleChannelHandler() {
                    @Override
                    public void handleUpstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
                        upstream.handleUpstream(ctx, e);
                        super.handleUpstream(ctx, e);
                    }

                    @Override
                    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
                        // prevent console error message
                    }
                });

        final ChannelFactory channelFactory = new DefaultLocalClientChannelFactory();

        context.checking(new Expectations() {
            {
                oneOf(upstream).handleUpstream(with(any(ChannelHandlerContext.class)), with(any(PreparationEvent.class)));
            }
        });

        Channel channel = channelFactory.newChannel(pipeline);
        ChannelFuture handlerFuture = handler.getHandlerFuture();

        ChannelBuffer text = copiedBuffer("Hello, world", UTF_8);

        fireMessageReceived(channel, text);

        assertFalse(handlerFuture.isSuccess());
    }

    @Test
    public void shouldConsumeMessageEventWithUnexpectedBoundary() throws Exception {

        final ChannelUpstreamHandler upstream = context.mock(ChannelUpstreamHandler.class);

        final RegionInfo regionInfo = newSequential(0, 12);
        final ReadHandler handler = new ReadHandler(singletonList(new ReadExactTextDecoder(regionInfo, "Hello, world", UTF_8)),
                                                    Masker.IDENTITY_MASKER) {
            @Override
            protected boolean isMessageAligned(ChannelConfig config) {
                return true;
            }
        };
        handler.setRegionInfo(regionInfo);

        final ChannelPipeline pipeline = Channels.pipeline(
                new SimpleChannelHandler() {
                    @Override
                    public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
                        // skip implicit channel open?
                    }
                },
                handler,
                new SimpleChannelHandler() {
                    @Override
                    public void handleUpstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
                        upstream.handleUpstream(ctx, e);
                        super.handleUpstream(ctx, e);
                    }

                    @Override
                    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
                        // prevent console error message
                    }
                });

        final ChannelFactory channelFactory = new DefaultLocalClientChannelFactory();

        context.checking(new Expectations() {
            {
                oneOf(upstream).handleUpstream(with(any(ChannelHandlerContext.class)), with(any(PreparationEvent.class)));
            }
        });

        Channel channel = channelFactory.newChannel(pipeline);
        ChannelFuture handlerFuture = handler.getHandlerFuture();

        ChannelBuffer text = copiedBuffer("Hello, ", UTF_8);
        text.markWriterIndex();

        fireMessageReceived(channel, text);

        assertFalse(handlerFuture.isSuccess());
    }

    @Test
    public void shouldConsumeMessageEventWithBoundaryAndExtraBytes() throws Exception {

        final ChannelUpstreamHandler upstream = context.mock(ChannelUpstreamHandler.class);

        final RegionInfo regionInfo = newSequential(0, 12);
        final ReadHandler handler = new ReadHandler(singletonList(new ReadExactTextDecoder(regionInfo, "Hello, world", UTF_8)),
                                                    Masker.IDENTITY_MASKER) {
            @Override
            protected boolean isMessageAligned(ChannelConfig config) {
                return true;
            }
        };
        handler.setRegionInfo(regionInfo);

        final ChannelPipeline pipeline = Channels.pipeline(
                new SimpleChannelHandler() {
                    @Override
                    public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
                        // skip implicit channel open?
                    }
                },
                handler,
                new SimpleChannelHandler() {
                    @Override
                    public void handleUpstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
                        upstream.handleUpstream(ctx, e);
                        super.handleUpstream(ctx, e);
                    }

                    @Override
                    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
                        // prevent console error message
                    }
                });

        final ChannelFactory channelFactory = new DefaultLocalClientChannelFactory();

        context.checking(new Expectations() {
            {
                oneOf(upstream).handleUpstream(with(any(ChannelHandlerContext.class)), with(any(PreparationEvent.class)));
            }
        });

        Channel channel = channelFactory.newChannel(pipeline);
        ChannelFuture handlerFuture = handler.getHandlerFuture();

        ChannelBuffer text = copiedBuffer("Hello, world! Extra! Extra!", UTF_8);
        text.writerIndex(12);

        fireMessageReceived(channel, text);

        assertFalse(handlerFuture.isSuccess());
    }

    @Test
    public void shouldConsumeMessageEventWithBoundaryAndExtraBytesWhenFragmented() throws Exception {

        final ChannelUpstreamHandler upstream = context.mock(ChannelUpstreamHandler.class);

        final RegionInfo regionInfo = newSequential(0, 12);
        final ReadHandler handler = new ReadHandler(singletonList(new ReadExactTextDecoder(regionInfo, "Hello, world", UTF_8)),
                                                    Masker.IDENTITY_MASKER) {
            @Override
            protected boolean isMessageAligned(ChannelConfig config) {
                return true;
            }
        };
        handler.setRegionInfo(regionInfo);

        final ChannelPipeline pipeline = Channels.pipeline(
                new SimpleChannelHandler() {
                    @Override
                    public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
                        // skip implicit channel open?
                    }
                },
                handler,
                new SimpleChannelHandler() {
                    @Override
                    public void handleUpstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
                        upstream.handleUpstream(ctx, e);
                        super.handleUpstream(ctx, e);
                    }

                    @Override
                    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
                        // prevent console error message
                    }
                });

        final ChannelFactory channelFactory = new DefaultLocalClientChannelFactory();

        context.checking(new Expectations() {
            {
                oneOf(upstream).handleUpstream(with(any(ChannelHandlerContext.class)), with(any(PreparationEvent.class)));
            }
        });

        Channel channel = channelFactory.newChannel(pipeline);
        ChannelFuture handlerFuture = handler.getHandlerFuture();

        ChannelBuffer text = copiedBuffer("Hello, ", UTF_8);
        ChannelBuffer last = copiedBuffer("world! Extra! Extra!", UTF_8);
        int writerIndex = last.writerIndex();
        last.writerIndex(5);
        last.markWriterIndex();
        last.writerIndex(writerIndex);

        fireMessageReceived(channel, text);
        fireMessageReceived(channel, last);

        assertFalse(handlerFuture.isSuccess());
    }
}
