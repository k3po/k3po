/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package com.kaazing.robot.behavior.handler.codec;

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
