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
import static org.junit.Assert.assertTrue;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import org.junit.rules.Verifier;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runners.model.Statement;
import org.kaazing.k3po.junit.annotation.Specification;
import org.kaazing.net.URLFactory;

/**
 * A K3poRule specifies how a Test using k3po is executed.
 *
 */
public class K3poRule extends Verifier {

    /*
     * For some reason earlier versions of JUnit will cause tests to either hang or succeed incorrectly without ever
     * talking to the K3PO. I'm not sure why but the apply method does not seem to be called. So we need to require
     * version 4.10 (I know 4.7 has the problem ... not sure about 4.8 and 4.9 but 4.10 works).
     */
    static {
        JUnitCore core = new JUnitCore();
        String version = core.getVersion();
        String[] versionTokens = version.split("\\.");
        Integer[] versionsInt = new Integer[versionTokens.length];
        for (int i = 0; i < versionTokens.length; i++) {
            String versionToken = versionTokens[i];
            if (versionToken.contains("-")) {
                versionToken = versionToken.substring(0, versionToken.indexOf("-"));
            }
            versionsInt[i] = Integer.parseInt(versionToken);
        }
        if (versionsInt[0] < 5) {
            if (versionsInt.length == 1 || versionsInt[0] < 4 || versionsInt[1] < 10) {
                throw new AssertionError("JUnit library 4.10+ required. Found version " + version);
            }
        }
    }

    private final Latch latch;
    private String scriptRoot;
    private URL controlURL;
    private SpecificationStatement statement;

    /**
     * Allocates a new K3poRule.
     */
    public K3poRule() {
        latch = new Latch();
    }

    /**
     * Sets the ClassPath root of where to look for scripts when resolving them.
     * @param scriptRoot is a directory/package name of where to resolve scripts from.
     * @return an instance of K3poRule for convenience
     */
    public K3poRule setScriptRoot(String scriptRoot) {
        this.scriptRoot = scriptRoot;
        return this;
    }

    /**
     * Sets the URI on which to communicate to the k3po driver.
     * @param controlURI the URI on which to connect
     * @return an instance of K3poRule for convenience
     */
    public K3poRule setControlURI(URI controlURI) {
        this.controlURL = createURL(controlURI.toString());
        return this;
    }

    @Override
    public Statement apply(Statement statement, final Description description) {

        Specification specification = description.getAnnotation(Specification.class);
        String[] scripts = (specification != null) ? specification.value() : null;

        if (scripts != null) {
            // decorate with K3PO behavior only if @Specification annotation is present
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

            URL controlURL = this.controlURL;
            if (controlURL == null) {
                // lazy dependency on TCP scheme
                controlURL = createURL("tcp://localhost:11642");
            }

            this.statement = new SpecificationStatement(statement, controlURL, scriptNames, latch);
            statement = this.statement;
        }

        return super.apply(statement, description);
    }

    /**
     * Starts the connects in the robot scripts.  The accepts are implicitly started just prior to the test logic in the
     * Specification.
     */
    public void start() {
        // script should already be prepared before annotated test can execute
        assertTrue(format("Did you call start() from outside @%s test?", Specification.class.getSimpleName()),
                latch.isPrepared());

        // notify script to start
        latch.notifyStartable();
    }

    /**
     * Blocking call to await for the K3po threads to stop executing.  If the connects have not already been initiated via the
     * start() method, they will be implicitly called.
     * @throws Exception if an error has occurred in the execution of the tests.
     */
    public void finish() throws Exception {
        assertTrue(format("Did you call finish() from outside @%s test?", Specification.class.getSimpleName()),
                !latch.isInInitState());

        // wait for script to finish
        latch.notifyStartable();

        latch.awaitFinished();
    }

    private static URL createURL(String location) {
        try {
            return URLFactory.createURL("tcp://localhost:11642");
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Wait for barrier to fire.
     * @param barrierName is the name of the barrier to await
     * @throws InterruptedException if await is interrupted
     */
    public void awaitBarrier(String barrierName) throws InterruptedException {
        statement.awaitBarrier(barrierName);
    }

    /**
     * Notify barrier to fire.
     * @param barrierName is the name for the barrier to notify
     * @throws InterruptedException if notify is interrupted (note: waits for confirm that is notified)
     */
    public void notifyBarrier(String barrierName) throws InterruptedException {
        statement.notifyBarrier(barrierName);
    }
}
