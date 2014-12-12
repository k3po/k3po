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

package org.kaazing.k3po.driver.netty.bootstrap.bbosh;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelConfig;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelSink;
import org.jboss.netty.channel.DefaultServerChannelConfig;
import org.kaazing.k3po.driver.netty.bootstrap.channel.AbstractServerChannel;
import org.kaazing.k3po.driver.netty.channel.ChannelAddress;

public class BBoshServerChannel extends AbstractServerChannel<ChannelConfig> {

    BBoshServerChannel(ChannelFactory factory, ChannelPipeline pipeline, ChannelSink sink) {
        super(factory, pipeline, sink, new DefaultServerChannelConfig());
    }

    @Override
    protected void setLocalAddress(ChannelAddress localAddress) {
        super.setLocalAddress(localAddress);
    }

    @Override
    protected void setBound() {
        super.setBound();
    }

    @Override
    protected void setTransport(Channel transport) {
        super.setTransport(transport);
    }

    @Override
    protected Channel getTransport() {
        return super.getTransport();
    }

    @Override
    protected boolean setClosed() {
        return super.setClosed();
    }

}
