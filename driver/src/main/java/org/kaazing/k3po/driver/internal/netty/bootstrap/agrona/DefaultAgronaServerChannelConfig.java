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
package org.kaazing.k3po.driver.internal.netty.bootstrap.agrona;

import static org.kaazing.k3po.driver.internal.netty.bootstrap.agrona.AgronaChannel.NATIVE_BUFFER_FACTORY;

import org.kaazing.k3po.driver.internal.netty.bootstrap.channel.DefaultServerChannelConfig;

public class DefaultAgronaServerChannelConfig extends DefaultServerChannelConfig implements AgronaChannelConfig {

    public DefaultAgronaServerChannelConfig() {
        super();
        setBufferFactory(NATIVE_BUFFER_FACTORY);
    }
}
