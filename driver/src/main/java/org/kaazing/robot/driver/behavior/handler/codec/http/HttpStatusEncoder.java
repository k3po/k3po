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

import static java.nio.charset.StandardCharsets.US_ASCII;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.kaazing.robot.driver.behavior.handler.codec.ConfigEncoder;
import org.kaazing.robot.driver.behavior.handler.codec.MessageEncoder;
import org.kaazing.robot.driver.netty.bootstrap.http.HttpChannelConfig;

public class HttpStatusEncoder implements ConfigEncoder {

    private final MessageEncoder codeEncoder;
    private final MessageEncoder reasonEncoder;

    public HttpStatusEncoder(MessageEncoder codeEncoder, MessageEncoder reasonEncoder) {
        this.codeEncoder = codeEncoder;
        this.reasonEncoder = reasonEncoder;
    }

    @Override
    public void encode(Channel channel) throws Exception {
        HttpChannelConfig httpConfig = (HttpChannelConfig) channel.getConfig();
        int code = Integer.parseInt(codeEncoder.encode().toString(US_ASCII));
        String reason = reasonEncoder.encode().toString(US_ASCII);
        HttpResponseStatus status = new HttpResponseStatus(code, reason);
        httpConfig.setStatus(status);
    }

}
