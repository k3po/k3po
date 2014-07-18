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
import java.net.URI;
import java.util.concurrent.Callable;

import org.kaazing.robot.control.RobotControl;
import org.kaazing.robot.control.RobotControlFactories;
import org.kaazing.robot.control.RobotControlFactory;
import org.kaazing.robot.control.command.AbortCommand;
import org.kaazing.robot.control.command.PrepareCommand;
import org.kaazing.robot.control.command.StartCommand;
import org.kaazing.robot.control.event.CommandEvent;
import org.kaazing.robot.control.event.ErrorEvent;
import org.kaazing.robot.control.event.FinishedEvent;
import org.kaazing.robot.junit.RoboticException;

final class ScriptRunner implements Callable<String> {


    private final RobotControlFactory controllerFactory;
    private final RobotControl controller;
    private final String name;
    private final String expected;
    private final RoboticLatch latch;

    private volatile boolean abortScheduled;

    ScriptRunner(String name, String expected, RoboticLatch latch) throws Exception {

        if (name == null) {
            throw new NullPointerException("name");
        }

        if (expected == null) {
            throw new NullPointerException("expected");
        }

        if (latch == null) {
            throw new NullPointerException("latch");
        }

        // TODO: make this port number a constant? Configurable?
        URI controlURI = URI.create("tcp://localhost:11642");

        this.controllerFactory = RobotControlFactories.createRobotControlFactory();
        this.controller = controllerFactory.newClient(controlURI);
        this.name = name;
        this.expected = expected;
        this.latch = latch;
    }

    public void abort() {
        this.abortScheduled = true;
        latch.notifyAbort();
    }

    @Override
    public String call() throws Exception {

        try {
            // We are already done if abort before we start
            if (abortScheduled) {
                return "";
            }

            controller.connect();

            // send PREPARE command
            PrepareCommand prepare = new PrepareCommand();
            prepare.setName(name);
            prepare.setScript(expected);

            controller.writeCommand(prepare);

            boolean abortWritten = false;
            while (true) {
                try {
                    // validate event name matches command name
                    CommandEvent event = controller.readEvent(200, MILLISECONDS);
                    if (!name.equals(event.getName())) {
                        throw new IllegalStateException(format(
                                "Unexpected %s event with script name '%s' while awaiting completion", event.getKind(),
                                event.getName()));
                    }

                    // process event
                    switch (event.getKind()) {
                    case PREPARED:
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
                            start.setName(name);
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
                        // observed script (possibly incomplete)
                        return finished.getScript();
                    default:
                        throw new IllegalArgumentException("Unrecognized event kind: " + event.getKind());
                    }
                }
                catch (SocketTimeoutException e) {
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
        }
        catch (ConnectException e) {
            Exception exception = new Exception("Failed to connect. Is the Robot running?", e);
            exception.fillInStackTrace();
            latch.notifyException(exception);
            throw e;
        } catch (Exception e) {
            latch.notifyException(e);
            throw e;

        }
        finally {
            latch.notifyFinished();
            controller.disconnect();
        }
    }

    private void sendAbortCommand() throws Exception {
        AbortCommand abort = new AbortCommand();
        abort.setName(name);
        controller.writeCommand(abort);
    }
}
