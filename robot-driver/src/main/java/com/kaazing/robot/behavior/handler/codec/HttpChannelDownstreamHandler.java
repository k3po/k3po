/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package com.kaazing.robot.behavior.handler.codec;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelDownstreamHandler;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.handler.codec.http.HttpChunk;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;

public interface HttpChannelDownstreamHandler extends ChannelDownstreamHandler {

    void writeHttpRequest(ChannelHandlerContext ctx, MessageEvent e, HttpRequest httpRequest) throws Exception;

    void writeHttpResponse(ChannelHandlerContext ctx, MessageEvent e, HttpResponse httpResponse) throws Exception;

    void writeHttpChunk(ChannelHandlerContext ctx, MessageEvent e, HttpChunk chunk) throws Exception;

    void writeHttpContent(ChannelHandlerContext ctx, MessageEvent e, ChannelBuffer httpContent) throws Exception;

    void writeHttpEndOfContent(ChannelHandlerContext ctx, MessageEvent e) throws Exception;
}
