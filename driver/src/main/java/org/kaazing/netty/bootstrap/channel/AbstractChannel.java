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

package org.kaazing.netty.bootstrap.channel;

import org.jboss.netty.channel.ChannelConfig;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelSink;
import org.jboss.netty.channel.ServerChannel;

import org.kaazing.netty.channel.ChannelAddress;

public abstract class AbstractChannel<T extends ChannelConfig> extends org.jboss.netty.channel.AbstractChannel {

    private static final int ST_OPEN = 0;
    private static final int ST_BOUND = 1;
    private static final int ST_CONNECTED = 2;
    private static final int ST_CLOSED = -1;

    private final T config;

    private volatile int state;
    private volatile ChannelAddress localAddress;
    private volatile ChannelAddress remoteAddress;

    public AbstractChannel(ServerChannel parent, ChannelFactory factory, ChannelPipeline pipeline, ChannelSink sink,
            T config) {
        super(parent, factory, pipeline, sink);

        this.config = config;
        this.state = ST_OPEN;
    }

    @Override
    public T getConfig() {
        return config;
    }

    @Override
    public ChannelAddress getLocalAddress() {
        return (state >= ST_BOUND) ? localAddress : null;
    }

    @Override
    public ChannelAddress getRemoteAddress() {
        return (state == ST_CONNECTED) ? remoteAddress : null;
    }

    @Override
    public boolean isOpen() {
        return state >= ST_OPEN;
    }

    @Override
    public boolean isBound() {
        return state >= ST_BOUND;
    }

    @Override
    public boolean isConnected() {
        return state == ST_CONNECTED;
    }

    protected void setBound() {
        assert state == ST_OPEN : String.format("Invalid state: %s", state);
        state = ST_BOUND;
    }

    protected void setConnected() {
        if (state != ST_CLOSED) {
            state = ST_CONNECTED;
        }
    }

    protected boolean setClosed() {
        state = ST_CLOSED;
        return super.setClosed();
    }

    protected void setLocalAddress(ChannelAddress localAddress) {
        this.localAddress = localAddress;
    }

    protected void setRemoteAddress(ChannelAddress remoteAddress) {
        this.remoteAddress = remoteAddress;
    }
}
