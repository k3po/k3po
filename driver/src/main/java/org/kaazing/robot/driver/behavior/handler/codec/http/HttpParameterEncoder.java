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
import static java.nio.charset.StandardCharsets.US_ASCII;
import static org.kaazing.robot.driver.channel.Channels.remoteAddress;

import java.net.URI;
import java.util.List;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.handler.codec.http.QueryStringEncoder;
import org.kaazing.robot.driver.behavior.handler.codec.ConfigEncoder;
import org.kaazing.robot.driver.behavior.handler.codec.MessageEncoder;
import org.kaazing.robot.driver.netty.bootstrap.http.HttpChannelConfig;
import org.kaazing.robot.driver.netty.channel.ChannelAddress;

public class HttpParameterEncoder implements ConfigEncoder {

    private final MessageEncoder nameEncoder;
    private final List<MessageEncoder> valueEncoders;

    public HttpParameterEncoder(MessageEncoder nameEncoder, List<MessageEncoder> valueEncoders) {
        this.nameEncoder = nameEncoder;
        this.valueEncoders = valueEncoders;
    }

    @Override
    public void encode(Channel channel) throws Exception {
        HttpChannelConfig httpConfig = (HttpChannelConfig) channel.getConfig();
        QueryStringEncoder query = httpConfig.getWriteQuery();
        if (query == null) {
            ChannelAddress remoteAddress = remoteAddress(channel);
            URI httpRemoteURI = remoteAddress.getLocation();
            query = new QueryStringEncoder(httpRemoteURI.toString());
            httpConfig.setWriteQuery(query);
        }

        String paramName = nameEncoder.encode().toString(US_ASCII);
        for (MessageEncoder valueEncoder : valueEncoders) {
            String paramValue = valueEncoder.encode().toString(US_ASCII);
            query.addParam(paramName, paramValue);
        }
    }

    @Override
    public String toString() {
        return format("http:parameter %s %s", nameEncoder, valueEncoders);
    }

}
