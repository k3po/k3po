/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.behavior.handler.codec;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelUpstreamHandler;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.handler.codec.http.HttpChunk;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;

public interface HttpChannelUpstreamHandler extends ChannelUpstreamHandler {

    void httpRequestReceived(ChannelHandlerContext ctx, MessageEvent e, HttpRequest httpRequest) throws Exception;

    void httpResponseReceived(ChannelHandlerContext ctx, MessageEvent e, HttpResponse httpResponse) throws Exception;

    void httpChunkReceived(ChannelHandlerContext ctx, MessageEvent e, HttpChunk chunk) throws Exception;

    void httpContentReceived(ChannelHandlerContext ctx, MessageEvent e, ChannelBuffer httpContent) throws Exception;

    void httpEndOfContentReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception;
}
