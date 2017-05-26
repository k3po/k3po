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
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import org.junit.AssumptionViolatedException;
import org.junit.ComparisonFailure;
import org.junit.runners.model.MultipleFailureException;
import org.junit.runners.model.Statement;
import org.kaazing.k3po.junit.rules.internal.ScriptPair;

final class SpecificationStatement extends Statement {

    private final Statement statement;
    private final Latch latch;
    private final ScriptRunner scriptRunner;

    SpecificationStatement(Statement statement, URL controlURL, List<String> scriptNames, Latch latch,
            List<String> overridenScriptProperties) {
        this.statement = statement;
        this.latch = latch;
        this.scriptRunner = new ScriptRunner(controlURL, scriptNames, latch, overridenScriptProperties);
    }

    @Override
    public void evaluate() throws Throwable {

        latch.setInterruptOnException(Thread.currentThread());

        FutureTask<ScriptPair> scriptFuture = new FutureTask<>(scriptRunner);

        try {
            // start the script execution
            new Thread(scriptFuture).start();

            try {
                // wait for script to be prepared (all binds ready for incoming connections from statement)
                latch.awaitPrepared();
            } catch (InterruptedException e) {
                if (latch.hasException()) {
                    // propagate exception if the latch has an exception
                    throw latch.getException();
                }
                throw e;
            }

            try {
                // note: JUnit timeout will trigger an exception
                statement.evaluate();
            } catch (Exception cause) {
                // any exception aborts the script (including timeout)
                if (!latch.isFinished()) {
                    scriptRunner.abort();
                }

                // wait at most 5sec for the observed script (due to the abort case)
                // should take less than a second for K3PO to complete
                ScriptPair scripts = null;
                Exception scriptFutureException = null;
                try {
                    scripts = scriptFuture.get(5, SECONDS);
                } catch (Exception e) {
                    scriptFutureException = e;
                }
                // now that the script is stopped, handle exception

                if (cause instanceof AssumptionViolatedException) {
                    throw cause;
                }

                // propagate exception if the latch has an exception
                if (latch.hasException()) {
                    if (cause instanceof InterruptedException) {
                        // take the error from the latch, it is more meaningful
                        throw latch.getException();
                    }
                    throw cause.initCause(latch.getException());
                }

                if (scriptFutureException != null) {
                    if (scriptFutureException instanceof ExecutionException) {
                        throw scriptFutureException.getCause().initCause(cause);
                    }

                    // we will ignore any other exception from scriptFuture.get()
                    throw cause;
                }

                try {
                    assertEquals("Specified behavior did not match", scripts.getExpectedScript(), scripts.getObservedScript());
                } catch (ComparisonFailure f) {
                    // throw an exception that highlights the difference in behavior, but caused by the timeout 
                    // (or original exception)

                    throw new MultipleFailureException(Arrays.asList(f, cause));
                }

                // Throw the original exception if we are equal
                throw cause;
            }

            // note: statement MUST call start() or finish to allow Specification script(s) to make progress
            String k3poSimpleName = K3poRule.class.getSimpleName();
            assertTrue(format("Did you instantiate %s with a @Rule and call %s.start() or %s.finish()?", k3poSimpleName, k3poSimpleName, k3poSimpleName),
                    latch.isStartable());

            ScriptPair scripts = scriptFuture.get();

            assertEquals("Specified behavior did not match", scripts.getExpectedScript(), scripts.getObservedScript());
        } finally {
            try {
                scriptRunner.dispose();
            } finally {
                // clean up the task if it is still running
                scriptFuture.cancel(true);
                Thread.interrupted(); // clear interrupted status
            }
        }
    }

    public void awaitBarrier(String barrierName) throws Exception {
        scriptRunner.awaitBarrier(barrierName);
    }

    public void notifyBarrier(String barrierName) throws Exception {
        scriptRunner.notifyBarrier(barrierName);
    }
}
