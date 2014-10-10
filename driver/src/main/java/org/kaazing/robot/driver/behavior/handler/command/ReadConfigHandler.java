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

package org.kaazing.robot.driver.behavior.handler.command;

import static java.util.Collections.singletonList;
import static java.util.Objects.requireNonNull;

import java.util.List;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.kaazing.robot.driver.behavior.handler.codec.ConfigDecoder;

public class ReadConfigHandler extends AbstractCommandHandler {

    private final List<ConfigDecoder> decoders;

    public ReadConfigHandler(ConfigDecoder decoder) {
        this(singletonList(decoder));
    }

    public ReadConfigHandler(List<ConfigDecoder> decoders) {
        requireNonNull(decoders, "decoders");
        if (decoders.size() == 0) {
            throw new IllegalArgumentException("must have at least one decoder");
        }
        this.decoders = decoders;
    }

    @Override
    protected void invokeCommand(ChannelHandlerContext ctx) throws Exception {
        try {
            Channel channel = ctx.getChannel();
            for (ConfigDecoder decoder : decoders) {
                decoder.decode(channel);
            }
            getHandlerFuture().setSuccess();
        }
        catch (Exception e) {
            getHandlerFuture().setFailure(e);
        }
    }

}
