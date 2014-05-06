/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package com.kaazing.robot.junit.rules;

import static java.lang.String.format;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;

import org.junit.Test;

public class ScriptUtilTest {

    @Test
    public void getPackageNameLevelCount() throws Exception {

        String packageName = "com.kaazing.rupert.rtests.tcp";
        int count = ScriptUtil.getPackageNameLevelCount(packageName);

        int expected = 5;
        assertTrue(format("Expected %d levels in package '%s', got %d", expected, packageName, count), count == expected);

        packageName = "kaazing";
        count = ScriptUtil.getPackageNameLevelCount(packageName);
        expected = 1;
        assertTrue(format("Expected %d levels in package '%s', got %d", expected, packageName, count), count == expected);
    }

    @Test
    public void getScriptNameLevelCount() throws Exception {

        String scriptName = "tcp.client.connect-then-close";
        int count = ScriptUtil.getScriptNameLevelCount(scriptName);

        int expected = 0;
        assertTrue(format("Expected %d levels in script '%s', got %d", expected, scriptName, count), count == expected);

        scriptName = "../../../tcp.client.connect-then-close";
        count = ScriptUtil.getScriptNameLevelCount(scriptName);
        expected = 3;
        assertTrue(format("Expected %d levels in script '%s', got %d", expected, scriptName, count), count == expected);
    }

    @Test
    public void getScriptFile() throws Exception {

        // XXX Need to add tests/smarts here

        String behaviorName = "tcp-client";
        InputStream is = ScriptUtil.getScriptFile(getClass(), behaviorName);
        assertTrue(format("Expected InputStream for '%s', found null", behaviorName), is != null);
        is.close();

        behaviorName = "tcp-server";
        is = ScriptUtil.getScriptFile(getClass(), behaviorName);
        assertTrue(format("Expected InputStream for '%s', found null", behaviorName), is != null);
        is.close();

        behaviorName = "my-tcp-server";
        is = ScriptUtil.getScriptFile(getClass(), behaviorName);
        assertTrue(format("Expected InputStream for '%s', found null", behaviorName), is != null);
        is.close();

        behaviorName = "../../my-tcp-client";
        is = ScriptUtil.getScriptFile(getClass(), behaviorName);
        assertTrue(format("Expected InputStream for '%s', found null", behaviorName), is != null);
        is.close();

        boolean sawExpectedEx = false;

        try {
            behaviorName = "this-script-does-not-exist";
            is = ScriptUtil.getScriptFile(getClass(), behaviorName);

        }
        catch (Exception e) {
            if (e instanceof RuntimeException) {
                if (e.getMessage().contains("Unable to locate script")) {
                    sawExpectedEx = true;
                }
            }
        }

        assertTrue(
                format("Expected 'Unable to locate script' RuntimeException for script '%s', found an InputStream unexpectedly",
                        behaviorName), sawExpectedEx);

        sawExpectedEx = false;

        try {
            behaviorName = "../../../../../../my-tcp-client";
            is = ScriptUtil.getScriptFile(getClass(), behaviorName);

        }
        catch (Exception e) {
            if (e instanceof RuntimeException) {
                if (e.getMessage().contains("Illegal relative path to script")) {
                    sawExpectedEx = true;
                }
            }
        }

        assertTrue(
                format("Did not see expected 'Illegal relative path to script' RuntimeException for script '%s'", behaviorName),
                sawExpectedEx);
    }
}
