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

package org.kaazing.k3po.driver.behavior.handler;

import static org.jboss.netty.channel.Channels.succeededFuture;
import static org.kaazing.k3po.driver.netty.channel.ChannelFutureListeners.chainedFuture;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.kaazing.k3po.driver.behavior.handler.prepare.DownstreamPreparationEvent;
import org.kaazing.k3po.driver.behavior.handler.prepare.PreparationEvent;

public class CompletionHandler extends ExecutionHandler {

    @Override
    public void prepareRequested(final ChannelHandlerContext ctx, final PreparationEvent evt) {

        super.prepareRequested(ctx, evt);

        // when the pipeline future completes, trigger success of this handler
        // future
        ChannelFuture pipelineFuture = getPipelineFuture();
        ChannelFuture handlerFuture = getHandlerFuture();

        pipelineFuture.addListener(chainedFuture(handlerFuture));

        ChannelFuture prepareFuture = evt.getFuture();
        prepareFuture.setSuccess();

        Channel channel = evt.getChannel();
        ctx.sendDownstream(new DownstreamPreparationEvent(channel, succeededFuture(channel)));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
        // ignore (already tracking completion status via completion future cause)
    }

    @Override
    public String toString() {
        return "complete";
    }
}
