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
