/*
 * Copyright (c) 2007-2014 Kaazing Corporation. All rights reserved.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.kaazing.k3po.junit.rules;

import static java.lang.String.format;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.kaazing.k3po.junit.rules.ScriptRunner.BarrierState.INITIAL;
import static org.kaazing.k3po.junit.rules.ScriptRunner.BarrierState.NOTIFIED;
import static org.kaazing.k3po.junit.rules.ScriptRunner.BarrierState.NOTIFYING;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

import org.kaazing.k3po.control.internal.Control;
import org.kaazing.k3po.control.internal.command.AbortCommand;
import org.kaazing.k3po.control.internal.command.PrepareCommand;
import org.kaazing.k3po.control.internal.command.StartCommand;
import org.kaazing.k3po.control.internal.event.CommandEvent;
import org.kaazing.k3po.control.internal.event.ErrorEvent;
import org.kaazing.k3po.control.internal.event.FinishedEvent;
import org.kaazing.k3po.control.internal.event.NotifiedEvent;
import org.kaazing.k3po.control.internal.event.PreparedEvent;
import org.kaazing.k3po.junit.rules.internal.ScriptPair;

final class ScriptRunner implements Callable<ScriptPair> {

    private final Control controller;
    private final List<String> names;
    private final Latch latch;

    private volatile boolean abortScheduled;
    private volatile Map<String, BarrierStateMachine> barriers;

    ScriptRunner(URL controlURL, List<String> names, Latch latch) {

        if (names == null) {
            throw new NullPointerException("names");
        }

        if (latch == null) {
            throw new NullPointerException("latch");
        }

        this.controller = new Control(controlURL);
        this.names = names;
        this.latch = latch;
        this.barriers = new HashMap<String, ScriptRunner.BarrierStateMachine>();
    }

    public void abort() {
        this.abortScheduled = true;
        latch.notifyAbort();
    }

    @Override
    public ScriptPair call() throws Exception {

        try {
            // We are already done if abort before we start
            if (abortScheduled) {
                return new ScriptPair();
            }

            controller.connect();

            // send PREPARE command
            PrepareCommand prepare = new PrepareCommand();
            prepare.setNames(names);

            controller.writeCommand(prepare);

            boolean abortWritten = false;
            String expectedScript = null;
            while (true) {
                try {
                    // validate event name matches command name
                    CommandEvent event = controller.readEvent(200, MILLISECONDS);

                    // process event
                    switch (event.getKind()) {
                    case PREPARED:
                        PreparedEvent prepared = (PreparedEvent) event;
                        expectedScript = prepared.getScript();
                        for (String barrier : prepared.getBarriers()) {
                            barriers.put(barrier, new BarrierStateMachine());
                        }

                        // notify script is prepared
                        latch.notifyPrepared();

                        latch.awaitStartable();

                        // Send ABORT if we were asked to abort otherwise send start command
                        if (abortScheduled && !abortWritten) {
                            sendAbortCommand();
                            abortWritten = true;
                        } else {
                            // send START command
                            StartCommand start = new StartCommand();
                            controller.writeCommand(start);
                        }
                        break;
                    case STARTED:
                        break;
                    case NOTIFIED:
                        NotifiedEvent notifiedEvent = (NotifiedEvent) event;
                        String barrier = notifiedEvent.getBarrier();
                        BarrierStateMachine stateMachine = barriers.get(barrier);
                        stateMachine.notified();
                        break;
                    case ERROR:
                        ErrorEvent error = (ErrorEvent) event;
                        throw new SpecificationException(format("%s:%s", error.getSummary(), error.getDescription()));
                    case FINISHED:
                        FinishedEvent finished = (FinishedEvent) event;
                        // note: observed script is possibly incomplete
                        String observedScript = finished.getScript();
                        return new ScriptPair(expectedScript, observedScript);
                    default:
                        throw new IllegalArgumentException("Unrecognized event kind: " + event.getKind());
                    }
                } catch (SocketTimeoutException e) {
                    if (abortScheduled && !abortWritten) {
                        sendAbortCommand();
                        abortWritten = true;
                    }

                    // defensive clean-up in case Robot control server stalls during response to ABORT
                    if (Thread.interrupted()) {
                        throw new InterruptedException();
                    }
                }
            }
        } catch (ConnectException e) {
            Exception exception = new Exception("Failed to connect. Is K3PO ready?", e);
            exception.fillInStackTrace();
            latch.notifyException(exception);
            throw e;
        } catch (Exception e) {
            latch.notifyException(e);
            throw e;

        } finally {
            latch.notifyFinished();
        }
    }

    private void sendAbortCommand() throws Exception {
        AbortCommand abort = new AbortCommand();
        controller.writeCommand(abort);
    }

    public void awaitBarrier(String barrierName) throws InterruptedException {
        if (!barriers.keySet().contains(barrierName)) {
            throw new IllegalArgumentException(String.format(
                    "Barrier with %s is not present in the script and thus can't be waited upon", barrierName));
        }
        final CountDownLatch notifiedLatch = new CountDownLatch(1);
        final BarrierStateMachine barrierStateMachine = barriers.get(barrierName);
        barrierStateMachine.addListener(new BarrierStateListener() {

            @Override
            public void initial() {
                // NOOP
            }

            @Override
            public void notifying() {
                // NOOP
            }

            @Override
            public void notified() {
                notifiedLatch.countDown();
            }

        });
        try {
            controller.await(barrierName);
        } catch (Exception e) {
            latch.notifyException(e);
        }
        notifiedLatch.await();
    }

    public void notifyBarrier(final String barrierName) throws InterruptedException {
        if (!barriers.keySet().contains(barrierName)) {
            throw new IllegalArgumentException(String.format(
                    "Barrier with %s is not present in the script and thus can't be notified", barrierName));
        }
        final CountDownLatch notifiedLatch = new CountDownLatch(1);
        final BarrierStateMachine barrierStateMachine = barriers.get(barrierName);
        barrierStateMachine.addListener(new BarrierStateListener() {

            @Override
            public void initial() {
                barrierStateMachine.notifying();
                // Only write to wire once
                try {
                    controller.notifyBarrier(barrierName);
                } catch (Exception e) {
                    latch.notifyException(e);
                }
            }

            @Override
            public void notifying() {
                // NOOP
            }

            @Override
            public void notified() {
                notifiedLatch.countDown();
            }
        });
        notifiedLatch.await();
    }

    private interface BarrierStateListener {
        void initial();

        void notified();

        void notifying();
    }

    enum BarrierState {
        INITIAL, NOTIFYING, NOTIFIED;
    }

    private class BarrierStateMachine implements BarrierStateListener {

        private BarrierState state = INITIAL;
        private List<BarrierStateListener> stateListeners = new ArrayList<>();

        @Override
        public void initial() {
            System.out.println("Hello");
            synchronized (this) {
                this.state = NOTIFYING;
                for (BarrierStateListener listener : stateListeners) {
                    listener.initial();
                }
            }
        }

        @Override
        public void notifying() {
            synchronized (this) {
                this.state = NOTIFYING;
                for (BarrierStateListener listener : stateListeners) {
                    listener.notifying();
                }
            }
        }

        @Override
        public void notified() {
            synchronized (this) {
                this.state = NOTIFIED;
                for (BarrierStateListener listener : stateListeners) {
                    listener.notified();
                }
            }
        }

        public void addListener(BarrierStateListener stateListener) {
            synchronized (this) {
                switch (this.state) {
                // notify right away if waiting on state
                case INITIAL:
                    stateListener.initial();
                    break;
                case NOTIFYING:
                    stateListener.notify();
                    break;
                case NOTIFIED:
                    stateListener.notified();
                    break;
                default:
                    break;
                }
                stateListeners.add(stateListener);
            }
        }
    }

    public void dispose() throws Exception {
        controller.dispose();
        try {
            // Give 5 seconds for cleanup to occur
            CommandEvent event = controller.readEvent(5000, MILLISECONDS);

            // ensure it is the correct event
            switch (event.getKind()) {
            case DISPOSED:
                latch.notifyDisposed();
                break;
            default:
                throw new IllegalArgumentException("Unrecognized event kind: " + event.getKind());
            }
        } finally {
            controller.disconnect();
        }
    }
}
