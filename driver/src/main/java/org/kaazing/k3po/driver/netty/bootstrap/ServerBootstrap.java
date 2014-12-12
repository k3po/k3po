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

package org.kaazing.k3po.driver.netty.bootstrap;

import java.net.SocketAddress;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelFuture;

public class ServerBootstrap extends org.jboss.netty.bootstrap.ServerBootstrap {

    public ServerBootstrap() {
        super();
    }

    public ServerBootstrap(ChannelFactory channelFactory) {
        super(channelFactory);
    }

    @Override
    public final Channel bind() {
        return super.bind();
    }

    @Override
    public final Channel bind(SocketAddress localAddress) {
        return super.bind(localAddress);
    }

    @Override
    public final ChannelFuture bindAsync() {
        return super.bindAsync();
    }

}
