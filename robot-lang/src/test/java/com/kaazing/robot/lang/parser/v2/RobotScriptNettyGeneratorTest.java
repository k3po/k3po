/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package com.kaazing.robot.lang.parser.v2;

//import com.kaazing.robot.behavior.BehaviorContext;
//import com.kaazing.robot.channel.BehaviorPipelineFactory;

public class RobotScriptNettyGeneratorTest {
    // private File scriptFile = null;
    // private UUID guid = null;
    //
    // @Before
    // public void setUp()
    // throws Exception {
    //
    // scriptFile = File.createTempFile("nettyGenerator", ".tmp");
    // guid = UUID.randomUUID();
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
    // public void shouldGenerateConnectNettyAST()
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
    // RobotScriptNettyGenerator netty = new RobotScriptNettyGenerator(guid,
    // is);
    // List<BehaviorContext> behaviors = netty.getBehaviors();
    // Assert.assertTrue(String.format("Expected 1 behavior, got %d",
    // behaviors.size()), behaviors.size() == 1);
    //
    // BehaviorPipelineFactory factory = (BehaviorPipelineFactory)
    // (behaviors.get(0).getBootstrap().getPipelineFactory());
    // List<String> handlerNames = factory.getHandlerNames();
    // System.err.println(String.format("handler names: %s", handlerNames));
    //
    // Assert.assertTrue(String.format("Expected 9 handlers, got %d",
    // handlerNames.size()), handlerNames.size() == 9);
    // }
    //
    // //@Test
    // public void generateConnectNoClosedNetty()
    // throws Exception {
    //
    // FileWriter writer = new FileWriter(scriptFile);
    // writer.write("# tcp.client.connect-then-close\n");
    // writer.write("connect tcp://localhost:7788\n");
    // writer.write("connected\n");
    // writer.write("close\n");
    // writer.close();
    //
    // InputStream is = new FileInputStream(scriptFile);
    // RobotScriptNettyGenerator netty = new RobotScriptNettyGenerator(guid,
    // is);
    // List<BehaviorContext> behaviors = netty.getBehaviors();
    // Assert.assertTrue(String.format("Expected 1 behavior, got %d",
    // behaviors.size()), behaviors.size() == 1);
    //
    // BehaviorPipelineFactory factory = (BehaviorPipelineFactory)
    // (behaviors.get(0).getBootstrap().getPipelineFactory());
    // List<String> handlerNames = factory.getHandlerNames();
    // System.err.println(String.format("handler names: %s", handlerNames));
    //
    // Assert.assertTrue(String.format("Expected 9 handlers, got %d",
    // handlerNames.size()), handlerNames.size() == 9);
    // }
    //
    // //@Test
    // public void generateAcceptNetty()
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
    // RobotScriptNettyGenerator netty = new RobotScriptNettyGenerator(guid,
    // is);
    // List<BehaviorContext> behaviors = netty.getBehaviors();
    // Assert.assertTrue(String.format("Expected 1 behavior, got %d",
    // behaviors.size()), behaviors.size() == 1);
    //
    // BehaviorPipelineFactory factory = (BehaviorPipelineFactory)
    // (behaviors.get(0).getBootstrap().getPipelineFactory());
    // List<String> handlerNames = factory.getHandlerNames();
    // System.err.println(String.format("handler names: %s", handlerNames));
    //
    // Assert.assertTrue(String.format("Expected 9 handlers, got %d",
    // handlerNames.size()), handlerNames.size() == 9);
    // }
    //
    // //@Test
    // public void generateMultiConnectNetty()
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
    // RobotScriptNettyGenerator netty = new RobotScriptNettyGenerator(guid,
    // is);
    // List<BehaviorContext> behaviors = netty.getBehaviors();
    // Assert.assertTrue(String.format("Expected 2 behaviors, got %d",
    // behaviors.size()), behaviors.size() == 2);
    //
    // BehaviorPipelineFactory factory = (BehaviorPipelineFactory)
    // (behaviors.get(0).getBootstrap().getPipelineFactory());
    // List<String> handlerNames = factory.getHandlerNames();
    // System.err.println(String.format("handler names: %s", handlerNames));
    // Assert.assertTrue(String.format("Expected 10 handlers, got %d",
    // handlerNames.size()), handlerNames.size() == 10);
    //
    // factory = (BehaviorPipelineFactory)
    // (behaviors.get(1).getBootstrap().getPipelineFactory());
    // handlerNames = factory.getHandlerNames();
    // System.err.println(String.format("handler names: %s", handlerNames));
    // Assert.assertTrue(String.format("Expected 12 handlers, got %d",
    // handlerNames.size()), handlerNames.size() == 12);
    // }
    //
    // //@Test
    // public void generateMultiAcceptNetty()
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
    // RobotScriptNettyGenerator netty = new RobotScriptNettyGenerator(guid,
    // is);
    // List<BehaviorContext> behaviors = netty.getBehaviors();
    // Assert.assertTrue(String.format("Expected 2 behaviors, got %d",
    // behaviors.size()), behaviors.size() == 2);
    //
    // BehaviorPipelineFactory factory = (BehaviorPipelineFactory)
    // (behaviors.get(0).getBootstrap().getPipelineFactory());
    // List<String> handlerNames = factory.getHandlerNames();
    // System.err.println(String.format("handler names: %s", handlerNames));
    // Assert.assertTrue(String.format("Expected 11 handlers, got %d",
    // handlerNames.size()), handlerNames.size() == 11);
    //
    // factory = (BehaviorPipelineFactory)
    // (behaviors.get(1).getBootstrap().getPipelineFactory());
    // handlerNames = factory.getHandlerNames();
    // System.err.println(String.format("handler names: %s", handlerNames));
    // Assert.assertTrue(String.format("Expected 10 handlers, got %d",
    // handlerNames.size()), handlerNames.size() == 10);
    // }
    //
    // //@Test // see http://jira.kaazing.wan/KG-3404
    // public void generateAcceptNettyWithNamedMessages()
    // throws Exception {
    //
    // FileWriter writer = new FileWriter(scriptFile);
    // writer.write("# tcp.server.single-echo\n");
    // writer.write("accept tcp://localhost:7788\n");
    // writer.write("connected\n");
    // writer.write("read ${echo}\n");
    // writer.write("write ${echo}\n");
    // writer.write("close\n");
    // writer.write("closed\n");
    // writer.close();
    //
    // InputStream is = new FileInputStream(scriptFile);
    // RobotScriptNettyGenerator netty = new RobotScriptNettyGenerator(guid,
    // is);
    // List<BehaviorContext> behaviors = netty.getBehaviors();
    // Assert.assertTrue(String.format("Expected 1 behavior, got %d",
    // behaviors.size()), behaviors.size() == 1);
    //
    // BehaviorPipelineFactory factory = (BehaviorPipelineFactory)
    // (behaviors.get(0).getBootstrap().getPipelineFactory());
    // List<String> handlerNames = factory.getHandlerNames();
    // System.err.println(String.format("handler names: %s", handlerNames));
    //
    // Assert.assertTrue(String.format("Expected 11 handlers, got %d",
    // handlerNames.size()), handlerNames.size() == 11);
    // }
    //
    // //@Test
    // public void generateAcceptNettyWithDelimitedPatternMessage()
    // throws Exception {
    //
    // FileWriter writer = new FileWriter(scriptFile);
    // writer.write("# tcp.server.single-delimited-regex-echo\n");
    // writer.write("accept tcp://localhost:7788\n");
    // writer.write("connected\n");
    // writer.write("read \"${echo:^\\D+$}\\r\\n\"\n");
    // writer.write("write ${echo}\n");
    // writer.write("close\n");
    // writer.write("closed\n");
    // writer.close();
    //
    // InputStream is = new FileInputStream(scriptFile);
    // RobotScriptNettyGenerator netty = new RobotScriptNettyGenerator(guid,
    // is);
    // List<BehaviorContext> behaviors = netty.getBehaviors();
    // Assert.assertTrue(String.format("Expected 1 behavior, got %d",
    // behaviors.size()), behaviors.size() == 1);
    //
    // BehaviorPipelineFactory factory = (BehaviorPipelineFactory)
    // (behaviors.get(0).getBootstrap().getPipelineFactory());
    // List<String> handlerNames = factory.getHandlerNames();
    // System.err.println(String.format("handler names: %s", handlerNames));
    //
    // Assert.assertTrue(String.format("Expected 12 handlers, got %d",
    // handlerNames.size()), handlerNames.size() == 12);
    // }
    //
    // //@Test
    // public void generateAcceptNettyWithDelimitedPatternMessage2()
    // throws Exception {
    //
    // FileWriter writer = new FileWriter(scriptFile);
    // writer.write("# tcp.server.single-delimited-regex-echo\n");
    // writer.write("accept tcp://localhost:7788\n");
    // writer.write("connected\n");
    // writer.write("read \"HTTP/1.1 ${echo:^\\D+$}\\r\\n\"\n");
    // writer.write("write ${echo}\n");
    // writer.write("close\n");
    // writer.write("closed\n");
    // writer.close();
    //
    // InputStream is = new FileInputStream(scriptFile);
    // RobotScriptNettyGenerator netty = new RobotScriptNettyGenerator(guid,
    // is);
    // List<BehaviorContext> behaviors = netty.getBehaviors();
    // Assert.assertTrue(String.format("Expected 1 behavior, got %d",
    // behaviors.size()), behaviors.size() == 1);
    //
    // BehaviorPipelineFactory factory = (BehaviorPipelineFactory)
    // (behaviors.get(0).getBootstrap().getPipelineFactory());
    // List<String> handlerNames = factory.getHandlerNames();
    // System.err.println(String.format("handler names: %s", handlerNames));
    //
    // Assert.assertTrue(String.format("Expected 13 handlers, got %d",
    // handlerNames.size()), handlerNames.size() == 13);
    // }
    //
    // // I expect an IllegalArgumentException here, with a message of:
    // //
    // // "'write' message missing reuqired backreference(s)"
    // //
    // // Since the "pattern" here doesn't contain any \N sequences.
    //
    // //@Test(expected = IllegalArgumentException.class)
    // public void generateAcceptNettyWithPatternMessageBackref()
    // throws Exception {
    //
    // FileWriter writer = new FileWriter(scriptFile);
    // writer.write("# tcp.server.single-delimited-regex-echo\n");
    // writer.write("accept tcp://localhost:7788\n");
    // writer.write("connected\n");
    // writer.write("read ${echo:^\\D+$}\n");
    // writer.write("write ${echo:foo}\n");
    // writer.write("close\n");
    // writer.write("closed\n");
    // writer.close();
    //
    // InputStream is = new FileInputStream(scriptFile);
    // RobotScriptNettyGenerator netty = new RobotScriptNettyGenerator(guid,
    // is);
    // List<BehaviorContext> behaviors = netty.getBehaviors();
    // }
    //
    // // I expect an IllegalArgumentException here, with a message of:
    // //
    // // "'write' message missing reuqired backreference(s)"
    // //
    // // Since the "pattern" here doesn't contain any UNESCAPED \N sequences.
    //
    // //@Test(expected = IllegalArgumentException.class)
    // public void generateAcceptNettyWithPatternMessageBackref2()
    // throws Exception {
    //
    // FileWriter writer = new FileWriter(scriptFile);
    // writer.write("# tcp.server.single-delimited-regex-echo\n");
    // writer.write("accept tcp://localhost:7788\n");
    // writer.write("connected\n");
    // writer.write("read ${echo:^\\D+$}\n");
    // writer.write("write ${echo:foo\\\\2}\n");
    // writer.write("close\n");
    // writer.write("closed\n");
    // writer.close();
    //
    // InputStream is = new FileInputStream(scriptFile);
    // RobotScriptNettyGenerator netty = new RobotScriptNettyGenerator(guid,
    // is);
    // List<BehaviorContext> behaviors = netty.getBehaviors();
    // }
    //
    // //@Test
    // public void generateAcceptNettyWithPatternMessageBackref3()
    // throws Exception {
    //
    // FileWriter writer = new FileWriter(scriptFile);
    // writer.write("# tcp.server.single-delimited-regex-echo\n");
    // writer.write("accept tcp://localhost:7788\n");
    // writer.write("connected\n");
    // writer.write("read ${echo:^\\D+$}\n");
    // writer.write("write ${echo:foo\\2}\n");
    // writer.write("close\n");
    // writer.write("closed\n");
    // writer.close();
    //
    // InputStream is = new FileInputStream(scriptFile);
    // RobotScriptNettyGenerator netty = new RobotScriptNettyGenerator(guid,
    // is);
    // List<BehaviorContext> behaviors = netty.getBehaviors();
    // Assert.assertTrue(String.format("Expected 1 behavior, got %d",
    // behaviors.size()), behaviors.size() == 1);
    //
    // BehaviorPipelineFactory factory = (BehaviorPipelineFactory)
    // (behaviors.get(0).getBootstrap().getPipelineFactory());
    // List<String> handlerNames = factory.getHandlerNames();
    // System.err.println(String.format("handler names: %s", handlerNames));
    //
    // Assert.assertTrue(String.format("Expected 11 handlers, got %d",
    // handlerNames.size()), handlerNames.size() == 11);
    // }
    //
    // //@Test
    // public void generateAcceptNettyWithPatternMessageBackref4()
    // throws Exception {
    //
    // FileWriter writer = new FileWriter(scriptFile);
    // writer.write("# tcp.server.single-delimited-regex-echo\n");
    // writer.write("accept tcp://localhost:7788\n");
    // writer.write("connected\n");
    // writer.write("read ${echo:^\\D+$}\n");
    // writer.write("write ${echo:foo\\2\\3\\4}\n");
    // writer.write("close\n");
    // writer.write("closed\n");
    // writer.close();
    //
    // InputStream is = new FileInputStream(scriptFile);
    // RobotScriptNettyGenerator netty = new RobotScriptNettyGenerator(guid,
    // is);
    // List<BehaviorContext> behaviors = netty.getBehaviors();
    // Assert.assertTrue(String.format("Expected 1 behavior, got %d",
    // behaviors.size()), behaviors.size() == 1);
    //
    // BehaviorPipelineFactory factory = (BehaviorPipelineFactory)
    // (behaviors.get(0).getBootstrap().getPipelineFactory());
    // List<String> handlerNames = factory.getHandlerNames();
    // System.err.println(String.format("handler names: %s", handlerNames));
    //
    // Assert.assertTrue(String.format("Expected 11 handlers, got %d",
    // handlerNames.size()), handlerNames.size() == 11);
    // }
    //
    // //@Test
    // public void generateAcceptNettyWithPatternMessageBackref5()
    // throws Exception {
    //
    // FileWriter writer = new FileWriter(scriptFile);
    // writer.write("# tcp.server.single-delimited-regex-echo\n");
    // writer.write("accept tcp://localhost:7788\n");
    // writer.write("connected\n");
    // writer.write("read ${echo:^\\D+$}\n");
    // writer.write("write ${echo:\\0}\n");
    // writer.write("close\n");
    // writer.write("closed\n");
    // writer.close();
    //
    // InputStream is = new FileInputStream(scriptFile);
    // RobotScriptNettyGenerator netty = new RobotScriptNettyGenerator(guid,
    // is);
    // List<BehaviorContext> behaviors = netty.getBehaviors();
    // Assert.assertTrue(String.format("Expected 1 behavior, got %d",
    // behaviors.size()), behaviors.size() == 1);
    //
    // BehaviorPipelineFactory factory = (BehaviorPipelineFactory)
    // (behaviors.get(0).getBootstrap().getPipelineFactory());
    // List<String> handlerNames = factory.getHandlerNames();
    // System.err.println(String.format("handler names: %s", handlerNames));
    //
    // Assert.assertTrue(String.format("Expected 11 handlers, got %d",
    // handlerNames.size()), handlerNames.size() == 11);
    // }
    //
    // //@Test // see http://jira.kaazing.wan/NR-35
    // public void generateConnectNettyWithNoClose()
    // throws Exception {
    //
    // FileWriter writer = new FileWriter(scriptFile);
    // writer.write("# tcp.client.non-closing\n");
    // writer.write("connect tcp://localhost:7788\n");
    // writer.write("connected\n");
    // writer.write("read ${echo:^\\D+$}\n");
    // writer.write("write ${echo:\\0}\n");
    // writer.write("closed\n");
    // writer.close();
    //
    // InputStream is = new FileInputStream(scriptFile);
    // RobotScriptNettyGenerator netty = new RobotScriptNettyGenerator(guid,
    // is);
    // List<BehaviorContext> behaviors = netty.getBehaviors();
    // Assert.assertTrue(String.format("Expected 1 behavior, got %d",
    // behaviors.size()), behaviors.size() == 1);
    //
    // BehaviorPipelineFactory factory = (BehaviorPipelineFactory)
    // (behaviors.get(0).getBootstrap().getPipelineFactory());
    // List<String> handlerNames = factory.getHandlerNames();
    // System.err.println(String.format("handler names: %s", handlerNames));
    //
    // Assert.assertTrue(String.format("Expected 10 handlers, got %d",
    // handlerNames.size()), handlerNames.size() == 10);
    // }

    // XXX Generate pipelines for TLS URIs, HTTP URIs, WS URIs, etc
    //
    // Consider:
    // accept https://...
    // connect wss://...
}
