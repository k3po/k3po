/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.behavior;

public class StartBehaviorTest {
    // public static final long TEST_TIMEOUT = 10000;
    //
    // public static String robotHost = "localhost";
    //
    // // Make this more dynamic; have the RobotServer choose the port, and
    // // set a system property with the chosen value
    // public static int robotPort = 10101;
    //
    // public RobotServer server;
    // public RobotClient client;
    //
    // public File scriptFile = null;
    //
    // private void createScriptFile()
    // throws Exception {
    //
    // String tmpSuffix = ".rpt";
    // String srcPrefix = "test.";
    //
    // try {
    // scriptFile = File.createTempFile(srcPrefix, tmpSuffix);
    //
    // } catch (Exception e) {
    // if (scriptFile != null) {
    // scriptFile.delete();
    // scriptFile = null;
    // }
    //
    // throw e;
    // }
    // }
    //
    // private void destroyScriptFile() {
    // if (scriptFile != null) {
    // scriptFile.delete();
    // scriptFile = null;
    // }
    // }
    //
    // @Before
    // public void setUp()
    // throws Exception {
    //
    // server = new RobotServer(robotHost, robotPort);
    // server.start();
    //
    // client = new RobotClient(robotHost, robotPort);
    // client.setSessionTimeout(TEST_TIMEOUT);
    // client.start();
    // }
    //
    // @After
    // public void tearDown()
    // throws Exception {
    // client.stop();
    // server.stop();
    // }
    //
    // @Test
    // public void startBehaviorOK()
    // throws Exception {
    //
    // createScriptFile();
    //
    // String behaviorName = "robot.test";
    // String behaviorID = null;
    //
    // try {
    // BufferedWriter writer = new BufferedWriter(new FileWriter(scriptFile));
    //
    // writer.write("# tcp.server.accept-then-close");
    // writer.newLine();
    // writer.write("accept tcp://localhost:7788");
    // writer.newLine();
    // writer.write("connected");
    // writer.newLine();
    // writer.write("close");
    // writer.newLine();
    // writer.write("closed");
    // writer.newLine();
    // writer.close();
    //
    // behaviorID = client.startBehavior(behaviorName, scriptFile);
    //
    // int code = client.getStatusCode();
    // Assert.assertTrue(String.format("Expected 201 status code, got %d",
    // code), code == 201);
    // Assert.assertTrue(String.format("Expected non-null behavior ID"),
    // behaviorID != null);
    //
    // } finally {
    // client.finishBehavior(behaviorName, behaviorID);
    // destroyScriptFile();
    // }
    // }
    //
    // @Test
    // public void startBehaviorWithTimeoutOK()
    // throws Exception {
    //
    // createScriptFile();
    //
    // String behaviorName = "robot.test";
    // String behaviorID = null;
    //
    // try {
    // BufferedWriter writer = new BufferedWriter(new FileWriter(scriptFile));
    //
    // writer.write("# tcp.server.accept-then-close");
    // writer.newLine();
    // writer.write("accept tcp://localhost:7788");
    // writer.newLine();
    // writer.write("connected");
    // writer.newLine();
    // writer.write("close");
    // writer.newLine();
    // writer.write("closed");
    // writer.newLine();
    // writer.close();
    //
    // Long behaviorTimeout = Long.valueOf(1000);
    // behaviorID = client.startBehavior(behaviorName, scriptFile,
    // behaviorTimeout);
    //
    // int code = client.getStatusCode();
    // Assert.assertTrue(String.format("Expected 201 status code, got %d",
    // code), code == 201);
    // Assert.assertTrue(String.format("Expected non-null behavior ID"),
    // behaviorID != null);
    //
    // } finally {
    // client.finishBehavior(behaviorName, behaviorID);
    // destroyScriptFile();
    // }
    // }
}
