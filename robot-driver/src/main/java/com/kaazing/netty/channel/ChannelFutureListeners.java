/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package com.kaazing.netty.channel;

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
