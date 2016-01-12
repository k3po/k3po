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

import org.jboss.netty.channel.Channel;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.kaazing.k3po.driver.internal.behavior.handler.codec.ConfigEncoder;
import org.kaazing.k3po.driver.internal.behavior.handler.codec.MessageEncoder;
import org.kaazing.k3po.driver.internal.netty.bootstrap.http.HttpChannelConfig;

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

    @Override
    public String toString() {
        return format("http:status %s %s", codeEncoder, reasonEncoder);
    }

}
