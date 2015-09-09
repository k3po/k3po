/*
 * Copyright 2014, Kaazing Corporation. All rights reserved.
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

package org.kaazing.k3po.driver.internal.control.handler;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelDownstreamHandler;
import org.kaazing.k3po.driver.internal.control.ControlMessage;

public class ControlDownstreamHandler extends SimpleChannelDownstreamHandler {

    @Override
    public void writeRequested(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        ControlMessage message = (ControlMessage) e.getMessage();

        switch (message.getKind()) {
        case PREPARED:
            writePreparedRequested(ctx, e);
            break;
        case STARTED:
            writeStartedRequested(ctx, e);
            break;
        case FINISHED:
            writeFinishedRequested(ctx, e);
            break;
        case ERROR:
            writeErrorRequested(ctx, e);
            break;
        case NOTIFY:
            writeNotifyRequested(ctx, e);
        default:
            throw new IllegalArgumentException(String.format("Unexpected control message: %s", message.getKind()));
        }

    }

    public void writePreparedRequested(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        super.writeRequested(ctx, e);
    }

    public void writeStartedRequested(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        super.writeRequested(ctx, e);
    }

    public void writeFinishedRequested(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        super.writeRequested(ctx, e);
    }

    public void writeErrorRequested(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        super.writeRequested(ctx, e);
    }

    public void writeNotifyRequested(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        super.writeRequested(ctx, e);
    }
}
