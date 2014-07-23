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

import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelFutureProgressListener;

public final class ChannelFutureListeners {

    private ChannelFutureListeners() {
        // no instances
    }

    public static ChannelFutureListener chainedFuture(ChannelFuture future) {
        return new ChannelFutureListenerDecorator(future);
    }

    public static ChannelFutureProgressListener chainedFutureWithProgress(ChannelFuture future) {
        return new ChannelFutureProgressListenerDecorator(future);
    }

    private static class ChannelFutureListenerDecorator implements ChannelFutureListener {

        protected final ChannelFuture future;

        ChannelFutureListenerDecorator(ChannelFuture future) {
            this.future = future;
        }

        @Override
        public void operationComplete(ChannelFuture f) throws Exception {
            if (f.isSuccess()) {
                future.setSuccess();
            } else {
                future.setFailure(f.getCause());
            }
        }
    }

    private static final class ChannelFutureProgressListenerDecorator extends ChannelFutureListenerDecorator implements
            ChannelFutureProgressListener {

        private ChannelFutureProgressListenerDecorator(ChannelFuture future) {
            super(future);
        }

        @Override
        public void operationProgressed(ChannelFuture f, long amount, long current, long total) throws Exception {
            future.setProgress(amount, current, total);
        }

    }

}
