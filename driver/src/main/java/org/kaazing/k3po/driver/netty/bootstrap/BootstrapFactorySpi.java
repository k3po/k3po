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

import org.jboss.netty.util.ExternalResourceReleasable;


public abstract class BootstrapFactorySpi implements ExternalResourceReleasable {

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

    public abstract void shutdown();

    @Override
    public abstract void releaseExternalResources();

}
