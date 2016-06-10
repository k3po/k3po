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
package org.kaazing.k3po.driver.internal.netty.bootstrap.udp;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.URI;
import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.Executor;

import javax.annotation.Resource;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.ClientSocketChannelFactory;
import org.jboss.netty.channel.socket.ServerSocketChannelFactory;
import org.jboss.netty.channel.socket.nio.NioClientBossPool;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.channel.socket.nio.NioDatagramChannelFactory;
import org.jboss.netty.channel.socket.nio.NioDatagramWorkerPool;
import org.jboss.netty.channel.socket.nio.NioServerBossPool;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.channel.socket.nio.NioWorkerPool;
import org.jboss.netty.util.ExternalResourceReleasable;
import org.kaazing.k3po.driver.internal.executor.ExecutorServiceFactory;
import org.kaazing.k3po.driver.internal.netty.bootstrap.BootstrapFactorySpi;
import org.kaazing.k3po.driver.internal.netty.bootstrap.ClientBootstrap;
import org.kaazing.k3po.driver.internal.netty.bootstrap.ServerBootstrap;
import org.kaazing.k3po.driver.internal.netty.bootstrap.ConnectionlessBootstrap;
import org.kaazing.k3po.driver.internal.netty.channel.ChannelAddress;

public final class UdpBootstrapFactorySpi extends BootstrapFactorySpi implements ExternalResourceReleasable {

    private final Collection<ChannelFactory> channelFactories;
    private ExecutorServiceFactory executorServiceFactory;
    private NioDatagramChannelFactory clientChannelFactory;
    private NioDatagramChannelFactory serverChannelFactory;

    public UdpBootstrapFactorySpi() {
        channelFactories = new ConcurrentLinkedDeque<>();
    }

    @Resource
    public void setExecutorServiceFactory(ExecutorServiceFactory executorServiceFactory) {
        this.executorServiceFactory = executorServiceFactory;
    }

//    @Resource
//    public void setNioClientSocketChannelFactory(NioClientSocketChannelFactory clientChannelFactory) {
//        this.clientChannelFactory = clientChannelFactory;
//    }

//    @Resource
//    public void setNioServerSocketChannelFactory(NioServerSocketChannelFactory serverChannelFactory) {
//        this.serverChannelFactory = serverChannelFactory;
//    }

    /**
     * Returns the name of the transport provided by factories using this service provider.
     */
    @Override
    public String getTransportName() {
        return "udp";
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

        NioDatagramChannelFactory clientChannelFactory = this.clientChannelFactory;

        if (clientChannelFactory == null) {
            Executor bossExecutor = executorServiceFactory.newExecutorService("boss.client");
            NioClientBossPool bossPool = new NioClientBossPool(bossExecutor, 1);
            Executor workerExecutor = executorServiceFactory.newExecutorService("worker.client");
            NioDatagramWorkerPool workerPool = new NioDatagramWorkerPool(workerExecutor, 1);
            clientChannelFactory = new NioDatagramChannelFactory(workerPool);

            // unshared
            channelFactories.add(clientChannelFactory);
        }

        return new ClientBootstrap(clientChannelFactory) {
            @Override
            public ChannelFuture connect(final SocketAddress localAddress, final SocketAddress remoteAddress) {
                final InetSocketAddress localChannelAddress = toInetSocketAddress((ChannelAddress) localAddress);
                final InetSocketAddress remoteChannelAddress = toInetSocketAddress((ChannelAddress) remoteAddress);
                return super.connect(localChannelAddress, remoteChannelAddress);
            }
        };
    }

    /**
     * Returns a {@link ServerBootstrap} instance for the named transport.
     */
    @Override
    public synchronized ConnectionlessBootstrap newServerBootstrap() throws Exception {
        NioDatagramChannelFactory serverChannelFactory = this.serverChannelFactory;

        if (serverChannelFactory == null) {
            Executor bossExecutor = executorServiceFactory.newExecutorService("boss.server");
            NioServerBossPool bossPool = new NioServerBossPool(bossExecutor, 1);
            Executor workerExecutor = executorServiceFactory.newExecutorService("worker.server");
            NioDatagramWorkerPool workerPool = new NioDatagramWorkerPool(workerExecutor, 1);
            serverChannelFactory = new NioDatagramChannelFactory(workerPool);

            // unshared
            channelFactories.add(serverChannelFactory);
        }

        return new ConnectionlessBootstrap(serverChannelFactory) {

            @Override
            public Channel bind(SocketAddress localAddress) {
                Channel channel = super.bind(toInetSocketAddress(localAddress));
                Channels.fireChannelConnected(channel, new InetSocketAddress("localhost", 2222));
                return channel;
            }

        };
    }

//    @Override
//    public synchronized ConnectionlessBootstrap newConnectionlessBootstrap() throws Exception {
//
//        NioDatagramChannelFactory serverChannelFactory = this.serverChannelFactory;
//
//        if (serverChannelFactory == null) {
//            Executor bossExecutor = executorServiceFactory.newExecutorService("boss.server");
//            NioServerBossPool bossPool = new NioServerBossPool(bossExecutor, 1);
//            Executor workerExecutor = executorServiceFactory.newExecutorService("worker.server");
//            NioDatagramWorkerPool workerPool = new NioDatagramWorkerPool(workerExecutor, 1);
//            serverChannelFactory = new NioDatagramChannelFactory(workerPool);
//
//            // unshared
//            channelFactories.add(serverChannelFactory);
//        }
//
//        return new ConnectionlessBootstrap(serverChannelFactory) {
//
//            @Override
//            public Channel bind(SocketAddress localAddress) {
//                return super.bind(toInetSocketAddress(localAddress));
//            }
//
//        };
//    }

    private static InetSocketAddress toInetSocketAddress(final SocketAddress localAddress) {
        if (localAddress instanceof ChannelAddress) {
            return toInetSocketAddress((ChannelAddress) localAddress);
        } else {
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
