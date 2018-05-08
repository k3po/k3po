/**
 * Copyright 2007-2015, Kaazing Corporation. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kaazing.k3po.driver.internal.netty.bootstrap.http;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.handler.codec.http.HttpChunk;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.kaazing.k3po.driver.internal.netty.channel.SimpleChannelHandler;


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
