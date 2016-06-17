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

import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.socket.nio.NioDatagramChannelFactory;
import org.jboss.netty.channel.socket.nio.NioDatagramWorkerPool;
import org.jboss.netty.util.ExternalResourceReleasable;
import org.kaazing.k3po.driver.internal.executor.ExecutorServiceFactory;
import org.kaazing.k3po.driver.internal.netty.bootstrap.BootstrapFactorySpi;
import org.kaazing.k3po.driver.internal.netty.bootstrap.ClientBootstrap;
import org.kaazing.k3po.driver.internal.netty.bootstrap.ServerBootstrap;
import org.kaazing.k3po.driver.internal.netty.channel.ChannelAddress;

import javax.annotation.Resource;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.URI;
import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.Executor;

public final class UdpBootstrapFactorySpi extends BootstrapFactorySpi implements ExternalResourceReleasable {

    private final Collection<ChannelFactory> channelFactories;
    private ExecutorServiceFactory executorServiceFactory;
    private NioDatagramChannelFactory clientChannelFactory;
    private UdpServerChannelFactory serverChannelFactory;

    public UdpBootstrapFactorySpi() {
        channelFactories = new ConcurrentLinkedDeque<>();
    }

    @Resource
    public void setExecutorServiceFactory(ExecutorServiceFactory executorServiceFactory) {
        this.executorServiceFactory = executorServiceFactory;
    }

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

        if (clientChannelFactory == null) {
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
    public synchronized ServerBootstrap newServerBootstrap() throws Exception {
        if (serverChannelFactory == null) {
            Executor workerExecutor = executorServiceFactory.newExecutorService("worker.server");
            NioDatagramWorkerPool workerPool = new NioDatagramWorkerPool(workerExecutor, 1);
            serverChannelFactory = new UdpServerChannelFactory(new UdpServerChannelSink(workerPool));

            // unshared
            channelFactories.add(serverChannelFactory);
        }

        return new ServerBootstrap(serverChannelFactory);
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
