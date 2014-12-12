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

package org.kaazing.k3po.driver.behavior.handler.barrier;

import static java.lang.String.format;
import static org.kaazing.k3po.driver.netty.channel.ChannelFutureListeners.chainedFuture;

import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.kaazing.k3po.driver.behavior.Barrier;
import org.kaazing.k3po.driver.behavior.handler.prepare.PreparationEvent;

public class NotifyBarrierHandler extends AbstractBarrierHandler {

    public NotifyBarrierHandler(Barrier barrier) {
        super(barrier);
    }

    @Override
    public void prepareRequested(final ChannelHandlerContext ctx, PreparationEvent evt) {

        super.prepareRequested(ctx, evt);

        Barrier barrier = getBarrier();
        final ChannelFuture barrierFuture = barrier.getFuture();

        ChannelFuture pipelineFuture = getPipelineFuture();
        ChannelFuture handlerFuture = getHandlerFuture();

        pipelineFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(final ChannelFuture f) throws Exception {
                if (f.isSuccess()) {
                    // We only want to set barrier future when the pipeline is success. Otherwise it could cause other
                    // streams to "fail" incorrectly.
                    barrierFuture.setSuccess();
                }
            }
        });

        pipelineFuture.addListener(chainedFuture(handlerFuture));
    }

    @Override
    public String toString() {
        return format("read|write notify %s", getBarrier());
    }

}
