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

import org.jboss.netty.buffer.ChannelBufferFactory;
import org.jboss.netty.channel.Channel;
import org.kaazing.k3po.driver.internal.behavior.handler.codec.ChannelEncoder;
import org.kaazing.k3po.driver.internal.behavior.handler.codec.MessageEncoder;
import org.kaazing.k3po.driver.internal.netty.bootstrap.http.HttpChannelConfig;
import org.kaazing.k3po.driver.internal.netty.bootstrap.http.HttpRequestForm;

public class HttpRequestFormEncoder implements ChannelEncoder {

    private MessageEncoder formEncoder;

    public HttpRequestFormEncoder(MessageEncoder formEncoder) {
        this.formEncoder = formEncoder;
    }

    @Override
    public void encode(Channel channel) throws Exception {
        HttpChannelConfig httpConfig = (HttpChannelConfig) channel.getConfig();
        ChannelBufferFactory bufferFactory = httpConfig.getBufferFactory();
        String formName = formEncoder.encode(bufferFactory).toString(US_ASCII);
        HttpRequestForm form = HttpRequestForm.valueOf(formName);
        httpConfig.setRequestForm(form);
    }

    @Override
    public String toString() {
        return format("http:request %s", formEncoder);
    }

}
