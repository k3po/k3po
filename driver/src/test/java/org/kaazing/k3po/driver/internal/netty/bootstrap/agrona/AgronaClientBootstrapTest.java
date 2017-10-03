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
import static java.nio.ByteOrder.nativeOrder;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.jboss.netty.buffer.ChannelBuffers.buffer;
import static org.jboss.netty.channel.Channels.pipeline;
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
import java.util.concurrent.atomic.AtomicInteger;
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
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.WriteCompletionEvent;
import org.junit.Rule;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.rules.DisableOnDebug;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;
import org.junit.runner.RunWith;
import org.kaazing.k3po.driver.internal.netty.bootstrap.ClientBootstrapRule;
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
public class AgronaClientBootstrapTest {

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
    public final ClientBootstrapRule bootstrap = new ClientBootstrapRule("agrona");

    @Rule
    public final TestRule timeout = new DisableOnDebug(new Timeout(5, SECONDS));

    @Theory
    public void shouldConnectEchoThenClose(WriterStrategy pingStrategy, ReaderStrategy pongStrategy) throws Exception {

        final AtomicInteger pongsReceived = new AtomicInteger();
        SimpleChannelHandler client = new SimpleChannelHandler() {
            @Override
            public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
                pongsReceived.incrementAndGet();
                super.messageReceived(ctx, e);
            }
        };
        SimpleChannelHandler clientSpy = spy(client);

        bootstrap.setPipeline(pipeline(clientSpy));

        ChannelReader pingReader;
        ChannelWriter pingWriter;
        switch (pingStrategy) {
        case MANY_TO_ONE_RING_BUFFER:
            UnsafeBuffer manyToOnePingBuffer = new UnsafeBuffer(new byte[RING_BUFFER_TOTAL_LENGTH]);
            pingReader = new RingBufferChannelReader(new ManyToOneRingBuffer(manyToOnePingBuffer));
            pingWriter = new RingBufferChannelWriter(new ManyToOneRingBuffer(manyToOnePingBuffer));
            break;
        case BROADCAST_TRANSMITTER:
            UnsafeBuffer broadcastPingBuffer = new UnsafeBuffer(new byte[BROADCAST_BUFFER_TOTAL_LENGTH]);
            BroadcastReceiver receiver = new BroadcastReceiver(broadcastPingBuffer);
            pingReader = new CopyBroadcastReceiverChannelReader(new CopyBroadcastReceiver(receiver));
            pingWriter = new BroadcastTransmitterChannelWriter(new BroadcastTransmitter(broadcastPingBuffer));
            break;
        default:
            throw new IllegalArgumentException(format("Unexpected writer strategy %s", pingStrategy));
        }

        ChannelReader pongReader;
        ChannelWriter pongWriter;
        switch (pongStrategy) {
        case MANY_TO_ONE_RING_BUFFER:
            UnsafeBuffer manyToOnePongBuffer = new UnsafeBuffer(new byte[RING_BUFFER_TOTAL_LENGTH]);
            pongReader = new RingBufferChannelReader(new ManyToOneRingBuffer(manyToOnePongBuffer));
            pongWriter = new RingBufferChannelWriter(new ManyToOneRingBuffer(manyToOnePongBuffer));
            break;
        case BROADCAST_RECEIVER:
            UnsafeBuffer broadcastPongBuffer = new UnsafeBuffer(new byte[BROADCAST_BUFFER_TOTAL_LENGTH]);
            BroadcastReceiver pongReceiver = new BroadcastReceiver(broadcastPongBuffer);
            pongReader = new CopyBroadcastReceiverChannelReader(new CopyBroadcastReceiver(pongReceiver));
            BroadcastTransmitter transmitter = new BroadcastTransmitter(broadcastPongBuffer);
            pongWriter = new BroadcastTransmitterChannelWriter(transmitter);
            break;
        default:
            throw new IllegalArgumentException(format("Unexpected reader strategy %s", pongStrategy));
        }

