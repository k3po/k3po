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

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.DefaultChannelFuture;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

/**
 * A {@link ChannelFuture} of {@link ChannelFuture}s. It is useful when you want
 * to get notified when all {@link ChannelFuture}s are complete.
 *
 * Note that this future is complete if and only if its containing futures are
 * complete Setting this future to complete does NOT result in containing
 * futures getting completed.
 *
 * This future is not cancelable. If one of the futures in which this future is
 * composed of is canceled. The CompositeChannelFuture2 will be considered
 * failed.
 *
 * @param <E> the type of the child futures.
 */
public class CompositeChannelFuture<E extends ChannelFuture> extends DefaultChannelFuture {

    private final ChannelFuture[] kidChannelFutures;

    public CompositeChannelFuture(Channel channel, Collection<E> kids) {
        this(channel, kids, false);
    }

    // If we fail fast it means the composite is set to failure as soon as we see a single future fail
    public CompositeChannelFuture(Channel channel, Collection<E> kids, boolean failFast) {
        super(channel, false);

        // Create a CompletableFuture for each ChannelFuture
        CompletableFuture[] kidCompletableFutures = new CompletableFuture[kids.size()];
        for (int i=0; i < kidCompletableFutures.length; i++) {
            CompletableFuture kidCF = new CompletableFuture();
            kidCompletableFutures[i] = kidCF;
        }
        CompletableFuture<?> allCompletableFuture = CompletableFuture.allOf(kidCompletableFutures);

        // Add a NotifyListener (which completes corresponding CompletableFuture) for each ChannelFuture
        kidChannelFutures = kids.toArray(new ChannelFuture[kids.size()]);
        for (int i=0; i < kidCompletableFutures.length; i++) {
            NotifyingListener listener = new NotifyingListener(kidCompletableFutures[i], allCompletableFuture, failFast);
            kidChannelFutures[i].addListener(listener);
        }

        // propagate all CompletableFuture to this DefaultChannelFuture
        allCompletableFuture.whenComplete((value, t) -> {
            if (t != null) {
                Throwable cause = t.getCause();
                setFailure((cause != null) ? cause : t);
            } else {
                setSuccess();
            }
        });
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(super.toString());
        s.append(" (");
        s.append(futureStatus(this));
        s.append(", ").append(kidChannelFutures.length).append(" kids)");
        for (ChannelFuture kid : kidChannelFutures) {
            s.append("\n  ");
            s.append(kid.toString());
            s.append(" (");
            s.append(futureStatus(kid));
            s.append(")");
        }
        return s.toString();
    }

    private static String futureStatus(ChannelFuture future) {
        return future.isSuccess() ? "success"
                                   : future.isCancelled() ? "cancelled"
                                   : future.getCause() == null ? "incomplete"
                                   : "failed - " + future.getCause();
    }

    private class NotifyingListener implements ChannelFutureListener {
        private final CompletableFuture<?> kidCF;
        private final boolean failFast;
        private final CompletableFuture allCF;

        NotifyingListener(CompletableFuture kidCF, CompletableFuture<?> allCF, boolean failFast) {
            this.kidCF = kidCF;
            this.failFast = failFast;
            this.allCF = allCF;
        }

        @Override
        public void operationComplete(ChannelFuture future) {
            // propage kid ChannelFuture to kid CompletableFuture
            if (future.isCancelled()) {
                kidCF.cancel(true);
            } else if (!future.isSuccess()) {
                Throwable cause = future.getCause();
                assert cause != null;
                kidCF.completeExceptionally(cause);
                if (failFast) {
                    allCF.completeExceptionally(cause);
                }
            } else {
                kidCF.complete(null);
            }
        }
    }
}
