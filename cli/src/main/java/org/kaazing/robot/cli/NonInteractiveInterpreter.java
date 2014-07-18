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

import jline.console.ConsoleReader;

import java.io.File;

public class NonInteractiveInterpreter extends AbstractInterpreter {

    private final String[] args;

    public NonInteractiveInterpreter(String[] args) {
        super();
        this.args = args;
    }

    private enum SupportedCommand {
        START(Command.START),
        //        STOP(Command.STOP),
        TEST(Command.TEST),
        //        SET_OUTPUT_DIR(Command.SET_OUTPUT_DIR),
        HELP(Command.HELP);

        private final Command cmd;
        private final String value;

        SupportedCommand(Command cmd) {
            this.cmd = cmd;
            value = cmd.toString();
        }

        public static SupportedCommand fromValue(String value) {
            for (SupportedCommand cmd : values()) {
                if (cmd.value.equalsIgnoreCase(value)) {
                    return cmd;
                }
            }
            throw new IllegalArgumentException("No Supported Command found");
        }
    }

    /**
     * TODO When running the CLI in non interactive mode, it should be possible to launch the robot and leave it running
     * With out blocking the process, a temp file will save the pid so it can be stopped in the future via the Stop
     * Command
     */
    @Override
    public void run(RobotController robotController) {
        try {
            if (args.length < 1) {
                throw new BadCommandException();
            } else {
                SupportedCommand cmd;
                try {
                    cmd = SupportedCommand.fromValue(args[0].toUpperCase());
                } catch (Exception e) {
                    throw new BadCommandException();
                }
                switch (cmd) {
                    case HELP:
                        printHelp();
                        break;
                    case START:
                        if (args.length == 2) {
//                            final String uri = args[1];
//                            URI uri = URI.create(uri)
//                            robotController.startRobotServer();
                            throw new Exception("As of now the non interactive cli does not support " +
                                    "launching the robot on a specified url");
                        } else if (args.length == 1) {
                            robotController.startRobotServer();
                            ConsoleReader reader = new ConsoleReader();
                            reader.setPrompt("Hit enter to kill robot>");
                            reader.readLine();
                            robotController.stopRobotServer();
                        } else {
                            throw new BadCommandException();
                        }
                        break;
//                    case STOP:
//                        robotController.stopRobotServer();
//                        break;
                    case TEST:
                        if (args.length == 3) {
                            final File file = new File(args[1]);
                            robotController.startRobotServer();
                            robotController.test(file, Integer.valueOf(args[2]));
                            robotController.stopRobotServer();
                        } else {
                            throw new BadCommandException();
                        }
                        break;
//                    case SET_OUTPUT_DIR:
//                        if (args.length == 2) {
//                            setOutputDir(args[1]);
//                        } else {
//                            throw new BadCommandException();
//                        }
//                        break;
                    default:
                        throw new BadCommandException();
                }
            }
        } catch (Exception e) {
            println(e.getMessage());
            System.exit(-1);
        }
        System.exit(0);
    }

    @Override
    public void println(String line) {
        System.out.println(line);
    }

    @Override
    public void printHelp() {
        println("Commands:");
        for (SupportedCommand supportedCommand : SupportedCommand.values()) {
            for (String hint : supportedCommand.cmd.getHints()) {
                println("\t" + hint);
            }
        }
    }

    private class BadCommandException extends Exception {
        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        public String getMessage() {
            println("Invalid command");
            printHelp();
            return "try using one of the ones listed above....";
        }

    }
}
