/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.cli;

        import java.io.File;

public interface Interpreter {

    void run(RobotController robotController);

    File getOutputDir();

    //so text can be formatted correctly depending on the view
    void println(String line);

    void setOutputDir(String name) throws Exception;

    void printHelp();
}
