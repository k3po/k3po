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

package org.kaazing.robot.driver.behavior.handler.codec.http;

import static java.lang.String.format;
import static org.jboss.netty.buffer.ChannelBuffers.copiedBuffer;
import static org.jboss.netty.util.CharsetUtil.UTF_8;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.kaazing.robot.driver.behavior.handler.codec.ConfigDecoder;
import org.kaazing.robot.driver.behavior.handler.codec.MessageDecoder;
import org.kaazing.robot.driver.netty.bootstrap.http.HttpChannelConfig;

public class HttpStatusDecoder implements ConfigDecoder {

    private final MessageDecoder codeDecoder;
    private final MessageDecoder reasonDecoder;

    public HttpStatusDecoder(MessageDecoder codeDecoder, MessageDecoder reasonDecoder) {
        this.codeDecoder = codeDecoder;
        this.reasonDecoder = reasonDecoder;
    }

    @Override
    public void decode(Channel channel) throws Exception {
        HttpChannelConfig httpConfig = (HttpChannelConfig) channel.getConfig();
        HttpResponseStatus status = httpConfig.getStatus();
        String code = Integer.toString(status.getCode());
        String reason = status.getReasonPhrase();

        ChannelBuffer buffer = copiedBuffer(code, UTF_8);
        codeDecoder.decode(buffer);
        buffer = copiedBuffer(reason, UTF_8);
        reasonDecoder.decode(buffer);
    }

    @Override
    public String toString() {
        return format("http:status %s %s", codeDecoder, reasonDecoder);
    }

}
