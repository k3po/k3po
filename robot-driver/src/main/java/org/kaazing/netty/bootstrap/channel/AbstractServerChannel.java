/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.netty.bootstrap.channel;

import static org.jboss.netty.channel.Channels.fireChannelOpen;

import java.util.concurrent.atomic.AtomicBoolean;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelConfig;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelSink;

import org.kaazing.netty.channel.ChannelAddress;

public class AbstractServerChannel<T extends ChannelConfig> extends org.jboss.netty.channel.AbstractServerChannel {

    private final T config;
    private final AtomicBoolean bound;

    private volatile ChannelAddress localAddress;
    private volatile Channel transport;

    public AbstractServerChannel(ChannelFactory factory, ChannelPipeline pipeline, ChannelSink sink, T config) {
        super(factory, pipeline, sink);

        this.config = config;
        this.bound = new AtomicBoolean();

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