        ChannelAddressFactory channelAddressFactory = newChannelAddressFactory();
        URI location = URI.create("agrona://stream/bidirectional");
        Map<String, Object> options = new HashMap<>();
        options.put(OPTION_READER, pongReader);
        options.put(OPTION_WRITER, pingWriter);
        ChannelAddress channelAddress = channelAddressFactory.newChannelAddress(location, options);

        ChannelBuffer ping = buffer(nativeOrder(), 256);
        ping.writeInt(0x01);
        ping.writeBytes("Hello, world".getBytes(UTF_8));

        Channel channel = bootstrap.connect(channelAddress).syncUninterruptibly().getChannel();
        channel.write(ping).syncUninterruptibly();
        flush(channel);

        final AtomicReference<Message> pongRef = new AtomicReference<>();

        final MessageHandler messageHandler = new MessageHandler() {
            @Override
            public void onMessage(int msgTypeId, MutableDirectBuffer buffer, int index, int length) {
                Message pong = new Message();
                pong.typeId = msgTypeId;
                pong.payload = buffer.getStringWithoutLengthUtf8(index, length);
                pongRef.set(pong);
            }
        };

        while (pingReader.read(messageHandler) == 0) {
            sleep(1);
        }

        Message pong = pongRef.get();
        assertNotNull(pong);

        UnsafeBuffer srcBuffer = new UnsafeBuffer(pong.payload.getBytes(UTF_8));
        pongWriter.write(pong.typeId, srcBuffer, 0, srcBuffer.capacity());

        while (pongsReceived.get() == 0) {
            sleep(1);
        }
        channel.close().syncUninterruptibly();

        bootstrap.shutdown();

        assertEquals(0x01, pong.typeId);
        assertEquals("Hello, world", pong.payload);

        verify(clientSpy, times(9)).handleUpstream(any(ChannelHandlerContext.class), any(ChannelEvent.class));
        verify(clientSpy, times(4)).handleDownstream(any(ChannelHandlerContext.class), any(ChannelEvent.class));

        InOrder childConnect = inOrder(clientSpy);
        childConnect.verify(clientSpy).channelOpen(any(ChannelHandlerContext.class), any(ChannelStateEvent.class));
        childConnect.verify(clientSpy).connectRequested(any(ChannelHandlerContext.class), any(ChannelStateEvent.class));
        childConnect.verify(clientSpy).channelBound(any(ChannelHandlerContext.class), any(ChannelStateEvent.class));
        childConnect.verify(clientSpy).channelConnected(any(ChannelHandlerContext.class), any(ChannelStateEvent.class));

        InOrder childWrite = inOrder(clientSpy);
        childWrite.verify(clientSpy).writeRequested(any(ChannelHandlerContext.class), any(MessageEvent.class));
        childWrite.verify(clientSpy).flushRequested(any(ChannelHandlerContext.class), any(FlushEvent.class));
        childWrite.verify(clientSpy).closeRequested(any(ChannelHandlerContext.class), any(ChannelStateEvent.class));
        // asynchronous
        verify(clientSpy).writeComplete(any(ChannelHandlerContext.class), any(WriteCompletionEvent.class));
        verify(clientSpy).flushed(any(ChannelHandlerContext.class), any(FlushEvent.class));

        InOrder childRead = inOrder(clientSpy);
        childRead.verify(clientSpy).messageReceived(any(ChannelHandlerContext.class), any(MessageEvent.class));
        childRead.verify(clientSpy).channelClosed(any(ChannelHandlerContext.class), any(ChannelStateEvent.class));

        InOrder childClose = inOrder(clientSpy);
        childClose.verify(clientSpy).channelDisconnected(any(ChannelHandlerContext.class), any(ChannelStateEvent.class));
        childClose.verify(clientSpy).channelUnbound(any(ChannelHandlerContext.class), any(ChannelStateEvent.class));
        childClose.verify(clientSpy).channelClosed(any(ChannelHandlerContext.class), any(ChannelStateEvent.class));

        verifyNoMoreInteractions(clientSpy);
    }

    private static final class Message {
        public int typeId;
        public String payload;
    }
}
