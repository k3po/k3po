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

package org.kaazing.netty.bootstrap.tcp;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.URI;
import java.util.concurrent.Executor;

import javax.annotation.Resource;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.socket.ClientSocketChannelFactory;
import org.jboss.netty.channel.socket.ServerSocketChannelFactory;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import org.kaazing.netty.bootstrap.ClientBootstrap;
import org.kaazing.netty.bootstrap.ServerBootstrap;
import org.kaazing.netty.bootstrap.spi.BootstrapFactorySpi;
import org.kaazing.netty.channel.ChannelAddress;
import org.kaazing.executor.ExecutorServiceFactory;

public class TcpBootstrapFactorySpi extends BootstrapFactorySpi {

    private ExecutorServiceFactory executorServiceFactory;

    public TcpBootstrapFactorySpi() {
    }

    @Resource
    public void setExecutorServiceFactory(ExecutorServiceFactory executorServiceFactory) {
        this.executorServiceFactory = executorServiceFactory;
    }

    /**
     * Returns the name of the transport provided by factories using this service provider.
     */
    public String getTransportName() {
        return "tcp";
    }

    /**
     * Returns a {@link ClientBootstrap} instance for the named transport.
     */
    public ClientBootstrap newClientBootstrap() throws Exception {

        Executor bossExecutor = executorServiceFactory.newExecutorService("tcp.client.boss");
        Executor workerExecutor = executorServiceFactory.newExecutorService("tcp.client.worker");

        ClientSocketChannelFactory factory = new NioClientSocketChannelFactory(bossExecutor, workerExecutor);
        return new ClientBootstrap(factory) {

            @Override
            public ChannelFuture connect(SocketAddress localAddress, SocketAddress remoteAddress) {
                InetSocketAddress localChannelAddress = toInetSocketAddress((ChannelAddress) localAddress);
                InetSocketAddress remoteChannelAddress = toInetSocketAddress((ChannelAddress) remoteAddress);
                return super.connect(localChannelAddress, remoteChannelAddress);
            }
        };
    }

    /**
     * Returns a {@link ServerBootstrap} instance for the named transport.
     */
    public ServerBootstrap newServerBootstrap() throws Exception {

        Executor bossExecutor = executorServiceFactory.newExecutorService("tcp.server.boss");
        Executor workerExecutor = executorServiceFactory.newExecutorService("tcp.server.worker");

        ServerSocketChannelFactory factory = new NioServerSocketChannelFactory(bossExecutor, workerExecutor);
        return new ServerBootstrap(factory) {

            @Override
            public Channel bind(SocketAddress localAddress) {
                return super.bind(toInetSocketAddress((ChannelAddress) localAddress));
            }

        };
    }

    private static InetSocketAddress toInetSocketAddress(ChannelAddress channelAddress) {
        if (channelAddress == null) {
            return null;
        }

        URI location = channelAddress.getLocation();
        String hostname = location.getHost();
        int port = location.getPort();
        return new InetSocketAddress(hostname, port);
    }
}
