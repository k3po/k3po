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

package org.kaazing.robot.junit.rules;

import static java.lang.String.format;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.Callable;

import org.kaazing.robot.control.RobotControl;
import org.kaazing.robot.control.command.AbortCommand;
import org.kaazing.robot.control.command.PrepareCommand;
import org.kaazing.robot.control.command.StartCommand;
import org.kaazing.robot.control.event.CommandEvent;
import org.kaazing.robot.control.event.ErrorEvent;
import org.kaazing.robot.control.event.FinishedEvent;
import org.kaazing.robot.control.event.PreparedEvent;

final class ScriptRunner implements Callable<ScriptPair> {

    private final RobotControl controller;
    private final List<String> names;
    private final RoboticLatch latch;

    private volatile boolean abortScheduled;

    ScriptRunner(URL controlURL, List<String> names, RoboticLatch latch) throws Exception {

        if (names == null) {
            throw new NullPointerException("names");
        }

        if (latch == null) {
            throw new NullPointerException("latch");
        }

        this.controller = new RobotControl(controlURL);
        this.names = names;
        this.latch = latch;
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
                    case ERROR:
                        ErrorEvent error = (ErrorEvent) event;
                        throw new RoboticException(format("%s:%s", error.getSummary(), error.getDescription()));
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
            Exception exception = new Exception("Failed to connect. Is the Robot running?", e);
            exception.fillInStackTrace();
            latch.notifyException(exception);
            throw e;
        } catch (Exception e) {
            latch.notifyException(e);
            throw e;

        } finally {
            latch.notifyFinished();
            controller.disconnect();
        }
    }

    private void sendAbortCommand() throws Exception {
        AbortCommand abort = new AbortCommand();
        controller.writeCommand(abort);
    }
}
