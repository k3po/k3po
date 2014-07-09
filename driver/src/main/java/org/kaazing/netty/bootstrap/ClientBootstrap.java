/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.netty.bootstrap;

import org.jboss.netty.channel.ChannelFactory;

public class ClientBootstrap extends org.jboss.netty.bootstrap.ClientBootstrap {

    public ClientBootstrap() {
        super();
    }

    public ClientBootstrap(ChannelFactory channelFactory) {
        super(channelFactory);
    }

}
