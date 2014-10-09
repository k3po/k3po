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

package org.kaazing.robot.driver.behavior.handler.command.http;

import static java.nio.charset.StandardCharsets.US_ASCII;
import static org.kaazing.robot.driver.channel.Channels.remoteAddress;

import java.net.URI;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.http.QueryStringEncoder;
import org.kaazing.robot.driver.behavior.handler.codec.MessageEncoder;
import org.kaazing.robot.driver.behavior.handler.command.AbstractCommandHandler;
import org.kaazing.robot.driver.netty.bootstrap.http.HttpChannelConfig;
import org.kaazing.robot.driver.netty.channel.ChannelAddress;

public class WriteHttpParameterHandler extends AbstractCommandHandler {

    private final MessageEncoder nameEncoder;
    private final MessageEncoder valueEncoder;

    public WriteHttpParameterHandler(MessageEncoder nameEncoder, MessageEncoder valueEncoder) {
        this.nameEncoder = nameEncoder;
        this.valueEncoder = valueEncoder;
    }

    @Override
    protected void invokeCommand(ChannelHandlerContext ctx) {
        HttpChannelConfig httpConfig = (HttpChannelConfig) ctx.getChannel().getConfig();
        String paramName = nameEncoder.encode().toString(US_ASCII);
        String paramValue = valueEncoder.encode().toString(US_ASCII);

        QueryStringEncoder query = httpConfig.getWriteQuery();
        if (query == null) {
            ChannelAddress remoteAddress = remoteAddress(ctx.getChannel());
            URI httpRemoteURI = remoteAddress.getLocation();
            query = new QueryStringEncoder(httpRemoteURI.toString());
        }
        query.addParam(paramName, paramValue);
        getHandlerFuture().setSuccess();
    }

}
