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

import org.jboss.netty.handler.codec.http.HttpMessage;

import org.kaazing.robot.behavior.handler.codec.MessageEncoder;

public class WriteHttpHeaderEncoder implements HttpMessageContributingEncoder {

    private final MessageEncoder nameEncoder;
    private final MessageEncoder valueEncoder;

    public WriteHttpHeaderEncoder(MessageEncoder nameEncoder, MessageEncoder valueEncoder) {
        this.nameEncoder = nameEncoder;
        this.valueEncoder = valueEncoder;
    }

    @Override
    public void encode(HttpMessage message) {
        message.addHeader(nameEncoder.encodeToString(), valueEncoder.encodeToString());
    }

    @Override
    public String toString() {
        return String.format("write http header encoder with %s %s", nameEncoder, valueEncoder);
    }
}
