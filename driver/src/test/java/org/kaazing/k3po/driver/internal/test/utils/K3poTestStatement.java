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

package org.kaazing.k3po.driver.internal.test.utils;

import static java.lang.String.format;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import org.junit.AssumptionViolatedException;
import org.junit.ComparisonFailure;
import org.junit.runners.model.Statement;
import org.kaazing.k3po.driver.internal.Robot;

public class K3poTestStatement extends Statement {

    private final Statement statement;
    private final Latch latch;

    private final List<String> scriptNames;
    private Robot robot;

    public K3poTestStatement(Statement statement, Latch latch, List<String> scriptNames) {
        this.latch = latch;
        this.statement = statement;
        this.scriptNames = scriptNames;
    }

    @Override
    public void evaluate() throws Throwable {

        robot = new Robot();
        ScriptTestRunner scriptRunner = new ScriptTestRunner(scriptNames, latch, robot);
        FutureTask<ScriptPair> scriptFuture = new FutureTask<>(scriptRunner);

        try {
            // start the script execution
            new Thread(scriptFuture).start();

            // wait for script to be prepared (all binds ready for incoming connections from statement)
            latch.awaitPrepared();

            try {
                // note: JUnit timeout will trigger an exception
                statement.evaluate();
            } catch (AssumptionViolatedException e) {

                if (!latch.isFinished()) {
                    scriptRunner.abort();
                }

                throw e;
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
                        // should take less than a second for K3PO to complete
                        ScriptPair scripts = scriptFuture.get(5, SECONDS);

                        try {
                            assertEquals("Specified behavior did not match", scripts.getExpectedScript(),
                                    scripts.getObservedScript());
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
            // and to allow Specification script(s) to make progress
            assertTrue(format("Did you call %s.finish()?", K3poTestRule.class.getSimpleName()), latch.isStartable());

            ScriptPair scripts = scriptFuture.get();

            String expectedScript = scripts.getExpectedScript();
            String observedScript = scripts.getObservedScript();
            assertEquals("Specified behavior did not match", expectedScript, observedScript);
        } finally {
            // clean up the task if it is still running
            scriptFuture.cancel(true);
            robot.dispose().await();
        }
    }

    public void awaitBarrier(String barrierName) throws Exception {
        robot.awaitBarrier(barrierName);
    }

    public void notifyBarrier(String barrierName) throws Exception {
        robot.notifyBarrier(barrierName);
    }

}
