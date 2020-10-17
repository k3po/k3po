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

import java.net.URI;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.kaazing.k3po.driver.internal.behavior.handler.codec.ChannelEncoder;
import org.kaazing.k3po.driver.internal.netty.bootstrap.channel.AbstractChannel;
import org.kaazing.k3po.driver.internal.netty.bootstrap.http.HttpChannelConfig;
import org.kaazing.k3po.driver.internal.netty.channel.ChannelAddress;

public class HttpHostEncoder implements ChannelEncoder {

    @Override
    @SuppressWarnings("unchecked")
    public void encode(Channel channel) throws Exception {
        AbstractChannel<HttpChannelConfig> httpChannel = (AbstractChannel<HttpChannelConfig>) channel;
        HttpChannelConfig httpConfig = httpChannel.getConfig();
        QueryStringEncoder query = httpConfig.getWriteQuery();
        ChannelAddress httpRemoteAddress = httpChannel.getRemoteAddress();
        URI httpRemoteURI = query != null ? query.toUri() : httpRemoteAddress.getLocation();
        String authority = httpRemoteURI.getAuthority();
        HttpHeaders writeHeaders = httpConfig.getWriteHeaders();
        writeHeaders.set(HttpHeaders.Names.HOST, authority);
    }

    @Override
    public String toString() {
        return "http:header host";
    }
}
