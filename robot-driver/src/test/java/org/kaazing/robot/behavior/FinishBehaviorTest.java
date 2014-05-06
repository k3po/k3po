/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.behavior;

public class FinishBehaviorTest {
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
    // public File srcFile = null;
    // public File dstFile = null;
    //
    // private void createTempFiles()
    // throws Exception {
    //
    // String tmpSuffix = ".test";
    // String srcPrefix = "src.";
    // String dstPrefix = "dst.";
    //
    // try {
    // srcFile = File.createTempFile(srcPrefix, tmpSuffix);
    // dstFile = File.createTempFile(dstPrefix, tmpSuffix);
    //
    // } catch (Exception e) {
    // if (srcFile != null) {
    // srcFile.delete();
    // srcFile = null;
    // }
    //
    // if (dstFile != null) {
    // dstFile.delete();
    // dstFile = null;
    // }
    //
    // throw e;
    // }
    // }
    //
    // private void destroyTempFiles() {
    // if (srcFile != null) {
    // srcFile.delete();
    // srcFile = null;
    // }
    //
    // if (dstFile != null) {
    // dstFile.delete();
    // dstFile = null;
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
    // public void finishBehaviorNoSuchBehavior()
    // throws Exception {
    //
    // boolean sawRBE = false;
    //
    // try {
    // String behaviorName = "foo";
    // String behaviorID = "123";
    //
    // client.finishBehavior(behaviorName, behaviorID);
    //
    // } catch (RobotBehaviorException rbe) {
    // int code = client.getStatusCode();
    // Assert.assertTrue(String.format("Expected 404 status code, got %d",
    // code), code == 404);
    //
    // sawRBE = true;
    // }
    //
    // Assert.assertTrue("Expected RobotBehaviorException, did not see one",
    // sawRBE);
    // }
    //
    // @Test
    // public void finishBehaviorInProgress()
    // throws Exception {
    //
    // String behaviorName = "foo";
    //
    // createTempFiles();
    //
    // try {
    // FileWriter writer = new FileWriter(srcFile);
    // writer.write("# tcp.server.accept-then-close\n");
    // writer.write("accept tcp://localhost:7788\n");
    // writer.write("connected\n");
    // writer.write("close\n");
    // writer.write("closed\n");
    // writer.close();
    //
    // String behaviorID = client.startBehavior(behaviorName, srcFile);
    // client.finishBehavior(behaviorName, behaviorID);
    //
    // int code = client.getStatusCode();
    // Assert.assertTrue(String.format("Expected 200 status code, got %d",
    // code), code == 200);
    //
    // // XXX Ideally we would somehow get the contents of the response,
    // // see the status of the behavior that was just stopped.
    //
    // } finally {
    // destroyTempFiles();
    // }
    // }
    //
    // @Test
    // public void finishBehaviorCompleted()
    // throws Exception {
    //
    // String behaviorName = "foo";
    // BufferedReader reader = null;
    //
    // createTempFiles();
    //
    // try {
    // FileWriter writer = new FileWriter(srcFile);
    // writer.write("# tcp.server.accept-then-close\n");
    // writer.write("accept tcp://localhost:7788\n");
    // writer.write("connected\n");
    // writer.write("close\n");
    // writer.write("closed\n");
    // writer.close();
    //
    // String behaviorID = client.startBehavior(behaviorName, srcFile);
    // InputStream is = client.finishBehavior(behaviorName, behaviorID);
    //
    // int code = client.getStatusCode();
    // Assert.assertTrue(String.format("Expected 200 status code, got %d",
    // code), code == 200);
    //
    // // XXX Ideally we would somehow get the contents of the response,
    // // see the status of the test that was just stopped.
    //
    // reader = new BufferedReader(new InputStreamReader(is));
    //
    // /* The result output should look something like:
    // *
    // * time.start=1330465489505
    // * time.finish=1330465490010
    // * behavior.status=IN_PROGRESS
    // *
    // * or:
    // *
    // * time.start=1330465489505
    // * time.finish=1330465490010
    // * behavior.status=FAILED
    // * behavior.status.reason=Caught unexpected exception
    // */
    //
    // Properties props = new Properties();
    // props.load(reader);
    //
    // String key = "time.start";
    // Assert.assertTrue(String.format("Expected key '%s' was not found", key),
    // props.getProperty(key) != null);
    //
    // key = "time.finish";
    // Assert.assertTrue(String.format("Expected key '%s' was not found", key),
    // props.getProperty(key) != null);
    //
    // key = BehaviorStatus.STATUS_PROP;
    // Assert.assertTrue(String.format("Expected key '%s' was not found", key),
    // props.getProperty(key) != null);
    //
    // String status = props.getProperty(key);
    // String expected = BehaviorStatus.IN_PROGRESS.toString();
    // Assert.assertTrue(String.format("Expected status '%s', got '%s'",
    // expected, status), status.equals(expected));
    //
    // } finally {
    // destroyTempFiles();
    //
    // if (reader != null) {
    // reader.close();
    // }
    // }
    // }
    //
    // @Test(timeout=5000)
    // public void finishBehaviorTimedOut()
    // throws Exception {
    //
    // String behaviorName = "foo";
    // BufferedReader reader = null;
    //
    // createTempFiles();
    //
    // try {
    // FileWriter writer = new FileWriter(srcFile);
    // writer.write("# tcp.server.accept-then-close\n");
    // writer.write("accept tcp://localhost:7788\n");
    // writer.write("connected\n");
    // writer.write("close\n");
    // writer.write("closed\n");
    // writer.close();
    //
    // long behaviorTimeout = 500;
    // String behaviorID = client.startBehavior(behaviorName, srcFile,
    // Long.valueOf(behaviorTimeout));
    //
    // // Wait for longer that the timeout, to see if the scheduled
    // // stopper in Robot did its job properly
    // try {
    // Thread.sleep(behaviorTimeout + 500);
    //
    // } catch (InterruptedException ie) {
    // // ignore
    // }
    //
    // InputStream is = client.finishBehavior(behaviorName, behaviorID);
    //
    // int code = client.getStatusCode();
    // Assert.assertTrue(String.format("Expected 200 status code, got %d",
    // code), code == 200);
    //
    // // XXX Ideally we would somehow get the contents of the response,
    // // see the status of the test that was just stopped.
    //
    // reader = new BufferedReader(new InputStreamReader(is));
    //
    // /* The result output should look something like:
    // *
    // * time.start=1330465489505
    // * time.finish=1330465490010
    // * behavior.status=Timed out
    // */
    //
    // Properties props = new Properties();
    // props.load(reader);
    //
    // String key = "time.start";
    // Assert.assertTrue(String.format("Expected key '%s' was not found", key),
    // props.getProperty(key) != null);
    //
    // key = "time.finish";
    // Assert.assertTrue(String.format("Expected key '%s' was not found", key),
    // props.getProperty(key) != null);
    //
    // key = BehaviorStatus.STATUS_PROP;
    // Assert.assertTrue(String.format("Expected key '%s' was not found", key),
    // props.getProperty(key) != null);
    //
    // String status = props.getProperty(key);
    // String expected = BehaviorStatus.TIMED_OUT.toString();
    // Assert.assertTrue(String.format("Expected status '%s', got '%s'",
    // expected, status), status.contains(expected));
    //
    // } finally {
    // destroyTempFiles();
    //
    // if (reader != null) {
    // reader.close();
    // }
    // }
    // }
}
