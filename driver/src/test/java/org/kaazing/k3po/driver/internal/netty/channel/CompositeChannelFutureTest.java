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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;
import org.kaazing.k3po.driver.internal.jmock.Expectations;

/**
 * Convenience class that simply overRides toString to not print out the channel
 * to facilitate easier testing (no regex's needed, simple string comparison)
 *
 */
public class CompositeChannelFutureTest {

    @Rule
    public final JUnitRuleMockery context = new JUnitRuleMockery();

    private final Channel channel = context.mock(Channel.class);

    @Test
    public void shouldBeSucceededAfterConstructionWithAllKidsSucceeded() throws Exception {
        final ChannelFuture future1 = context.mock(ChannelFuture.class, "future1");
        final ChannelFuture future2 = context.mock(ChannelFuture.class, "future2");
        Collection<ChannelFuture> futures = Arrays.asList(future1, future2);

        context.checking(new Expectations() {
            {
                oneOf(future1).addListener(with(any(ChannelFutureListener.class)));
                will(callChannelFutureListener(future1));
                oneOf(future2).addListener(with(any(ChannelFutureListener.class)));
                will(callChannelFutureListener(future2));
                oneOf(future1).isSuccess(); will(returnValue(true));
                oneOf(future1).isCancelled(); will(returnValue(false));
                oneOf(future2).isSuccess(); will(returnValue(true));
                oneOf(future2).isCancelled(); will(returnValue(false));
            }
        });
        CompositeChannelFuture<ChannelFuture> composite = new CompositeChannelFuture<ChannelFuture>(channel, futures);
        assertTrue(composite.isSuccess());
    }

    @Test
    public void shouldBeSucceededAfterConstructionWithKidsSucceededAndCancelled() throws Exception {
        final ChannelFuture future1 = context.mock(ChannelFuture.class, "future1");
        final ChannelFuture future2 = context.mock(ChannelFuture.class, "future2");
        Collection<ChannelFuture> futures = Arrays.asList(future1, future2);

        context.checking(new Expectations() {
            {
                oneOf(future1).addListener(with(any(ChannelFutureListener.class)));
                will(callChannelFutureListener(future1));
                oneOf(future2).addListener(with(any(ChannelFutureListener.class)));
                will(callChannelFutureListener(future2));
                oneOf(future1).isSuccess(); will(returnValue(true));
                oneOf(future1).isCancelled(); will(returnValue(false));
                oneOf(future2).isSuccess(); will(returnValue(false));
                oneOf(future2).isCancelled(); will(returnValue(true));
            }
        });
        CompositeChannelFuture<ChannelFuture> composite = new CompositeChannelFuture<ChannelFuture>(channel, futures);
        assertTrue(composite.isDone());
        assertTrue(composite.isSuccess());
        assertFalse(composite.isCancelled());
    }

    @Test
    public void shouldBeFailedAfterConstructionWithAllKidsDoneOneFailed() throws Exception {
        final ChannelFuture future1 = context.mock(ChannelFuture.class, "future1");
        final ChannelFuture future2 = context.mock(ChannelFuture.class, "future2");
        Collection<ChannelFuture> futures = Arrays.asList(future1, future2);
        final Exception exception = new Exception("Test exception");

        context.checking(new Expectations() {
            {
                oneOf(future1).addListener(with(any(ChannelFutureListener.class)));
                will(callChannelFutureListener(future1));
                oneOf(future2).addListener(with(any(ChannelFutureListener.class)));
                will(callChannelFutureListener(future2));
                oneOf(future1).isSuccess(); will(returnValue(true));
                oneOf(future1).isCancelled(); will(returnValue(false));
                oneOf(future1).getCause();
                oneOf(future2).isSuccess(); will(returnValue(false));
                oneOf(future2).isCancelled(); will(returnValue(false));
                oneOf(future2).getCause(); will(returnValue(exception));
            }
        });
        CompositeChannelFuture<ChannelFuture> composite = new CompositeChannelFuture<ChannelFuture>(channel, futures);
        assertTrue(composite.isDone());
        assertSame(exception, composite.getCause());
    }

