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

import java.util.Iterator;
import java.util.List;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.kaazing.robot.driver.behavior.ScriptProgressException;
import org.kaazing.robot.driver.behavior.handler.codec.AbstractConfigDecoder;
import org.kaazing.robot.driver.behavior.handler.codec.MessageDecoder;
import org.kaazing.robot.driver.netty.bootstrap.http.HttpChannelConfig;

public class HttpHeaderDecoder extends AbstractConfigDecoder {

    private String name;
    private List<MessageDecoder> valueDecoders;

    public HttpHeaderDecoder(String name, List<MessageDecoder> valueDecoders) {
        this.name = name;
        this.valueDecoders = valueDecoders;
    }

    @Override
    public void decode(Channel channel) throws Exception {
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
