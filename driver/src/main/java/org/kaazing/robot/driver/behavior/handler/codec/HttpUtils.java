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

package org.kaazing.robot.driver.behavior.handler.codec;

import java.net.URI;

import org.jboss.netty.buffer.BigEndianHeapChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;

public final class HttpUtils {

    public static final ChannelBuffer END_OF_HTTP_MESSAGE_BUFFER = new BigEndianHeapChannelBuffer(0);
    public static final String HTTP_CODEC_NAME = "http.codec.httpDefaultCodec";
    public static final String HTTP_MESSAGE_AGGREGATING_CODEC_NAME = "http.codec.httpMessageAggregatingCodec";
    public static final String HTTP_MEESAGE_SPLITTING_CODEC_NAME = "http.codec.httpMessageSplittingCodec";

    private HttpUtils() {

    }

    public static boolean isOneOOneResponseMessage(HttpResponse httpResponse) {
        if (httpResponse.getStatus().equals(HttpResponseStatus.SWITCHING_PROTOCOLS)) {
            return true;
        }
        return false;
    }

    public static void removeHttpFiltersFromPipeline(ChannelPipeline channelPipeline) {
        for (String name : channelPipeline.getNames()) {
            if (name.startsWith("http.codec")) {
                channelPipeline.remove(name);
            }
        }
    }

    public static boolean isUriHttp(URI uri) {
        return uri.getScheme().equals("http");
    }
}
