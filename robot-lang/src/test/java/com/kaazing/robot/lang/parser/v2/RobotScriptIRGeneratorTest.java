/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package com.kaazing.robot.lang.parser.v2;

public class RobotScriptIRGeneratorTest {
    // private File scriptFile = null;
    //
    // @Before
    // public void setUp()
    // throws Exception {
    //
    // scriptFile = File.createTempFile("irGenerator", ".tmp");
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
    // // see http://jira.kaazing.wan/NR-8
    // @Test(expected = IllegalArgumentException.class)
    // public void shouldFailASTGenerationForWritten()
    // throws Exception {
    //
    // FileWriter writer = new FileWriter(scriptFile);
    // writer.write("# tcp.client.connect-then-close\n");
    // writer.write("connect tcp://localhost:7788\n");
    // writer.write("connected\n");
    // writer.write("written\n");
    // writer.write("close\n");
    // writer.write("closed\n");
    // writer.close();
    //
    // InputStream is = new FileInputStream(scriptFile);
    // RobotScriptIRGenerator ir = new RobotScriptIRGenerator(is);
    // AstScriptNode script = ir.getAST();
    // }
    //
    // @Test
    // public void shouldGenerateConnectAST()
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
    // RobotScriptIRGenerator ir = new RobotScriptIRGenerator(is);
    // AstScriptNode script = ir.getAST();
    // System.err.println(String.format("ast: %s", script));
    //
    // List<AstStreamNode> streams = script.getStreams();
    // Assert.assertTrue(String.format("Expected 1 stream, got %d",
    // streams.size()), streams.size() == 1);
    //
    // AstStreamNode stream = streams.get(0);
    // List<AstStreamableNode> nodes = stream.getStreamables();
    // Assert.assertTrue(String.format("Expected 8 nodes, got %d",
    // nodes.size()), nodes.size() == 8);
    //
    // int i = 0;
    // Assert.assertTrue(String.format("Expected Connect node at index %d, got %s",
    // i, nodes.get(i).getClass().getSimpleName()), nodes.get(i) instanceof
    // AstConnectNode);
    //
    // i = 1;
    // Assert.assertTrue(String.format("Expected Opened node at index %d, got %s",
    // i, nodes.get(i).getClass().getSimpleName()), nodes.get(i) instanceof
    // AstOpenedNode);
    //
    // i = 2;
    // Assert.assertTrue(String.format("Expected Bound node at index %d, got %s",
    // i, nodes.get(i).getClass().getSimpleName()), nodes.get(i) instanceof
    // AstBoundNode);
    //
    // i = 3;
    // Assert.assertTrue(String.format("Expected Connected node at index %d, got %s",
    // i, nodes.get(i).getClass().getSimpleName()), nodes.get(i) instanceof
    // AstConnectedNode);
    //
    // i = 4;
    // Assert.assertTrue(String.format("Expected Close node at index %d, got %s",
    // i, nodes.get(i).getClass().getSimpleName()), nodes.get(i) instanceof
    // AstCloseNode);
    //
    // i = 5;
    // Assert.assertTrue(String.format("Expected Disconnected node at index %d, got %s",
    // i, nodes.get(i).getClass().getSimpleName()), nodes.get(i) instanceof
    // AstDisconnectedNode);
    //
    // i = 6;
    // Assert.assertTrue(String.format("Expected Unbound node at index %d, got %s",
    // i, nodes.get(i).getClass().getSimpleName()), nodes.get(i) instanceof
    // AstUnboundNode);
    //
    // i = 7;
    // Assert.assertTrue(String.format("Expected Closed node at index %d, got %s",
    // i, nodes.get(i).getClass().getSimpleName()), nodes.get(i) instanceof
    // AstClosedNode);
    // }
    //
    // @Test
    // public void shouldGenerateAcceptAST()
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
    // RobotScriptIRGenerator ir = new RobotScriptIRGenerator(is);
    // AstScriptNode script = ir.getAST();
    // System.err.println(String.format("ast: %s", script));
    //
    // List<AstStreamNode> streams = script.getStreams();
    // Assert.assertTrue(String.format("Expected 1 stream, got %d",
    // streams.size()), streams.size() == 1);
    //
    // AstStreamNode stream = streams.get(0);
    // List<AstStreamableNode> nodes = stream.getStreamables();
    // Assert.assertTrue(String.format("Expected 8 nodes, got %d",
    // nodes.size()), nodes.size() == 8);
    //
    // int i = 0;
    // Assert.assertTrue(String.format("Expected Accept node at index %d, got %s",
    // i, nodes.get(i).getClass().getSimpleName()), nodes.get(i) instanceof
    // AstAcceptNode);
    //
    // i = 1;
    // Assert.assertTrue(String.format("Expected Opened node at index %d, got %s",
    // i, nodes.get(i).getClass().getSimpleName()), nodes.get(i) instanceof
    // AstOpenedNode);
    //
    // i = 2;
    // Assert.assertTrue(String.format("Expected Bound node at index %d, got %s",
    // i, nodes.get(i).getClass().getSimpleName()), nodes.get(i) instanceof
    // AstBoundNode);
    //
    // i = 3;
    // Assert.assertTrue(String.format("Expected Connected node at index %d, got %s",
    // i, nodes.get(i).getClass().getSimpleName()), nodes.get(i) instanceof
    // AstConnectedNode);
    //
    // i = 4;
    // Assert.assertTrue(String.format("Expected Close node at index %d, got %s",
    // i, nodes.get(i).getClass().getSimpleName()), nodes.get(i) instanceof
    // AstCloseNode);
    //
    // i = 5;
    // Assert.assertTrue(String.format("Expected Disconnected node at index %d, got %s",
    // i, nodes.get(i).getClass().getSimpleName()), nodes.get(i) instanceof
    // AstDisconnectedNode);
    //
    // i = 6;
    // Assert.assertTrue(String.format("Expected Unbound node at index %d, got %s",
    // i, nodes.get(i).getClass().getSimpleName()), nodes.get(i) instanceof
    // AstUnboundNode);
    //
    // i = 7;
    // Assert.assertTrue(String.format("Expected Closed node at index %d, got %s",
    // i, nodes.get(i).getClass().getSimpleName()), nodes.get(i) instanceof
    // AstClosedNode);
    // }
    //
    // @Test // see http://jira.kaazing.wan/KG-3404
    // public void shouldGenerateAcceptASTWithExpressions()
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
    // RobotScriptIRGenerator ir = new RobotScriptIRGenerator(is);
    // AstScriptNode script = ir.getAST();
    // System.err.println(String.format("ast: %s", script));
    //
    // List<AstStreamNode> streams = script.getStreams();
    // Assert.assertTrue(String.format("Expected 1 stream, got %d",
    // streams.size()), streams.size() == 1);
    //
    // AstStreamNode stream = streams.get(0);
    // List<AstStreamableNode> nodes = stream.getStreamables();
    // Assert.assertTrue(String.format("Expected 12 nodes, got %d",
    // nodes.size()), nodes.size() == 12);
    //
    // int i = 0;
    // Assert.assertTrue(String.format("Expected Accept node at index %d, got %s",
    // i, nodes.get(i).getClass().getSimpleName()), nodes.get(i) instanceof
    // AstAcceptNode);
    //
    // i = 1;
    // Assert.assertTrue(String.format("Expected Opened node at index %d, got %s",
    // i, nodes.get(i).getClass().getSimpleName()), nodes.get(i) instanceof
    // AstOpenedNode);
    //
    // i = 2;
    // Assert.assertTrue(String.format("Expected Bound node at index %d, got %s",
    // i, nodes.get(i).getClass().getSimpleName()), nodes.get(i) instanceof
    // AstBoundNode);
    //
    // i = 3;
    // Assert.assertTrue(String.format("Expected Connected node at index %d, got %s",
    // i, nodes.get(i).getClass().getSimpleName()), nodes.get(i) instanceof
    // AstConnectedNode);
    //
    // i = 4;
    // Assert.assertTrue(String.format("Expected Read node at index %d, got %s",
    // i, nodes.get(i).getClass().getSimpleName()), nodes.get(i) instanceof
    // AstReadValueNode);
    //
    // i = 5;
    // Assert.assertTrue(String.format("Expected ReadingNotify node at index %d, got %s",
    // i, nodes.get(i).getClass().getSimpleName()), nodes.get(i) instanceof
    // AstReadNotifyNode);
    //
    // i = 6;
    // Assert.assertTrue(String.format("Expected WritingAwait node at index %d, got %s",
    // i, nodes.get(i).getClass().getSimpleName()), nodes.get(i) instanceof
    // AstWriteAwaitNode);
    //
    // i = 7;
    // Assert.assertTrue(String.format("Expected Write node at index %d, got %s",
    // i, nodes.get(i).getClass().getSimpleName()), nodes.get(i) instanceof
    // AstWriteValueNode);
    //
    // i = 8;
    // Assert.assertTrue(String.format("Expected Close node at index %d, got %s",
    // i, nodes.get(i).getClass().getSimpleName()), nodes.get(i) instanceof
    // AstCloseNode);
    //
    // i = 9;
    // Assert.assertTrue(String.format("Expected Disconnected node at index %d, got %s",
    // i, nodes.get(i).getClass().getSimpleName()), nodes.get(i) instanceof
    // AstDisconnectedNode);
    //
    // i = 10;
    // Assert.assertTrue(String.format("Expected Unbound node at index %d, got %s",
    // i, nodes.get(i).getClass().getSimpleName()), nodes.get(i) instanceof
    // AstUnboundNode);
    //
    // i = 11;
    // Assert.assertTrue(String.format("Expected Closed node at index %d, got %s",
    // i, nodes.get(i).getClass().getSimpleName()), nodes.get(i) instanceof
    // AstClosedNode);
    // }
    //
    // @Test
    // public void shouldGenerateMultiConnectAST()
    // throws Exception {
    //
    // FileWriter writer = new FileWriter(scriptFile);
    //
    // writer.write("# tcp.client.echo-multi-conn.upstream\n");
    // writer.write("connect tcp://localhost:8785\n");
    // writer.write("connected\n");
    // writer.write("write \"Hello, World!\"\n");
    // writer.write("writing notify BARRIER\n");
    // writer.write("close\n");
    // writer.write("closed\n");
    // writer.write("\n");
    // writer.write("# tcp.client.echo-multi-conn.downstream\n");
    // writer.write("connect tcp://localhost:8783\n");
    // writer.write("connected\n");
    // writer.write("reading await BARRIER\n");
    // writer.write("read \"Hello, World!\"\n");
    // writer.write("close\n");
    // writer.write("closed\n");
    // writer.close();
    //
    // InputStream is = new FileInputStream(scriptFile);
    // RobotScriptIRGenerator ir = new RobotScriptIRGenerator(is);
    // AstScriptNode script = ir.getAST();
    // System.err.println(String.format("ast: %s", script));
    //
    // List<AstStreamNode> streams = script.getStreams();
    // Assert.assertTrue(String.format("Expected 2 streams, got %d",
    // streams.size()), streams.size() == 2);
    //
    // AstStreamNode stream = streams.get(0);
    // List<AstStreamableNode> nodes = stream.getStreamables();
    // Assert.assertTrue(String.format("Expected 10 nodes, got %d",
    // nodes.size()), nodes.size() == 10);
    //
    // int i = 0;
    // Assert.assertTrue(String.format("Expected Connect node at index %d, got %s",
    // i, nodes.get(i).getClass().getSimpleName()), nodes.get(i) instanceof
    // AstConnectNode);
    //
    // i = 1;
    // Assert.assertTrue(String.format("Expected Opened node at index %d, got %s",
    // i, nodes.get(i).getClass().getSimpleName()), nodes.get(i) instanceof
    // AstOpenedNode);
    //
    // i = 2;
    // Assert.assertTrue(String.format("Expected Bound node at index %d, got %s",
    // i, nodes.get(i).getClass().getSimpleName()), nodes.get(i) instanceof
    // AstBoundNode);
    //
    // i = 3;
    // Assert.assertTrue(String.format("Expected Connected node at index %d, got %s",
    // i, nodes.get(i).getClass().getSimpleName()), nodes.get(i) instanceof
    // AstConnectedNode);
    //
    // i = 4;
    // Assert.assertTrue(String.format("Expected Write node at index %d, got %s",
    // i, nodes.get(i).getClass().getSimpleName()), nodes.get(i) instanceof
    // AstWriteValueNode);
    //
    // i = 5;
    // Assert.assertTrue(String.format("Expected WritingNotify node at index %d, got %s",
    // i, nodes.get(i).getClass().getSimpleName()), nodes.get(i) instanceof
    // AstWriteNotifyNode);
    //
    // i = 6;
    // Assert.assertTrue(String.format("Expected Close node at index %d, got %s",
    // i, nodes.get(i).getClass().getSimpleName()), nodes.get(i) instanceof
    // AstCloseNode);
    //
    // i = 7;
    // Assert.assertTrue(String.format("Expected Disconnected node at index %d, got %s",
    // i, nodes.get(i).getClass().getSimpleName()), nodes.get(i) instanceof
    // AstDisconnectedNode);
    //
    // i = 8;
    // Assert.assertTrue(String.format("Expected Unbound node at index %d, got %s",
    // i, nodes.get(i).getClass().getSimpleName()), nodes.get(i) instanceof
    // AstUnboundNode);
    //
    // i = 9;
    // Assert.assertTrue(String.format("Expected Closed node at index %d, got %s",
    // i, nodes.get(i).getClass().getSimpleName()), nodes.get(i) instanceof
    // AstClosedNode);
    //
    // stream = streams.get(1);
    // nodes = stream.getStreamables();
    // Assert.assertTrue(String.format("Expected 10 nodes, got %d",
    // nodes.size()), nodes.size() == 10);
    //
    // i = 0;
    // Assert.assertTrue(String.format("Expected Connect node at index %d, got %s",
    // i, nodes.get(i).getClass().getSimpleName()), nodes.get(i) instanceof
    // AstConnectNode);
    //
    // i = 1;
    // Assert.assertTrue(String.format("Expected Opened node at index %d, got %s",
    // i, nodes.get(i).getClass().getSimpleName()), nodes.get(i) instanceof
    // AstOpenedNode);
    //
    // i = 2;
    // Assert.assertTrue(String.format("Expected Bound node at index %d, got %s",
    // i, nodes.get(i).getClass().getSimpleName()), nodes.get(i) instanceof
    // AstBoundNode);
    //
    // i = 3;
    // Assert.assertTrue(String.format("Expected Connected node at index %d, got %s",
    // i, nodes.get(i).getClass().getSimpleName()), nodes.get(i) instanceof
    // AstConnectedNode);
    //
    // i = 4;
    // Assert.assertTrue(String.format("Expected ReadingAwait node at index %d, got %s",
    // i, nodes.get(i).getClass().getSimpleName()), nodes.get(i) instanceof
    // AstReadAwaitNode);
    //
    // i = 5;
    // Assert.assertTrue(String.format("Expected Read node at index %d, got %s",
    // i, nodes.get(i).getClass().getSimpleName()), nodes.get(i) instanceof
    // AstReadValueNode);
    //
    // i = 6;
    // Assert.assertTrue(String.format("Expected Close node at index %d, got %s",
    // i, nodes.get(i).getClass().getSimpleName()), nodes.get(i) instanceof
    // AstCloseNode);
    //
    // i = 7;
    // Assert.assertTrue(String.format("Expected Disconnected node at index %d, got %s",
    // i, nodes.get(i).getClass().getSimpleName()), nodes.get(i) instanceof
    // AstDisconnectedNode);
    //
    // i = 8;
    // Assert.assertTrue(String.format("Expected Unbound node at index %d, got %s",
    // i, nodes.get(i).getClass().getSimpleName()), nodes.get(i) instanceof
    // AstUnboundNode);
    //
    // i = 9;
    // Assert.assertTrue(String.format("Expected Closed node at index %d, got %s",
    // i, nodes.get(i).getClass().getSimpleName()), nodes.get(i) instanceof
    // AstClosedNode);
    // }
    //
    // /*
    // //@Test
    // public void generateMultiAcceptIR()
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
    // RobotScriptIRGenerator generator = new RobotScriptIRGenerator(is);
    // List<Node> ir = generator.generateIR();
    // System.err.println(String.format("ir: %s", ir));
    //
    // Assert.assertTrue(String.format("Expected 10 nodes, got %d", ir.size()),
    // ir.size() == 10);
    //
    // int i = 0;
    // Assert.assertTrue(String.format("Expected node %d type %s, got type %s",
    // i + 1, AstNode.NodeKind.INIT.name(), ir.get(i).getNodeKind().name()),
    // ir.get(i).getNodeKind().equals(AstNode.NodeKind.INIT));
    //
    // i = 1;
    // Assert.assertTrue(String.format("Expected node %d type %s, got type %s",
    // i + 1, AstNode.NodeKind.STATE.name(), ir.get(i).getNodeKind().name()),
    // ir.get(i).getNodeKind().equals(AstNode.NodeKind.STATE));
    //
    // i = 2;
    // Assert.assertTrue(String.format("Expected node %d type %s, got type %s",
    // i + 1, AstNode.NodeKind.COMPLETION.name(),
    // ir.get(i).getNodeKind().name()),
    // ir.get(i).getNodeKind().equals(AstNode.NodeKind.COMPLETION));
    //
    // i = 3;
    // Assert.assertTrue(String.format("Expected node %d type %s, got type %s",
    // i + 1, AstNode.NodeKind.CLOSE.name(), ir.get(i).getNodeKind().name()),
    // ir.get(i).getNodeKind().equals(AstNode.NodeKind.CLOSE));
    //
    // i = 4;
    // Assert.assertTrue(String.format("Expected node %d type %s, got type %s",
    // i + 1, AstNode.NodeKind.STATE.name(), ir.get(i).getNodeKind().name()),
    // ir.get(i).getNodeKind().equals(AstNode.NodeKind.STATE));
    //
    // i = 5;
    // Assert.assertTrue(String.format("Expected node %d type %s, got type %s",
    // i + 1, AstNode.NodeKind.INIT.name(), ir.get(i).getNodeKind().name()),
    // ir.get(i).getNodeKind().equals(AstNode.NodeKind.INIT));
    //
    // i = 6;
    // Assert.assertTrue(String.format("Expected node %d type %s, got type %s",
    // i + 1, AstNode.NodeKind.STATE.name(), ir.get(i).getNodeKind().name()),
    // ir.get(i).getNodeKind().equals(AstNode.NodeKind.STATE));
    //
    // i = 7;
    // Assert.assertTrue(String.format("Expected node %d type %s, got type %s",
    // i + 1, AstNode.NodeKind.CONDITION.name(),
    // ir.get(i).getNodeKind().name()),
    // ir.get(i).getNodeKind().equals(AstNode.NodeKind.CONDITION));
    //
    // i = 8;
    // Assert.assertTrue(String.format("Expected node %d type %s, got type %s",
    // i + 1, AstNode.NodeKind.CLOSE.name(), ir.get(i).getNodeKind().name()),
    // ir.get(i).getNodeKind().equals(AstNode.NodeKind.CLOSE));
    //
    // i = 9;
    // Assert.assertTrue(String.format("Expected node %d type %s, got type %s",
    // i + 1, AstNode.NodeKind.STATE.name(), ir.get(i).getNodeKind().name()),
    // ir.get(i).getNodeKind().equals(AstNode.NodeKind.STATE));
    // }
    //
    // //@Test
    // public void generateMultiAcceptIROneLine()
    // throws Exception {
    //
    // FileWriter writer = new FileWriter(scriptFile);
    //
    // // Write the entire script on one line
    // writer.write("# tcp.server.echo-multi-conn.upstream\n");
    // writer.write("accept tcp://localhost:8783 ");
    // writer.write("connected\n");
    // writer.write("read \"Hello, World!\", notify BARRIER ");
    // writer.write("close ");
    // writer.write("closed\n");
    // writer.write("# tcp.server.echo-multi-conn.downstream\n");
    // writer.write("accept tcp://localhost:8785 ");
    // writer.write("connected ");
    // writer.write("await BARRIER, write \"Hello, World!\" ");
    // writer.write("close ");
    // writer.write("closed ");
    // writer.close();
    //
    // InputStream is = new FileInputStream(scriptFile);
    // RobotScriptIRGenerator generator = new RobotScriptIRGenerator(is);
    // List<Node> ir = generator.generateIR();
    // System.err.println(String.format("ir: %s", ir));
    //
    // Assert.assertTrue(String.format("Expected 10 nodes, got %d", ir.size()),
    // ir.size() == 10);
    //
    // int i = 0;
    // Assert.assertTrue(String.format("Expected node %d type %s, got type %s",
    // i + 1, AstNode.NodeKind.INIT.name(), ir.get(i).getNodeKind().name()),
    // ir.get(i).getNodeKind().equals(AstNode.NodeKind.INIT));
    //
    // i = 1;
    // Assert.assertTrue(String.format("Expected node %d type %s, got type %s",
    // i + 1, AstNode.NodeKind.STATE.name(), ir.get(i).getNodeKind().name()),
    // ir.get(i).getNodeKind().equals(AstNode.NodeKind.STATE));
    //
    // i = 2;
    // Assert.assertTrue(String.format("Expected node %d type %s, got type %s",
    // i + 1, AstNode.NodeKind.COMPLETION.name(),
    // ir.get(i).getNodeKind().name()),
    // ir.get(i).getNodeKind().equals(AstNode.NodeKind.COMPLETION));
    //
    // i = 3;
    // Assert.assertTrue(String.format("Expected node %d type %s, got type %s",
    // i + 1, AstNode.NodeKind.CLOSE.name(), ir.get(i).getNodeKind().name()),
    // ir.get(i).getNodeKind().equals(AstNode.NodeKind.CLOSE));
    //
    // i = 4;
    // Assert.assertTrue(String.format("Expected node %d type %s, got type %s",
    // i + 1, AstNode.NodeKind.STATE.name(), ir.get(i).getNodeKind().name()),
    // ir.get(i).getNodeKind().equals(AstNode.NodeKind.STATE));
    //
    // i = 5;
    // Assert.assertTrue(String.format("Expected node %d type %s, got type %s",
    // i + 1, AstNode.NodeKind.INIT.name(), ir.get(i).getNodeKind().name()),
    // ir.get(i).getNodeKind().equals(AstNode.NodeKind.INIT));
    //
    // i = 6;
    // Assert.assertTrue(String.format("Expected node %d type %s, got type %s",
    // i + 1, AstNode.NodeKind.STATE.name(), ir.get(i).getNodeKind().name()),
    // ir.get(i).getNodeKind().equals(AstNode.NodeKind.STATE));
    //
    // i = 7;
    // Assert.assertTrue(String.format("Expected node %d type %s, got type %s",
    // i + 1, AstNode.NodeKind.CONDITION.name(),
    // ir.get(i).getNodeKind().name()),
    // ir.get(i).getNodeKind().equals(AstNode.NodeKind.CONDITION));
    //
    // i = 8;
    // Assert.assertTrue(String.format("Expected node %d type %s, got type %s",
    // i + 1, AstNode.NodeKind.CLOSE.name(), ir.get(i).getNodeKind().name()),
    // ir.get(i).getNodeKind().equals(AstNode.NodeKind.CLOSE));
    //
    // i = 9;
    // Assert.assertTrue(String.format("Expected node %d type %s, got type %s",
    // i + 1, AstNode.NodeKind.STATE.name(), ir.get(i).getNodeKind().name()),
    // ir.get(i).getNodeKind().equals(AstNode.NodeKind.STATE));
    // }
    // */
    //
    // @Test
    // public void shouldGetASTBarriers()
    // throws Exception {
    //
    // FileWriter writer = new FileWriter(scriptFile);
    //
    // writer.write("# tcp.server.echo-multi-conn.upstream\n");
    // writer.write("accept tcp://localhost:8783\n");
    // writer.write("connected\n");
    // writer.write("read \"Hello, World!\"\n");
    // writer.write("reading notify BARRIER\n");
    // writer.write("close\n");
    // writer.write("closed\n");
    // writer.write("\n");
    // writer.write("# tcp.server.echo-multi-conn.downstream\n");
    // writer.write("accept tcp://localhost:8785\n");
    // writer.write("connected\n");
    // writer.write("writing await BARRIER\n");
    // writer.write("write \"Hello, World!\"\n");
    // writer.write("close\n");
    // writer.write("closed\n");
    // writer.close();
    //
    // InputStream is = new FileInputStream(scriptFile);
    // RobotScriptIRGenerator ir = new RobotScriptIRGenerator(is);
    // AstScriptNode script = ir.getAST();
    // System.err.println(String.format("ast: %s", script));
    //
    // List<AstStreamNode> streams = script.getStreams();
    // Assert.assertTrue(String.format("Expected 2 streams, got %d",
    // streams.size()), streams.size() == 2);
    //
    // Map<String, List<AstBarrierNode>> barriers = ir.getBarriers();
    // Assert.assertTrue(String.format("Expected 1 barrier, got %d",
    // barriers.size()), barriers.size() == 1);
    // System.err.println(String.format("barriers: %s", barriers));
    // }
}
