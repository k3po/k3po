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

import static org.jboss.netty.channel.Channels.fireChannelBound;
import static org.jboss.netty.channel.Channels.fireChannelConnected;
import static org.jboss.netty.channel.Channels.fireChannelOpen;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandler.Sharable;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.kaazing.k3po.driver.internal.netty.channel.ChannelAddress;

@Sharable
public abstract class AbstractServerChannelChildHandler<S extends AbstractServerChannel<?>, T extends AbstractChannel<?>>
        extends SimpleChannelHandler {

    @Override
    @SuppressWarnings("unchecked")
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {

        S serverChannel = (S) ctx.getAttachment();

        T childChannel = createChildChannel(serverChannel, e.getChannel());
        fireChannelOpen(childChannel);

        ChannelAddress localAddress = serverChannel.getLocalAddress();
        childChannel.setLocalAddress(localAddress);
        childChannel.setBound();
        fireChannelBound(childChannel, localAddress);

        ctx.setAttachment(childChannel);

        ctx.sendUpstream(e);

        // TODO: fire CONNECTED_BARRIER event to next pipeline
        // then fire CONNECTED event when future completes successfully
        ChannelAddress remoteAddress = localAddress.newEphemeralAddress();
        childChannel.setRemoteAddress(remoteAddress);
        childChannel.setConnected();
        fireChannelConnected(childChannel, remoteAddress);
    }

    protected abstract T createChildChannel(S parent, Channel transport) throws Exception;

}
