/*
 * Copyright 2014, Kaazing Corporation. All rights reserved.
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelFutureProgressListener;
import org.jboss.netty.channel.DefaultChannelFuture;

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

    private final NotifyingListener listener = new NotifyingListener();
    private volatile boolean constructionFinished;
    private final Collection<E> kids;
    private final boolean failFast;

    public CompositeChannelFuture(Channel channel, Collection<E> kids) {
        this(channel, kids, false);
    }

    // If we fail fast it means the composite is set to failure as soon as we see a single future fail
    public CompositeChannelFuture(Channel channel, Collection<E> kids, boolean failFast) {
        super(channel, false);

        this.failFast = failFast;
        this.kids = new ArrayList<>(kids);

        for (E k : kids) {
            k.addListener(listener);
        }
        /*
         * Note that a composite with no children will be automatically set to
         * success
         */
        constructionFinished = true;
        scanFutures();
    }

    @Override
    public Throwable getCause() {
        Throwable t = super.getCause();
        if (t != null) {
            return t;
        }

        Iterator<E> i = kids.iterator();
        while (i.hasNext()) {
            E future = i.next();
            t = future.getCause();
            if (t != null) {
                /*
                 * If we found one then the listener hasn't been notified yet
                 */
                if (failFast) {
                    setFailure(t);
                }
                return t;
            }
        }
        return null;
    }

    private void scanFutures() {
        int done = 0;
        int successCount = 0;
        int cancelledCount = 0;
        for (E future : kids) {
            if (future.isDone()) {
                done++;
                if (future.isSuccess()) {
                    successCount++;
                }
                else if (future.isCancelled()) {
                    cancelledCount++;
                }
            }
        }
        final int totalKids = kids.size();
        if (done == totalKids) {
            if (totalKids == successCount) {
                setSuccess();
            } else if (totalKids == cancelledCount) {
                if (!cancel()) {
                    if (!isCancelled()) {
                        // Then the composite was non-cancellable. Set to success
                        setSuccess();
                    }
                }
            } else if (totalKids == (successCount + cancelledCount)) {
                setSuccess();
            } else {
                for (E f : kids) {
                    Throwable t = f.getCause();
                    if (t != null) {
                        setFailure(t);
                        return;
                    }
                }
            }
        }
    }

    private class NotifyingListener implements ChannelFutureListener, ChannelFutureProgressListener {

        @Override
        public void operationProgressed(ChannelFuture future, long amount, long current, long total) throws Exception {

            if (constructionFinished) {
                setProgress(amount, current, total);
            }
        }

        @Override
        public void operationComplete(final ChannelFuture future) {
            if (!constructionFinished) {
                return;
            }
            if (CompositeChannelFuture.super.isDone()) {
                // Then we must have failed fast or already succeeded.
                return;
            }
            if (future.isDone()) {
                if (future.getCause() != null && failFast) {
                    setFailure(future.getCause());
                    return;
                }
                scanFutures();
            }
        }

    }
}
