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

package org.kaazing.k3po.driver.netty.bootstrap.channel;

import static org.jboss.netty.channel.Channels.fireChannelOpen;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelConfig;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelSink;
import org.kaazing.k3po.driver.netty.channel.ChannelAddress;

public class AbstractServerChannel<T extends ChannelConfig> extends org.jboss.netty.channel.AbstractServerChannel {

    private final T config;
    private final AtomicBoolean bound;
    private final AtomicInteger bindCount;

    private volatile ChannelAddress localAddress;
    private volatile Channel transport;

    public AbstractServerChannel(ChannelFactory factory, ChannelPipeline pipeline, ChannelSink sink, T config) {
        super(factory, pipeline, sink);

        this.config = config;
        this.bound = new AtomicBoolean();
        this.bindCount = new AtomicInteger();

        // required by ServerBootstrap
        fireChannelOpen(this);
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
