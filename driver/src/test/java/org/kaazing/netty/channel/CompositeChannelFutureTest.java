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

package org.kaazing.netty.channel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.Channels;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CompositeChannelFutureTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void singleTriggerComposite() throws Exception {

        final AtomicBoolean compositeTriggered = new AtomicBoolean(false);
        Channel channel = null;

        ChannelFuture triggerFuture = Channels.future(channel);

        List<ChannelFuture> triggers = new ArrayList<ChannelFuture>(1);
        triggers.add(triggerFuture);
        ChannelFuture composite = new CompositeChannelFuture<ChannelFuture>(channel, triggers);

        composite.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) {
                compositeTriggered.set(true);
            }
        });
        Assert.assertFalse("Composite future triggered unexpectedly", compositeTriggered.get());

        triggerFuture.setSuccess();
        Assert.assertTrue("Composite future not triggered as expected", compositeTriggered.get());
    }

    @Test
    public void doubleTriggerCompositeTriggerFirstThenSecond() throws Exception {

        final AtomicBoolean compositeTriggered = new AtomicBoolean(false);
        Channel channel = null;

        ChannelFuture firstTriggerFuture = Channels.future(channel);
        ChannelFuture secondTriggerFuture = Channels.future(channel);

        List<ChannelFuture> triggers = new ArrayList<ChannelFuture>(2);
        triggers.add(firstTriggerFuture);
        triggers.add(secondTriggerFuture);

        ChannelFuture composite = new CompositeChannelFuture<ChannelFuture>(channel, triggers);
        Assert.assertFalse("Composite future triggered unexpectedly", compositeTriggered.get());

        composite.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) {
                compositeTriggered.set(true);
            }
        });
        Assert.assertFalse("Composite future triggered unexpectedly", compositeTriggered.get());

        firstTriggerFuture.setSuccess();
        Assert.assertFalse("Composite future triggered unexpectedly", compositeTriggered.get());

        secondTriggerFuture.setSuccess();
        Assert.assertTrue("Composite future not triggered as expected", compositeTriggered.get());
    }

    @Test
    public void doubleTriggerCompositeTriggerSecondThenFirst() throws Exception {

        final AtomicBoolean compositeTriggered = new AtomicBoolean(false);
        Channel channel = null;

        ChannelFuture firstTriggerFuture = Channels.future(channel);
        ChannelFuture secondTriggerFuture = Channels.future(channel);

        List<ChannelFuture> triggers = new ArrayList<ChannelFuture>(2);
        triggers.add(firstTriggerFuture);
        triggers.add(secondTriggerFuture);

        ChannelFuture composite = new CompositeChannelFuture<ChannelFuture>(channel, triggers);
        Assert.assertFalse("Composite future triggered unexpectedly", compositeTriggered.get());

        composite.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) {
                compositeTriggered.set(true);
            }
        });
        Assert.assertFalse("Composite future triggered unexpectedly", compositeTriggered.get());

        secondTriggerFuture.setSuccess();
        Assert.assertFalse("Composite future triggered unexpectedly", compositeTriggered.get());

        firstTriggerFuture.setSuccess();
        Assert.assertTrue("Composite future not triggered as expected", compositeTriggered.get());
    }

    @Test
    public void compositeTriggerComposite() throws Exception {

        final AtomicBoolean firstCompositeTriggered = new AtomicBoolean(false);
        final AtomicBoolean secondCompositeTriggered = new AtomicBoolean(false);
        Channel channel = null;

        ChannelFuture initialTriggerFuture = Channels.future(channel);

        List<ChannelFuture> triggers = new ArrayList<ChannelFuture>(1);

        triggers.add(initialTriggerFuture);
        ChannelFuture firstComposite = new CompositeChannelFuture<ChannelFuture>(channel, triggers);
        firstComposite.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) {
                firstCompositeTriggered.set(true);
            }
        });

        triggers = new ArrayList<ChannelFuture>(1);
        triggers.add(firstComposite);

        ChannelFuture secondComposite = new CompositeChannelFuture<ChannelFuture>(channel, triggers);
        secondComposite.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) {
                secondCompositeTriggered.set(true);
            }
        });

        Assert.assertFalse("Composite future #1 triggered unexpectedly", firstCompositeTriggered.get());
        Assert.assertFalse("Composite future #2 triggered unexpectedly", firstCompositeTriggered.get());

        initialTriggerFuture.setSuccess();
        Assert.assertTrue("Composite future #1 not triggered as expected", firstCompositeTriggered.get());
        Assert.assertTrue("Composite future #2 not triggered as expected", secondCompositeTriggered.get());
    }
}
