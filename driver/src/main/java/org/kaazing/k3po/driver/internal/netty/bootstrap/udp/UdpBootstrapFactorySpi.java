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

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineException;
import org.jboss.netty.channel.socket.nio.NioDatagramChannelFactory;
import org.jboss.netty.channel.socket.nio.NioDatagramWorkerPool;
import org.jboss.netty.handler.timeout.IdleStateHandler;
import org.jboss.netty.util.ExternalResourceReleasable;
import org.jboss.netty.util.HashedWheelTimer;
import org.jboss.netty.util.Timer;
import org.kaazing.k3po.driver.internal.executor.ExecutorServiceFactory;
import org.kaazing.k3po.driver.internal.netty.bootstrap.BootstrapFactorySpi;
import org.kaazing.k3po.driver.internal.netty.bootstrap.ClientBootstrap;
import org.kaazing.k3po.driver.internal.netty.bootstrap.ServerBootstrap;
import org.kaazing.k3po.driver.internal.netty.channel.ChannelAddress;
import org.kaazing.k3po.driver.internal.netty.channel.udp.UdpChannelAddress;

import javax.annotation.Resource;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.URI;
import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

public final class UdpBootstrapFactorySpi extends BootstrapFactorySpi implements ExternalResourceReleasable {

    private final Collection<ChannelFactory> channelFactories;
    private ExecutorServiceFactory executorServiceFactory;
    private NioDatagramChannelFactory clientChannelFactory;
    private UdpServerChannelFactory serverChannelFactory;
    private final Timer timer;

    public UdpBootstrapFactorySpi() {
        channelFactories = new ConcurrentLinkedDeque<>();
        timer = new HashedWheelTimer();
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
        timer.stop();
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

        return new UdpClientBootstrap(clientChannelFactory, timer);
    }

    /**
     * Returns a {@link ServerBootstrap} instance for the named transport.
     */
    @Override
    public synchronized ServerBootstrap newServerBootstrap() throws Exception {
        if (serverChannelFactory == null) {
            Executor workerExecutor = executorServiceFactory.newExecutorService("worker.server");
            NioDatagramWorkerPool workerPool = new NioDatagramWorkerPool(workerExecutor, 1);
            serverChannelFactory = new UdpServerChannelFactory(workerPool, timer);

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

    // Subclassing for two reasons:
    // 1) uses InetSocketAddress for local and remote addresses
    // 2) Adds IdleStateHandler filter to track UDP idle client connections
    private static class UdpClientBootstrap extends ClientBootstrap {

        private final Timer timer;

        UdpClientBootstrap(ChannelFactory channelFactory, Timer timer) {
            super(channelFactory);
            this.timer = timer;
        }

        @Override
        public ChannelFuture connect(final SocketAddress remoteChannelAddress, final SocketAddress localChannelAddress) {


            InetSocketAddress localAddress = toInetSocketAddress((ChannelAddress) localChannelAddress);
            InetSocketAddress remoteAddress = toInetSocketAddress((ChannelAddress) remoteChannelAddress);

            if (remoteAddress == null) {
                throw new NullPointerException("remoteAddress");
            }

            ChannelPipeline pipeline;
            try {
                pipeline = getPipelineFactory().getPipeline();
            } catch (Exception e) {
                throw new ChannelPipelineException("Failed to initialize a pipeline.", e);
            }

            long timeout = ((UdpChannelAddress) remoteChannelAddress).timeout();
            if (timeout != 0) {
                IdleStateHandler idleStateHandler = new IdleStateHandler(timer, 0, 0, timeout, TimeUnit.MILLISECONDS);
                pipeline.addFirst("idleHandler", new UdpIdleHandler());
                pipeline.addFirst("idleStateHandler", idleStateHandler);
            }
            setPipeline(pipeline);

            // Set the options.
            Channel ch = getFactory().newChannel(pipeline);
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
            if (localAddress != null) {
                ch.bind(localAddress);
            }

            // Connect.
            return ch.connect(remoteAddress);
        }
    }

}
