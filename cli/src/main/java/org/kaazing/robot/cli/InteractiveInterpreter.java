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
import jline.console.completer.ArgumentCompleter;
import jline.console.completer.Completer;
import jline.console.completer.NullCompleter;
import jline.console.completer.StringsCompleter;
import jline.console.completer.FileNameCompleter;
import jline.console.completer.AggregateCompleter;

import java.io.PrintWriter;
import java.io.IOException;
import java.io.File;
import java.net.URI;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class InteractiveInterpreter extends AbstractInterpreter {

    private static final List<Completer> completors = new LinkedList<>();
    private final ConsoleReader reader;
    private final PrintWriter out;

    public InteractiveInterpreter() throws IOException {
        super();
        for (SupportedCommand cmd : SupportedCommand.values()) {
            Collections.addAll(completors, cmd.getCompleters());
        }
        reader = new ConsoleReader();
        out = new PrintWriter(reader.getOutput());
    }

    private static class CommandCompleter extends StringsCompleter {
        public CommandCompleter(Command cmd) {
            super(cmd.toString());
        }
    }

    private enum SupportedCommand {
        // Quit has no completers because we don't advertise its existence
        QUIT(Command.QUIT, new Completer[]{}),
        EXIT(Command.EXIT, new Completer[]{new CommandCompleter(Command.EXIT)}),
        START(Command.START, new Completer[]{new CommandCompleter(Command.START),
                new ArgumentCompleter(new CommandCompleter(Command.EXIT), new NullCompleter())}),
        STOP(Command.STOP, new Completer[]{new CommandCompleter(Command.STOP)}),
        TEST(Command.TEST, new Completer[]{new ArgumentCompleter(new CommandCompleter(Command.TEST),
                new FileNameCompleter(), new NullCompleter())}),
        HELP(Command.HELP, new Completer[]{new CommandCompleter(Command.HELP)}),
        SET_OUTPUT_DIR(Command.SET_OUTPUT_DIR, new Completer[]{new ArgumentCompleter(
                new CommandCompleter(Command.SET_OUTPUT_DIR), new FileNameCompleter())});

        private final Command cmd;
        private final Completer[] completers;
        private final String value;

        SupportedCommand(Command cmd, Completer[] completers) {
            this.cmd = cmd;
            this.completers = completers;
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

        public Completer[] getCompleters() {
            return completers;
        }
    }


    @Override
    public void run(RobotController robotController) {
        try {
            reader.clearScreen();
            reader.addCompleter(new AggregateCompleter(completors));
            reader.setPrompt("\u001B[1mRobot$ \u001B[0m");
            String line;

            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split("\\s+");
                if (tokens.length < 1) {
                    printHelp();
                } else {
                    try {
                        SupportedCommand cmd;
                        try {
                            cmd = SupportedCommand.fromValue(tokens[0]);
                        } catch (Exception e) {
                            throw new Exception("Invalid command");
                        }
                        switch (cmd) {
                            case EXIT:
                            case QUIT:
                                System.exit(0);
                                return;
                            case HELP:
                                printHelp();
                                break;
                            case START:
                                if (tokens.length == 2) {
                                    final String uri = tokens[1];
                                    robotController.setURI(URI.create(uri));
                                    robotController.startRobotServer();
                                } else if (tokens.length == 1) {
                                    robotController.startRobotServer();
                                } else {
                                    throw new Exception("Invalid command");
                                }
                                break;
                            case STOP:
                                robotController.stopRobotServer();
                                break;
                            case TEST:
                                if (tokens.length == 3) {
                                    final File file = new File(tokens[1]);
                                    robotController.test(file, Integer.valueOf(tokens[2]));
                                } else {
                                    throw new Exception("Invalid command");
                                }
                                break;
                            case SET_OUTPUT_DIR:
                                if (tokens.length == 2) {
                                    setOutputDir(tokens[1]);
                                } else {
                                    throw new Exception("Invalid command");
                                }
                                break;
                            default:
                                throw new Exception("Invalid command");
                        }
                    } catch (Exception e) {
                        println("Failed to run command: " + e.getMessage());
                        printHelp();
                    }
                }
            }
        } catch (IOException e) {

            System.exit(-1);
        }
    }

    @Override
    public void println(String line) {
        out.println("\u001B[1m" + line + "\u001B[0m");
        out.flush();
    }

    @Override
    public void printHelp() {
        println("Commands:");
        for (SupportedCommand supportedCommand : SupportedCommand.values()) {
            for (String hint : supportedCommand.cmd.getHints()) {
                println(hint);
            }
        }
    }

}
