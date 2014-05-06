/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package com.kaazing.netty.bootstrap.tcp.kaazing.netty.bootstrap.tcp;

import org.junit.Assert;
import org.junit.Test;

import com.kaazing.netty.bootstrap.BootstrapFactory;
import com.kaazing.netty.bootstrap.ClientBootstrap;
import com.kaazing.netty.bootstrap.ServerBootstrap;
import com.kaazing.netty.bootstrap.spi.BootstrapFactorySpi;
import com.kaazing.netty.bootstrap.tcp.TcpBootstrapFactorySpi;

public class TcpBootstrapFactorySpiTest {

    @Test
    public void transportNameOK() throws Exception {

        BootstrapFactorySpi provider = new TcpBootstrapFactorySpi();

        String transportName = provider.getTransportName();
        String expected = "tcp";
        Assert.assertTrue(String.format("Expected transport name '%s', got '%s'", expected, transportName),
                expected.equals(transportName));
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
