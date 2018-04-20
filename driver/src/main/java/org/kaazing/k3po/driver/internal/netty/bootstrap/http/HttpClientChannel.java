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
package org.kaazing.k3po.driver.internal.netty.bootstrap.http;

import static org.jboss.netty.channel.Channels.fireChannelOpen;

import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelSink;
import org.kaazing.k3po.driver.internal.netty.bootstrap.channel.AbstractChannel;
import org.kaazing.k3po.driver.internal.netty.channel.ChannelAddress;

public class HttpClientChannel extends AbstractChannel<HttpChannelConfig> {

    public enum HttpReadState { REQUEST, CONTENT_CHUNKED, CONTENT_COMPLETE, UPGRADED }
    public enum HttpWriteState { REQUEST, CONTENT_CHUNKED, CONTENT_BUFFERED, CONTENT_STREAMED, CONTENT_COMPLETE, UPGRADEABLE }

    private HttpWriteState writeState;
    private HttpReadState readState;

    HttpClientChannel(ChannelFactory factory, ChannelPipeline pipeline, ChannelSink sink) {
        super(null, factory, pipeline, sink, new DefaultHttpChannelConfig());
        this.writeState = HttpWriteState.REQUEST;

        fireChannelOpen(this);
    }

    public HttpWriteState writeState() {
        return writeState;
    }

    public void writeState(HttpWriteState state) {
        this.writeState = state;
    }

    public HttpReadState readState() {
        return readState;
    }

    public void readState(HttpReadState state) {
        this.readState = state;
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

    @Override
    protected boolean setClosed() {
        return super.setClosed();
    }

    protected void setInterestOpsNow(int interestOps) {
        super.setInternalInterestOps(interestOps);
    }

}
