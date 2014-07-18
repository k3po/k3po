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

package org.kaazing.robot.behavior.handler.command.http;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpMessage;

import org.kaazing.robot.behavior.handler.command.AbstractCommandHandler;

public class WriteHttpContentLengthHandler extends AbstractCommandHandler {

    private HttpMessage httpMessage;

    public WriteHttpContentLengthHandler(HttpMessage httpMessage) {
        this.httpMessage = httpMessage;
    }

    @Override
    protected void invokeCommand(ChannelHandlerContext ctx) throws Exception {
        HttpHeaders.setContentLength(httpMessage, 0);
        getHandlerFuture().setSuccess();
    }
}
