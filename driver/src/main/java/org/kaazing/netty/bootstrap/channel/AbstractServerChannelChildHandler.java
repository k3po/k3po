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

import static org.jboss.netty.channel.Channels.fireChannelBound;
import static org.jboss.netty.channel.Channels.fireChannelConnected;
import static org.jboss.netty.channel.Channels.fireChannelOpen;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandler.Sharable;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;

import org.kaazing.netty.channel.ChannelAddress;

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
