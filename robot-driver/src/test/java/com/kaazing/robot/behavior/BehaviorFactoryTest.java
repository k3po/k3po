/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package com.kaazing.robot.behavior;

public class BehaviorFactoryTest {
    // private File scriptFile = null;
    //
    // @Before
    // public void setUp()
    // throws Exception {
    //
    // scriptFile = File.createTempFile("factory", ".tmp");
    // }
    //
    // @After
    // public void tearDown()
    // throws Exception {
    //
    // if (scriptFile != null) {
    // scriptFile.delete();
    // scriptFile = null;
    // }
    // }
    //
    // @Test
    // public void createEmptyBehavior()
    // throws Exception {
    //
    // InputStream is = new FileInputStream(scriptFile);
    // Behavior b = BehaviorFactory.getInstance().newBehavior(is);
    //
    // String expected = "EmptyBehavior";
    // String behaviorName = b.getName();
    //
    // Assert.assertTrue(String.format("Expected behavior name '%s', got '%s'",
    // expected, behaviorName), behaviorName.equals(expected));
    // }
    //
    // @Test
    // public void createConnectBehavior()
    // throws Exception {
    //
    // FileWriter writer = new FileWriter(scriptFile);
    // writer.write("# tcp.client.connect-then-close\n");
    // writer.write("connect tcp://localhost:7788\n");
    // writer.write("connected\n");
    // writer.write("close\n");
    // writer.write("closed\n");
    // writer.close();
    //
    // InputStream is = new FileInputStream(scriptFile);
    // Behavior b = BehaviorFactory.getInstance().newBehavior(is);
    //
    // String expected = "ConnectBehavior";
    // String behaviorName = b.getName();
    //
    // Assert.assertTrue(String.format("Expected behavior name '%s', got '%s'",
    // expected, behaviorName), behaviorName.equals(expected));
    // }
    //
    // @Test
    // public void createAcceptBehavior()
    // throws Exception {
    //
    // FileWriter writer = new FileWriter(scriptFile);
    // writer.write("# tcp.server.accept-then-close\n");
    // writer.write("accept tcp://localhost:7788\n");
    // writer.write("connected\n");
    // writer.write("close\n");
    // writer.write("closed\n");
    // writer.close();
    //
    // InputStream is = new FileInputStream(scriptFile);
    // Behavior b = BehaviorFactory.getInstance().newBehavior(is);
    //
    // String expected = "AcceptBehavior";
    // String behaviorName = b.getName();
    // Assert.assertTrue(String.format("Expected behavior name '%s', got '%s'",
    // expected, behaviorName), behaviorName.equals(expected));
    // }
    //
    // @Test
    // public void createKnownTCPMultiConnEchoClientBehavior()
    // throws Exception {
    //
    // FileWriter writer = new FileWriter(scriptFile);
    //
    // writer.write("# tcp.client.echo-multi-conn.upstream\n");
    // writer.write("connect tcp://localhost:8785\n");
    // writer.write("connected\n");
    // writer.write("write \"Hello, World!\", notify BARRIER\n");
    // writer.write("close\n");
    // writer.write("closed\n");
    // writer.write("\n");
    // writer.write("# tcp.client.echo-multi-conn.downstream\n");
    // writer.write("connect tcp://localhost:8783\n");
    // writer.write("connected\n");
    // writer.write("await BARRIER, read \"Hello, World!\"\n");
    // writer.write("close\n");
    // writer.write("closed\n");
    // writer.close();
    //
    // InputStream is = new FileInputStream(scriptFile);
    // Behavior b = BehaviorFactory.getInstance().newBehavior(is);
    //
    // Assert.assertTrue("Expected behavior, got null", b != null);
    // Assert.assertTrue("Expected composite behavior, got individual behavior",
    // b.isComposite() == true);
    //
    // int expectedSize = 2;
    // Assert.assertTrue(String.format("Expected %d behaviors, got %d",
    // expectedSize, b.size()), b.size() == expectedSize);
    //
    // System.err.println(String.format("Returned behavior: %s", b));
    // }
    //
    // @Test
    // public void createKnownTCPMultiConnEchoServerBehavior()
    // throws Exception {
    //
    // FileWriter writer = new FileWriter(scriptFile);
    //
    // writer.write("# tcp.server.echo-multi-conn.upstream\n");
    // writer.write("accept tcp://localhost:8783\n");
    // writer.write("connected\n");
    // writer.write("read \"Hello, World!\", notify BARRIER\n");
    // writer.write("close\n");
    // writer.write("closed\n");
    // writer.write("\n");
    // writer.write("# tcp.server.echo-multi-conn.downstream\n");
    // writer.write("accept tcp://localhost:8785\n");
    // writer.write("connected\n");
    // writer.write("await BARRIER, write \"Hello, World!\"\n");
    // writer.write("close\n");
    // writer.write("closed\n");
    // writer.close();
    //
    // InputStream is = new FileInputStream(scriptFile);
    // Behavior b = BehaviorFactory.getInstance().newBehavior(is);
    //
    // Assert.assertTrue("Expected behavior, got null", b != null);
    // Assert.assertTrue("Expected composite behavior, got individual behavior",
    // b.isComposite() == true);
    //
    // int expectedSize = 2;
    // Assert.assertTrue(String.format("Expected %d behaviors, got %d",
    // expectedSize, b.size()), b.size() == expectedSize);
    //
    // System.err.println(String.format("Returned behavior: %s", b));
    // }
}
