/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.netty.bootstrap.tcp;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.URI;

import javax.annotation.Resource;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.socket.ClientSocketChannelFactory;
import org.jboss.netty.channel.socket.ServerSocketChannelFactory;
import org.jboss.netty.channel.socket.nio.BossPool;
import org.jboss.netty.channel.socket.nio.NioClientBoss;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.channel.socket.nio.NioServerBoss;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.channel.socket.nio.NioWorker;
import org.jboss.netty.channel.socket.nio.WorkerPool;

import org.kaazing.executor.ExecutorServiceFactory;
import org.kaazing.netty.bootstrap.ClientBootstrap;
import org.kaazing.netty.bootstrap.ServerBootstrap;
import org.kaazing.netty.bootstrap.spi.BootstrapFactorySpi;
import org.kaazing.netty.channel.ChannelAddress;
import org.kaazing.robot.netty.channel.socket.nio.ShareableClientBossPool;
import org.kaazing.robot.netty.channel.socket.nio.ShareableServerBossPool;
import org.kaazing.robot.netty.channel.socket.nio.ShareableWorkerPool;

// TODO: Create the ShareableWorker/Boss SPIs and fit it into the Bootstrap factor.
// This class was copied out of netty.bootstrap.tcp. And the dependency on that removed from the robot. until that is done.
//
public class TcpBootstrapFactorySpi extends BootstrapFactorySpi {

    private ExecutorServiceFactory executorServiceFactory;


    public TcpBootstrapFactorySpi() {
    }

    @Resource
    public void setExecutorServiceFactory(ExecutorServiceFactory executorServiceFactory) {
        this.executorServiceFactory = executorServiceFactory;
    }

    /**
     * Returns the name of the transport provided by factories using this
     * service provider.
     */
    @Override
    public String getTransportName() {
        return "tcp";
    }

    /**
     * Returns a {@link ClientBootstrap} instance for the named transport.
     */
    @Override
    public synchronized ClientBootstrap newClientBootstrap() throws Exception {

        // Note the details are in the Shareable classes. But the workerPool (of size 1) will be shared with clients and all
        // servers. There is also only one ClientBoss pool (of size 1) for all clients.
        WorkerPool<NioWorker> workerPool = ShareableWorkerPool.getInstance(executorServiceFactory);
        BossPool<NioClientBoss> bossPool = ShareableClientBossPool.getInstance(executorServiceFactory);

        ClientSocketChannelFactory clientSocketFactory = new NioClientSocketChannelFactory(bossPool, workerPool);

        return new ClientBootstrap(clientSocketFactory) {
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

        // Note the details are in the Shareable classes. But the workerPool (of size 1) will be shared with clients and all
        // servers. There is also only one ClientBoss pool (of size 1) for all clients.
        WorkerPool<NioWorker> workerPool = ShareableWorkerPool.getInstance(executorServiceFactory);
        BossPool<NioServerBoss> bossPool = ShareableServerBossPool.getInstance(executorServiceFactory);

        ServerSocketChannelFactory serverSocketFactory = new NioServerSocketChannelFactory(bossPool, workerPool);

        return new ServerBootstrap(serverSocketFactory) {

            @Override
            public Channel bind(SocketAddress localAddress) {
                return super.bind(localAddressTransformation(localAddress));
            }

            @Override
            public ChannelFuture bindAsync(SocketAddress localAddress) {
                return super.bindAsync(localAddressTransformation(localAddress));
            }

        };
    }

    private static SocketAddress localAddressTransformation(final SocketAddress localAddress) {
        if (localAddress instanceof ChannelAddress) {
            return toInetSocketAddress((ChannelAddress) localAddress);
        } else {
            return localAddress;
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
