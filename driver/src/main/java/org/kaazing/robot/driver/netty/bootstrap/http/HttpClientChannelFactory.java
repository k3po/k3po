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

package org.kaazing.robot.driver.netty.bootstrap.http;

import static org.jboss.netty.handler.codec.http.HttpMethod.POST;
import static org.jboss.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelPipeline;

public class HttpClientChannelFactory implements ChannelFactory {

    private final HttpClientChannelSinkFactory channelSinkFactory;

    public HttpClientChannelFactory(HttpClientChannelSinkFactory channelSinkFactory) {
        this.channelSinkFactory = channelSinkFactory;
    }

    @Override
    public HttpClientChannel newChannel(ChannelPipeline pipeline) {
        HttpClientChannel httpChannel = new HttpClientChannel(this, pipeline, channelSinkFactory.newChannelSink());

        // default configuration
        HttpChannelConfig httpChannelConfig = httpChannel.getConfig();
        httpChannelConfig.setMethod(POST);
        httpChannelConfig.setVersion(HTTP_1_1);

        // see HttpClientChannelSource for httpChannel.setReadable(true)
        httpChannel.setReadable(false);

        return httpChannel;
    }

    @Override
    public void shutdown() {
    }

    @Override
    public void releaseExternalResources() {
    }

}
