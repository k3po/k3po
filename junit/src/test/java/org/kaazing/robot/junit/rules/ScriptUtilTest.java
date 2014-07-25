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
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

public class ScriptUtilTest {

    private InputStream getScriptInputStream(Class<?> c, String scriptName) throws IOException{
        String path = ScriptUtil.getScriptPath(c, scriptName);
        File file = new File(path);
        return new FileInputStream(file);
    }

    @Test
    public void getPackageNameLevelCount() throws Exception {

        String packageName = "org.kaazing.rupert.rtests.tcp";
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
        InputStream is = getScriptInputStream(getClass(), behaviorName);
        assertTrue(format("Expected InputStream for '%s', found null", behaviorName), is != null);
        is.close();

        behaviorName = "tcp-server";
        is = getScriptInputStream(getClass(), behaviorName);
        assertTrue(format("Expected InputStream for '%s', found null", behaviorName), is != null);
        is.close();

        behaviorName = "my-tcp-server";
        is = getScriptInputStream(getClass(), behaviorName);
        assertTrue(format("Expected InputStream for '%s', found null", behaviorName), is != null);
        is.close();

        behaviorName = "../../my-tcp-client";
        is = getScriptInputStream(getClass(), behaviorName);
        assertTrue(format("Expected InputStream for '%s', found null", behaviorName), is != null);
        is.close();

        boolean sawExpectedEx = false;

        try {
            behaviorName = "this-script-does-not-exist";
            is = getScriptInputStream(getClass(), behaviorName);

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
            is = getScriptInputStream(getClass(), behaviorName);

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
