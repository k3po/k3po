/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package com.kaazing.robot.lang.parser.v2;

//import com.kaazing.robot.lang.ast.AstScriptNode;

public class ParseErrorsTest {
    // private File scriptFile = null;
    // private UUID guid = null;
    //
    // @Before
    // public void setUp()
    // throws Exception {
    //
    // scriptFile = File.createTempFile("parseErrors", ".tmp");
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
    // @Ignore
    // @Test
    // public void shouldReportErrorsCommandBeforeStreamStart()
    // throws Exception {
    //
    // FileWriter writer = new FileWriter(scriptFile);
    // writer.write("# tcp.client.connect-then-close\n");
    // writer.write("read [0x01 0x02 0x03]\n");
    // writer.write("connect tcp://localhost:7788\n");
    // writer.write("connected\n");
    // writer.write("close\n");
    // writer.write("closed\n");
    // writer.close();
    //
    // ParseErrors errors = new ParseErrors();
    //
    // InputStream is = new FileInputStream(scriptFile);
    // RobotScriptIRGenerator ir = new RobotScriptIRGenerator(is, errors);
    // AstScriptNode script = ir.getAST();
    // System.err.println(String.format("errors:\n%s", errors));
    //
    // Assert.assertTrue(String.format("Expected 1 error, got %d",
    // errors.size()), errors.size() == 1);
    // String error = errors.getError(0);
    //
    // String expected = "missing 'accept' or 'connect' directive";
    // Assert.assertTrue(String.format("Expected '%s', got '%s'", expected,
    // error), error.contains(expected));
    // }
    //
    // @Ignore
    // @Test
    // public void shouldReportErrorsMissingStreamStart()
    // throws Exception {
    //
    // FileWriter writer = new FileWriter(scriptFile);
    // writer.write("# tcp.client.connect-then-close\n");
    // writer.write("connected\n");
    // writer.write("close\n");
    // writer.write("closed\n");
    // writer.close();
    //
    // ParseErrors errors = new ParseErrors();
    //
    // InputStream is = new FileInputStream(scriptFile);
    // RobotScriptIRGenerator ir = new RobotScriptIRGenerator(is, errors);
    // AstScriptNode script = ir.getAST();
    // System.err.println(String.format("errors:\n%s", errors));
    //
    // Assert.assertTrue(String.format("Expected 4 errors, got %d",
    // errors.size()), errors.size() == 4);
    //
    // String expected = "before channel opened";
    // for (int i = 0; i < 3; i++) {
    // Assert.assertTrue(String.format("Expected '%s', got '%s'", expected,
    // errors.getError(i)), errors.getError(i).contains(expected));
    // }
    //
    // expected = "Missing 'close' directive";
    // Assert.assertTrue(String.format("Expected '%s', got '%s'", expected,
    // errors.getError(3)), errors.getError(3).contains(expected));
    // }
    //
    // @Ignore
    // @Test
    // public void shouldReportErrorsMissingClose()
    // throws Exception {
    //
    // FileWriter writer = new FileWriter(scriptFile);
    // writer.write("# tcp.server.accept-then-close\n");
    // writer.write("accept tcp://localhost:7788\n");
    // writer.write("connected\n");
    // writer.close();
    //
    // ParseErrors errors = new ParseErrors();
    //
    // InputStream is = new FileInputStream(scriptFile);
    // RobotScriptIRGenerator ir = new RobotScriptIRGenerator(is, errors);
    // AstScriptNode script = ir.getAST();
    // System.err.println(String.format("errors:\n%s", errors));
    //
    // Assert.assertTrue(String.format("Expected 1 error, got %d",
    // errors.size()), errors.size() == 1);
    //
    // String expected = "Missing 'close' directive";
    // Assert.assertTrue(String.format("Expected '%s', got '%s'", expected,
    // errors.getError(0)), errors.getError(0).contains(expected));
    // }
    //
    // @Ignore
    // @Test
    // public void shouldReportErrorsDoubleStreamStart()
    // throws Exception {
    //
    // FileWriter writer = new FileWriter(scriptFile);
    //
    // writer.write("# tcp.client.echo-multi-conn.upstream\n");
    // writer.write("connect tcp://localhost:8785\n");
    // writer.write("connected\n");
    // writer.write("write \"Hello, World!\"\n");
    // writer.write("writing notify BARRIER\n");
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
    // ParseErrors errors = new ParseErrors();
    //
    // InputStream is = new FileInputStream(scriptFile);
    // RobotScriptIRGenerator ir = new RobotScriptIRGenerator(is, errors);
    // AstScriptNode script = ir.getAST();
    // System.err.println(String.format("errors:\n%s", errors));
    //
    // Assert.assertTrue(String.format("Expected 1 error, got %d",
    // errors.size()), errors.size() == 1);
    //
    // String expected = "'connect' after channel already opened";
    // Assert.assertTrue(String.format("Expected '%s', got '%s'", expected,
    // errors.getError(0)), errors.getError(0).contains(expected));
    // }
    //
    // @Ignore
    // @Test
    // public void shouldReportErrorsDoubleClose()
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
    // writer.write("connected\n");
    // writer.write("writing await BARRIER\n");
    // writer.write("write \"Hello, World!\"\n");
    // writer.write("close\n");
    // writer.write("closed\n");
    // writer.close();
    //
    // ParseErrors errors = new ParseErrors();
    //
    // InputStream is = new FileInputStream(scriptFile);
    // RobotScriptIRGenerator ir = new RobotScriptIRGenerator(is, errors);
    // AstScriptNode script = ir.getAST();
    // System.err.println(String.format("errors:\n%s", errors));
    //
    // Assert.assertTrue(String.format("Expected 2 errors, got %d",
    // errors.size()), errors.size() == 2);
    //
    // String expected = "missing 'accept' or 'connect' directive";
    // Assert.assertTrue(String.format("Expected '%s', got '%s'", expected,
    // errors.getError(0)), errors.getError(0).contains(expected));
    //
    // expected = "'close' before channel opened";
    // Assert.assertTrue(String.format("Expected '%s', got '%s'", expected,
    // errors.getError(1)), errors.getError(1).contains(expected));
    // }
    //
    // @Ignore
    // @Test
    // public void shouldReportErrorsBarrierNotifyNoAwaitOK()
    // throws Exception {
    //
    // FileWriter writer = new FileWriter(scriptFile);
    // writer.write("# tcp.client.connect-then-close\n");
    // writer.write("connect tcp://localhost:7788\n");
    // writer.write("connected\n");
    // writer.write("read \"Hello, World!\"\n");
    // writer.write("reading notify BARRIER\n");
    // writer.write("close\n");
    // writer.write("closed\n");
    // writer.close();
    //
    // ParseErrors errors = new ParseErrors();
    //
    // InputStream is = new FileInputStream(scriptFile);
    // RobotScriptIRGenerator ir = new RobotScriptIRGenerator(is, errors);
    // AstScriptNode script = ir.getAST();
    //
    // Assert.assertTrue(String.format("Expected no errors, got %d",
    // errors.size()), errors.isEmpty());
    // }
    //
    // @Ignore
    // @Test
    // public void shouldReportErrorsBarrierAwaitNoNotify()
    // throws Exception {
    //
    // FileWriter writer = new FileWriter(scriptFile);
    // writer.write("# tcp.client.connect-then-close\n");
    // writer.write("connect tcp://localhost:7788\n");
    // writer.write("connected\n");
    // writer.write("writing await BARRIER\n");
    // writer.write("write \"Hello, World!\"\n");
    // writer.write("close\n");
    // writer.write("closed\n");
    // writer.close();
    //
    // ParseErrors errors = new ParseErrors();
    //
    // InputStream is = new FileInputStream(scriptFile);
    // RobotScriptIRGenerator ir = new RobotScriptIRGenerator(is, errors);
    // AstScriptNode script = ir.getAST();
    // System.err.println(String.format("errors:\n%s", errors));
    //
    // Assert.assertTrue(String.format("Expected 1 error, got %d",
    // errors.size()), errors.size() == 1);
    //
    // String expected = "barrier name 'BARRIER' not generated by any";
    // Assert.assertTrue(String.format("Expected '%s', got '%s'", expected,
    // errors.getError(0)), errors.getError(0).contains(expected));
    // }
    //
    // @Ignore
    // @Test
    // public void shouldReportErrorsMalformedURI()
    // throws Exception {
    //
    // FileWriter writer = new FileWriter(scriptFile);
    // writer.write("# tcp.client.connect-then-close\n");
    // writer.write("connect tcp.localhost.7788\n");
    // writer.write("connected\n");
    // writer.write("write \"Hello, World!\"\n");
    // writer.write("close\n");
    // writer.write("closed\n");
    // writer.close();
    //
    // ParseErrors errors = new ParseErrors();
    //
    // InputStream is = new FileInputStream(scriptFile);
    // RobotScriptIRGenerator ir = new RobotScriptIRGenerator(is, errors);
    // AstScriptNode script = ir.getAST();
    // System.err.println(String.format("errors:\n%s", errors));
    //
    // Assert.assertTrue(String.format("Expected 1 error, got %d",
    // errors.size()), errors.size() == 1);
    //
    // String expected = "malformed URI";
    // Assert.assertTrue(String.format("Expected '%s', got '%s'", expected,
    // errors.getError(0)), errors.getError(0).contains(expected));
    // }
    //
    // @Ignore
    // @Test
    // public void shouldReportErrorsUnknownURIScheme()
    // throws Exception {
    //
    // FileWriter writer = new FileWriter(scriptFile);
    // writer.write("# tcp.client.connect-then-close\n");
    // writer.write("connect tcps://localhost:7788\n");
    // writer.write("connected\n");
    // writer.write("write \"Hello, World!\"\n");
    // writer.write("close\n");
    // writer.write("closed\n");
    // writer.close();
    //
    // ParseErrors errors = new ParseErrors();
    //
    // InputStream is = new FileInputStream(scriptFile);
    // RobotScriptNettyGenerator netty = new RobotScriptNettyGenerator(guid,
    // is, errors);
    // List<BehaviorContext> behaviors = netty.getBehaviors();
    // System.err.println(String.format("errors:\n%s", errors));
    //
    // Assert.assertTrue(String.format("Expected 1 error, got %d",
    // errors.size()), errors.size() == 1);
    //
    // String expected = "Unable to find transport data for scheme 'tcps'";
    // Assert.assertTrue(String.format("Expected '%s', got '%s'", expected,
    // errors.getError(0)), errors.getError(0).contains(expected));
    // }
    //
    // @Ignore
    // @Test
    // public void shouldReportErrorsMalformedTextString()
    // throws Exception {
    //
    // FileWriter writer = new FileWriter(scriptFile);
    // writer.write("# tcp.client.connect-then-close\n");
    // writer.write("connect tcp://localhost:7788\n");
    // writer.write("connected\n");
    // writer.write("write \"Hello, World!\n");
    // writer.write("close\n");
    // writer.write("closed\n");
    // writer.close();
    //
    // ParseErrors errors = new ParseErrors();
    //
    // InputStream is = new FileInputStream(scriptFile);
    // RobotScriptNettyGenerator netty = new RobotScriptNettyGenerator(guid,
    // is, errors);
    //
    // boolean sawExpectedEx = false;
    //
    // try {
    // List<BehaviorContext> behaviors = netty.getBehaviors();
    //
    // } catch (TooManyErrorsException tmee) {
    // sawExpectedEx = true;
    // }
    //
    // Assert.assertTrue("Did not see expected TooManyErrorsException",
    // sawExpectedEx == true);
    //
    // System.err.println(String.format("errors:\n%s", errors));
    //
    // Assert.assertTrue(String.format("Expected 1 error, got %d",
    // errors.size()), errors.size() == 1);
    // String expected = "unexpected character";
    // Assert.assertTrue(String.format("Expected '%s', got '%s'", expected,
    // errors.getError(0)), errors.getError(0).contains(expected));
    // }
    //
    // @Ignore
    // @Test
    // public void shouldReportErrorsMalformedHexString()
    // throws Exception {
    //
    // FileWriter writer = new FileWriter(scriptFile);
    // writer.write("# tcp.client.connect-then-close\n");
    // writer.write("connect tcp://localhost:7788\n");
    // writer.write("connected\n");
    // writer.write("write [0x01 0x0af 0x0e 0n\n");
    // writer.write("close\n");
    // writer.write("closed\n");
    // writer.close();
    //
    // ParseErrors errors = new ParseErrors();
    //
    // InputStream is = new FileInputStream(scriptFile);
    // RobotScriptNettyGenerator netty = new RobotScriptNettyGenerator(guid,
    // is, errors);
    //
    // boolean sawExpectedEx = false;
    //
    // try {
    // List<BehaviorContext> behaviors = netty.getBehaviors();
    //
    // } catch (TooManyErrorsException tmee) {
    // sawExpectedEx = true;
    // }
    //
    // Assert.assertTrue("Did not see expected TooManyErrorsException",
    // sawExpectedEx == true);
    //
    // System.err.println(String.format("errors:\n%s", errors));
    //
    // Assert.assertTrue(String.format("Expected 1 error, got %d",
    // errors.size()), errors.size() == 1);
    // String expected = "unexpected character";
    // Assert.assertTrue(String.format("Expected '%s', got '%s'", expected,
    // errors.getError(0)), errors.getError(0).contains(expected));
    // }
    //
    // @Ignore
    // @Test
    // public void shouldReportErrorsMalformedBarrierName()
    // throws Exception {
    //
    // FileWriter writer = new FileWriter(scriptFile);
    // writer.write("# tcp.client.connect-then-close\n");
    // writer.write("connect tcp://localhost:7788\n");
    // writer.write("connected\n");
    // writer.write("write [0x01 0x0af 0x0e 0xAA]\n");
    // writer.write("writing notify foo^bar\n");
    // writer.write("close\n");
    // writer.write("closed\n");
    // writer.close();
    //
    // ParseErrors errors = new ParseErrors();
    //
    // InputStream is = new FileInputStream(scriptFile);
    // RobotScriptNettyGenerator netty = new RobotScriptNettyGenerator(guid,
    // is, errors);
    //
    // boolean sawExpectedEx = false;
    //
    // try {
    // List<BehaviorContext> behaviors = netty.getBehaviors();
    //
    // } catch (TooManyErrorsException tmee) {
    // sawExpectedEx = true;
    // }
    //
    // Assert.assertTrue("Did not see expected TooManyErrorsException",
    // sawExpectedEx == true);
    //
    // System.err.println(String.format("errors:\n%s", errors));
    //
    // Assert.assertTrue(String.format("Expected 1 error, got %d",
    // errors.size()), errors.size() == 1);
    // String expected = "unexpected character";
    // Assert.assertTrue(String.format("Expected '%s', got '%s'", expected,
    // errors.getError(0)), errors.getError(0).contains(expected));
    // }
}
