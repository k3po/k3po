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

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.handler.codec.http.HttpChunk;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;

public class HttpChannelHandler extends SimpleChannelHandler {

    @Override
    public final void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        Object message = e.getMessage();
        if (message instanceof HttpRequest) {
            httpMessageReceived(ctx, e, (HttpRequest) message);
        }
        else if (message instanceof HttpResponse) {
            httpMessageReceived(ctx, e, (HttpResponse) message);
        }
        else if (message instanceof HttpChunk) {
            httpMessageReceived(ctx, e, (HttpChunk) message);
        }
        else if (message instanceof ChannelBuffer) {
            httpMessageReceived(ctx, e, (ChannelBuffer) message);
        }
        else {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public void writeRequested(ChannelHandlerContext ctx, MessageEvent e)
            throws Exception {
        Object message = e.getMessage();
        if (message instanceof HttpRequest) {
            httpWriteRequested(ctx, e, (HttpRequest) message);
        }
        else if (message instanceof HttpResponse) {
            httpWriteRequested(ctx, e, (HttpResponse) message);
        }
        else if (message instanceof HttpChunk) {
            httpWriteRequested(ctx, e, (HttpChunk) message);
        }
        else if (message instanceof ChannelBuffer) {
            httpWriteRequested(ctx, e, (ChannelBuffer) message);
        }
        else {
            throw new IllegalArgumentException();
        }
    }

    protected void httpMessageReceived(ChannelHandlerContext ctx, MessageEvent e, HttpRequest message) throws Exception {
        super.messageReceived(ctx, e);
    }

    protected void httpMessageReceived(ChannelHandlerContext ctx, MessageEvent e, HttpResponse message) throws Exception {
        super.messageReceived(ctx, e);
    }

    protected void httpMessageReceived(ChannelHandlerContext ctx, MessageEvent e, HttpChunk message) throws Exception {
        super.messageReceived(ctx, e);
    }

    protected void httpMessageReceived(ChannelHandlerContext ctx, MessageEvent e, ChannelBuffer message) throws Exception {
        super.messageReceived(ctx, e);
    }

    protected void httpWriteRequested(ChannelHandlerContext ctx, MessageEvent e, HttpRequest message) throws Exception {
        super.writeRequested(ctx, e);
    }

    protected void httpWriteRequested(ChannelHandlerContext ctx, MessageEvent e, HttpResponse message) throws Exception {
        super.writeRequested(ctx, e);
    }

    protected void httpWriteRequested(ChannelHandlerContext ctx, MessageEvent e, HttpChunk message) throws Exception {
        super.writeRequested(ctx, e);
    }

    protected void httpWriteRequested(ChannelHandlerContext ctx, MessageEvent e, ChannelBuffer message) throws Exception {
        super.writeRequested(ctx, e);
    }

}
