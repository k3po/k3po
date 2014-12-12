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

package org.kaazing.k3po.driver.netty.bootstrap.http;

import static org.jboss.netty.channel.Channels.pipeline;

import java.net.URI;
import java.util.NavigableMap;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
import org.jboss.netty.handler.codec.http.HttpResponseEncoder;

final class HttpChildChannelPipelineFactory implements ChannelPipelineFactory {

    private final NavigableMap<URI, HttpServerChannel> httpBindings;

    public HttpChildChannelPipelineFactory(NavigableMap<URI, HttpServerChannel> httpBindings) {
        this.httpBindings = httpBindings;
    }

    @Override
    public ChannelPipeline getPipeline() throws Exception {
        return pipeline(new HttpRequestDecoder(), new HttpResponseEncoder(), new HttpChildChannelSource(httpBindings));
    }

}
