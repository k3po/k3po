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

package org.kaazing.robot.driver.behavior;

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
