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
package org.kaazing.k3po.driver.internal.netty.bootstrap;

import static org.kaazing.k3po.driver.internal.netty.bootstrap.BootstrapFactory.newBootstrapFactory;

import java.util.Collections;
import java.util.Map;

import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.kaazing.k3po.driver.internal.netty.channel.ChannelAddress;
import org.kaazing.k3po.driver.internal.netty.channel.ChannelAddressFactory;

public class ServerBootstrapRule implements TestRule {

    private final String transportName;
    private final BootstrapFactory bootstrapFactory;
    private final ChannelAddressFactory addressFactory;

    private ServerBootstrap bootstrap;

    public ServerBootstrapRule(String transportName) {
        this.transportName = transportName;
        this.addressFactory = ChannelAddressFactory.newChannelAddressFactory();
        Map<Class<?>, Object> options = Collections.<Class<?>, Object>singletonMap(ChannelAddressFactory.class, addressFactory);
        this.bootstrapFactory = newBootstrapFactory(options);
    }

    @Override
    public Statement apply(final Statement base, Description description) {
        return new Statement() {

            @Override
            public void evaluate() throws Throwable {
                bootstrap = bootstrapFactory.newServerBootstrap(transportName);
                try {
                    base.evaluate();
                } finally {
                    bootstrapFactory.shutdown();
                    bootstrapFactory.releaseExternalResources();
                }
            }
        };
    }

    public ChannelFuture bind(ChannelAddress localAddress) {
        return bootstrap.bindAsync(localAddress);
    }

    public void setParentHandler(ChannelHandler parentHandler) {
        bootstrap.setParentHandler(parentHandler);
    }

    public void setPipeline(ChannelPipeline pipeline) {
        bootstrap.setPipeline(pipeline);
    }

    public void setPipelineFactory(ChannelPipelineFactory pipelineFactory) {
        bootstrap.setPipelineFactory(pipelineFactory);
    }

    public ChannelAddressFactory getAddressFactory() {
        return addressFactory;
    }

    public void setOption(String key, Object value) {
        bootstrap.setOption(key, value);
    }

    public void getOption(String key) {
        bootstrap.getOption(key);
    }

    public void shutdown() {
        bootstrapFactory.shutdown();
    }
}
