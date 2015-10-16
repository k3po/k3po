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

import static org.jboss.netty.channel.Channels.fireMessageReceived;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelSink;
import org.kaazing.k3po.driver.internal.netty.bootstrap.channel.AbstractChannel;
import org.kaazing.k3po.driver.internal.netty.channel.ChannelAddress;
import org.kaazing.k3po.driver.internal.netty.channel.agrona.AgronaChannelAddress;

import uk.co.real_logic.agrona.MutableDirectBuffer;
import uk.co.real_logic.agrona.concurrent.MessageHandler;

public abstract class AgronaChannel extends AbstractChannel<AgronaChannelConfig> {

    final AgronaWorker worker;

    final MessageHandler messageHandler = new MessageHandler() {

        @Override
        public void onMessage(int msgTypeId, MutableDirectBuffer buffer, int index, int length) {
            // TODO: msgTypeId ignored (!)
            ChannelBuffer message = ChannelBuffers.buffer(length);
            buffer.getBytes(index, message.array(), message.arrayOffset(), length);
            message.writerIndex(length);
            fireMessageReceived(AgronaChannel.this, message);
        }

    };

    AgronaChannel(AgronaServerChannel parent, ChannelFactory factory,
            ChannelPipeline pipeline, ChannelSink sink, AgronaWorker worker) {
        super(parent, factory, pipeline, sink, new DefaultAgronaChannelConfig());

        this.worker = worker;
    }

    @Override
    public AgronaChannelAddress getLocalAddress() {
        return (AgronaChannelAddress) super.getLocalAddress();
    }

    @Override
    public AgronaChannelAddress getRemoteAddress() {
        return (AgronaChannelAddress) super.getRemoteAddress();
    }

    @Override
    protected void setBound() {
        super.setBound();
    }

    @Override
    protected void setConnected() {
        super.setConnected();
    }

    @Override
    protected boolean setClosed() {
        return super.setClosed();
    }

    @Override
    protected void setRemoteAddress(ChannelAddress remoteAddress) {
        super.setRemoteAddress(remoteAddress);
    }

    @Override
    protected void setLocalAddress(ChannelAddress localAddress) {
        super.setLocalAddress(localAddress);
    }

    @Override
    public String toString() {
        ChannelAddress localAddress = this.getLocalAddress();
        return localAddress != null ? localAddress.toString() : super.toString();
    }
}