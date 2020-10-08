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
package org.kaazing.k3po.driver.internal.functions.agrona;

import org.agrona.concurrent.AtomicBuffer;
import org.agrona.concurrent.broadcast.BroadcastReceiver;
import org.agrona.concurrent.broadcast.BroadcastTransmitter;
import org.agrona.concurrent.broadcast.CopyBroadcastReceiver;
import org.agrona.concurrent.ringbuffer.ManyToOneRingBuffer;
import org.agrona.concurrent.ringbuffer.OneToOneRingBuffer;
import org.kaazing.k3po.driver.internal.netty.channel.agrona.BroadcastTransmitterChannelWriter;
import org.kaazing.k3po.driver.internal.netty.channel.agrona.ChannelReader;
import org.kaazing.k3po.driver.internal.netty.channel.agrona.ChannelWriter;
import org.kaazing.k3po.driver.internal.netty.channel.agrona.CopyBroadcastReceiverChannelReader;
import org.kaazing.k3po.driver.internal.netty.channel.agrona.RingBufferChannelReader;
import org.kaazing.k3po.driver.internal.netty.channel.agrona.RingBufferChannelWriter;
import org.kaazing.k3po.lang.el.Function;
import org.kaazing.k3po.lang.el.spi.FunctionMapperSpi;

public final class Functions {

    @Function
    public static ChannelReader oneToOneReader(AtomicBuffer buffer) {
        return new RingBufferChannelReader(new OneToOneRingBuffer(buffer));
    }

    @Function
    public static ChannelWriter oneToOneWriter(AtomicBuffer buffer) {
        return new RingBufferChannelWriter(new OneToOneRingBuffer(buffer));
    }

    @Function
    public static ChannelReader manyToOneReader(AtomicBuffer buffer) {
        return new RingBufferChannelReader(new ManyToOneRingBuffer(buffer));
    }

    @Function
    public static ChannelWriter manyToOneWriter(AtomicBuffer buffer) {
        return new RingBufferChannelWriter(new ManyToOneRingBuffer(buffer));
    }

    @Function
    public static ChannelReader broadcastReceiver(AtomicBuffer buffer) {
        return new CopyBroadcastReceiverChannelReader(new FlushingCopyBroadcastReceiver(new BroadcastReceiver(buffer)));
    }

    @Function
    public static ChannelWriter broadcastTransmitter(AtomicBuffer buffer) {
        return new BroadcastTransmitterChannelWriter(new BroadcastTransmitter(buffer));
    }

    public static class Mapper extends FunctionMapperSpi.Reflective {

        public Mapper() {
            super(Functions.class);
        }

        @Override
        public String getPrefixName() {
            return "agrona";
        }

    }

    private static final class FlushingCopyBroadcastReceiver extends CopyBroadcastReceiver
    {
        public FlushingCopyBroadcastReceiver(
            BroadcastReceiver receiver)
        {
            super(receiver);

            while (receiver.receiveNext())
            {
                // flush to latest
            }
        }
    }

    private Functions() {
        // utility
    }

}
