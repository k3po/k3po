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

package org.kaazing.robot.driver.behavior.handler.event.http;

import static org.jboss.netty.buffer.ChannelBuffers.copiedBuffer;
import static org.jboss.netty.util.CharsetUtil.UTF_8;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.kaazing.robot.driver.behavior.handler.codec.MessageDecoder;
import org.kaazing.robot.driver.behavior.handler.command.AbstractCommandHandler;
import org.kaazing.robot.driver.netty.bootstrap.http.HttpChannelConfig;

public class ReadHttpStatusHandler extends AbstractCommandHandler {

    private final MessageDecoder codeDecoder;
    private final MessageDecoder reasonDecoder;

    public ReadHttpStatusHandler(MessageDecoder codeDecoder, MessageDecoder reasonDecoder) {
        this.codeDecoder = codeDecoder;
        this.reasonDecoder = reasonDecoder;
    }

    @Override
    protected void invokeCommand(ChannelHandlerContext ctx) throws Exception {
        HttpChannelConfig httpConfig = (HttpChannelConfig) ctx.getChannel().getConfig();
        HttpResponseStatus status = httpConfig.getStatus();
        String code = Integer.toString(status.getCode());
        String reason = status.getReasonPhrase();

        try {
            ChannelBuffer buffer = copiedBuffer(code, UTF_8);
            codeDecoder.decode(buffer);
            buffer = copiedBuffer(reason, UTF_8);
            reasonDecoder.decode(buffer);
            getHandlerFuture().setSuccess();
        }
        catch (Exception e) {
            getHandlerFuture().setFailure(e);
        }
    }

}
