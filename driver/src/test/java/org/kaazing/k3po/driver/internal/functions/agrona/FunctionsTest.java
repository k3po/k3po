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

import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.kaazing.k3po.lang.internal.el.ExpressionFactoryUtils.newExpressionFactory;

import javax.el.ELContext;
import javax.el.ELResolver;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;

import org.junit.Before;
import org.junit.Test;
import org.kaazing.k3po.driver.internal.netty.channel.agrona.BroadcastTransmitterChannelWriter;
import org.kaazing.k3po.driver.internal.netty.channel.agrona.ChannelReader;
import org.kaazing.k3po.driver.internal.netty.channel.agrona.ChannelWriter;
import org.kaazing.k3po.driver.internal.netty.channel.agrona.CopyBroadcastReceiverChannelReader;
import org.kaazing.k3po.driver.internal.netty.channel.agrona.RingBufferChannelReader;
import org.kaazing.k3po.driver.internal.netty.channel.agrona.RingBufferChannelWriter;
import org.kaazing.k3po.lang.internal.el.ExpressionContext;

import org.agrona.concurrent.UnsafeBuffer;
import org.agrona.concurrent.broadcast.BroadcastBufferDescriptor;
import org.agrona.concurrent.ringbuffer.RingBufferDescriptor;

public class FunctionsTest {

    private static final int BUFFER_CAPACITY = 1024;
    private static final int BROADCAST_BUFFER_TOTAL_LENGTH = BUFFER_CAPACITY + BroadcastBufferDescriptor.TRAILER_LENGTH;
    private static final int RING_BUFFER_TOTAL_LENGTH = BUFFER_CAPACITY + RingBufferDescriptor.TRAILER_LENGTH;
    private ExpressionFactory factory;
    private ELContext environment;

    @Before
    public void setUp() throws Exception {

        factory = newExpressionFactory();
        environment = new ExpressionContext();

        // TODO: Remove when JUEL sync bug is fixed https://github.com/k3po/k3po/issues/147
        synchronized (environment) {
            ELResolver resolver = environment.getELResolver();

            UnsafeBuffer bufferWithRingTrailer = new UnsafeBuffer(new byte[RING_BUFFER_TOTAL_LENGTH]);
            resolver.setValue(environment, null, "bufferWithRingTrailer", bufferWithRingTrailer);

            UnsafeBuffer bufferWithBroadcastTrailer = new UnsafeBuffer(new byte[BROADCAST_BUFFER_TOTAL_LENGTH]);
            resolver.setValue(environment, null, "bufferWithBroadcastTrailer", bufferWithBroadcastTrailer);
        }
    }

    @Test
    public void shouldCreateOneToOneChannelReader() throws Exception {

        String expressionText = "${agrona:oneToOneReader(bufferWithRingTrailer)}";
        ValueExpression expression = factory.createValueExpression(environment, expressionText, ChannelReader.class);
        ChannelReader reader = (ChannelReader) expression.getValue(environment);

        assertThat(reader, instanceOf(RingBufferChannelReader.class));
    }

    @Test
    public void shouldCreateOneToOneChannelWriter() throws Exception {

        String expressionText = "${agrona:oneToOneWriter(bufferWithRingTrailer)}";
        ValueExpression expression = factory.createValueExpression(environment, expressionText, ChannelWriter.class);
        ChannelWriter writer = (ChannelWriter) expression.getValue(environment);

        assertThat(writer, instanceOf(RingBufferChannelWriter.class));
    }

    @Test
    public void shouldCreateManyToOneChannelReader() throws Exception {

        String expressionText = "${agrona:manyToOneReader(bufferWithRingTrailer)}";
        ValueExpression expression = factory.createValueExpression(environment, expressionText, ChannelReader.class);
        ChannelReader reader = (ChannelReader) expression.getValue(environment);

        assertThat(reader, instanceOf(RingBufferChannelReader.class));
    }

    @Test
    public void shouldCreateManyToOneChannelWriter() throws Exception {

        String expressionText = "${agrona:manyToOneWriter(bufferWithRingTrailer)}";
        ValueExpression expression = factory.createValueExpression(environment, expressionText, ChannelWriter.class);
        ChannelWriter writer = (ChannelWriter) expression.getValue(environment);

        assertThat(writer, instanceOf(RingBufferChannelWriter.class));
    }

    @Test
    public void shouldCreateBroadcastReceiverChannelReader() throws Exception {

        String expressionText = "${agrona:broadcastReceiver(bufferWithBroadcastTrailer)}";
        ValueExpression expression = factory.createValueExpression(environment, expressionText, ChannelReader.class);
        ChannelReader reader = (ChannelReader) expression.getValue(environment);

        assertThat(reader, instanceOf(CopyBroadcastReceiverChannelReader.class));
    }

    @Test
    public void shouldCreateBroadcastTransmitterChannelWriter() throws Exception {

        String expressionText = "${agrona:broadcastTransmitter(bufferWithBroadcastTrailer)}";
        ValueExpression expression = factory.createValueExpression(environment, expressionText, ChannelWriter.class);
        ChannelWriter writer = (ChannelWriter) expression.getValue(environment);

        assertThat(writer, instanceOf(BroadcastTransmitterChannelWriter.class));
    }

}