    @Test
    public void shouldSucceedOnlyWhenAllKidsHaveSucceeded() throws Exception {
        final ChannelFuture future1 = context.mock(ChannelFuture.class, "future1");
        final ChannelFuture future2 = context.mock(ChannelFuture.class, "future2");
        Collection<ChannelFuture> futures = Arrays.asList(future1, future2);
        final ChannelFutureListener testListener = context.mock(ChannelFutureListener.class, "testListener");
        final List<ChannelFutureListener> listeners = new ArrayList<ChannelFutureListener>();

        context.checking(new Expectations() {
            {
                oneOf(future1).addListener(with(any(ChannelFutureListener.class)));
                will(saveParameter(0, listeners));
                oneOf(future2).addListener(with(any(ChannelFutureListener.class)));
                will(saveParameter(0, listeners));
            }
        });
        CompositeChannelFuture<ChannelFuture> composite = new CompositeChannelFuture<ChannelFuture>(channel, futures);
        context.assertIsSatisfied();
        composite.addListener(testListener);

        context.checking(new Expectations() {
            {
                oneOf(future1).isDone(); will(returnValue(false));
            }
        });
        assertFalse(composite.isDone());
        context.assertIsSatisfied();

        context.checking(new Expectations() {
            {
                oneOf(future1).isSuccess(); will(returnValue(true));
                oneOf(future1).isCancelled(); will(returnValue(false));
            }
        });
        listeners.get(0).operationComplete(future1);
        context.assertIsSatisfied();

        context.checking(new Expectations() {
            {
                oneOf(future1).isDone(); will(returnValue(true));
                oneOf(future2).isDone(); will(returnValue(false));
            }
        });
        assertFalse(composite.isDone());
        context.assertIsSatisfied();

        context.checking(new Expectations() {
            {
                oneOf(future2).isSuccess(); will(returnValue(true));
                oneOf(future2).isCancelled(); will(returnValue(false));
                oneOf(testListener).operationComplete(with(composite));
            }
        });
        listeners.get(1).operationComplete(future2);

        assertTrue(composite.isSuccess());
    }

    @Test
    public void shouldFailOnlyWhenAllKidsHaveCompleted() throws Exception {
        final ChannelFuture future1 = context.mock(ChannelFuture.class, "future1");
        final ChannelFuture future2 = context.mock(ChannelFuture.class, "future2");
        Collection<ChannelFuture> futures = Arrays.asList(future1, future2);
        final ChannelFutureListener testListener = context.mock(ChannelFutureListener.class, "testListener");
        final List<ChannelFutureListener> listeners = new ArrayList<ChannelFutureListener>();
        final Exception testException = new Exception("test exception");

        context.checking(new Expectations() {
            {
                oneOf(future1).addListener(with(any(ChannelFutureListener.class)));
                will(saveParameter(0, listeners));
                oneOf(future2).addListener(with(any(ChannelFutureListener.class)));
                will(saveParameter(0, listeners));
            }
        });
        CompositeChannelFuture<ChannelFuture> composite = new CompositeChannelFuture<ChannelFuture>(channel, futures);
        context.assertIsSatisfied();
        composite.addListener(testListener);

        context.checking(new Expectations() {
            {
                oneOf(future1).isDone(); will(returnValue(false));
            }
        });
        assertFalse(composite.isDone());

        context.checking(new Expectations() {
            {
                oneOf(future1).isSuccess(); will(returnValue(false));
                oneOf(future1).isCancelled(); will(returnValue(false));
                oneOf(future1).getCause(); will(returnValue(testException));
            }
        });
        listeners.get(0).operationComplete(future1);
        context.checking(new Expectations() {
            {
                oneOf(future1).isDone(); will(returnValue(true));
                oneOf(future2).isDone(); will(returnValue(false));
            }
        });
        assertFalse(composite.isDone());

        context.checking(new Expectations() {
            {
                oneOf(future2).isSuccess(); will(returnValue(true));
                oneOf(future2).isCancelled(); will(returnValue(false));
                oneOf(testListener).operationComplete(with(composite));
            }
        });
        listeners.get(1).operationComplete(future2);
        assertTrue(composite.isDone());
        assertSame(testException, composite.getCause());
    }

