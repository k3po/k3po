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

package org.kaazing.k3po.driver.behavior.handler.command;

import static java.lang.String.format;
import static java.util.Collections.singletonList;
import static java.util.Objects.requireNonNull;

import java.util.List;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.kaazing.k3po.driver.behavior.handler.codec.ConfigEncoder;

public class WriteConfigHandler extends AbstractCommandHandler {

    private final List<ConfigEncoder> encoders;

    public WriteConfigHandler(ConfigEncoder encoder) {
        this(singletonList(encoder));
    }

    public WriteConfigHandler(List<ConfigEncoder> encoders) {
        requireNonNull(encoders, "encoders");
        if (encoders.size() == 0) {
            throw new IllegalArgumentException("must have at least one encoder");
        }
        this.encoders = encoders;
    }

    @Override
    protected void invokeCommand(ChannelHandlerContext ctx) throws Exception {
        try {
            Channel channel = ctx.getChannel();
            for (ConfigEncoder encoder : encoders) {
                encoder.encode(channel);
            }
            getHandlerFuture().setSuccess();
        }
        catch (Exception e) {
            getHandlerFuture().setFailure(e);
        }
    }

    @Override
    public String toString() {
        return format("write config %s", encoders);
    }

}
