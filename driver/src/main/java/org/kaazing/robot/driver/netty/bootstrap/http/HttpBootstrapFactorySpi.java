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

package org.kaazing.robot.driver.netty.bootstrap.http;

import javax.annotation.Resource;

import org.kaazing.robot.driver.netty.bootstrap.BootstrapFactory;
import org.kaazing.robot.driver.netty.bootstrap.BootstrapFactorySpi;
import org.kaazing.robot.driver.netty.bootstrap.ClientBootstrap;
import org.kaazing.robot.driver.netty.bootstrap.ServerBootstrap;

public class HttpBootstrapFactorySpi extends BootstrapFactorySpi {

    private final HttpServerChannelSink serverChannelSink;
    private final HttpServerChannelFactory serverChannelFactory;
    private final HttpClientChannelSinkFactory clientChannelSinkFactory;
    private final HttpClientChannelFactory clientChannelFactory;

    public HttpBootstrapFactorySpi() {
        this.serverChannelSink = new HttpServerChannelSink();
        this.serverChannelFactory = new HttpServerChannelFactory(serverChannelSink);
        this.clientChannelSinkFactory = new HttpClientChannelSinkFactory();
        this.clientChannelFactory = new HttpClientChannelFactory(clientChannelSinkFactory);
    }

    @Resource
    public void setBootstrapFactory(BootstrapFactory bootstrapFactory) {
        serverChannelSink.setBootstrapFactory(bootstrapFactory);
        clientChannelSinkFactory.setBootstrapFactory(bootstrapFactory);
    }

    /**
     * Returns the name of the transport provided by factories using this
     * service provider.
     */
    @Override
    public String getTransportName() {
        return "http";
    }

    /**
     * Returns a {@link ClientBootstrap} instance for the named transport.
     */
    @Override
    public synchronized ClientBootstrap newClientBootstrap() throws Exception {
        return new ClientBootstrap(clientChannelFactory);
    }

    /**
     * Returns a {@link ServerBootstrap} instance for the named transport.
     */
    @Override
    public synchronized ServerBootstrap newServerBootstrap() throws Exception {
        return new ServerBootstrap(serverChannelFactory);
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
