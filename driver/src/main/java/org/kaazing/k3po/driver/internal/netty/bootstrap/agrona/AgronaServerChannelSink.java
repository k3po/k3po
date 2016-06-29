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
package org.kaazing.k3po.driver.internal.netty.bootstrap.agrona;

import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelStateEvent;
import org.kaazing.k3po.driver.internal.netty.bootstrap.channel.AbstractServerChannelSink;
import org.kaazing.k3po.driver.internal.netty.channel.agrona.AgronaChannelAddress;

public class AgronaServerChannelSink extends AbstractServerChannelSink<AgronaServerChannel> {

    @Override
    protected void bindRequested(ChannelPipeline pipeline, ChannelStateEvent evt) throws Exception {

        AgronaServerChannel serverChannel = (AgronaServerChannel) evt.getChannel();
        AgronaChannelAddress localAddress = (AgronaChannelAddress) evt.getValue();
        ChannelFuture bindFuture = evt.getFuture();

        serverChannel.boss.bind(serverChannel, localAddress, bindFuture);
    }

    @Override
    protected void unbindRequested(ChannelPipeline pipeline, ChannelStateEvent evt) throws Exception {
        final AgronaServerChannel serverChannel = (AgronaServerChannel) evt.getChannel();
        final ChannelFuture unbindFuture = evt.getFuture();

        serverChannel.boss.unbind(serverChannel, unbindFuture);
    }

    @Override
    protected void closeRequested(ChannelPipeline pipeline, ChannelStateEvent evt) throws Exception {
        final AgronaServerChannel serverChannel = (AgronaServerChannel) evt.getChannel();

        serverChannel.boss.close(serverChannel);
    }

}
