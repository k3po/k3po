/*
 * Copyright 2014, Kaazing Corporation. All rights reserved.
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

package org.kaazing.k3po.driver.netty.bootstrap.tcp;

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
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineException;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.DefaultChannelFuture;
import org.jboss.netty.channel.socket.ClientSocketChannelFactory;
import org.jboss.netty.channel.socket.ServerSocketChannelFactory;
import org.jboss.netty.channel.socket.nio.NioClientBossPool;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.channel.socket.nio.NioServerBossPool;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.channel.socket.nio.NioWorkerPool;
import org.jboss.netty.util.ExternalResourceReleasable;
import org.kaazing.k3po.driver.behavior.Barrier;
import org.kaazing.k3po.driver.executor.ExecutorServiceFactory;
import org.kaazing.k3po.driver.netty.bootstrap.BootstrapFactorySpi;
import org.kaazing.k3po.driver.netty.bootstrap.ClientBootstrap;
import org.kaazing.k3po.driver.netty.bootstrap.ServerBootstrap;
import org.kaazing.k3po.driver.netty.channel.ChannelAddress;

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

                Object barrier = getOption("barrier");
                if (barrier == null) {
                    return super.connect(localChannelAddress, remoteChannelAddress);
                } else {
                    // pulled code from super.connect in order to get access to the channel but not actually connect
                    // until later
                    if (localChannelAddress == null) {
                        throw new NullPointerException("localAddress");
                    }

                    ChannelPipeline pipeline;
                    try {
                        pipeline = getPipelineFactory().getPipeline();
                    } catch (Exception e) {
                        throw new ChannelPipelineException("Failed to initialize a pipeline.", e);
                    }

                    // Set the options.
                    final Channel ch = getFactory().newChannel(pipeline);
                    boolean success = false;
                    try {
                        ch.getConfig().setOptions(getOptions());
                        success = true;
                    } finally {
                        if (!success) {
                            ch.close();
                        }
                    }

                    // Bind.
                    if (remoteChannelAddress != null) {
                        ch.bind(remoteChannelAddress);
                    }

                    final ChannelFuture connectedFuture = Channels.future(ch, true);
                    ((Barrier) barrier).getFuture().addListener(new ChannelFutureListener() {
                        @Override
                        public void operationComplete(ChannelFuture future) throws Exception {
                            if (!connectedFuture.isCancelled()) {
                                ch.connect(localChannelAddress).addListener(new ChannelFutureListener() {
                                    @Override
                                    public void operationComplete(ChannelFuture future) throws Exception {
                                        connectedFuture.setSuccess();
                                    }
                                });
                            }
                        }
                    });
                    return connectedFuture;
                }
            }
        };
        // return new ClientBootstrap(clientChannelFactory) {
        // @Override
        // public ChannelFuture connect(final SocketAddress localAddress, final SocketAddress remoteAddress) {
        // Object barrier = getOption("barrier");
        //
        // if (barrier == null) {
        // return connect0(localAddress, remoteAddress);
        // } else {
        // final ChannelFuture future = Channels.future(null, true);
        // ((Barrier) barrier).getFuture().addListener(new ChannelFutureListener() {
        // @Override
        // public void operationComplete(ChannelFuture future) throws Exception {
        // if (!future.isCancelled()) {
        // connect0(localAddress, remoteAddress).addListener(new ChannelFutureListener() {
        //
        // @Override
        // public void operationComplete(ChannelFuture future) throws Exception {
        // future.setSuccess();
        // }
        // });
        // }
        // }
        // });
        // return future;
        // }
        // }
        //
        // public ChannelFuture connect0(final SocketAddress localAddress, final SocketAddress remoteAddress) {
        // final InetSocketAddress localChannelAddress = toInetSocketAddress((ChannelAddress) localAddress);
        // final InetSocketAddress remoteChannelAddress = toInetSocketAddress((ChannelAddress) remoteAddress);
        // return super.connect(localChannelAddress, remoteChannelAddress);
        // }
        // };
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
