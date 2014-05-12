/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.cli;

import jline.console.ConsoleReader;
import jline.console.completer.ArgumentCompleter;
import jline.console.completer.Completer;
import jline.console.completer.NullCompleter;
import jline.console.completer.StringsCompleter;
import jline.console.completer.FileNameCompleter;
import jline.console.completer.AggregateCompleter;
import org.kaazing.robot.RobotServer;
import org.kaazing.robot.control.RobotControl;
import org.kaazing.robot.control.RobotControlFactory;
import org.kaazing.robot.control.command.AbortCommand;
import org.kaazing.robot.control.command.PrepareCommand;
import org.kaazing.robot.control.command.StartCommand;
import org.kaazing.robot.control.event.CommandEvent;
import org.kaazing.robot.control.event.ErrorEvent;
import org.kaazing.robot.control.event.FinishedEvent;
import org.kaazing.robot.control.event.PreparedEvent;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public final class RobotCLI {

    private RobotCLI() {

    }

    private static RobotServer server;
    private static URI connectURI = URI.create("tcp://localhost:11642");
    private static RobotControlFactory robotControlFactory = new RobotControlFactory();

    public static final String cmdQuit = "quit";
    public static final String cmdExit = "exit";
    public static final String cmdStartRobot = "start";
    public static final String cmdStopRobot = "stop";
    public static final String cmdRunTest = "test";
    public static final String cmdHelp = "help";
    public static final String cmdSetOutputDir = "setOutputDir";

    private static File outputDir = new File("robot-cli-out");

    public static final List<String> hints = new ArrayList<String>();
    private static RobotControl client;

    public static void main(String... args) throws Exception {
        // to read in from command line
        boolean interactive = true;

        try {
            if (interactive) {
                //interactive
                ConsoleReader reader = new ConsoleReader();

                reader.clearScreen();

                List<Completer> completors = new LinkedList<Completer>();
                // exit
                hints.add(cmdExit);
                completors.add(new ArgumentCompleter(new StringsCompleter(cmdExit)));
                // start robot
                hints.add(cmdStartRobot);
                hints.add(cmdStartRobot + " <ipAddress>");
                completors.add(new ArgumentCompleter(new StringsCompleter(cmdStartRobot), new NullCompleter()));
                // stop robot
                hints.add(cmdStopRobot);
                completors.add(new ArgumentCompleter(new StringsCompleter(cmdStopRobot)));
                // run Test
                hints.add(cmdRunTest + " <scriptFile> <timeout>");
                completors.add(new ArgumentCompleter(new StringsCompleter(cmdRunTest), new FileNameCompleter(),
                        new NullCompleter()));
                // set output dir
                hints.add(cmdSetOutputDir + " <scriptFile>");
                completors.add(new ArgumentCompleter(new StringsCompleter(cmdRunTest), new FileNameCompleter()));
                // help
                hints.add(cmdHelp);
                completors.add(new StringsCompleter(cmdHelp));

                reader.addCompleter(new AggregateCompleter(completors));
                giveHints();
                reader.setPrompt("\u001B[1mrobot>  \u001B[0m");

                String line;
                PrintWriter out = new PrintWriter(reader.getOutput());
                while ((line = reader.readLine()) != null) {
                    String[] tokens = line.split("\\s+");
                    out.flush();
                    if (tokens.length < 1) {
                        badCommand();
                    } else {
                        switch (tokens[0]) {
                            case cmdExit:
                            case cmdQuit:
                                return;
                            case cmdHelp:
                                giveHints();
                                break;
                            case cmdStartRobot:
                                if (tokens.length == 1) {
                                    startRobot();
                                } else if (tokens.length == 2) {
                                    try {
                                        connectURI = URI.create(tokens[1]);
                                        startRobot();
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                        badCommand();
                                    }
                                } else {
                                    badCommand();
                                }
                                break;
                            case cmdStopRobot:
                                stopRobot();
                                break;
                            case cmdRunTest:
                                if (tokens.length == 3) {
                                    runScript(new File(tokens[1]), Integer.valueOf(tokens[2]));
                                } else {
                                    badCommand();
                                }
                                break;
                            case cmdSetOutputDir:
                                if (tokens.length == 2) {
                                    outputDir = new File(tokens[1]);
                                    if (!outputDir.exists()) {
                                        outputDir.mkdir();
                                    }
                                } else {
                                    badCommand();
                                }
                                break;
                            default:
                                badCommand();
                        }
                    }
                }
                return;
            } else {
                // non interactive mode

                return;
            }
        } finally {
            // kill robot driver
            if (server != null) {
                stopRobot();
            }
        }

    }

    static void badCommand() {
        print("invalid command");
        giveHints();
    }

    static void giveHints() {

        print("Usage:");
        for (String hint : hints) {
            print("\t" + hint);
        }
    }

    static void print(String line) {
        System.out.println("\u001B[1m" + line + "\u001B[0m");
    }

    private static final String ROBOT_INFO_FILE = "RobotServerPID.txt";

    static void startRobot() throws Exception {
        if (server == null) {
            server = new RobotServer();
            server.setAccept(connectURI);
            server.setVerbose(false);
            server.join();
            try {
                print("Starting robot");
                server.start();
//                File robotDetailsFile = new File(outputDir, ROBOT_INFO_FILE);
//                server.
                print("Started robot: ");
            } catch (Exception e) {
                e.printStackTrace();
                throw new Exception("Robot failed to start");
            }

            client = robotControlFactory.newClient(connectURI);
        } else {
            print("Robot already running");
        }
    }

    static void stopRobot() throws Exception {
        if (server == null) {
            print("Robot not running, thus can not be stopped");
        } else {
            try {
                print("Stopping robot");
                server.stop();
                print("Stopped robot");
            } catch (Exception e) {
                e.printStackTrace();
                throw new Exception("Robot failed to stop");
            }
            server = null;
        }
    }

    static int runScript(File scriptFile, int timeout) throws Exception {
        boolean startedRobotInThisMethod = false;
        if (server == null) {
            print("Robot is not running, starting robot");
            startRobot();
            startedRobotInThisMethod = true;
        }
        try {
            try {
                client.connect();
            } catch (Exception e) {
                print("Test Error: Failed to connect to robot, is it running?");
                return -1;
            }
            // prepare
            PrepareCommand prepareCommand = new PrepareCommand();

            byte[] encoded = Files.readAllBytes(Paths.get(scriptFile.getAbsolutePath()));
            String originalScript = new String(encoded, StandardCharsets.UTF_8);
            prepareCommand.setScript(originalScript);
            final String testName = scriptFile.getName();
            prepareCommand.setName(testName);
            client.writeCommand(prepareCommand);
            CommandEvent event = client.readEvent(10, TimeUnit.SECONDS);
            if (!(event instanceof PreparedEvent)) {
                throw new Exception("Unexpected event: " + event);
            }

            // start
            StartCommand startCommand = new StartCommand();
            startCommand.setName(testName);
            client.writeCommand(startCommand);

            event = client.readEvent(5, TimeUnit.SECONDS);
            switch (event.getKind()) {
                case STARTED:
                    print("Script Started");
                    break;
                case ERROR:
                    ErrorEvent errorEvent = (ErrorEvent) event;
                    print(errorEvent.getSummary());
                    writeErrorResultToFile(testName, "Failed to start test");
                    print("Test Error: Failed to start test");
                    return -1;
                default:
                    throw new Exception("Failed to start script, Unexpected event: " + event);
            }

            try {
                event = client.readEvent(timeout, TimeUnit.SECONDS);
                switch (event.getKind()) {
                    case FINISHED:
                        FinishedEvent finishedEvent = (FinishedEvent) event;
                        String actualScript = finishedEvent.getScript();
                        if (actualScript.equals(originalScript)) {
                            writePassedResultToFile(testName);
                            print("Test passed!");
                            return 0;
                        } else {
                            writeFailedResultToFile(testName, "Actual did not match expected", originalScript,
                                    actualScript);
                            print("Test failed!");
                            print("Expected:");
                            print("-----------------------");
                            print(originalScript);
                            print("----------------------");
                            print("Actual:");
                            print("-----------------------");
                            print(actualScript);
                            print("-----------------------");
                            return -1;
                        }
                    default:
                        throw new Exception("Unexpected event when expecting finished event: " + event);
                }
            } catch (Exception e) {
                print("Script timed out");
                AbortCommand abort = new AbortCommand();
                client.writeCommand(abort);
            }

            event = client.readEvent(5, TimeUnit.SECONDS);
            switch (event.getKind()) {
                case FINISHED:
                    FinishedEvent finishedEvent = (FinishedEvent) event;
                    String actualScript = finishedEvent.getScript();
                    if (actualScript.equals(originalScript)) {
                        writeFailedResultToFile(testName, originalScript, actualScript, "Script timed out");
                        print("Test Failed: Scripts are the same but failed because of Timeout");
                        print("Actual matches expected");
                    } else {
                        writeFailedResultToFile(testName, "Test Failed: Script timed out",
                                originalScript, actualScript);
                        print("Expected:");
                        print("-----------------------");
                        print(originalScript);
                        print("----------------------");
                        print("Actual:");
                        print("-----------------------");
                        print(actualScript);
                        print("-----------------------");
                    }
                    return -1;
                default:
                    throw new Exception("Unexpected event when expecting finished event: " + event);
            }
        } finally {
            if (startedRobotInThisMethod) {
                stopRobot();
            }
        }
    }

    private static void deleteFile(File file) {
        if (file.isDirectory()) {
            for (File child : file.listFiles()) {
                deleteFile(child);
            }
        } else {
            file.delete();
        }
    }

    private static File getScriptResultOutputDir(String scriptName) {
        if (scriptName.endsWith(".rpt")) {
            scriptName = scriptName.substring(0, scriptName.length() - 4);
        }
        File scriptResultDir = new File(outputDir, scriptName);
        if (scriptResultDir.exists()) {
            deleteFile(scriptResultDir);
        }
        scriptResultDir.mkdirs();
        return scriptResultDir;
    }

    private static PrintWriter getScriptResultFile(File scriptResultDir) throws IOException {
        print("Saving results to: " + scriptResultDir);
        return getScriptResultFile(scriptResultDir, "result.txt");
    }

    private static PrintWriter getScriptResultFile(File scriptResultDir, String filename) throws IOException {
        File scriptResultFile = new File(scriptResultDir, filename);
        scriptResultFile.createNewFile();
        return new PrintWriter(scriptResultFile.getPath());
    }

    private static void writePassedResultToFile(String scriptName) throws IOException {
        File scriptResultDir = getScriptResultOutputDir(scriptName);
        PrintWriter out = getScriptResultFile(scriptResultDir);
        out.println("passed");
        out.close();
    }

    private static void writeFailedResultToFile(String scriptName, String reason, String expected, String actual)
            throws IOException {
        File scriptResultDir = getScriptResultOutputDir(scriptName);
        PrintWriter out = getScriptResultFile(scriptResultDir);
        out.println("failed");
        out.println("reason: " + reason);
        out.close();
        PrintWriter outExpected = getScriptResultFile(scriptResultDir, "expectedScript.rpt");
        outExpected.println(expected);
        outExpected.close();
        PrintWriter outActual = getScriptResultFile(scriptResultDir, "actualScript.rpt");
        outActual.println(actual);
        outActual.close();
    }

    private static void writeErrorResultToFile(String scriptName, String reason) throws IOException {
        File scriptResultDir = getScriptResultOutputDir(scriptName);
        PrintWriter out = getScriptResultFile(scriptResultDir);
        out.println("passed");
        out.println("reason: " + reason);
        out.close();
    }
}
