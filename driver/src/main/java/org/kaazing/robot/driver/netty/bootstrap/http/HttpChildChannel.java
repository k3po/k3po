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

import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelSink;
import org.jboss.netty.channel.ServerChannel;
import org.kaazing.robot.driver.netty.bootstrap.channel.AbstractChannel;

public class HttpChildChannel extends AbstractChannel<HttpChannelConfig> {

    public enum HttpState { RESPONSE, CONTENT_CHUNKED, CONTENT_CLOSE, CONTENT_BUFFERED, CONTENT_COMPLETE, UPGRADED }

    private HttpState state;

    HttpChildChannel(ServerChannel parent, ChannelFactory factory, ChannelPipeline pipeline, ChannelSink sink) {
        super(parent, factory, pipeline, sink, new DefaultHttpChannelConfig());
        this.state = HttpState.RESPONSE;
    }

    public HttpState state() {
        return state;
    }

    public void state(HttpState state) {
        this.state = state;
    }

    @Override
    protected boolean setClosed() {
        return super.setClosed();
    }
}
