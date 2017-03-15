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
package org.kaazing.k3po.driver.internal.netty.bootstrap.tcp;

import static org.kaazing.k3po.driver.internal.channel.Channels.toInetSocketAddress;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
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
import org.kaazing.k3po.driver.internal.executor.ExecutorServiceFactory;
import org.kaazing.k3po.driver.internal.netty.bootstrap.BootstrapFactorySpi;
import org.kaazing.k3po.driver.internal.netty.bootstrap.ClientBootstrap;
import org.kaazing.k3po.driver.internal.netty.bootstrap.ServerBootstrap;
import org.kaazing.k3po.driver.internal.netty.channel.ChannelAddress;

public final class TcpBootstrapFactorySpi extends BootstrapFactorySpi implements ExternalResourceReleasable {

    private final Collection<ChannelFactory> channelFactories;
    private ExecutorServiceFactory executorServiceFactory;
    private NioClientSocketChannelFactory clientChannelFactory;
    private NioServerSocketChannelFactory serverChannelFactory;

    public TcpBootstrapFactorySpi() {
        channelFactories = new ConcurrentLinkedDeque<>();
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
     * Returns the name of the transport provided by factories using this service provider.
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

}
