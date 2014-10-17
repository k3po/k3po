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

package org.kaazing.robot.driver.netty.bootstrap.tcp;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.URI;
import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.Executor;

import javax.annotation.Resource;

import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.socket.ClientSocketChannelFactory;
import org.jboss.netty.channel.socket.ServerSocketChannelFactory;
import org.jboss.netty.channel.socket.nio.NioClientBossPool;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.channel.socket.nio.NioServerBossPool;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.channel.socket.nio.NioWorkerPool;
import org.jboss.netty.util.ExternalResourceReleasable;
import org.kaazing.robot.driver.executor.ExecutorServiceFactory;
import org.kaazing.robot.driver.netty.bootstrap.BootstrapFactorySpi;
import org.kaazing.robot.driver.netty.bootstrap.ClientBootstrap;
import org.kaazing.robot.driver.netty.bootstrap.ServerBootstrap;
import org.kaazing.robot.driver.netty.channel.ChannelAddress;

public final class TcpBootstrapFactorySpi extends BootstrapFactorySpi implements ExternalResourceReleasable {

    private final Collection<ChannelFactory> channelFactories;
    private ExecutorServiceFactory executorServiceFactory;
    private NioClientSocketChannelFactory clientChannelFactory;
    private NioServerSocketChannelFactory serverChannelFactory;

    public TcpBootstrapFactorySpi() {
        channelFactories = new ConcurrentLinkedDeque<ChannelFactory>();
    }

    @Resource
    public void setExecutorServiceFactory(ExecutorServiceFactory executorServiceFactory) {
        this.executorServiceFactory = executorServiceFactory;
    }

    @Resource
    public void setNioClientSocketChannelFactory(NioClientSocketChannelFactory clientChannelFactory) {
        this.clientChannelFactory = clientChannelFactory;
    }

    @Resource
    public void setNioServerSocketChannelFactory(NioServerSocketChannelFactory serverChannelFactory) {
        this.serverChannelFactory = serverChannelFactory;
    }

    /**
     * Returns the name of the transport provided by factories using this
     * service provider.
     */
    @Override
    public String getTransportName() {
        return "tcp";
    }

    @Override
    public void shutdown() {
        for (ChannelFactory channelFactory : channelFactories) {
            channelFactory.shutdown();
        }
    }

    @Override
    public void releaseExternalResources() {
        for (ChannelFactory channelFactory : channelFactories) {
            channelFactory.releaseExternalResources();
        }
    }

    /**
     * Returns a {@link ClientBootstrap} instance for the named transport.
     */
    @Override
    public synchronized ClientBootstrap newClientBootstrap() throws Exception {

        ClientSocketChannelFactory clientChannelFactory = this.clientChannelFactory;

        if (clientChannelFactory == null) {
            Executor bossExecutor = executorServiceFactory.newExecutorService("boss.client");
            NioClientBossPool bossPool = new NioClientBossPool(bossExecutor, 1);
            Executor workerExecutor = executorServiceFactory.newExecutorService("worker.client");
            NioWorkerPool workerPool = new NioWorkerPool(workerExecutor, 1);
            clientChannelFactory = new NioClientSocketChannelFactory(bossPool, workerPool);

            // unshared
            channelFactories.add(clientChannelFactory);
        }

        return new ClientBootstrap(clientChannelFactory) {
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
    @Override
    public synchronized ServerBootstrap newServerBootstrap() throws Exception {

        ServerSocketChannelFactory serverChannelFactory = this.serverChannelFactory;

        if (serverChannelFactory == null) {
            Executor bossExecutor = executorServiceFactory.newExecutorService("boss.server");
            NioServerBossPool bossPool = new NioServerBossPool(bossExecutor, 1);
            Executor workerExecutor = executorServiceFactory.newExecutorService("worker.server");
            NioWorkerPool workerPool = new NioWorkerPool(workerExecutor, 1);
            serverChannelFactory = new NioServerSocketChannelFactory(bossPool, workerPool);

            // unshared
            channelFactories.add(serverChannelFactory);
        }

        return new ServerBootstrap(serverChannelFactory) {

            @Override
            public ChannelFuture bindAsync(SocketAddress localAddress) {
                return super.bindAsync(toInetSocketAddress(localAddress));
            }

        };
    }

    private static InetSocketAddress toInetSocketAddress(final SocketAddress localAddress) {
        if (localAddress instanceof ChannelAddress) {
            return toInetSocketAddress((ChannelAddress) localAddress);
        }
        else {
            return (InetSocketAddress) localAddress;
        }
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
