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

package org.kaazing.k3po.driver.netty.bootstrap;

import static org.kaazing.k3po.driver.netty.bootstrap.BootstrapFactory.newBootstrapFactory;

import java.util.Collections;
import java.util.Map;

import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.kaazing.k3po.driver.netty.bootstrap.BootstrapFactory;
import org.kaazing.k3po.driver.netty.bootstrap.ServerBootstrap;
import org.kaazing.k3po.driver.netty.channel.ChannelAddress;
import org.kaazing.k3po.driver.netty.channel.ChannelAddressFactory;

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
                }
                finally {
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
}