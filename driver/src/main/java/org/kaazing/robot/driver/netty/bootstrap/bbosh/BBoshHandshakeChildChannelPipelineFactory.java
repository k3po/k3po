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

package org.kaazing.robot.driver.netty.bootstrap.bbosh;

import static org.jboss.netty.channel.Channels.pipeline;

import java.net.URI;
import java.util.NavigableMap;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.kaazing.robot.driver.netty.bootstrap.BootstrapFactory;
import org.kaazing.robot.driver.netty.channel.ChannelAddressFactory;

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
