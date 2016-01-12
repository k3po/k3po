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
def workingdir = project.properties.workingdir
def mvnParams = project.properties.mvnParams

if (mvnParams == null) {
    mvnParams = ""
}

def executeOnShell(String command) {
    return executeOnShell(command, new File(workingdir))
}

private def executeOnShell(String command, File workingDir) {
    println command
    def process = new ProcessBuilder(addShellPrefix(command))
            .directory(workingDir)
            .redirectErrorStream(true)
            .start()
    process.inputStream.eachLine {println "--> " + it}
    process.waitFor();
    return process.exitValue()
}

private def addShellPrefix(String command) {
    commandArray = new String[3]
    commandArray[0] = "bash"
    commandArray[1] = "-c"
    commandArray[2] = command
    return commandArray
}

myCommand = "mvn clean install " + mvnParams;

if(0 != executeOnShell("mvn clean install " + mvnParams, new File(workingdir))){
    String errorMsg = "FAILED TO RUN: " + myCommand + "-  in: " + workingdir;
    log.error(errorMsg)
    throw new Exception(errorMsg);
}

