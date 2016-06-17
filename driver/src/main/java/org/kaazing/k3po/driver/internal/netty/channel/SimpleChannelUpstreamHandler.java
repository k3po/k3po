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

public class SimpleChannelUpstreamHandler extends org.jboss.netty.channel.SimpleChannelUpstreamHandler {

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
        else if (e instanceof AbortEvent) {
            aborted(ctx, (AbortEvent) e);
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

    public void aborted(ChannelHandlerContext ctx, AbortEvent e) {
        ctx.sendUpstream(e);
    }
}
