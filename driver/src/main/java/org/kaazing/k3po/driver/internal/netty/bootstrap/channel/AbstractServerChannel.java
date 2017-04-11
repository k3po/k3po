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
package org.kaazing.k3po.driver.internal.netty.bootstrap.channel;

import static org.jboss.netty.channel.Channels.fireChannelOpen;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelConfig;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelSink;
import org.kaazing.k3po.driver.internal.netty.channel.ChannelAddress;

public class AbstractServerChannel<T extends ChannelConfig> extends org.jboss.netty.channel.AbstractServerChannel {

    private final T config;
    private final AtomicBoolean bound;
    private final AtomicInteger bindCount;

    private volatile ChannelAddress localAddress;
    private volatile Channel transport;

    protected AbstractServerChannel(ChannelFactory factory, ChannelPipeline pipeline, ChannelSink sink, T config) {
        this(factory, pipeline, sink, config, true);
    }

    protected AbstractServerChannel(ChannelFactory factory, ChannelPipeline pipeline, ChannelSink sink, T config,
            boolean fireChannelOpen) {
        super(factory, pipeline, sink);

        this.config = config;
        this.bound = new AtomicBoolean();
        this.bindCount = new AtomicInteger();

        if (fireChannelOpen) {
            // required by ServerBootstrap
            fireChannelOpen(this);
        }
    }

    @Override
    public T getConfig() {
        return config;
    }

    @Override
    public ChannelAddress getLocalAddress() {
        return bound.get() ? localAddress : null;
    }

    @Override
    public ChannelAddress getRemoteAddress() {
        return null;
    }

    @Override
    public boolean isBound() {
        return isOpen() && bound.get();
    }

    public AtomicInteger getBindCount() {
        return bindCount;
    }

    protected void setLocalAddress(ChannelAddress localAddress) {
        this.localAddress = localAddress;
    }

    protected void setBound() {
        bound.set(true);
    }

    protected void setTransport(Channel transport) {
        this.transport = transport;
    }

    protected Channel getTransport() {
        return transport;
    }
}
