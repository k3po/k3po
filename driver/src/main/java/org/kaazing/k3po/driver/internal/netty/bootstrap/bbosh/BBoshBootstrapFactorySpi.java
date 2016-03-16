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
package org.kaazing.k3po.driver.internal.netty.bootstrap.bbosh;

import javax.annotation.Resource;

import org.kaazing.k3po.driver.internal.netty.bootstrap.BootstrapFactory;
import org.kaazing.k3po.driver.internal.netty.bootstrap.BootstrapFactorySpi;
import org.kaazing.k3po.driver.internal.netty.bootstrap.ClientBootstrap;
import org.kaazing.k3po.driver.internal.netty.bootstrap.ServerBootstrap;
import org.kaazing.k3po.driver.internal.netty.channel.ChannelAddressFactory;

public class BBoshBootstrapFactorySpi extends BootstrapFactorySpi {

    private final BBoshServerChannelFactory channelFactory;

    public BBoshBootstrapFactorySpi() {
        this.channelFactory = new BBoshServerChannelFactory(new BBoshServerChannelSink());
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
        return "bbosh";
    }

    /**
     * Returns a {@link ClientBootstrap} instance for the named transport.
     */
    @Override
    public synchronized ClientBootstrap newClientBootstrap() throws Exception {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns a {@link ServerBootstrap} instance for the named transport.
     */
    @Override
    public synchronized ServerBootstrap newServerBootstrap() throws Exception {

        return new ServerBootstrap(channelFactory);
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
