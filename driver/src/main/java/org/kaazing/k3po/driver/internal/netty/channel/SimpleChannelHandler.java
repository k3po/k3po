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
package org.kaazing.k3po.driver.internal.netty.channel;

import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;

public class SimpleChannelHandler extends org.jboss.netty.channel.SimpleChannelHandler {

    @Override
    public void handleUpstream(ChannelHandlerContext ctx, ChannelEvent e)
            throws Exception {
        if (e instanceof ShutdownInputEvent) {
            inputShutdown(ctx, (ShutdownInputEvent) e);
        }
        else if (e instanceof ShutdownOutputEvent) {
            outputShutdown(ctx, (ShutdownOutputEvent) e);
        }
        else if (e instanceof FlushEvent) {
            flushed(ctx, (FlushEvent) e);
        }
        else if (e instanceof ReadAbortEvent) {
            inputAborted(ctx, (ReadAbortEvent) e);
        }
        else if (e instanceof WriteAbortEvent) {
            outputAborted(ctx, (WriteAbortEvent) e);
        }
        else if (e instanceof ReadAdviseEvent) {
            inputAdvised(ctx, (ReadAdviseEvent) e);
        }
        else if (e instanceof WriteAdviseEvent) {
            outputAdvised(ctx, (WriteAdviseEvent) e);
        }
        else {
            super.handleUpstream(ctx, e);
        }
    }

    public void inputShutdown(ChannelHandlerContext ctx, ShutdownInputEvent e) {
        ctx.sendUpstream(e);
    }

    public void outputShutdown(ChannelHandlerContext ctx, ShutdownOutputEvent e) {
        ctx.sendUpstream(e);
    }

    public void flushed(ChannelHandlerContext ctx, FlushEvent e) {
        ctx.sendUpstream(e);
    }

    public void inputAborted(ChannelHandlerContext ctx, ReadAbortEvent e) {
        ctx.sendUpstream(e);
    }

    public void outputAborted(ChannelHandlerContext ctx, WriteAbortEvent e) {
        ctx.sendUpstream(e);
    }

    public void inputAdvised(ChannelHandlerContext ctx, ReadAdviseEvent e) {
        ctx.sendUpstream(e);
    }

    public void outputAdvised(ChannelHandlerContext ctx, WriteAdviseEvent e) {
        ctx.sendUpstream(e);
    }

    @Override
    public void handleDownstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
        if (e instanceof ShutdownInputEvent) {
            shutdownInputRequested(ctx, (ShutdownInputEvent) e);
        }
        else if (e instanceof ShutdownOutputEvent) {
            shutdownOutputRequested(ctx, (ShutdownOutputEvent) e);
        }
        else if (e instanceof FlushEvent) {
            flushRequested(ctx, (FlushEvent) e);
        }
        else if (e instanceof ReadAbortEvent) {
            abortInputRequested(ctx, (ReadAbortEvent) e);
        }
        else if (e instanceof WriteAbortEvent) {
            abortOutputRequested(ctx, (WriteAbortEvent) e);
        }
        else if (e instanceof ReadAdviseEvent) {
            adviseInputRequested(ctx, (ReadAdviseEvent) e);
        }
        else if (e instanceof WriteAdviseEvent) {
            adviseOutputRequested(ctx, (WriteAdviseEvent) e);
        }
        else {
            super.handleDownstream(ctx, e);
        }
    }

    public void flushRequested(ChannelHandlerContext ctx, FlushEvent e) {
        ctx.sendDownstream(e);
    }

    public void shutdownInputRequested(ChannelHandlerContext ctx, ShutdownInputEvent e) {
        ctx.sendDownstream(e);
    }

    public void shutdownOutputRequested(ChannelHandlerContext ctx, ShutdownOutputEvent e) {
        ctx.sendDownstream(e);
    }

    public void abortInputRequested(ChannelHandlerContext ctx, ReadAbortEvent e) {
        ctx.sendDownstream(e);
    }

    public void abortOutputRequested(ChannelHandlerContext ctx, WriteAbortEvent e) {
        ctx.sendDownstream(e);
    }

    public void adviseInputRequested(ChannelHandlerContext ctx, ReadAdviseEvent e) {
        ctx.sendDownstream(e);
    }

    public void adviseOutputRequested(ChannelHandlerContext ctx, WriteAdviseEvent e) {
        ctx.sendDownstream(e);
    }

}
