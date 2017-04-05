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
package org.kaazing.k3po.driver.internal.behavior.handler.codec;

import static org.jboss.netty.buffer.ChannelBuffers.wrappedBuffer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.jboss.netty.buffer.ChannelBuffer;

public class WriteLongEncoder implements MessageEncoder {

    private final long value;
    private final ByteOrder endian;

    public WriteLongEncoder(long value, ByteOrder endian) {
        this.value = value;
        this.endian = endian;
    }

    @Override
    public ChannelBuffer encode() {
        byte[] array = ByteBuffer.allocate(Long.BYTES)
                                 .order(endian)
                                 .putLong(value)
                                 .array();
        return wrappedBuffer(array);
    }

    @Override
    public String toString() {
        return Long.toString(value);
    }

}
