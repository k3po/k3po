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

import static org.jboss.netty.channel.Channels.pipeline;

import java.net.URI;
import java.util.NavigableMap;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.kaazing.k3po.driver.internal.netty.bootstrap.BootstrapFactory;
import org.kaazing.k3po.driver.internal.netty.channel.ChannelAddressFactory;

public class BBoshHandshakeChildChannelPipelineFactory implements ChannelPipelineFactory {

    private final BBoshHandshakeChildChannelSource handshaker;

    public BBoshHandshakeChildChannelPipelineFactory(NavigableMap<URI, BBoshServerChannel> bboshBindings) {
        handshaker = new BBoshHandshakeChildChannelSource(bboshBindings);
    }

    @Override
    public ChannelPipeline getPipeline() throws Exception {
        return pipeline(handshaker);
    }

    public void setAddressFactory(ChannelAddressFactory addressFactory) {
        handshaker.setAddressFactory(addressFactory);
    }

    public void setBootstrapFactory(BootstrapFactory bootstrapFactory) {
        handshaker.setBootstrapFactory(bootstrapFactory);
    }
}
