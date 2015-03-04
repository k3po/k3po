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

package org.kaazing.k3po.executable;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.Parser;
import org.apache.commons.cli.PosixParser;
import org.kaazing.k3po.driver.RobotServer;

public class Main {
    
    public static void main(String... args) throws Exception {
        
        CommandLine cmd = null;
        Options options = createOptions();

        Parser parser = new PosixParser();
        cmd = parser.parse(options, args);
        
        String scriptPath = cmd.getOptionValue("scriptPath");
        if (scriptPath == null) {
            throw new IllegalArgumentException("The required option scriptPath is missing");
        }
        
        File scriptEntryFilePath = new File(scriptPath);
        
        if (!scriptEntryFilePath.exists()) {
            throw new FileNotFoundException(args[0]);
        }
        
        URL scriptPathEntry = scriptEntryFilePath.toURI().toURL();
        URLClassLoader scriptLoader = new URLClassLoader(new URL[]{scriptPathEntry});
        RobotServer server = new RobotServer(URI.create("tcp://localhost:11642"), true, scriptLoader);
        server.start();
        server.join();
    }
    
    private static Options createOptions() {
        Options options = new Options();
        options.addOption(null, "scriptPath", true, "path to directory/jar for script(s) lookup");
        return options;
    }
}
