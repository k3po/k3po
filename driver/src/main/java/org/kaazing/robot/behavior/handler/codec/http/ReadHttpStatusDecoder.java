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

package org.kaazing.robot.behavior.handler.codec.http;

import static org.jboss.netty.buffer.ChannelBuffers.copiedBuffer;
import static org.jboss.netty.util.CharsetUtil.UTF_8;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.handler.codec.http.HttpMessage;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;

import org.kaazing.robot.behavior.handler.codec.MessageDecoder;
import org.kaazing.robot.behavior.handler.codec.MessageMismatchException;

public class ReadHttpStatusDecoder implements HttpMessageContributingDecoder {

    private final MessageDecoder codeDecoder;
    private final MessageDecoder reasonDecoder;

    public ReadHttpStatusDecoder(MessageDecoder codeDecoder, MessageDecoder reasonDecoder) {
        this.codeDecoder = codeDecoder;
        this.reasonDecoder = reasonDecoder;
    }

    @Override
    public void decode(HttpMessage message) throws Exception {
        HttpResponse response;
        if (message instanceof HttpResponse) {
            response = (HttpResponse) message;

        } else {
            throw new MessageMismatchException("Can not match a http status on a http request", codeDecoder + ","
                    + reasonDecoder, null);
        }

        HttpResponseStatus status = response.getStatus();
        String code = Integer.toString(status.getCode());
        String reason = status.getReasonPhrase();

        ChannelBuffer buffer = copiedBuffer(code, UTF_8);
        codeDecoder.decode(buffer);
        buffer = copiedBuffer(reason, UTF_8);
        reasonDecoder.decode(buffer);
    }

    @Override
    public String toString() {
        return String.format("read http status decoder with: %s, %s", codeDecoder, reasonDecoder);
    }
}
