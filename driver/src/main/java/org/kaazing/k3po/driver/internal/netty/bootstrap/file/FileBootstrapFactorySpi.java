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

package org.kaazing.k3po.driver.internal.netty.bootstrap.file;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.URI;
import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.Executor;

import javax.annotation.Resource;

import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.DefaultChannelFuture;
import org.jboss.netty.channel.DefaultChannelPipeline;
import org.jboss.netty.channel.socket.ClientSocketChannelFactory;
import org.jboss.netty.channel.socket.ServerSocketChannelFactory;
import org.jboss.netty.channel.socket.nio.NioClientBossPool;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.channel.socket.nio.NioServerBossPool;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.channel.socket.nio.NioWorkerPool;
import org.jboss.netty.util.ExternalResourceReleasable;
import org.kaazing.k3po.driver.internal.executor.ExecutorServiceFactory;
import org.kaazing.k3po.driver.internal.netty.bootstrap.BootstrapFactory;
import org.kaazing.k3po.driver.internal.netty.bootstrap.BootstrapFactorySpi;
import org.kaazing.k3po.driver.internal.netty.bootstrap.ClientBootstrap;
import org.kaazing.k3po.driver.internal.netty.bootstrap.ServerBootstrap;
import org.kaazing.k3po.driver.internal.netty.bootstrap.bbosh.BBoshServerChannelFactory;
import org.kaazing.k3po.driver.internal.netty.bootstrap.bbosh.BBoshServerChannelSink;
import org.kaazing.k3po.driver.internal.netty.channel.ChannelAddress;
import org.kaazing.k3po.driver.internal.netty.channel.ChannelAddressFactory;

public final class FileBootstrapFactorySpi extends BootstrapFactorySpi implements ExternalResourceReleasable {

    private final FileChannelFactory channelFactory;

    public FileBootstrapFactorySpi() {
        this.channelFactory = new FileChannelFactory(new FileChannelSink());
    }

    @Resource
    public void setAddressFactory(ChannelAddressFactory addressFactory) {
        channelFactory.setAddressFactory(addressFactory);
    }

    @Resource
    public void setBootstrapFactory(BootstrapFactory bootstrapFactory) {
        channelFactory.setBootstrapFactory(bootstrapFactory);
    }

    /**
     * Returns the name of the transport provided by factories using this
     * service provider.
     */
    @Override
    public String getTransportName() {
        return "file";
    }

    /**
     * Returns a {@link ClientBootstrap} instance for the named transport.
     */
    @Override
    public synchronized ClientBootstrap newClientBootstrap() throws Exception {
        return new ClientBootstrap(channelFactory) {

//            public ChannelFuture connect(final SocketAddress localAddress, final SocketAddress remoteAddress) {
//                throw new UnsupportedOperationException();
//            }
        };
    }

    /**
     * Returns a {@link ServerBootstrap} instance for the named transport.
     */
    @Override
    public synchronized ServerBootstrap newServerBootstrap() throws Exception {
        throw new UnsupportedOperationException();
/*
        ServerBootstrap bootstrap =  new ServerBootstrap(channelFactory);
        bootstrap.setPipeline(Channels.pipeline(new FileChannelPipelineFactory.FileChannelHandler()));
//        bootstrap.setPipelineFactory(new FileChannelPipelineFactory());
//        bootstrap.setParentHandler(new FileChannelPipelineFactory.FileChannelHandler());
        return bootstrap;
        */
    }

    @Override
    public void shutdown() {
        // ignore
    }

    @Override
    public void releaseExternalResources() {
        // ignore
    }
}
