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
