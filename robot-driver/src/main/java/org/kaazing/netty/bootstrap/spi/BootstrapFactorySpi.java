/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.netty.bootstrap.spi;

import org.kaazing.netty.bootstrap.ClientBootstrap;
import org.kaazing.netty.bootstrap.ServerBootstrap;

public abstract class BootstrapFactorySpi {

    /**
     * Returns the name of the transport provided by factories using this service provider.
     */
    public abstract String getTransportName();

    /**
     * Returns a {@link ClientBootstrap} instance for the named transport.
     */
    public abstract ClientBootstrap newClientBootstrap() throws Exception;

    /**
     * Returns a {@link ServerBootstrap} instance for the named transport.
     */
    public abstract ServerBootstrap newServerBootstrap() throws Exception;
}
