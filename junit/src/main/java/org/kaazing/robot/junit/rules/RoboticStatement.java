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
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import org.junit.ComparisonFailure;
import org.junit.runners.model.Statement;
import org.kaazing.robot.junit.ScriptPair;

final class RoboticStatement extends Statement {

    private final Statement statement;
    private final String scriptName;
    private final RoboticLatch latch;

    RoboticStatement(Statement statement, String scriptName, RoboticLatch latch) {
        this.statement = statement;
        this.scriptName = scriptName;
        this.latch = latch;
    }

    @Override
    public void evaluate() throws Throwable {

        // TODO: remove resolution from JUnit integration
        //       instead add "testScriptDir" (plugin default: src/test/scripts) to Robot for server-size resolution
        //       only relative script path hits the network, never absolute path, never with extension

        ScriptRunner scriptRunner = new ScriptRunner(scriptName, latch);
        FutureTask<ScriptPair> scriptFuture = new FutureTask<ScriptPair>(scriptRunner);

        try {
            // start the script execution
            new Thread(scriptFuture).start();

            // wait for script to be prepared (all binds ready for incoming connections from statement)
            latch.awaitPrepared();

            try {
                // note: JUnit timeout will trigger an exception
                statement.evaluate();
            } catch (Throwable cause) {
                // any exception aborts the script (including timeout)
                if (latch.hasException()) {
                    // propagate exception if the latch has an exception
                    throw cause;
                } else {
                    // It is possible that the script is finished even if we get an exception
                    // in particular a timeout may occur but the script may finish before we actually
                    // process it the timeout, send the abort and get the result back.

                    // No reason to send the abort if we are already finished.
                    // Note that there is a race in that the script may be finished before
                    // we actually send the abort. But that is ok.
                    if (!latch.isFinished()) {
                        scriptRunner.abort();
                    }

                    try {
                        // wait at most 5sec for the observed script (due to the abort case)
                        // should take less than a second for the Robot to complete
                        ScriptPair scripts = scriptFuture.get(5, SECONDS);

                        try {
                            assertEquals("Robotic behavior did not match", scripts.getExpectedScript(), scripts.getObservedScript());
                            // Throw the original exception if we are equal
                            throw cause;
                        } catch (ComparisonFailure f) {
                            // throw an exception that highlights the difference in behavior, but caused by the timeout
                            // (or
                            // original exception)
                            f.initCause(cause);
                            throw f;
                        }

                    } catch (ExecutionException ee) {
                        throw ee.getCause().initCause(cause);
                    } catch (Exception e) {
                        // Note that ComparisonFailure are not an Exception we do want those to bubble up.
                        throw cause;
                    }
                }
            }

            // note: statement MUST call join() to ensure wrapped Rule(s) do not complete early
            // and to allow Robot script to make progress
            assertTrue(format("Did you call %s.join()?", RobotRule.class.getSimpleName()), latch.isStartable());

            ScriptPair scripts = scriptFuture.get();

            assertEquals("Robotic behavior did not match expected", scripts.getExpectedScript(), scripts.getObservedScript());
        } finally {
            // clean up the task if it is still running
            scriptFuture.cancel(true);
        }
    }
}
