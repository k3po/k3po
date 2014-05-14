/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.cli;

import java.io.File;

public class NonInteractiveInterpreter extends AbstractInterpreter {

    private final String[] args;

    public NonInteractiveInterpreter(String[] args) {
        super();
        this.args = args;
    }

    private enum SupportedCommand {
        //        START(Command.START),
//        STOP(Command.STOP),
        TEST(Command.TEST),
        HELP(Command.HELP),
        SET_OUTPUT_DIR(Command.SET_OUTPUT_DIR);

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
     * The FileDrivenRobotController Needs to be completed before this is done, once that is finished this class should
     * implement more commands, such as start, stop, and test will not start and stop the robot implicitly
     */
    @Override
    public void run(AbstractRobotController robotController) {
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
//                    case START:
//                        if (args.length == 2) {
//                            final String uri = args[1];
//                            robotController.start(URI.create(uri));
//                        } else if (args.length == 1) {
//                            robotController.start();
//                        } else {
//                            throw new BadCommandException();
//                        }
//                        break;
//                    case STOP:
//                        robotController.stop();
//                        break;
                    case TEST:
                        if (args.length == 3) {
                            final File file = new File(args[1]);
                            robotController.start();
                            robotController.test(file, Integer.valueOf(args[2]));
                            robotController.stop();
                        } else {
                            throw new BadCommandException();
                        }
                        break;
                    case SET_OUTPUT_DIR:
                        if (args.length == 2) {
                            setOutputDir(args[1]);
                        } else {
                            throw new BadCommandException();
                        }
                        break;
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
        println("Usage");
        for (SupportedCommand supportedCommand : SupportedCommand.values()) {
            for (String hint : supportedCommand.cmd.getHints()) {
                println("\t" + hint);
            }
        }
    }

    private class BadCommandException extends Exception {
        public String getMessage() {
            println("Invalid command");
            printHelp();
            return "try using one of the ones listed above....";
        }

    }
}
