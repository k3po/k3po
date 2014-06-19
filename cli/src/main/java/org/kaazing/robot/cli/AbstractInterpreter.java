/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.cli;

import java.io.File;

public abstract class AbstractInterpreter implements Interpreter {
    private File outputDir;

    public AbstractInterpreter() {
        this(new File("robot-cli-out"));
    }

    public AbstractInterpreter(File defaultOutputDir) {
        this.outputDir = defaultOutputDir;
    }

    @Override
    public File getOutputDir() {
        return outputDir;
    }

    @Override
    public void setOutputDir(String name) throws Exception {
        outputDir = new File(name);
        if (!outputDir.exists()) {
            if (!outputDir.mkdirs()) {
                throw new Exception("Failed to create output directory");
            }
        }
    }
}
