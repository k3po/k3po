/**
 * Copyright (c) 2007-2014, Kaazing Corporation. All rights reserved.
 */
def workingdir = project.properties.workingdir

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

myCommand = "./kdc stop ; ./kdc clean ; rm ../krb5.keytab"

if(0 != executeOnShell(myCommand, new File(workingdir))){
    String errorMsg = "FAILED TO RUN: " + myCommand + "-  in: " + workingdir;
    log.error(errorMsg)
    throw new Exception(errorMsg);
}

