/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.netty.bootstrap;

import org.jboss.netty.channel.ChannelFactory;

// TODO: add asynchronous bind returning ChannelFuture instead of Channel
public class ServerBootstrap extends org.jboss.netty.bootstrap.ServerBootstrap {

    public ServerBootstrap() {
        super();
    }

    public ServerBootstrap(ChannelFactory channelFactory) {
        super(channelFactory);
    }

}
