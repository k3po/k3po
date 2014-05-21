/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.cli;

import org.kaazing.robot.control.RobotControl;
import org.kaazing.robot.control.command.AbortCommand;
import org.kaazing.robot.control.command.PrepareCommand;
import org.kaazing.robot.control.command.StartCommand;
import org.kaazing.robot.control.event.CommandEvent;
import org.kaazing.robot.control.event.ErrorEvent;
import org.kaazing.robot.control.event.FinishedEvent;
import org.kaazing.robot.control.event.PreparedEvent;

import java.io.File;
import java.io.PrintWriter;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

public abstract class AbstractRobotController implements RobotController {

    protected final Interpreter interpreter;

    public AbstractRobotController(Interpreter interpreter) {
        this.interpreter = interpreter;
    }

    abstract RobotControl getRobotClient() throws Exception;

    @Override
    public void test(File scriptFile, Integer timeout) throws Exception {
        test(getRobotClient(), scriptFile, timeout);
    }

    public void test(RobotControl client, File scriptFile, Integer timeout) throws Exception {
        try {
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

            // startRobotServer
            StartCommand startCommand = new StartCommand();
            startCommand.setName(testName);
            client.writeCommand(startCommand);

            event = client.readEvent(5, TimeUnit.SECONDS);
            switch (event.getKind()) {
                case STARTED:
                    interpreter.println("Script Started");
                    break;
                case ERROR:
                    ErrorEvent errorEvent = (ErrorEvent) event;
                    interpreter.println(errorEvent.getSummary());
                    writeErrorResultToFile(testName, "Failed to start test");
                    throw new Exception("Test Error: Failed to start test");
                default:
                    throw new Exception("Failed to startRobotServer script, Unexpected event: " + event);
            }

            try {
                event = client.readEvent(timeout, TimeUnit.SECONDS);
                switch (event.getKind()) {
                    case FINISHED:
                        FinishedEvent finishedEvent = (FinishedEvent) event;
                        String actualScript = finishedEvent.getScript();
                        if (actualScript.equals(originalScript)) {
                            writePassedResultToFile(testName);
                            interpreter.println("Test passed!");
                            return;
                        } else {
                            writeFailedResultToFile(testName, "Actual did not match expected", originalScript,
                                    actualScript);
                            interpreter.println("Test failed!");
                            interpreter.println("Expected:");
                            interpreter.println("-----------------------");
                            interpreter.println(originalScript);
                            interpreter.println("----------------------");
                            interpreter.println("Actual:");
                            interpreter.println("-----------------------");
                            interpreter.println(actualScript);
                            interpreter.println("-----------------------");
                            return;
                        }
                    default:
                        throw new Exception("Unexpected event when expecting finished event: " + event);
                }
            } catch (Exception e) {
                interpreter.println("Failed to run script:" + e);
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
                        interpreter.println("Test Failed: Scripts are the same but failed because of Timeout");
                        interpreter.println("Actual matches expected");
                    } else {
                        writeFailedResultToFile(testName, "Test Failed: Script timed out",
                                originalScript, actualScript);
                        interpreter.println("Expected:");
                        interpreter.println("-----------------------");
                        interpreter.println(originalScript);
                        interpreter.println("----------------------");
                        interpreter.println("Actual:");
                        interpreter.println("-----------------------");
                        interpreter.println(actualScript);
                        interpreter.println("-----------------------");
                    }
                    return;
                default:
                    throw new Exception("Unexpected event when expecting finished event: " + event);
            }
        } finally {
            if (client != null) {
                try {
                    client.disconnect();
                } catch (Exception e) {
                    // NOOP, Already thrown exceptions may cause this to happen
                }
            }
        }
    }

    protected File getScriptResultOutputDir(String scriptName) throws Exception {
        if (scriptName.endsWith(".rpt")) {
            scriptName = scriptName.substring(0, scriptName.length() - 4);
        }
        File scriptResultDir = new File(interpreter.getOutputDir(), scriptName);
        if (scriptResultDir.exists()) {
            FileUtil.deleteFile(scriptResultDir);
        }
        if (!scriptResultDir.mkdirs()) {
            throw new Exception("Could not create folder to save results in: " + scriptResultDir);
        }
        return scriptResultDir;
    }

    protected PrintWriter getScriptResultFile(File scriptResultDir) throws Exception {
        interpreter.println("Saving results to: " + scriptResultDir);
        return getScriptResultFile(scriptResultDir, "result.txt");
    }

    protected PrintWriter getScriptResultFile(File scriptResultDir, String filename) throws Exception {
        File scriptResultFile = new File(scriptResultDir, filename);
        if (!scriptResultFile.createNewFile()) {
            throw new Exception("Could not create file to save results in: " + scriptResultFile);
        }
        return new PrintWriter(scriptResultFile.getPath());
    }

    protected void writePassedResultToFile(String scriptName) throws Exception {
        File scriptResultDir = getScriptResultOutputDir(scriptName);
        PrintWriter out = getScriptResultFile(scriptResultDir);
        out.println("passed");
        out.close();
    }

    protected void writeFailedResultToFile(String scriptName, String reason, String expected, String actual)
            throws Exception {
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

    protected void writeErrorResultToFile(String scriptName, String reason) throws Exception {
        File scriptResultDir = getScriptResultOutputDir(scriptName);
        PrintWriter out = getScriptResultFile(scriptResultDir);
        out.println("passed");
        out.println("reason: " + reason);
        out.close();
    }
}
