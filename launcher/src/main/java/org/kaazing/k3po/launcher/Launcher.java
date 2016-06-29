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
package org.kaazing.k3po.launcher;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.Parser;
import org.apache.commons.cli.PosixParser;
import org.kaazing.k3po.driver.internal.RobotServer;

/**
 * Launcher / CLI to run the K3PO.
 *
 */
public final class Launcher {

    private Launcher() {
        // no instances
    }

    /**
     * Main entry point to running K3PO.
     * @param args to run with
     * @throws Exception if fails to run
     */
    public static void main(String... args) throws Exception {
        Options options = createOptions();

        try {
            Parser parser = new PosixParser();
            CommandLine cmd = parser.parse(options, args);

            if (cmd.hasOption("version")) {
                System.out.println("Version: " + Launcher.class.getPackage().getImplementationVersion());
            }

            String scriptPathEntries = cmd.getOptionValue("scriptpath", "src/test/scripts");
            String[] scriptPathEntryArray = scriptPathEntries.split(";");
            List<URL> scriptUrls = new ArrayList<>();

            for (String scriptPathEntry : scriptPathEntryArray) {
                File scriptEntryFilePath = new File(scriptPathEntry);
                scriptUrls.add(scriptEntryFilePath.toURI().toURL());
            }

            String controlURI = cmd.getOptionValue("control");
            if (controlURI == null) {
                controlURI = "tcp://localhost:11642";
            }

            boolean verbose = cmd.hasOption("verbose");

            URLClassLoader scriptLoader = new URLClassLoader(scriptUrls.toArray(new URL[0]));
            RobotServer server = new RobotServer(URI.create(controlURI), verbose, scriptLoader);
            server.start();
            server.join();
        } catch (ParseException ex) {
            HelpFormatter helpFormatter = new HelpFormatter();
            helpFormatter.printHelp(Launcher.class.getSimpleName(), options, true);
        }

    }

    private static Options createOptions() {
        Options options = new Options();
        Option scriptPath = new Option(null, "scriptpath", true,
                "Path(s) to directory/jar for script(s) lookup. Multiple entries should be separated by semicolon.");
        Option control = new Option(null, "control", true, "location to listen for K3PO control connections");
        Option verbose = new Option(null, "verbose", false, "verbose");
        Option version = new Option(null, "version", false, "version");
        options.addOption(scriptPath);
        options.addOption(control);
        options.addOption(verbose);
        options.addOption(version);
        return options;
    }
}
