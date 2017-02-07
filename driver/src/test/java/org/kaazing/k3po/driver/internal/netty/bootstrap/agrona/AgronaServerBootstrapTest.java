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
package org.kaazing.k3po.driver.internal.netty.bootstrap.agrona;

import static java.lang.String.format;
import static java.lang.Thread.sleep;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.jboss.netty.channel.Channels.close;
import static org.jboss.netty.channel.Channels.future;
import static org.jboss.netty.channel.Channels.pipeline;
import static org.jboss.netty.channel.Channels.write;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.kaazing.k3po.driver.internal.netty.channel.ChannelAddressFactory.newChannelAddressFactory;
import static org.kaazing.k3po.driver.internal.netty.channel.Channels.flush;
import static org.kaazing.k3po.driver.internal.netty.channel.agrona.AgronaChannelAddress.OPTION_READER;
import static org.kaazing.k3po.driver.internal.netty.channel.agrona.AgronaChannelAddress.OPTION_WRITER;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import java.net.URI;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.agrona.MutableDirectBuffer;
import org.agrona.concurrent.MessageHandler;
import org.agrona.concurrent.UnsafeBuffer;
import org.agrona.concurrent.broadcast.BroadcastBufferDescriptor;
import org.agrona.concurrent.broadcast.BroadcastReceiver;
import org.agrona.concurrent.broadcast.BroadcastTransmitter;
import org.agrona.concurrent.broadcast.CopyBroadcastReceiver;
import org.agrona.concurrent.ringbuffer.ManyToOneRingBuffer;
import org.agrona.concurrent.ringbuffer.RingBufferDescriptor;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ChildChannelStateEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.WriteCompletionEvent;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.junit.Rule;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.rules.DisableOnDebug;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;
import org.junit.runner.RunWith;
import org.kaazing.k3po.driver.internal.netty.bootstrap.ServerBootstrapRule;
import org.kaazing.k3po.driver.internal.netty.channel.ChannelAddress;
import org.kaazing.k3po.driver.internal.netty.channel.ChannelAddressFactory;
import org.kaazing.k3po.driver.internal.netty.channel.FlushEvent;
import org.kaazing.k3po.driver.internal.netty.channel.SimpleChannelHandler;
import org.kaazing.k3po.driver.internal.netty.channel.agrona.BroadcastTransmitterChannelWriter;
import org.kaazing.k3po.driver.internal.netty.channel.agrona.ChannelReader;
import org.kaazing.k3po.driver.internal.netty.channel.agrona.ChannelWriter;
import org.kaazing.k3po.driver.internal.netty.channel.agrona.CopyBroadcastReceiverChannelReader;
import org.kaazing.k3po.driver.internal.netty.channel.agrona.RingBufferChannelReader;
import org.kaazing.k3po.driver.internal.netty.channel.agrona.RingBufferChannelWriter;
import org.mockito.InOrder;

@RunWith(Theories.class)
public class AgronaServerBootstrapTest {

    private static final int BUFFER_CAPACITY = 4096;

    private static final int BROADCAST_BUFFER_TOTAL_LENGTH = BUFFER_CAPACITY + BroadcastBufferDescriptor.TRAILER_LENGTH;

    private static final int RING_BUFFER_TOTAL_LENGTH = BUFFER_CAPACITY + RingBufferDescriptor.TRAILER_LENGTH;

    private enum ReaderStrategy {
        MANY_TO_ONE_RING_BUFFER, BROADCAST_RECEIVER
    }

    private enum WriterStrategy {
        MANY_TO_ONE_RING_BUFFER, BROADCAST_TRANSMITTER
    }

    @DataPoints
    public static final Set<ReaderStrategy> READER_STRATEGIES = EnumSet.allOf(ReaderStrategy.class);

    @DataPoints
    public static final Set<WriterStrategy> WRITER_STRATEGIES = EnumSet.allOf(WriterStrategy.class);

    @Rule
    public final ServerBootstrapRule server = new ServerBootstrapRule("agrona");

    @Rule
    public final TestRule timeout = new DisableOnDebug(new Timeout(5, SECONDS));

