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

package org.kaazing.k3po.driver.internal.netty.bootstrap.file;

import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelSink;
import org.jboss.netty.channel.ServerChannel;
import org.kaazing.k3po.driver.internal.netty.bootstrap.channel.AbstractChannel;
import org.kaazing.k3po.driver.internal.netty.bootstrap.channel.AbstractServerChannel;
import org.kaazing.k3po.driver.internal.netty.bootstrap.http.DefaultHttpChannelConfig;
import org.kaazing.k3po.driver.internal.netty.channel.ChannelAddress;

import static org.jboss.netty.channel.Channels.fireChannelOpen;

public final class FileChannel extends AbstractChannel<FileChannelConfig> {

    FileChannel(ChannelFactory factory, ChannelPipeline pipeline, ChannelSink sink) {
        super(null, factory, pipeline, sink, new DefaultFileChannelConfig());

        fireChannelOpen(this);
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
    protected void setLocalAddress(ChannelAddress localAddress) {
        super.setLocalAddress(localAddress);
    }

}
