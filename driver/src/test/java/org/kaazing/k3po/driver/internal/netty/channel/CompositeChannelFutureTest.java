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
                oneOf(future1).isDone(); will(returnValue(true));
                oneOf(future1).isSuccess(); will(returnValue(true));
                oneOf(future2).isDone(); will(returnValue(true));
                oneOf(future2).isSuccess(); will(returnValue(true));
            }
        });
        CompositeChannelFuture<ChannelFuture> composite = new CompositeChannelFuture<ChannelFuture>(channel, futures);
        assertTrue(composite.isSuccess());
    }

    @Test
    public void shouldBeSucceededAfterConstructionWithKidsOSucceededAndCancelled() throws Exception {
        final ChannelFuture future1 = context.mock(ChannelFuture.class, "future1");
        final ChannelFuture future2 = context.mock(ChannelFuture.class, "future2");
        Collection<ChannelFuture> futures = Arrays.asList(future1, future2);

        context.checking(new Expectations() {
            {
                oneOf(future1).addListener(with(any(ChannelFutureListener.class)));
                will(callChannelFutureListener(future1));
                oneOf(future2).addListener(with(any(ChannelFutureListener.class)));
                will(callChannelFutureListener(future2));
                oneOf(future1).isDone(); will(returnValue(true));
                oneOf(future1).isSuccess(); will(returnValue(true));
                oneOf(future2).isDone(); will(returnValue(true));
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
                oneOf(future1).isDone(); will(returnValue(true));
                oneOf(future1).isSuccess(); will(returnValue(true));
                oneOf(future1).getCause();
                oneOf(future2).isDone(); will(returnValue(true));
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
                oneOf(future1).isDone(); will(returnValue(false));
                oneOf(future2).isDone(); will(returnValue(false));
            }
        });
        CompositeChannelFuture<ChannelFuture> composite = new CompositeChannelFuture<ChannelFuture>(channel, futures);
        composite.addListener(testListener);
        assertFalse(composite.isDone());

        context.checking(new Expectations() {
            {
                exactly(2).of(future1).isDone(); will(returnValue(true));
                oneOf(future1).isSuccess(); will(returnValue(true));
                oneOf(future1).getCause();
                oneOf(future2).isDone(); will(returnValue(false));
            }
        });
        listeners.get(0).operationComplete(future1);
        assertFalse(composite.isDone());

        context.checking(new Expectations() {
            {
                oneOf(future1).isDone(); will(returnValue(true));
                oneOf(future1).isSuccess(); will(returnValue(true));
                exactly(2).of(future2).isDone(); will(returnValue(true));
                oneOf(future2).getCause();
                oneOf(future2).isSuccess(); will(returnValue(true));
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
                oneOf(future1).isDone(); will(returnValue(false));
                oneOf(future2).isDone(); will(returnValue(false));
            }
        });
        CompositeChannelFuture<ChannelFuture> composite = new CompositeChannelFuture<ChannelFuture>(channel, futures);
        composite.addListener(testListener);
        assertFalse(composite.isDone());

        context.checking(new Expectations() {
            {
                exactly(2).of(future1).isDone(); will(returnValue(true));
                oneOf(future1).isSuccess(); will(returnValue(false));
                oneOf(future1).isCancelled(); will(returnValue(false));
                oneOf(future1).getCause(); will(returnValue(testException));
                oneOf(future2).isDone(); will(returnValue(false));
            }
        });
        listeners.get(0).operationComplete(future1);
        assertFalse(composite.isDone());

        context.checking(new Expectations() {
            {
                oneOf(future1).isDone(); will(returnValue(true));
                oneOf(future1).isSuccess(); will(returnValue(false));
                oneOf(future1).isCancelled(); will(returnValue(false));
                oneOf(future1).getCause(); will(returnValue(testException));
                exactly(2).of(future2).isDone(); will(returnValue(true));
                oneOf(future2).getCause();
                oneOf(future2).isSuccess(); will(returnValue(true));
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
                oneOf(future1).isDone(); will(returnValue(false));
                oneOf(future2).isDone(); will(returnValue(false));
            }
        });
        CompositeChannelFuture<ChannelFuture> composite = new CompositeChannelFuture<ChannelFuture>(channel, futures, true);
        composite.addListener(testListener);
        assertFalse(composite.isDone());

        context.checking(new Expectations() {
            {
                oneOf(future1).isDone(); will(returnValue(true));
                exactly(2).of(future1).getCause(); will(returnValue(testException));
                oneOf(testListener).operationComplete(with(composite));
            }
        });
        listeners.get(0).operationComplete(future1);
        assertTrue(composite.isDone());
        assertSame(testException, composite.getCause());
    }


}
