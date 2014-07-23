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

import static org.jboss.netty.buffer.ChannelBuffers.copiedBuffer;
import static org.jboss.netty.util.CharsetUtil.UTF_8;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.handler.codec.http.HttpMessage;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;

import org.kaazing.robot.driver.behavior.handler.codec.MessageDecoder;
import org.kaazing.robot.driver.behavior.handler.codec.MessageMismatchException;

public class ReadHttpMethodDecoder implements HttpMessageContributingDecoder {

    private MessageDecoder methodValueDecoder;

    public ReadHttpMethodDecoder(MessageDecoder methodValueDecoder) {
        this.methodValueDecoder = methodValueDecoder;
    }

    @Override
    public void decode(HttpMessage message) throws Exception {
        HttpRequest request = null;
        if (message instanceof HttpRequest) {
            request = (HttpRequest) message;
        } else {
            throw new MessageMismatchException("Can not match a request method on a http response", methodValueDecoder,
                    null);
        }
        HttpMethod method = request.getMethod();
        ChannelBuffer buffer = copiedBuffer(method.getName(), UTF_8);
        methodValueDecoder.decode(buffer);
    }

    @Override
    public String toString() {
        return String.format("read http method decoder with: %s", methodValueDecoder);
    }

}
