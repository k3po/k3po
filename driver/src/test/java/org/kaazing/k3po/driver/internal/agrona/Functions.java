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
package org.kaazing.k3po.driver.internal.agrona;

import static org.agrona.IoUtil.mapExistingFile;
import static org.agrona.IoUtil.mapNewFile;
import static org.agrona.IoUtil.unmap;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.util.Random;

import org.kaazing.k3po.lang.el.Function;
import org.kaazing.k3po.lang.el.spi.FunctionMapperSpi;

import org.agrona.concurrent.AtomicBuffer;
import org.agrona.concurrent.UnsafeBuffer;
import org.agrona.concurrent.broadcast.BroadcastBufferDescriptor;
import org.agrona.concurrent.ringbuffer.RingBufferDescriptor;

public final class Functions {

    private static final Random RANDOM = new Random();

    @Function
    public static byte[] randomBytes(int length) {
        byte[] bytes = new byte[length];
        for (int i = 0; i < length; i++) {
            bytes[i] = (byte) RANDOM.nextInt(0x100);
        }
        return bytes;
    }

    @Function
    public static Integer randomInt() {
        return RANDOM.nextInt();
    }

    @Function
    public static Long randomLong() {
        return RANDOM.nextLong();
    }

    @Function
    public static byte[] intToNativeBytes(int value) {
        return ByteBuffer.allocate(Integer.BYTES).order(ByteOrder.nativeOrder()).putInt(value).array();
    }

    @Function
    public static byte[] longToNativeBytes(long value) {
        return ByteBuffer.allocate(Long.BYTES).order(ByteOrder.nativeOrder()).putLong(value).array();
    }

    @Function
    public static Layout layoutInit(String filename, int ringCapacity, int broadcastCapacity) {
        return layout(filename, ringCapacity, broadcastCapacity, true);
    }

    @Function
    public static Layout layout(String filename, int ringCapacity, int broadcastCapacity) {
        return layout(filename, ringCapacity, broadcastCapacity, false);
    }

    private static Layout layout(String filename, int ringCapacity, int broadcastCapacity, boolean create) {
        File location = new File(filename);
        int totalRingLength = ringCapacity + RingBufferDescriptor.TRAILER_LENGTH;
        int totalBroadcastLength = broadcastCapacity + BroadcastBufferDescriptor.TRAILER_LENGTH;
        create &= !location.exists();
        MappedByteBuffer buffer = create ? mapNewFile(location, totalRingLength + totalBroadcastLength)
                                         : mapExistingFile(location, filename);
        AtomicBuffer ring = new UnsafeBuffer(buffer, 0, totalRingLength);
        AtomicBuffer broadcast = new UnsafeBuffer(buffer, totalRingLength, totalBroadcastLength);
        return new Layout(buffer, ring, broadcast);
    }

    public static final class Layout implements AutoCloseable {

        private final AtomicBuffer ring;
        private final AtomicBuffer broadcast;
        private final MappedByteBuffer buffer;

        public Layout(MappedByteBuffer buffer, AtomicBuffer ring, AtomicBuffer broadcast) {
            this.buffer = buffer;
            this.ring = ring;
            this.broadcast = broadcast;
        }

        public AtomicBuffer getRing() {
            return ring;
        }

        public AtomicBuffer getBroadcast() {
            return broadcast;
        }

        @Override
        public void close() {
            unmap(buffer);
        }

    }

    public static class Mapper extends FunctionMapperSpi.Reflective {

        public Mapper() {
            super(Functions.class);
        }

        @Override
        public String getPrefixName() {
            return "agronaIT";
        }

    }

    private Functions() {
        // utility
    }

}

