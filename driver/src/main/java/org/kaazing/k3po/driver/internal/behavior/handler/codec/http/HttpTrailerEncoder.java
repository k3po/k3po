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

import java.util.List;

import org.jboss.netty.buffer.ChannelBufferFactory;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.kaazing.k3po.driver.internal.behavior.handler.codec.ChannelEncoder;
import org.kaazing.k3po.driver.internal.behavior.handler.codec.MessageEncoder;
import org.kaazing.k3po.driver.internal.netty.bootstrap.http.HttpChannelConfig;

public class HttpTrailerEncoder implements ChannelEncoder {

    private final MessageEncoder nameEncoder;
    private final List<MessageEncoder> valueEncoders;

    public HttpTrailerEncoder(MessageEncoder nameEncoder, List<MessageEncoder> valueEncoders) {
        this.nameEncoder = nameEncoder;
        this.valueEncoders = valueEncoders;
    }

    @Override
    public void encode(Channel channel) throws Exception {
        HttpChannelConfig httpConfig = (HttpChannelConfig) channel.getConfig();
        HttpHeaders writeTrailers = httpConfig.getWriteTrailers();
        ChannelBufferFactory bufferFactory = httpConfig.getBufferFactory();

        String headerName = nameEncoder.encode(bufferFactory).toString(US_ASCII);
        if (valueEncoders.size() == 1) {
            MessageEncoder valueEncoder = valueEncoders.get(0);
            String headerValue = valueEncoder.encode(bufferFactory).toString(US_ASCII);
            writeTrailers.add(headerName, headerValue);
        } else {
            for (MessageEncoder valueEncoder : valueEncoders) {
                String headerValue = valueEncoder.encode(bufferFactory).toString(US_ASCII);
                writeTrailers.add(headerName, headerValue);
            }
        }
    }

    @Override
    public String toString() {
        return format("http:trailer %s %s", nameEncoder, valueEncoders);
    }

}
