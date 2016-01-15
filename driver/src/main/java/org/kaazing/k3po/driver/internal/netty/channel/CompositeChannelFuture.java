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

import static java.util.Collections.unmodifiableCollection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

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
    private final AtomicInteger unnotified = new AtomicInteger();
    private volatile boolean constructionFinished;
    private final Collection<E> kids;
    private volatile int successCount;
    private volatile int failedCount;
    private volatile int cancelledCount;
    private final boolean failFast;

    public CompositeChannelFuture(Channel channel, Collection<E> kids) {
        this(channel, kids, false);
    }

    // If we fail fast it means the composite is set to failure as soon as we see a single future fail
    public CompositeChannelFuture(Channel channel, Collection<E> kids, boolean failFast) {
        super(channel, false);

        this.failFast = failFast;
        this.kids = unmodifiableCollection(new ArrayList<>(kids));

        for (E k : kids) {
            unnotified.incrementAndGet();
            k.addListener(listener);
        }
        /*
         * Note that a composite with no children will be automatically set to
         * success
         */
        constructionFinished = true;
        if (unnotified.get() == 0) {
            setSuccess();
        }
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

    private interface CompositeTrue {
        boolean isTrue(ChannelFuture f);
    }

    @Override
    public boolean isSuccess() {

        if (super.isSuccess()) {
            return true;
        }

        boolean result = this.allTrue(new CompositeTrue() {
            @Override
            public boolean isTrue(ChannelFuture f) {
                return f.isSuccess();
            }
        });

        /*
         * If true we know we are done and the listener just hasn't been
         * notified yet to set this.setSuccess(). So we do this now. But it may
         * have since been marked success sine we last check so make sure we
         * still return true.
         */
        // return result ? (super.setSuccess() || true) : false;
        return result;

    }

    @Override
    public boolean isDone() {

        if (super.isDone()) {
            return true;
        }

        return this.allTrue(new CompositeTrue() {
            @Override
            public boolean isTrue(ChannelFuture f) {
                return f.isDone();
            }
        });

    }

    @Override
    public String toString() {
        StringBuffer s = new StringBuffer(super.toString());
        s.append(" (");
        s.append(futureStatus(this));
        s.append(", " + kids.size() + " kids)");
        for (E kid : kids) {
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

    private boolean allTrue(CompositeTrue predicate) {
        /* An empty list should evaluate to false. Always. */
        if (kids.isEmpty()) {
            return false;
        }

        Iterator<E> i = kids.iterator();
        while (i.hasNext()) {
            E future = i.next();
            if (!predicate.isTrue(future)) {
                return false;
            }
        }
        return true;
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

            boolean isSuccess = future.isSuccess();
            boolean isCancelled = future.isCancelled();
            boolean failed = false;

            /* We need to synchronize here due to the addChildren method */
            synchronized (CompositeChannelFuture.this) {

                if (CompositeChannelFuture.super.isDone()) {
                    // Then we must have failed fast.
                    return;
                }
                int currentUnnotified = unnotified.decrementAndGet();

                if (isSuccess) {
                    successCount++;
                } else if (isCancelled) {
                    cancelledCount++;
                } else {
                    failed = true;
                    failedCount++;
                }

                // callSetDone = successCount + failureCount == futures.size();
                if (currentUnnotified == 0 && constructionFinished) {
                    final int totalKids = kids.size();
                    if (totalKids == successCount) {
                        setSuccess();
                    } else if (totalKids == cancelledCount) {
                        if (!cancel()) {
                            if (!isCancelled()) {
                                // Then the composite was non-cancellable. Set to success
                                setSuccess();
                            }
                        }
                    } else {
                        for (E f : kids) {
                            Throwable t = f.getCause();
                            if (t != null) {
                                setFailure(t);
                                return;
                            }
                        }
                    }
                } else if (failed && failFast && constructionFinished) {
                    setFailure(future.getCause());
                }

            }
        }
    }
}