    @Theory
    public void shouldAcceptEchoThenClose(ReaderStrategy pingStrategy, WriterStrategy pongStrategy) throws Exception {

        ChannelHandler echoHandler = new SimpleChannelHandler() {
            @Override
            public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
                Channel channel = ctx.getChannel();
                ChannelBuffer message = (ChannelBuffer) e.getMessage();
                write(ctx, future(channel), message);
                flush(ctx, future(channel));
                close(ctx, future(channel));
            }
        };

        final ChannelGroup childChannels = new DefaultChannelGroup();
        SimpleChannelHandler parent = new SimpleChannelHandler() {

            @Override
            public void childChannelOpen(ChannelHandlerContext ctx, ChildChannelStateEvent e) throws Exception {
                childChannels.add(e.getChildChannel());
                super.childChannelOpen(ctx, e);
            }

        };
        SimpleChannelHandler parentSpy = spy(parent);

        SimpleChannelHandler child = new SimpleChannelHandler();
        SimpleChannelHandler childSpy = spy(child);

        server.setParentHandler(parentSpy);
        server.setPipeline(pipeline(childSpy, echoHandler));

        ChannelReader pingReader;
        ChannelWriter pingWriter;
        switch (pingStrategy) {
        case MANY_TO_ONE_RING_BUFFER:
            UnsafeBuffer manyToOnePingBuffer = new UnsafeBuffer(new byte[RING_BUFFER_TOTAL_LENGTH]);
            pingReader = new RingBufferChannelReader(new ManyToOneRingBuffer(manyToOnePingBuffer));
            pingWriter = new RingBufferChannelWriter(new ManyToOneRingBuffer(manyToOnePingBuffer));
            break;
        case BROADCAST_RECEIVER:
            UnsafeBuffer broadcastPingBuffer = new UnsafeBuffer(new byte[BROADCAST_BUFFER_TOTAL_LENGTH]);
            BroadcastReceiver pingReceiver = new BroadcastReceiver(broadcastPingBuffer);
            pingReader = new CopyBroadcastReceiverChannelReader(new CopyBroadcastReceiver(pingReceiver));
            pingWriter = new BroadcastTransmitterChannelWriter(new BroadcastTransmitter(broadcastPingBuffer));
            break;
        default:
            throw new IllegalArgumentException(format("Unexpected reader strategy %s", pingStrategy));
        }

        ChannelWriter pongWriter;
        ChannelReader pongReader;
        switch (pongStrategy) {
        case MANY_TO_ONE_RING_BUFFER:
            UnsafeBuffer manyToOnePongBuffer = new UnsafeBuffer(new byte[RING_BUFFER_TOTAL_LENGTH]);
            pongWriter = new RingBufferChannelWriter(new ManyToOneRingBuffer(manyToOnePongBuffer));
            pongReader = new RingBufferChannelReader(new ManyToOneRingBuffer(manyToOnePongBuffer));
            break;
        case BROADCAST_TRANSMITTER:
            UnsafeBuffer broadcastPongBuffer = new UnsafeBuffer(new byte[BROADCAST_BUFFER_TOTAL_LENGTH]);
            pongWriter = new BroadcastTransmitterChannelWriter(new BroadcastTransmitter(broadcastPongBuffer));
            BroadcastReceiver receiver = new BroadcastReceiver(broadcastPongBuffer);
            pongReader = new CopyBroadcastReceiverChannelReader(new CopyBroadcastReceiver(receiver));
            break;
        default:
            throw new IllegalArgumentException(format("Unexpected writer strategy %s", pongStrategy));
        }

        ChannelAddressFactory channelAddressFactory = newChannelAddressFactory();
        URI location = URI.create("agrona://stream/bidirectional");
        Map<String, Object> options = new HashMap<>();
        options.put(OPTION_READER, pingReader);
        options.put(OPTION_WRITER, pongWriter);
        ChannelAddress channelAddress = channelAddressFactory.newChannelAddress(location, options);
        Channel binding = server.bind(channelAddress).syncUninterruptibly().getChannel();

        final AtomicReference<Message> messageRef = new AtomicReference<>();

        final MessageHandler messageHandler = new MessageHandler() {
            @Override
            public void onMessage(int msgTypeId, MutableDirectBuffer buffer, int index, int length) {
                Message message = new Message();
                message.typeId = msgTypeId;
                message.payload = buffer.getStringWithoutLengthUtf8(index, length);
                messageRef.set(message);
            }
        };

