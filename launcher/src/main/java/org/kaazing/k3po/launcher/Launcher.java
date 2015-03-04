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

package org.kaazing.k3po.launcher;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.Parser;
import org.apache.commons.cli.PosixParser;
import org.kaazing.k3po.driver.RobotServer;

public final class Launcher {

    private Launcher() {
        // Added since the checkstyle-plugin is complaining about Utility
        // classes should not have a public or default constructor.
    }

    public static void main(String... args) throws Exception {
        Options options = createOptions();

        try {
            Parser parser = new PosixParser();
            CommandLine cmd = parser.parse(options, args);
            String scriptPath = cmd.getOptionValue("scriptPath");

            File scriptEntryFilePath = new File(scriptPath);

            if (!scriptEntryFilePath.exists()) {
                throw new FileNotFoundException(args[0]);
            }

            String controlURI = cmd.getOptionValue("control");
            if (controlURI == null) {
                controlURI = "tcp://localhost:11642";
            }

            boolean verbose = cmd.hasOption("verbose");

            System.out.println("Script Path: " + scriptPath);
            System.out.println("Control URI: " + controlURI);

            URL scriptPathEntry = scriptEntryFilePath.toURI().toURL();
            URLClassLoader scriptLoader = new URLClassLoader(new URL[]{scriptPathEntry});
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
        Option scriptPath = new Option(null, "scriptPath", true, "path to directory/jar for script(s) lookup");
        scriptPath.setRequired(true);
        Option control = new Option(null, "control", true, "location to listen for K3PO control connections");
        Option verbose = new Option(null, "verbose", false, "verbose");
        options.addOption(scriptPath);
        options.addOption(control);
        options.addOption(verbose);
        return options;
    }
}
