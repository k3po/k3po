/**
 * Copyright 2007-2015, Kaazing Corporation. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kaazing.k3po.driver.internal.behavior.handler.codec.http;

import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.US_ASCII;
import static org.kaazing.k3po.driver.internal.channel.Channels.remoteAddress;

import java.net.URI;
import java.util.List;

import org.jboss.netty.buffer.ChannelBufferFactory;
import org.jboss.netty.channel.Channel;
import org.kaazing.k3po.driver.internal.behavior.handler.codec.ChannelEncoder;
import org.kaazing.k3po.driver.internal.behavior.handler.codec.MessageEncoder;
import org.kaazing.k3po.driver.internal.netty.bootstrap.http.HttpChannelConfig;
import org.kaazing.k3po.driver.internal.netty.channel.ChannelAddress;

public class HttpParameterEncoder implements ChannelEncoder {

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

        ChannelBufferFactory bufferFactory = httpConfig.getBufferFactory();
        String paramName = nameEncoder.encode(bufferFactory).toString(US_ASCII);
        for (MessageEncoder valueEncoder : valueEncoders) {
            String paramValue = valueEncoder.encode(bufferFactory).toString(US_ASCII);
            query.addParam(paramName, paramValue);
        }
    }

    @Override
    public String toString() {
        return format("http:parameter %s %s", nameEncoder, valueEncoders);
    }

}
