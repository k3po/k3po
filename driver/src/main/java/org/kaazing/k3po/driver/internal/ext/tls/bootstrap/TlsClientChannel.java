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
package org.kaazing.k3po.driver.internal.ext.tls.bootstrap;

import static org.jboss.netty.channel.Channels.fireChannelOpen;

import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelSink;
import org.kaazing.k3po.driver.internal.netty.bootstrap.channel.AbstractChannel;
import org.kaazing.k3po.driver.internal.netty.channel.ChannelAddress;

public class TlsClientChannel extends AbstractChannel<TlsChannelConfig> {

    TlsClientChannel(ChannelFactory factory, ChannelPipeline pipeline, ChannelSink sink) {
        super(null, factory, pipeline, sink, new DefaultTlsChannelConfig());

        fireChannelOpen(this);
    }

    @Override
    protected void setLocalAddress(ChannelAddress localAddress) {
        super.setLocalAddress(localAddress);
    }

    @Override
    protected void setRemoteAddress(ChannelAddress remoteAddress) {
        super.setRemoteAddress(remoteAddress);
    }

    @Override
    protected void setBound() {
        super.setBound();
    }

    @Override
    protected void setConnected() {
        super.setConnected();
    }

    protected boolean setReadClosed() {
        return super.setReadClosed();
    }

    protected boolean setWriteClosed() {
        return super.setWriteClosed();
    }

    protected void setInterestOpsNow(int interestOps) {
        super.setInternalInterestOps(interestOps);
    }

    protected boolean setReadAborted() {
        return super.setReadAborted();
    }

    protected boolean setWriteAborted() {
        return super.setWriteAborted();
    }

    public boolean isReadClosed() {
        return super.isReadClosed();
    }
}
