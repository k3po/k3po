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

package org.kaazing.k3po.driver.netty.bootstrap.http;

import java.util.concurrent.atomic.AtomicInteger;

import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelSink;
import org.jboss.netty.channel.ServerChannel;
import org.kaazing.k3po.driver.netty.bootstrap.channel.AbstractChannel;
import org.kaazing.k3po.driver.netty.channel.ChannelAddress;

public final class HttpChildChannel extends AbstractChannel<HttpChannelConfig> {

    public enum HttpReadState { REQUEST, CONTENT_CHUNKED, CONTENT_COMPLETE, UPGRADED }
    public enum HttpWriteState { RESPONSE, CONTENT_CHUNKED, CONTENT_CLOSE, CONTENT_BUFFERED, CONTENT_COMPLETE, UPGRADED }

    private final AtomicInteger closeState;
    private HttpReadState readState;
    private HttpWriteState writeState;

    HttpChildChannel(ServerChannel parent, ChannelFactory factory, ChannelPipeline pipeline, ChannelSink sink) {
        super(parent, factory, pipeline, sink, new DefaultHttpChannelConfig());
        this.closeState = new AtomicInteger();
        this.readState = HttpReadState.REQUEST;
        this.writeState = HttpWriteState.RESPONSE;
    }

    HttpWriteState writeState() {
        return writeState;
    }

    void writeState(HttpWriteState writeState) {
        this.writeState = writeState;
    }

    HttpReadState readState() {
        return readState;
    }

    void readState(HttpReadState readState) {
        this.readState = readState;
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
    protected void setLocalAddress(ChannelAddress localAddress) {
        super.setLocalAddress(localAddress);
    }

    @Override
    protected void setRemoteAddress(ChannelAddress remoteAddress) {
        super.setRemoteAddress(remoteAddress);
    }

    protected boolean setReadClosed() {
        if ((this.closeState.get() & 0x01) == 0x00) {
            int closeStatus = this.closeState.addAndGet(1);
            if (closeStatus == 0x03) {
                return super.setClosed();
            }
        }
        return false;
    }

    protected boolean setWriteClosed() {
        if ((this.closeState.get() & 0x02) == 0x00) {
            int closeStatus = this.closeState.addAndGet(2);
            if (closeStatus == 0x03) {
                return super.setClosed();
            }
        }
        return false;
    }
}
