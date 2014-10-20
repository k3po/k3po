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

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ServerChannelFactory;
import org.kaazing.robot.driver.netty.bootstrap.BootstrapFactory;
import org.kaazing.robot.driver.netty.channel.ChannelAddressFactory;

public class BBoshServerChannelFactory implements ServerChannelFactory {

    private final BBoshServerChannelSink channelSink;

    public BBoshServerChannelFactory(BBoshServerChannelSink channelSink) {
        this.channelSink = channelSink;
    }

    public void setAddressFactory(ChannelAddressFactory addressFactory) {
        channelSink.setAddressFactory(addressFactory);
    }

    public void setBootstrapFactory(BootstrapFactory bootstrapFactory) {
        channelSink.setBootstrapFactory(bootstrapFactory);
    }

    @Override
    public BBoshServerChannel newChannel(ChannelPipeline pipeline) {
        return new BBoshServerChannel(this, pipeline, channelSink);
    }

    @Override
    public void shutdown() {
    }

    @Override
    public void releaseExternalResources() {
    }

}