    @Test
    public void shouldFailFast() throws Exception {
        final ChannelFuture future1 = context.mock(ChannelFuture.class, "future1");
        final ChannelFuture future2 = context.mock(ChannelFuture.class, "future2");
        Collection<ChannelFuture> futures = Arrays.asList(future1, future2);
        final ChannelFutureListener testListener = context.mock(ChannelFutureListener.class, "testListener");
        final List<ChannelFutureListener> listeners = new ArrayList<ChannelFutureListener>();
        final Exception testException = new Exception("test exception");

        context.checking(new Expectations() {
            {
                oneOf(future1).addListener(with(any(ChannelFutureListener.class)));
                will(saveParameter(0, listeners));
                oneOf(future2).addListener(with(any(ChannelFutureListener.class)));
                will(saveParameter(0, listeners));
            }
        });
        CompositeChannelFuture<ChannelFuture> composite = new CompositeChannelFuture<ChannelFuture>(channel, futures, true);
        composite.addListener(testListener);

        context.checking(new Expectations() {
            {
                oneOf(future1).isDone(); will(returnValue(false));
            }
        });
        assertFalse(composite.isDone());

        context.checking(new Expectations() {
            {
                oneOf(future1).isSuccess(); will(returnValue(false));
                oneOf(future1).isCancelled(); will(returnValue(false));
                oneOf(future1).getCause(); will(returnValue(testException));
                oneOf(testListener).operationComplete(with(composite));
            }
        });
        listeners.get(0).operationComplete(future1);
        assertTrue(composite.isDone());
        assertSame(testException, composite.getCause());
    }

    @Test
    public void toStringShouldBeHelpful() throws Exception {
        final ChannelFuture success = context.mock(ChannelFuture.class, "success");
        final ChannelFuture cancelled = context.mock(ChannelFuture.class, "cancelled");
        final ChannelFuture incomplete = context.mock(ChannelFuture.class, "incomplete");
        final ChannelFuture failed = context.mock(ChannelFuture.class, "failed");
        Collection<ChannelFuture> futures = Arrays.asList(success, cancelled, incomplete, failed);
        final Exception testException = new Exception("test exception");

        context.checking(new Expectations() {
            {
                oneOf(success).addListener(with(any(ChannelFutureListener.class)));
                oneOf(cancelled).addListener(with(any(ChannelFutureListener.class)));
                oneOf(incomplete).addListener(with(any(ChannelFutureListener.class)));
                oneOf(failed).addListener(with(any(ChannelFutureListener.class)));
                allowing(success).isDone(); will(returnValue(true));
                allowing(success).isSuccess(); will(returnValue(true));
                allowing(success).getCause();
                allowing(cancelled).isDone(); will(returnValue(true));
                allowing(cancelled).isSuccess(); will(returnValue(false));
                allowing(cancelled).isCancelled(); will(returnValue(true));
                allowing(cancelled).getCause();
                allowing(incomplete).isDone(); will(returnValue(false));
                allowing(incomplete).isSuccess(); will(returnValue(false));
                allowing(incomplete).isCancelled(); will(returnValue(false));
                allowing(incomplete).getCause();
                allowing(failed).isDone(); will(returnValue(true));
                allowing(failed).isSuccess(); will(returnValue(false));
                allowing(failed).isCancelled(); will(returnValue(false));
                allowing(failed).getCause(); will(returnValue(testException));
            }
        });
        CompositeChannelFuture<ChannelFuture> composite = new CompositeChannelFuture<ChannelFuture>(channel, futures, true);
        assertFalse(composite.isDone());
        //System.out.println(composite.toString());
        assertTrue(composite.toString().matches(
                "(?s).*success.*success.*cancelled.*cancelled.*incomplete.*incomplete.*failed.*failed.*test exception.*"));
    }


}
