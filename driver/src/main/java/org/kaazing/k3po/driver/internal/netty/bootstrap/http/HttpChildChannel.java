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

import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelSink;
import org.jboss.netty.channel.ServerChannel;
import org.kaazing.k3po.driver.internal.netty.bootstrap.channel.AbstractChannel;
import org.kaazing.k3po.driver.internal.netty.channel.ChannelAddress;

public final class HttpChildChannel extends AbstractChannel<HttpChannelConfig> {

    public enum HttpReadState { REQUEST, CONTENT_CHUNKED, CONTENT_COMPLETE, UPGRADED }
    public enum HttpWriteState { RESPONSE, CONTENT_CHUNKED, CONTENT_CLOSE, CONTENT_CLOSING, CONTENT_BUFFERED, CONTENT_COMPLETE, UPGRADED }

    private HttpReadState readState;
    private HttpWriteState writeState;

    HttpChildChannel(ServerChannel parent, ChannelFactory factory, ChannelPipeline pipeline, ChannelSink sink) {
        super(parent, factory, pipeline, sink, new DefaultHttpChannelConfig());
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
        return super.setReadClosed();
    }

    protected boolean setWriteClosed() {
        return super.setWriteClosed();
    }

    protected boolean setReadAborted() {
        return super.setReadAborted();
    }

    protected boolean setClosed() {
        return super.setClosed();
    }
}
