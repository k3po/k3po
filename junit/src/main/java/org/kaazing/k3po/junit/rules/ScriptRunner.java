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
package org.kaazing.k3po.junit.rules;

import static java.lang.String.format;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

import org.kaazing.k3po.control.internal.Control;
import org.kaazing.k3po.control.internal.command.AbortCommand;
import org.kaazing.k3po.control.internal.command.CloseCommand;
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
    private volatile Map<String, CountDownLatch> barriers;
    private final List<String> overridenScriptProperties;

    ScriptRunner(URL controlURL, List<String> names, Latch latch, List<String> overridenScriptProperties) {

        if (names == null) {
            throw new NullPointerException("names");
        }

        if (latch == null) {
            throw new NullPointerException("latch");
        }

        this.controller = new Control(controlURL);
        this.names = names;
        this.latch = latch;
        this.barriers = new HashMap<String, CountDownLatch>();
        this.overridenScriptProperties = overridenScriptProperties;
    }

    public void abort() {
        // logging with system.out as I don't believe there is a standard junit logger, in the future
        // we will send this on the wire to appear in the diff (https://github.com/k3po/k3po/issues/332)
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
            prepare.setOverriddenScriptProperties(overridenScriptProperties);

            controller.writeCommand(prepare);

            boolean abortWritten = false;
            String expectedScript = null;
            while (true) {
                try {
                    // validate event name matches command name
                    CommandEvent event = controller.readEvent(200, MILLISECONDS);
                    if (event == null)
                    {
                        // connection closed
                        return new ScriptPair(expectedScript, "");
                    }

                    // process event
                    switch (event.getKind()) {
                    case PREPARED:
                        PreparedEvent prepared = (PreparedEvent) event;
                        expectedScript = prepared.getScript();
                        for (String barrier : prepared.getBarriers()) {
                            barriers.put(barrier, new CountDownLatch(1));
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
                        CountDownLatch notifiedLatch = barriers.get(barrier);
                        notifiedLatch.countDown();
                        break;
                    case ERROR:
                        ErrorEvent error = (ErrorEvent) event;
                        throw new SpecificationException(format("%s:%s", error.getSummary(), error.getDescription()));
                    case FINISHED:
                        FinishedEvent finished = (FinishedEvent) event;
                        // notify all barriers
                        notifyBarriers(finished);
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

    private void notifyBarriers(FinishedEvent event) {
        for( String barrierName : event.getCompletedBarriers())
        {
            CountDownLatch barrier = barriers.get(barrierName);
            barrier.countDown();
        }
    }

    private void sendAbortCommand() throws Exception {
        AbortCommand abort = new AbortCommand();
        controller.writeCommand(abort);
    }

    public void awaitBarrier(String barrierName) throws Exception {
        if (!barriers.keySet().contains(barrierName)) {
            throw new IllegalArgumentException(String.format(
                    "Barrier with %s is not present in the script and thus can't be waited upon", barrierName));
        }
        controller.sendAwaitBarrier(barrierName);
        final CountDownLatch notifiedLatch = barriers.get(barrierName);
        notifiedLatch.await();
    }

    public void notifyBarrier(final String barrierName) throws Exception {
        if (!barriers.keySet().contains(barrierName)) {
            throw new IllegalArgumentException(String.format(
                    "Barrier with %s is not present in the script and thus can't be notified", barrierName));
        }
        final CountDownLatch notifiedLatch = barriers.get(barrierName);
        if (notifiedLatch.getCount() > 0) {
            controller.notifyBarrier(barrierName);
        }
        notifiedLatch.await();
    }

    public void dispose() throws Exception {
        if (controller.isConnected()) {
            controller.writeCommand(new CloseCommand());
            while (controller.readEvent(0, SECONDS) != null) {
                Thread.sleep(20);
            }
        }
    }
}