        UnsafeBuffer srcBuffer = new UnsafeBuffer("Hello, world".getBytes(UTF_8));
        pingWriter.write(0x01, srcBuffer, 0, srcBuffer.capacity());

        while (pongReader.read(messageHandler) == 0) {
            sleep(1);
        }

        // wait for child channels to close
        for (Channel childChannel : childChannels) {
            ChannelFuture childCloseFuture = childChannel.getCloseFuture();
            childCloseFuture.syncUninterruptibly();
        }

        // wait for server channel to close
        binding.close().syncUninterruptibly();

        server.shutdown();

        Message message = messageRef.get();
        assertNotNull(message);
        assertEquals(0x01, message.typeId);
        assertEquals("Hello, world", message.payload);

        verify(parentSpy, times(6)).handleUpstream(any(ChannelHandlerContext.class), any(ChannelEvent.class));
        verify(parentSpy, times(2)).handleDownstream(any(ChannelHandlerContext.class), any(ChannelEvent.class));

        InOrder parentBind = inOrder(parentSpy);
        parentBind.verify(parentSpy).channelOpen(any(ChannelHandlerContext.class), any(ChannelStateEvent.class));
        parentBind.verify(parentSpy).bindRequested(any(ChannelHandlerContext.class), any(ChannelStateEvent.class));
        parentBind.verify(parentSpy).channelBound(any(ChannelHandlerContext.class), any(ChannelStateEvent.class));

        InOrder parentChild = inOrder(parentSpy);
        parentChild.verify(parentSpy).childChannelOpen(any(ChannelHandlerContext.class), any(ChildChannelStateEvent.class));
        parentChild.verify(parentSpy).childChannelClosed(any(ChannelHandlerContext.class), any(ChildChannelStateEvent.class));

        InOrder parentClose = inOrder(parentSpy);
        parentClose.verify(parentSpy).closeRequested(any(ChannelHandlerContext.class), any(ChannelStateEvent.class));
        parentClose.verify(parentSpy).channelUnbound(any(ChannelHandlerContext.class), any(ChannelStateEvent.class));
        parentClose.verify(parentSpy).channelClosed(any(ChannelHandlerContext.class), any(ChannelStateEvent.class));

        verify(childSpy, times(9)).handleUpstream(any(ChannelHandlerContext.class), any(ChannelStateEvent.class));
        verify(childSpy, times(3)).handleDownstream(any(ChannelHandlerContext.class), any(ChannelStateEvent.class));

        InOrder childConnect = inOrder(childSpy);
        childConnect.verify(childSpy).channelOpen(any(ChannelHandlerContext.class), any(ChannelStateEvent.class));
        childConnect.verify(childSpy).channelBound(any(ChannelHandlerContext.class), any(ChannelStateEvent.class));
        childConnect.verify(childSpy).channelConnected(any(ChannelHandlerContext.class), any(ChannelStateEvent.class));

        InOrder childRead = inOrder(childSpy);
        childRead.verify(childSpy).messageReceived(any(ChannelHandlerContext.class), any(MessageEvent.class));

        InOrder childWrite = inOrder(childSpy);
        childWrite.verify(childSpy).writeRequested(any(ChannelHandlerContext.class), any(MessageEvent.class));
        childWrite.verify(childSpy).flushRequested(any(ChannelHandlerContext.class), any(FlushEvent.class));
        childWrite.verify(childSpy).writeComplete(any(ChannelHandlerContext.class), any(WriteCompletionEvent.class));
        childWrite.verify(childSpy).flushed(any(ChannelHandlerContext.class), any(FlushEvent.class));

        InOrder childClose = inOrder(childSpy);
        childClose.verify(childSpy).closeRequested(any(ChannelHandlerContext.class), any(ChannelStateEvent.class));
        childClose.verify(childSpy).channelDisconnected(any(ChannelHandlerContext.class), any(ChannelStateEvent.class));
        childClose.verify(childSpy).channelUnbound(any(ChannelHandlerContext.class), any(ChannelStateEvent.class));
        childClose.verify(childSpy).channelClosed(any(ChannelHandlerContext.class), any(ChannelStateEvent.class));

        verifyNoMoreInteractions(parentSpy, childSpy);
    }

    private static final class Message {
        public int typeId;
        public String payload;
    }
}
