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

package org.kaazing.k3po.driver.internal.resolver;

import java.net.URI;
import java.util.Map;

import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.logging.InternalLoggerFactory;
import org.kaazing.k3po.driver.internal.netty.bootstrap.BootstrapFactory;
import org.kaazing.k3po.driver.internal.netty.bootstrap.ServerBootstrap;
import org.kaazing.k3po.driver.internal.netty.channel.ChannelAddress;
import org.kaazing.k3po.driver.internal.netty.channel.ChannelAddressFactory;

/**
 * The class is used to defer the initialization of {@link ServerBootstrap}.
 */
public class ServerBootstrapResolver {

    private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(ClientBootstrapResolver.class);

    private final BootstrapFactory bootstrapFactory;
    private final ChannelAddressFactory addressFactory;
    private final ChannelPipelineFactory pipelineFactory;
    private final LocationResolver locationResolver;
    private final LocationResolver transportResolver;

    private final Map<String, Object> acceptOptions;

    private ServerBootstrap bootstrap;

    public ServerBootstrapResolver(BootstrapFactory bootstrapFactory, ChannelAddressFactory addressFactory,
            ChannelPipelineFactory pipelineFactory, LocationResolver locationResolver,
            LocationResolver transportResolver, Map<String, Object> acceptOptions) {
        this.bootstrapFactory = bootstrapFactory;
        this.addressFactory = addressFactory;
        this.pipelineFactory = pipelineFactory;
        this.locationResolver = locationResolver;
        this.transportResolver = transportResolver;
        this.acceptOptions = acceptOptions;
    }

    public ServerBootstrap resolve() throws Exception {
        if (bootstrap == null) {
            URI acceptURI = locationResolver.resolve();
            if (transportResolver != null) {
                URI transportURI = transportResolver.resolve();
                acceptOptions.put("transport", transportURI);
            }
            ChannelAddress localAddress = addressFactory.newChannelAddress(acceptURI, acceptOptions);
            LOGGER.debug("Initializing server Bootstrap binding to address " + localAddress);
            ServerBootstrap serverBootstrapCandidate = bootstrapFactory.newServerBootstrap(acceptURI.getScheme());
            acceptOptions.put("localAddress", localAddress);
            serverBootstrapCandidate.setOptions(acceptOptions);
            serverBootstrapCandidate.setPipelineFactory(pipelineFactory);
            bootstrap = serverBootstrapCandidate;
        }
        return bootstrap;
    }

}
