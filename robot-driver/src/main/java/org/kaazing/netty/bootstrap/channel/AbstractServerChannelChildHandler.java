/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
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
