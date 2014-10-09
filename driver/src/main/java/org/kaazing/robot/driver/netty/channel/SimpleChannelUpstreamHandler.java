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

package org.kaazing.robot.driver.netty.channel;

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
}
