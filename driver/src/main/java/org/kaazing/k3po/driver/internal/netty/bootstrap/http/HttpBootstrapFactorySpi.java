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
package org.kaazing.k3po.driver.internal.netty.bootstrap.http;

import javax.annotation.Resource;

import org.kaazing.k3po.driver.internal.netty.bootstrap.BootstrapFactory;
import org.kaazing.k3po.driver.internal.netty.bootstrap.BootstrapFactorySpi;
import org.kaazing.k3po.driver.internal.netty.bootstrap.ClientBootstrap;
import org.kaazing.k3po.driver.internal.netty.bootstrap.ServerBootstrap;

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
        // ignore, no external resources to shutdown as it always runs on top of another transport (tcp)
    }

    @Override
    public void releaseExternalResources() {
        // ignore, no external resources to shutdown as it always runs on top of another transport (tcp)
    }
}
