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

import org.jboss.netty.channel.ChannelConfig;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelSink;
import org.jboss.netty.channel.ServerChannel;
import org.kaazing.k3po.driver.internal.netty.channel.ChannelAddress;

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
