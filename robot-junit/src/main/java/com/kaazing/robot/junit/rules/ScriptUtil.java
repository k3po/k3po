/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package com.kaazing.robot.junit.rules;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.CharBuffer;

final class ScriptUtil {

    private static final String SCRIPT_BASE = "META-INF/robot-scripts";

    static String readScriptFile(Class<?> c, String scriptName) throws IOException {

        InputStream in = getScriptFile(c, scriptName);
        Reader r = new InputStreamReader(in);

        StringBuilder sb = new StringBuilder();
        CharBuffer target = CharBuffer.allocate(8192);
        while (r.ready()) {
            int charsRead = r.read(target);
            if (charsRead == -1) {
                break;
            }

            sb.append(target.flip());
            target.rewind();
        }

        return sb.toString();
    }

    // Return the number of "levels" in the given package name. For example,
    // a package name of "com.kaazing.rupert.test" has 4 levels.

    static int getPackageNameLevelCount(String packageName) {
        int levelCount = 1;

        for (int i = 0; i < packageName.length(); i++) {
            if (packageName.charAt(i) == '.') {
                levelCount++;
            }
        }

        return levelCount;
    }

    // Return the number of "levels" in the given script name. For example,
    // a script name of "../../script.rpt" has two levels; we ignore the
    // leaf "script.rpt".
    static int getScriptNameLevelCount(String scriptName) {
        int levelCount = 0;

        // Work backwards, looking for '/' characters.
        int idx = scriptName.lastIndexOf('/');
        while (idx >= 0) {
            levelCount++;

            idx = scriptName.lastIndexOf('/', idx - 1);
        }

        return levelCount;
    }

    /*
     * First, look for a <b>file</b> in
     * "src/test/scripts/<i>package</>/<i>script</i>.rpt", for the cases where
     * we are running in a unit/integration test.
     *
     * If not found there, look for a <b>resource</b> with
     * "META-INF/robot-scripts/<i>package</i>/<i>script</i>.rpt".
     *
     * If not found there, look in the default/base location of
     * "META-INF/robot-scripts/<i>script</i>.rpt".
     *
     * Make sure that we handle "relative path" script names, such as
     * "../../<i>script</i>.rpt"
     */
    static InputStream getScriptFile(Class<?> c, String scriptName) throws IOException {

        ClassLoader loader = ScriptUtil.class.getClassLoader();
        String packageName = c.getPackage().getName();

        int packageLevelCount = getPackageNameLevelCount(packageName);
        int scriptLevelCount = getScriptNameLevelCount(scriptName);

        if (scriptLevelCount > packageLevelCount) {
            System.err.println(String.format(
                    "Relative path script name '%s' tries to refer above package '%s', rejecting script name", scriptName,
                    packageName));
            throw new RuntimeException(String.format("Illegal relative path to script '%s'", scriptName));
        }

        String resourceURL = null;
        URL resource = null;

        // First, check in the src/test/scripts, package-specific location
        resourceURL = String.format("src/test/scripts/%s/%s.rpt", packageName.replace('.', '/'), scriptName);
        File f = new File(resourceURL);
        if (f.exists() && f.isFile()) {
            System.err.println(String.format("Using script %s from '%s'", scriptName, resourceURL));
            return new FileInputStream(f);
        }

        // Next, check in the package-specific location
        resourceURL = String.format("%s/%s/%s.rpt", SCRIPT_BASE, packageName.replace('.', '/'), scriptName);
        resource = loader.getResource(resourceURL);
        if (resource != null) {
            System.err.println(String.format("Using script %s from '%s'", scriptName, resourceURL));
            return resource.openStream();
        }

        // If the resource was not been found, try the default,
        // non-package-specific location.
        // URL at a non-package-specific location.
        resourceURL = String.format("%s/%s.rpt", SCRIPT_BASE, scriptName);
        resource = loader.getResource(resourceURL);
        if (resource == null) {
            throw new RuntimeException(String.format("Unable to locate script '%s'", scriptName));
        }

        return resource.openStream();
    }

    private ScriptUtil() {
        // utility class
    }
}
