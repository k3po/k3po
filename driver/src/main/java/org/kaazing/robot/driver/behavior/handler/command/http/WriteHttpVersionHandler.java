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

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.kaazing.robot.driver.behavior.handler.codec.MessageEncoder;
import org.kaazing.robot.driver.behavior.handler.command.AbstractCommandHandler;
import org.kaazing.robot.driver.netty.bootstrap.http.HttpChannelConfig;

public class WriteHttpVersionHandler extends AbstractCommandHandler {

    private MessageEncoder versionEncoder;

    public WriteHttpVersionHandler(MessageEncoder versionEncoder) {
        this.versionEncoder = versionEncoder;
    }

    @Override
    protected void invokeCommand(ChannelHandlerContext ctx) {
        HttpChannelConfig httpConfig = (HttpChannelConfig) ctx.getChannel().getConfig();
        String versionName = versionEncoder.encode().toString(US_ASCII);
        HttpVersion version = HttpVersion.valueOf(versionName);
        httpConfig.setVersion(version);
    }

}
