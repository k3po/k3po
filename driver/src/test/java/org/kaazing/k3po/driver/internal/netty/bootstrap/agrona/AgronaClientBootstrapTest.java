/*
 * Copyright 2014, Kaazing Corporation. All rights reserved.
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
import static org.jboss.netty.buffer.ChannelBuffers.copiedBuffer;
import static org.jboss.netty.channel.Channels.pipeline;
import static org.junit.Assert.assertEquals;
import static org.kaazing.k3po.driver.internal.netty.channel.ChannelAddressFactory.newChannelAddressFactory;
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
import org.kaazing.k3po.driver.internal.netty.channel.SimpleChannelHandler;
import org.kaazing.k3po.driver.internal.netty.channel.agrona.BroadcastTransmitterChannelWriter;
import org.kaazing.k3po.driver.internal.netty.channel.agrona.ChannelReader;
import org.kaazing.k3po.driver.internal.netty.channel.agrona.ChannelWriter;
import org.kaazing.k3po.driver.internal.netty.channel.agrona.CopyBroadcastReceiverChannelReader;
import org.kaazing.k3po.driver.internal.netty.channel.agrona.RingBufferChannelReader;
import org.kaazing.k3po.driver.internal.netty.channel.agrona.RingBufferChannelWriter;
import org.mockito.InOrder;

import uk.co.real_logic.agrona.MutableDirectBuffer;
import uk.co.real_logic.agrona.concurrent.MessageHandler;
import uk.co.real_logic.agrona.concurrent.UnsafeBuffer;
import uk.co.real_logic.agrona.concurrent.broadcast.BroadcastBufferDescriptor;
import uk.co.real_logic.agrona.concurrent.broadcast.BroadcastReceiver;
import uk.co.real_logic.agrona.concurrent.broadcast.BroadcastTransmitter;
import uk.co.real_logic.agrona.concurrent.broadcast.CopyBroadcastReceiver;
import uk.co.real_logic.agrona.concurrent.ringbuffer.ManyToOneRingBuffer;
import uk.co.real_logic.agrona.concurrent.ringbuffer.RingBufferDescriptor;

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

        SimpleChannelHandler client = new SimpleChannelHandler();
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

        Channel channel = bootstrap.connect(channelAddress).syncUninterruptibly().getChannel();
        channel.write(copiedBuffer("Hello, world", UTF_8)).syncUninterruptibly();

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

        while (pingReader.read(messageHandler) == 0) {
            sleep(200);
        }

        UnsafeBuffer srcBuffer = new UnsafeBuffer("Hello, world".getBytes(UTF_8));
        pongWriter.write(0x01, srcBuffer, 0, srcBuffer.capacity());

        // no INPUT_SHUTDOWN signal, so coordinate on echoed message instead
        Message message;
        do {
            Thread.sleep(1L);
        } while ((message = messageRef.get()) == null);

        channel.close().syncUninterruptibly();

        bootstrap.shutdown();

        assertEquals(0x01, message.typeId);
        assertEquals("Hello, world", message.payload);

        verify(clientSpy, times(8)).handleUpstream(any(ChannelHandlerContext.class), any(ChannelEvent.class));
        verify(clientSpy, times(3)).handleDownstream(any(ChannelHandlerContext.class), any(ChannelEvent.class));

        InOrder childConnect = inOrder(clientSpy);
        childConnect.verify(clientSpy).channelOpen(any(ChannelHandlerContext.class), any(ChannelStateEvent.class));
        childConnect.verify(clientSpy).connectRequested(any(ChannelHandlerContext.class), any(ChannelStateEvent.class));
        childConnect.verify(clientSpy).channelBound(any(ChannelHandlerContext.class), any(ChannelStateEvent.class));
        childConnect.verify(clientSpy).channelConnected(any(ChannelHandlerContext.class), any(ChannelStateEvent.class));

        InOrder childWrite = inOrder(clientSpy);
        childWrite.verify(clientSpy).writeRequested(any(ChannelHandlerContext.class), any(MessageEvent.class));
        childWrite.verify(clientSpy).closeRequested(any(ChannelHandlerContext.class), any(ChannelStateEvent.class));
        // asynchronous
        verify(clientSpy).writeComplete(any(ChannelHandlerContext.class), any(WriteCompletionEvent.class));

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
