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
