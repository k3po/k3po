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

package org.kaazing.k3po.driver.behavior.handler.prepare;

import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.kaazing.k3po.driver.netty.channel.SimpleChannelUpstreamHandler;

public abstract class SimplePrepareUpstreamHandler extends SimpleChannelUpstreamHandler {

    @Override
    public final void handleUpstream(ChannelHandlerContext ctx, ChannelEvent evt) throws Exception {

        if (evt instanceof PreparationEvent) {
            prepareRequested(ctx, (PreparationEvent) evt);
        }
        else {
            handleUpstream0(ctx, evt);
        }
    }

    public void prepareRequested(ChannelHandlerContext ctx, PreparationEvent evt) {
        ctx.sendUpstream(evt);
    }

    protected void handleUpstream0(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {

        super.handleUpstream(ctx, e);
    }

}
