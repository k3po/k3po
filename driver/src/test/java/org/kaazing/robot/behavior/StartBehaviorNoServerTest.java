/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.behavior;

public class StartBehaviorNoServerTest {
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
    // public void startBehaviorConnectionRefused()
    // throws Exception {
    //
    // String behaviorName = "foo";
    // boolean sawExpectedEx = false;
    //
    // try {
    // client.startBehavior(behaviorName, scriptFile);
    //
    // } catch (RobotBehaviorException rbe) {
    // String expected = "Connection refused";
    //
    // if (rbe.getMessage().contains(expected)) {
    // sawExpectedEx = true;
    // }
    // }
    //
    // Assert.assertTrue("Did not see expected RobotBehaviorException",
    // sawExpectedEx);
    // }
}
