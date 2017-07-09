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
package org.kaazing.k3po.driver.internal.netty.bootstrap.bbosh;

import static java.lang.String.format;
import static org.jboss.netty.channel.Channels.future;
import static org.kaazing.k3po.driver.internal.channel.Channels.chainWriteCompletes;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelException;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.kaazing.k3po.driver.internal.netty.bootstrap.bbosh.BBoshHttpHeaders.Names;
import org.kaazing.k3po.driver.internal.netty.bootstrap.bbosh.BBoshHttpHeaders.Values;
import org.kaazing.k3po.driver.internal.netty.bootstrap.channel.AbstractChannelSink;
import org.kaazing.k3po.driver.internal.netty.bootstrap.http.HttpChannelConfig;
import org.kaazing.k3po.driver.internal.netty.bootstrap.http.HttpChildChannel;

public class BBoshPollingChildChannelSink extends AbstractChannelSink {

    private final Deque<MessageEvent> messages;

    private AtomicInteger nextSequenceNo;
    private AtomicReference<HttpChildChannel> httpChannelRef;

    private final Runnable flushTask = new Runnable() {
        @Override
        public void run() {
            HttpChildChannel httpChannel = httpChannelRef.get();
            if (!messages.isEmpty()) {
                while (!messages.isEmpty()) {
                    MessageEvent head = messages.removeFirst();
                    ChannelBuffer message = (ChannelBuffer) head.getMessage();
                    int readableBytes = message.readableBytes();
                    ChannelFuture future = head.getFuture();

                    ChannelFuture httpFuture = httpChannel.write(message);
                    chainWriteCompletes(httpFuture, future, readableBytes);
                }
                httpChannelRef.compareAndSet(httpChannel, null);
                httpChannel.close();
            }
        }
    };

    public BBoshPollingChildChannelSink(int nextSequenceNo) {
        this.nextSequenceNo = new AtomicInteger(nextSequenceNo);
        this.httpChannelRef = new AtomicReference<>();
        this.messages = new ConcurrentLinkedDeque<>();
    }

    @Override
    protected void writeRequested(ChannelPipeline pipeline, MessageEvent e) throws Exception {
        messages.addLast(e);

        HttpChildChannel httpChannel = httpChannelRef.get();
        if (httpChannel != null) {
            ChannelPipeline httpPipeline = httpChannel.getPipeline();
            httpPipeline.execute(flushTask);
        }
    }

    public ChannelFuture attach(int sequenceNo, HttpChildChannel httpChannel) {

        ChannelFuture httpFuture = future(httpChannel);
        attach(sequenceNo, httpChannel, httpFuture);
        return httpFuture;
    }

    public void detach(HttpChildChannel httpChannel) {
        if (httpChannel.isOpen()) {
            assert httpChannel == httpChannelRef.get();
            httpChannelRef.set(null);
            httpChannel.close();
        }
    }

    private void attach(final int sequenceNo, final HttpChildChannel httpChannel, final ChannelFuture httpFuture) {

        // TODO: handle out of order sequence arrival more defensively
        if (sequenceNo < nextSequenceNo.get()) {
            String message = format("Replayed sequence number: %d", sequenceNo);
            ChannelException exception = new ChannelException(message);
            httpFuture.setFailure(exception);
        }
        else if (nextSequenceNo.compareAndSet(sequenceNo, sequenceNo + 1)) {
            HttpChannelConfig httpConfig = httpChannel.getConfig();
            HttpHeaders httpHeaders = httpConfig.getWriteHeaders();
            httpHeaders.set(Names.CACHE_CONTROL, Values.NO_CACHE);
            httpHeaders.set(Names.CONTENT_TYPE, Values.APPLICATION_OCTET_STREAM);
            httpConfig.setMaximumBufferedContentLength(8192);

            httpChannelRef.set(httpChannel);

            flushTask.run();
            httpFuture.setSuccess();
        }
        else {
            Runnable reorderTask = new Runnable() {
                @Override
                public void run() {
                    attach(sequenceNo, httpChannel, httpFuture);
                }
            };
            ChannelPipeline httpPipeline = httpChannel.getPipeline();
            httpPipeline.execute(reorderTask);
        }
    }


}
