/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.junit.rules;

import static org.kaazing.robot.junit.rules.ScriptUtil.readScriptFile;
import static java.lang.String.format;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import org.junit.ComparisonFailure;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import org.kaazing.robot.junit.annotation.Robotic;

final class RoboticStatement extends Statement {

    private final Description description;
    private final Statement statement;
    private final RoboticLatch latch;

    RoboticStatement(Description description, Statement statement, RoboticLatch latch) {
        this.description = description;
        this.statement = statement;
        this.latch = latch;
    }

    @Override
    public void evaluate() throws Throwable {

        // wrapped statement only executes if Robotic annotation is
        // present
        Robotic robotic = description.getAnnotation(Robotic.class);
        assert robotic != null;

        // script is a required attribute on @Robotic
        String scriptName = robotic.script();

        // Get the class which provides the annotated method;
        // we want to look for behavior scripts based on that
        // class' package name.
        Class<?> testClass = description.getTestClass();

        // Discover the script associated with this behavior name
        String expectedScript = readScriptFile(testClass, scriptName);

        ScriptRunner scriptRunner = new ScriptRunner(scriptName, expectedScript, latch);
        FutureTask<String> scriptFuture = new FutureTask<String>(scriptRunner);

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
                        String observedScript = scriptFuture.get(5, SECONDS);

                        try {
                            assertEquals("Robotic behavior did not match", expectedScript, observedScript);
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

            String observedScript = scriptFuture.get();

            assertEquals("Robotic behavior did not match expected", expectedScript, observedScript);
        } finally {
            // clean up the task if it is still running
            scriptFuture.cancel(true);
        }
    }
}
