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

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.kaazing.k3po.driver.internal.behavior.handler.codec.AbstractConfigDecoder;
import org.kaazing.k3po.driver.internal.behavior.handler.codec.MessageDecoder;
import org.kaazing.k3po.driver.internal.netty.bootstrap.http.HttpChannelConfig;

public class HttpVersionDecoder extends AbstractConfigDecoder {

    private MessageDecoder versionDecoder;

    public HttpVersionDecoder(MessageDecoder versionDecoder) {
        this.versionDecoder = versionDecoder;
    }

    @Override
    public boolean decode(Channel channel) throws Exception {
        HttpChannelConfig httpConfig = (HttpChannelConfig) channel.getConfig();
        HttpVersion version = httpConfig.getVersion();
        ChannelBuffer buffer = copiedBuffer(version.getText(), UTF_8);
        versionDecoder.decode(buffer);
        return true;
    }

    @Override
    public String toString() {
        return format("http:version %s", versionDecoder);
    }

}
