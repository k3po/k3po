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

package org.kaazing.robot.behavior.handler.codec;

import static org.kaazing.robot.behavior.handler.codec.HttpUtils.END_OF_HTTP_MESSAGE_BUFFER;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelDownstreamHandler;
import org.jboss.netty.handler.codec.http.HttpChunk;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;


public class SimpleHttpChannelDownstreamHandler extends SimpleChannelDownstreamHandler implements
        HttpChannelDownstreamHandler {

    @Override
    public void writeRequested(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        Object message = e.getMessage();
        if (message instanceof HttpRequest) {
            writeHttpRequest(ctx, e, (HttpRequest) message);
        } else if (message instanceof HttpResponse) {
            writeHttpResponse(ctx, e, (HttpResponse) message);
        } else if (message instanceof HttpChunk) {
            writeHttpChunk(ctx, e, (HttpChunk) message);
        } else if (message instanceof ChannelBuffer) {
            if (message == END_OF_HTTP_MESSAGE_BUFFER) {
                writeHttpEndOfContent(ctx, e);
            } else {
                writeHttpContent(ctx, e, (ChannelBuffer) message);
            }
        } else {
            super.writeRequested(ctx, e);
        }
    }

    @Override
    public void writeHttpRequest(ChannelHandlerContext ctx, MessageEvent e, HttpRequest httpRequest) throws Exception {
        super.writeRequested(ctx, e);
    }

    @Override
    public void writeHttpResponse(ChannelHandlerContext ctx, MessageEvent e, HttpResponse httpResponse)
            throws Exception {
        super.writeRequested(ctx, e);
    }

    @Override
    public void writeHttpChunk(ChannelHandlerContext ctx, MessageEvent e, HttpChunk chunk) throws Exception {
        super.writeRequested(ctx, e);
    }

    @Override
    public void writeHttpContent(ChannelHandlerContext ctx, MessageEvent e, ChannelBuffer httpContent) throws Exception {
        super.writeRequested(ctx, e);
    }

    @Override
    public void writeHttpEndOfContent(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        super.writeRequested(ctx, e);
    }
}
