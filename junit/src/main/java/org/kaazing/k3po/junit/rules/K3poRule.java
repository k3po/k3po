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
import static org.junit.Assert.assertTrue;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.junit.AssumptionViolatedException;
import org.junit.rules.Verifier;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runners.model.Statement;
import org.kaazing.k3po.junit.annotation.ScriptProperty;
import org.kaazing.k3po.junit.annotation.Specification;
import org.kaazing.net.URLFactory;

/**
 * A K3poRule specifies how a Test using k3po is executed.
 *
 */
public class K3poRule extends Verifier {

    private static final String VERSION_SEP = "-";

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
            if (versionToken.contains(VERSION_SEP)) {
                versionToken = versionToken.substring(0, versionToken.indexOf(VERSION_SEP));
            }
            versionsInt[i] = Integer.parseInt(versionToken);
        }
        if (versionsInt[0] < 5 && (versionsInt.length == 1 || versionsInt[0] < 4 || versionsInt[1] < 10)) {
            throw new AssertionError("JUnit library 4.10+ required. Found version " + version);
        }
    }

    private final Latch latch;
    private String scriptRoot;
    private URL controlURL;
    private SpecificationStatement statement;
    private List<String> classOverriddenProperties;

    /**
     * Allocates a new K3poRule.
     */
    public K3poRule() {
        latch = new Latch();
        classOverriddenProperties = new ArrayList<>();
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
     * @throws MalformedURLException 
     */
    public K3poRule setControlURI(URI controlURI) throws MalformedURLException {
        this.controlURL = URLFactory.createURL(controlURI.toString());
        return this;
    }

    @Override
    public Statement apply(Statement callersStatement, final Description description) {

        String[] scriptNames = getScriptNames(description);
        // decorate with K3PO behavior only if @Specification annotation is present
        if (scriptNames != null) {

            List<String> methodOverridenScriptProperties = getOverridenProperties(description);

            List<String> scriptLocations = getScriptLocations(description, scriptNames);

            setControlURL();

            methodOverridenScriptProperties.addAll(classOverriddenProperties);
            this.statement = new SpecificationStatement(callersStatement, controlURL, scriptLocations, latch,
                    methodOverridenScriptProperties);
            return super.apply(this.statement, description);
        } else {
            return super.apply(callersStatement, description);
        }

    }

    private void setControlURL() {
        if (this.controlURL == null) {
            try {
                this.controlURL = URLFactory.createURL("tcp://localhost:11642");
            } catch (MalformedURLException e) {
                throw new AssumptionViolatedException("K3po Control URL could not be set", e);
            }
        }
    }

    private String[] getScriptNames(final Description description) {
        Specification specification = description.getAnnotation(Specification.class);
        return (specification != null) ? specification.value() : null;
    }

    private List<String> getScriptLocations(final Description description, String[] scriptNames) {
        String packagePath = this.scriptRoot;
        if (packagePath == null) {
            Class<?> testClass = description.getTestClass();
            String packageName = testClass.getPackage().getName();
            packagePath = packageName.replaceAll("\\.", "/");
        }

        List<String> scriptLocations = new LinkedList<>();
        for (String script : scriptNames) {
            // strict compatibility (relax to support fully qualified paths later)
            if (script.startsWith("/")) {
                throw new IllegalArgumentException("Script path must be relative");
            }

            String scriptName = format("%s/%s", packagePath, script);
            scriptLocations.add(scriptName);
        }
        return scriptLocations;
    }

    private List<String> getOverridenProperties(final Description description) {
        List<String> methodOverridenScriptProperties = new ArrayList<>();
        ScriptProperty annotation = description.getAnnotation(ScriptProperty.class);
        if (annotation != null && annotation.value() != null) {
            methodOverridenScriptProperties.addAll(Arrays.asList(annotation.value()));
        }
        return methodOverridenScriptProperties;
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

    /**
     * Wait for barrier to fire.
     * @param barrierName is the name of the barrier to await
     * @throws InterruptedException if await is interrupted
     */
    public void awaitBarrier(String barrierName) throws InterruptedException {
        statement.awaitBarrier(barrierName);
    }

    /**
     * Overrides a script property.
     * @param property of script
     * @return K3po rule for convenience
     */
    public K3poRule scriptProperty(String property) {
        this.classOverriddenProperties.add(property);
        return this;
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
