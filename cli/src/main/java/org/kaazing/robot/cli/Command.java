/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.cli;

public enum Command {
    QUIT("quit", new String[]{"quit"}),
    EXIT("exit", new String[]{"exit"}),
    START("start", new String[]{"start", "start <ipAddress>"}),
    STOP("stop", new String[]{"stop"}),
    TEST("test", new String[]{"test <scriptFile> <timeout>"}),
    HELP("help", new String[]{"help"}),
    SET_OUTPUT_DIR("setOutputDir", new String[]{"setOutputDir <directory>"});

    private final String readableCmd;
    private final String[] hints;

    Command(String readableCmd, String[] hints) {
        this.readableCmd = readableCmd;
        this.hints = hints;
    }

    @Override
    public String toString() {
        return readableCmd;
    }

    public String[] getHints() {
        return hints;
    }
}
