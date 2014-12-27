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

package org.kaazing.k3po.driver.behavior.handler.codec.http;

import java.net.URI;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.QueryStringEncoder;
import org.kaazing.k3po.driver.behavior.handler.codec.ConfigEncoder;
import org.kaazing.k3po.driver.netty.bootstrap.channel.AbstractChannel;
import org.kaazing.k3po.driver.netty.bootstrap.http.HttpChannelConfig;
import org.kaazing.k3po.driver.netty.channel.ChannelAddress;

public class HttpHostEncoder implements ConfigEncoder {

    @Override
    @SuppressWarnings("unchecked")
    public void encode(Channel channel) throws Exception {
        AbstractChannel<HttpChannelConfig> httpChannel = (AbstractChannel<HttpChannelConfig>) channel;
        HttpChannelConfig httpConfig = (HttpChannelConfig) httpChannel.getConfig();
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
