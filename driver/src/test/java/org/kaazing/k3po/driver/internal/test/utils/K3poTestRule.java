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
import static org.junit.Assert.assertTrue;

import java.util.LinkedList;
import java.util.List;

import org.junit.rules.Verifier;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class K3poTestRule extends Verifier {

    private String scriptRoot;
    private final Latch latch;
    private K3poTestStatement k3poTestStatement;

    public K3poTestRule() {
        latch = new Latch();
    }

    public K3poTestRule setScriptRoot(String scriptRoot) {
        this.scriptRoot = scriptRoot;
        return this;
    }

    @Override
    public Statement apply(Statement statement, final Description description) {
        TestSpecification testSpecification = description.getAnnotation(TestSpecification.class);
        String[] scripts = (testSpecification != null) ? testSpecification.value() : null;

        if (scripts != null) {

            String packagePath = this.scriptRoot;
            if (packagePath == null) {
                Class<?> testClass = description.getTestClass();
                String packageName = testClass.getPackage().getName();
                packagePath = packageName.replaceAll("\\.", "/");
            }

            List<String> scriptNames = new LinkedList<>();
            for (int i = 0; i < scripts.length; i++) {
                // strict compatibility (relax to support fully qualified paths later)
                if (scripts[i].startsWith("/")) {
                    throw new IllegalArgumentException("Script path must be relative");
                }

                String scriptName = format("%s/%s", packagePath, scripts[i]);
                scriptNames.add(scriptName);
            }

            this.k3poTestStatement = new K3poTestStatement(statement, latch, scriptNames);
            statement = this.k3poTestStatement;
        }

        return super.apply(statement, description);
    }

    public void finish() throws Exception {
        assertTrue(format("Did you call finish() from outside @%s test?", TestSpecification.class.getSimpleName()),
                latch.isPrepared());

        // notify script to start
        latch.notifyStartable();

        // wait for script to finish
        latch.awaitFinished();
    }

    /**
     * Wait for barrier to fire
     * @param string
     * @throws Exception
     */
    public void awaitBarrier(String barrierName) throws Exception {
        k3poTestStatement.awaitBarrier(barrierName);
    }

    /**
     * Notify barrier to fire
     * @param string
     * @throws Exception
     */
    public void notifyBarrier(String barrierName) throws Exception {
        k3poTestStatement.notifyBarrier(barrierName);
    }

}
