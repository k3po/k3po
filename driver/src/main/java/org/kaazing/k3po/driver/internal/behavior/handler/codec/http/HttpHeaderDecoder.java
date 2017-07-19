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
import static org.jboss.netty.buffer.ChannelBuffers.copiedBuffer;
import static org.jboss.netty.util.CharsetUtil.UTF_8;

import java.util.Iterator;
import java.util.List;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.kaazing.k3po.driver.internal.behavior.ScriptProgressException;
import org.kaazing.k3po.driver.internal.behavior.handler.codec.AbstractConfigDecoder;
import org.kaazing.k3po.driver.internal.behavior.handler.codec.MessageDecoder;
import org.kaazing.k3po.driver.internal.netty.bootstrap.http.HttpChannelConfig;

public class HttpHeaderDecoder extends AbstractConfigDecoder {

    private String name;
    private List<MessageDecoder> valueDecoders;

    public HttpHeaderDecoder(String name, List<MessageDecoder> valueDecoders) {
        this.name = name;
        this.valueDecoders = valueDecoders;
    }

    @Override
    public boolean decode(Channel channel) throws Exception {
        HttpChannelConfig httpConfig = (HttpChannelConfig) channel.getConfig();
        HttpHeaders headers = httpConfig.getReadHeaders();
        List<String> headerValues = headers.getAll(name);
        if (valueDecoders.size() == 1) {
            MessageDecoder valueDecoder = valueDecoders.get(0);
            decodeHeaderValue(headers, headerValues, valueDecoder);
        }
        else {
            for (MessageDecoder valueDecoder : valueDecoders) {
                decodeHeaderValue(headers, headerValues, valueDecoder);
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return format("http:header %s %s", name, valueDecoders);
    }

    private void decodeHeaderValue(HttpHeaders headers,
            List<String> headerValues, MessageDecoder valueDecoder)
            throws Exception {

        int headerValueCount = headerValues.size();
        if (headerValueCount == 0) {
            throw new ScriptProgressException(getRegionInfo(), format("Missing HTTP header: %s", name));
        }
        else if (headerValueCount == 1) {
            // efficiently handle single-valued HTTP header
            String headerValue = headerValues.get(0);
            valueDecoder.decodeLast(copiedBuffer(headerValue, UTF_8));
        }
        else {
            // attempt to match each HTTP header value with decoder
            // throw last decode failure exception if none match
            Exception decodeFailure = null;
            for (Iterator<String> $i = headerValues.iterator(); $i.hasNext();) {
                String headerValue = $i.next();
                try {
                    valueDecoder.decodeLast(copiedBuffer(headerValue, UTF_8));
                    $i.remove();
                    break;
                }
                catch (Exception e) {
                    decodeFailure = e;
                }
            }

            if (headerValues.size() != headerValueCount) {
                headers.set(name, headerValues);
            }
            else {
                assert decodeFailure != null;
                throw decodeFailure;
            }
        }
    }

}
