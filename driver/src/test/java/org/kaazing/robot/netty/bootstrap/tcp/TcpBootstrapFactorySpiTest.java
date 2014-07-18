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

package org.kaazing.robot.netty.bootstrap.tcp;

import org.junit.Assert;
import org.junit.Test;

import org.kaazing.netty.bootstrap.BootstrapFactory;
import org.kaazing.netty.bootstrap.ClientBootstrap;
import org.kaazing.netty.bootstrap.ServerBootstrap;
import org.kaazing.netty.bootstrap.spi.BootstrapFactorySpi;

public class TcpBootstrapFactorySpiTest {

    @Test
    public void transportNameOK()
        throws Exception {

        BootstrapFactorySpi provider = new TcpBootstrapFactorySpi();

        String transportName = provider.getTransportName();
        String expected = "tcp";
        Assert.assertTrue(String.format("Expected transport name '%s', got '%s'", expected, transportName), expected
                .equals(transportName));
    }

    @Test
    public void shouldCreateNewClientBootstrap() throws Exception {

        BootstrapFactory bootstrapFactory = BootstrapFactory.newBootstrapFactory();
        ClientBootstrap bootstrap = null;
        try {
            bootstrap = bootstrapFactory.newClientBootstrap("tcp");
        } finally {
            if (bootstrap != null) {
                bootstrap.releaseExternalResources();
            }
        }
    }

    @Test
    public void shouldCreateNewServerBootstrap() throws Exception {

        BootstrapFactory bootstrapFactory = BootstrapFactory.newBootstrapFactory();
        ServerBootstrap bootstrap = null;
        try {
            bootstrap = bootstrapFactory.newServerBootstrap("tcp");
        } finally {
            if (bootstrap != null) {
                bootstrap.releaseExternalResources();
            }
        }
    }
}
