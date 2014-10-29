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

package org.kaazing.robot.driver.netty.bootstrap;

import static org.kaazing.robot.driver.netty.bootstrap.BootstrapFactory.newBootstrapFactory;

import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelPipeline;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.kaazing.robot.driver.netty.channel.ChannelAddress;

public class ClientBootstrapRule implements TestRule {

    private final String transportName;
    private final BootstrapFactory bootstrapFactory;

    private ClientBootstrap bootstrap;

    public ClientBootstrapRule(String transportName) {
        this.transportName = transportName;
        this.bootstrapFactory = newBootstrapFactory();
    }

    @Override
    public Statement apply(final Statement base, Description description) {
        return new Statement() {

            @Override
            public void evaluate() throws Throwable {
                bootstrap = bootstrapFactory.newClientBootstrap(transportName);
                try {
                    base.evaluate();
                }
                finally {
                    bootstrapFactory.releaseExternalResources();
                }
            }
        };
    }

    public ChannelFuture connect(ChannelAddress remoteAddress) {
        return bootstrap.connect(remoteAddress);
    }

    public void setPipeline(ChannelPipeline pipeline) {
        bootstrap.setPipeline(pipeline);
    }

}