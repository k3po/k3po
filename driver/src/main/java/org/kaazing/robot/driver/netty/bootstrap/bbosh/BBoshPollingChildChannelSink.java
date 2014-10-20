/*
 * Copyright (c) 2014 "Kaazing Corporation," (www.kaazing.com)
 *
 * This file is part of Robot.
 *
 * Robot is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.kaazing.robot.driver.netty.bootstrap.bbosh;

import static java.lang.String.format;
import static org.jboss.netty.channel.Channels.future;
import static org.kaazing.robot.driver.channel.Channels.chainWriteCompletes;

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
import org.kaazing.robot.driver.netty.bootstrap.bbosh.BBoshHttpHeaders.Names;
import org.kaazing.robot.driver.netty.bootstrap.bbosh.BBoshHttpHeaders.Values;
import org.kaazing.robot.driver.netty.bootstrap.channel.AbstractChannelSink;
import org.kaazing.robot.driver.netty.bootstrap.http.HttpChannelConfig;
import org.kaazing.robot.driver.netty.bootstrap.http.HttpChildChannel;

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
        this.httpChannelRef = new AtomicReference<HttpChildChannel>();
        this.messages = new ConcurrentLinkedDeque<MessageEvent>();
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
            exception.fillInStackTrace();
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
