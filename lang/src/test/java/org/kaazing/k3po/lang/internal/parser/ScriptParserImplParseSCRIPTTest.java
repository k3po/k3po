/**
 * Copyright 2007-2015, Kaazing Corporation. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
//
// TODO: Move back to Robot tests to test AST after visitor transformations.
//
package org.kaazing.k3po.lang.internal.parser;
//
// import static org.kaazing.k3po.lang.parser.v2.ScriptParseStrategy.SCRIPT;
// import static org.junit.Assert.assertEquals;
//
// import java.io.ByteArrayInputStream;
// import java.net.URI;
// import java.nio.charset.StandardCharsets;
// import java.util.Arrays;
// import java.util.LinkedList;
// import java.util.List;
// import java.util.ListIterator;
//
// import javax.el.ExpressionFactory;
//
// import org.junit.Test;
// import org.junit.runner.RunWith;
// import org.junit.runners.Parameterized;
// import org.junit.runners.Parameterized.Parameters;
//
// import org.kaazing.k3po.lang.ast.AstAcceptNode;
// import org.kaazing.k3po.lang.ast.AstAcceptableNode;
// import org.kaazing.k3po.lang.ast.AstScriptNode;
// import org.kaazing.k3po.lang.ast.AstStreamNode;
// import org.kaazing.k3po.lang.ast.builder.AstScriptNodeBuilder;
// import org.kaazing.k3po.lang.el.ExpressionContext;
//
// @RunWith(Parameterized.class)
public class ScriptParserImplParseSCRIPTTest {
//
// /*
// * Uck named parameters breaks eclipse. Uncomment if your having trouble
// * figuring figuring out which parameters were being used.
// * @Parameters(name= "{index}: (parseWithStrat={0} cononicalize={1}")
// */
// @Parameters
// public static List<Object[]> createTestData() {
// return Arrays.asList(new Object[][] { { true, true }, { false, true },
// { false, false } });
// }
//
// private final Boolean parseWithStrategy;
// private final Boolean canonicalize;
//
// /**
// * Return true if the result of parsing a script should result in a
// * canonicalized script.
// * @return
// */
// public boolean isCanonicalizedScript() {
// return canonicalize && !parseWithStrategy;
// }
//
// public ScriptParserImplParseSCRIPTTest(Boolean parseWithStrategy,
// Boolean canonicalize) {
// this.parseWithStrategy = parseWithStrategy;
// this.canonicalize = canonicalize;
// }
//
// private AstScriptNode doParse(String script, ExpressionFactory factory,
// ExpressionContext context) throws Exception {
// return doParse(script, new ScriptParserImpl(factory, context));
// }
//
// private AstScriptNode doParse(String script) throws Exception {
// return doParse(script, new ScriptParserImpl());
// }
//
// private AstScriptNode doParse(String script, ScriptParserImpl parser)
// throws Exception {
//
// // parser.setOption("canonicalize", canonicalize.toString());
//
// AstScriptNode result;
// if (parseWithStrategy) {
// result = parser.parseWithStrategy(script, SCRIPT);
//
// } else {
// result = parser.parse(new ByteArrayInputStream(script.getBytes(StandardCharsets.UTF_8)));
// }
// return result;
// }
//
//    // @formatter:off
//    @Test
//    public void shouldParseConnectScript()
//        throws Exception {
//
//        String script = "# tcp.client.connect-then-close\n" +
//            "connect tcp://localhost:7788\n" +
//            "connected\n" +
//            "close\n" +
//            "closed\n";
//
//        AstScriptNode actual = doParse(script);
//
//        AstScriptNode expected;
//
//        if (!isCanonicalizedScript()) {
//            expected = new AstScriptNodeBuilder()
//                .addConnectStream()
//                    .setNextLineInfo(2, 0)
//                    .setLocation(URI.create("tcp://localhost:7788"))
//                    .addConnectedEvent()
//                        .setNextLineInfo(1, 0)
//                        .done()
//                    .addCloseCommand()
//                        .setNextLineInfo(1, 0)
//                        .done()
//                    .addClosedEvent()
//                        .setNextLineInfo(1, 0)
//                        .done()
//                    .done()
//                .done();
//        } else {
//            expected = new AstScriptNodeBuilder()
//                    .addConnectStream()
//                    .setNextLineInfo(2, 0)
//                    .setLocation(URI.create("tcp://localhost:7788"))
//                    .addOpenedEvent()
//                        .setNextLineInfo(0, 0)
//                        .done()
//                    .addBoundEvent()
//                        .setNextLineInfo(0, 0)
//                        .done()
//                    .addConnectedEvent()
//                    .setNextLineInfo(1, 0)
//                        .done()
//                    .addCloseCommand()
//                        .setNextLineInfo(1, 0)
//                        .done()
//                    .addDisconnectedEvent()
//                        .setNextLineInfo(0, 0)
//                        .done()
//                    .addUnboundEvent()
//                        .setNextLineInfo(0, 0)
//                        .done()
//                    .addClosedEvent()
//                        .setNextLineInfo(1, 0)
//                        .done()
//                    .done()
//                .done();
//        }
//
//        assertEquals(expected, actual);
//    }
//
//    @Test
//    public void shouldParseConnectScriptWithComments()
//        throws Exception {
//
//        String script = "# tcp.client.connect-then-close\n" +
//                        "connect tcp://localhost:7788 # Comment 1\n" +
//                        "\t\t # Comment 2\n" +
//                        "connected\n" +
//                        "close\n" +
//                        "closed\n";
//
//        AstScriptNode actual = doParse(script);
//        AstScriptNode expected;
//
//        if (!isCanonicalizedScript()) {
//            expected = new AstScriptNodeBuilder()
//                .addConnectStream()
//                    .setNextLineInfo(2, 0)
//                      .setLocation(URI.create("tcp://localhost:7788"))
//                    .addConnectedEvent()
//                          .setNextLineInfo(2, 0)
//                        .done()
//                       .addCloseCommand()
//                        .setNextLineInfo(1, 0)
//                        .done()
//                    .addClosedEvent()
//                        .setNextLineInfo(1, 0)
//                        .done()
//                    .done()
//                .done();
//        } else {
//            expected = new AstScriptNodeBuilder()
//            .addConnectStream()
//                .setNextLineInfo(2, 0)
//                .setLocation(URI.create("tcp://localhost:7788"))
//                .addOpenedEvent()
//                    .setNextLineInfo(0, 0)
//                    .done()
//                .addBoundEvent()
//                    .setNextLineInfo(0, 0)
//                    .done()
//                .addConnectedEvent()
//                    .setNextLineInfo(2, 0)
//                    .done()
//                .addCloseCommand()
//                    .setNextLineInfo(1, 0)
//                    .done()
//                .addDisconnectedEvent()
//                    .setNextLineInfo(0, 0)
//                    .done()
//                 .addUnboundEvent()
//                     .setNextLineInfo(0, 0)
//                     .done()
//                .addClosedEvent()
//                    .setNextLineInfo(1, 0)
//                    .done()
//                .done()
//            .done();
//        }
//
//        assertEquals(expected, actual);
//    }
//
//    @Test
//    public void shouldParseAcceptScript()
//        throws Exception {
//
//        String script =
//            "# tcp.client.accept-then-close\n" +
//            "accept tcp://localhost:7788\n" +
//            "accepted\n" +
//            "connected\n" +
//            "close\n" +
//            "closed\n";
//
//        AstScriptNode actual = doParse(script);
//        AstScriptNode expected;
//
//        if (!isCanonicalizedScript()) {
//            expected = new AstScriptNodeBuilder()
//                .addAcceptStream()
//                       .setNextLineInfo(2, 0)
//                       .setLocation(URI.create("tcp://localhost:7788"))
//                    .done()
//                   .addAcceptedStream()
//                    .setNextLineInfo(1, 0)
//                    .addConnectedEvent()
//                        .setNextLineInfo(1, 0)
//                        .done()
//                    .addCloseCommand()
//                           .setNextLineInfo(1, 0)
//                        .done()
//                    .addClosedEvent()
//                        .setNextLineInfo(1, 0)
//                        .done()
//                    .done()
//                .done();
//        } else {
//            expected = new AstScriptNodeBuilder()
//            .addAcceptStream()
//                .setNextLineInfo(2, 0)
//                .setLocation(URI.create("tcp://localhost:7788"))
//                .done()
//            .addAcceptedStream()
//                .setNextLineInfo(1, 0)
//                .addOpenedEvent()
//                    .setNextLineInfo(0, 0)
//                    .done()
//                .addBoundEvent()
//                    .setNextLineInfo(0, 0)
//                    .done()
//                .addConnectedEvent()
//                    .setNextLineInfo(1, 0)
//                    .done()
//                .addCloseCommand()
//                    .setNextLineInfo(1, 0)
//                    .done()
//                .addDisconnectedEvent()
//                    .setNextLineInfo(0, 0)
//                    .done()
//                .addUnboundEvent()
//                    .setNextLineInfo(0, 0)
//                    .done()
//                .addClosedEvent()
//                    .setNextLineInfo(1, 0)
//                    .done()
//                .done()
//            .done();
//
//            expected = transformScript(expected);
//
//        }
//
//        assertEquals(expected, actual);
//    }
//
//
//    @Test
//    public void shouldParseMultiConnectScript()
//        throws Exception {
//
//        String script =
//            "# tcp.client.echo-multi-conn.upstream\n" +
//            "connect tcp://localhost:8785\n" +
//            "connected\n" +
//            "write \"Hello, world!\"\n" +
//            "write notify BARRIER\n" +
//            "close\n" +
//            "closed\n" +
//            "# tcp.client.echo-multi-conn.downstream\n" +
//            "connect tcp://localhost:8783\n" +
//            "connected\n" +
//            "read await BARRIER\n" +
//            "read \"Hello, world!\"\n" +
//            "close\n" +
//            "closed\n";
//
//        AstScriptNode actual = doParse(script);
//        AstScriptNode expected;
//
//        if (!isCanonicalizedScript()) {
//            expected = new AstScriptNodeBuilder()
//                .addConnectStream()
//                    .setNextLineInfo(2, 0)
//                    .setLocation(URI.create("tcp://localhost:8785"))
//                    .addConnectedEvent()
//                        .setNextLineInfo(1, 0)
//                        .done()
//                    .addWriteCommand()
//                        .setNextLineInfo(1, 0)
//                        .setExactText("Hello, world!")
//                        .done()
//                    .addWriteNotifyBarrier()
//                        .setNextLineInfo(1, 0)
//                        .setBarrierName("BARRIER")
//                        .done()
//                    .addCloseCommand()
//                        .setNextLineInfo(1, 0)
//                        .done()
//                    .addClosedEvent()
//                        .setNextLineInfo(1, 0)
//                        .done()
//                    .done()
//                .addConnectStream()
//                    .setNextLineInfo(2, 0)
//                    .setLocation(URI.create("tcp://localhost:8783"))
//                    .addConnectedEvent()
//                        .setNextLineInfo(1, 0)
//                        .done()
//                    .addReadAwaitBarrier()
//                        .setNextLineInfo(1, 0)
//                        .setBarrierName("BARRIER")
//                        .done()
//                    .addReadEvent()
//                        .setNextLineInfo(1, 0)
//                        .setExactText("Hello, world!")
//                        .done()
//                    .addCloseCommand()
//                        .setNextLineInfo(1, 0)
//                        .done()
//                    .addClosedEvent()
//                        .setNextLineInfo(1, 0)
//                        .done()
//                    .done()
//                .done();
//        } else {
//            expected = new AstScriptNodeBuilder()
//            .addConnectStream()
//                .setNextLineInfo(2, 0)
//                .setLocation(URI.create("tcp://localhost:8785"))
//                .addOpenedEvent()
//                    .setNextLineInfo(0, 0)
//                    .done()
//                .addBoundEvent()
//                    .setNextLineInfo(0, 0)
//                    .done()
//                .addConnectedEvent()
//                    .setNextLineInfo(1, 0)
//                    .done()
//                .addWriteCommand()
//                    .setNextLineInfo(1, 0)
//                    .setExactText("Hello, world!")
//                    .done()
//                .addWriteNotifyBarrier()
//                    .setNextLineInfo(1, 0)
//                    .setBarrierName("BARRIER")
//                    .done()
//                .addCloseCommand()
//                    .setNextLineInfo(1, 0)
//                    .done()
//                .addDisconnectedEvent()
//                    .setNextLineInfo(0, 0)
//                    .done()
//                .addUnboundEvent()
//                    .setNextLineInfo(0, 0)
//                    .done()
//                .addClosedEvent()
//                    .setNextLineInfo(1, 0)
//                    .done()
//                .done()
//            .addConnectStream()
//                .setNextLineInfo(2, 0)
//                .setLocation(URI.create("tcp://localhost:8783"))
//                .addOpenedEvent()
//                    .setNextLineInfo(0, 0)
//                    .done()
//                .addBoundEvent()
//                    .setNextLineInfo(0, 0)
//                    .done()
//                .addConnectedEvent()
//                    .setNextLineInfo(1, 0)
//                    .done()
//                .addReadAwaitBarrier()
//                    .setNextLineInfo(1, 0)
//                    .setBarrierName("BARRIER")
//                    .done()
//                .addReadEvent()
//                    .setNextLineInfo(1, 0)
//                    .setExactText("Hello, world!")
//                    .done()
//                .addCloseCommand()
//                    .setNextLineInfo(1, 0)
//                    .done()
//                .addDisconnectedEvent()
//                    .setNextLineInfo(0, 0)
//                    .done()
//                 .addUnboundEvent()
//                     .setNextLineInfo(0, 0)
//                     .done()
//                .addClosedEvent()
//                    .setNextLineInfo(1, 0)
//                    .done()
//                .done()
//            .done();
//        }
//
//
//        assertEquals(expected, actual);
//    }
//
//    /*  The parse actually decorates the AST. So we
//     *  get different results depending what doParse does (canonicalize off and
//     *  parseWithStrategy on causes the annotation. We need to expect different results
//     *  in this case.
//    */
//    @Test
//    public void shouldParseMultiAcceptScript()
//        throws Exception {
//
//        String script =
//            "# tcp.server.echo-multi-conn.upstream\n" +
//            "accept tcp://localhost:8783\n" +
//            "accepted\n" +
//            "connected\n" +
//            "read await BARRIER\n" +
//            "read \"Hello, world!\"\n" +
//            "close\n" +
//            "closed\n" +
//
//            "# tcp.server.echo-multi-conn.downstream\n" +
//            "accept tcp://localhost:8785\n" +
//            "accepted\n" +
//            "connected\n" +
//            "write \"Hello, world!\"\n" +
//            "write notify BARRIER\n" +
//            "close\n" +
//            "closed\n";
//
//        AstScriptNode actual = doParse(script);
//        AstScriptNode expected;
//        if (!isCanonicalizedScript()) {
//            expected = new AstScriptNodeBuilder()
//                .addAcceptStream()
//                    .setNextLineInfo(2, 0)
//                    .setLocation(URI.create("tcp://localhost:8783"))
//                    .done()
//                    .addAcceptedStream()
//                        .setNextLineInfo(1, 0)
//                        .addConnectedEvent()
//                        .setNextLineInfo(1, 0)
//                        .done()
//                    .addReadAwaitBarrier()
//                        .setNextLineInfo(1, 0)
//                        .setBarrierName("BARRIER")
//                        .done()
//                    .addReadEvent()
//                        .setNextLineInfo(1, 0)
//                        .setExactText("Hello, world!")
//                        .done()
//                    .addCloseCommand()
//                        .setNextLineInfo(1, 0)
//                        .done()
//                    .addClosedEvent()
//                        .setNextLineInfo(1, 0)
//                        .done()
//                    .done()
//                .addAcceptStream()
//                    .setNextLineInfo(2, 0)
//                    .setLocation(URI.create("tcp://localhost:8785"))
//                    .done()
//                .addAcceptedStream()
//                        .setNextLineInfo(1, 0)
//                        .addConnectedEvent()
//                        .setNextLineInfo(1, 0)
//                        .done()
//                    .addWriteCommand()
//                        .setNextLineInfo(1, 0)
//                        .setExactText("Hello, world!")
//                        .done()
//                    .addWriteNotifyBarrier()
//                        .setNextLineInfo(1, 0)
//                        .setBarrierName("BARRIER")
//                        .done()
//                    .addCloseCommand()
//                        .setNextLineInfo(1, 0)
//                        .done()
//                    .addClosedEvent()
//                        .setNextLineInfo(1, 0)
//                        .done()
//                    .done()
//                .done();
//        } else {
//            expected = new AstScriptNodeBuilder()
//                .addAcceptStream()
//                    .setNextLineInfo(2, 0)
//                    .setLocation(URI.create("tcp://localhost:8783"))
//                    .done()
//                    .addAcceptedStream()
//                        .setNextLineInfo(1, 0)
//                    .addOpenedEvent()
//                        .setNextLineInfo(0, 0)
//                        .done()
//                    .addBoundEvent()
//                        .setNextLineInfo(0, 0)
//                        .done()
//                    .addConnectedEvent()
//                        .setNextLineInfo(1, 0)
//                        .done()
//                    .addReadAwaitBarrier()
//                        .setNextLineInfo(1, 0)
//                        .setBarrierName("BARRIER")
//                        .done()
//                    .addReadEvent()
//                        .setNextLineInfo(1, 0)
//                        .setExactText("Hello, world!")
//                        .done()
//                    .addCloseCommand()
//                        .setNextLineInfo(1, 0)
//                        .done()
//                    .addDisconnectedEvent()
//                        .setNextLineInfo(0, 0)
//                        .done()
//                    .addUnboundEvent()
//                        .setNextLineInfo(0, 0)
//                        .done()
//                    .addClosedEvent()
//                        .setNextLineInfo(1, 0)
//                        .done()
//                    .done()
//                .addAcceptStream()
//                    .setNextLineInfo(2, 0)
//                    .setLocation(URI.create("tcp://localhost:8785"))
//                    .done()
//                .addAcceptedStream()
//                        .setNextLineInfo(1, 0)
//                    .addOpenedEvent()
//                        .setNextLineInfo(0, 0)
//                        .done()
//                    .addBoundEvent()
//                        .setNextLineInfo(0, 0)
//                        .done()
//                    .addConnectedEvent()
//                        .setNextLineInfo(1, 0)
//                        .done()
//                    .addWriteCommand()
//                        .setNextLineInfo(1, 0)
//                        .setExactText("Hello, world!")
//                        .done()
//                    .addWriteNotifyBarrier()
//                        .setNextLineInfo(1, 0)
//                        .setBarrierName("BARRIER")
//                        .done()
//                    .addCloseCommand()
//                        .setNextLineInfo(1, 0)
//                        .done()
//                    .addDisconnectedEvent()
//                        .setNextLineInfo(0, 0)
//                        .done()
//                    .addUnboundEvent()
//                        .setNextLineInfo(0, 0)
//                        .done()
//                    .addClosedEvent()
//                        .setNextLineInfo(1, 0)
//                        .done()
//                    .done()
//                .done();
//            expected = transformScript(expected);
//        }
//
//        assertEquals(expected, actual);
//    }
//
//    @Test
//    public void shouldParseAcceptAndConnectScript()
//        throws Exception {
//
//        String script =
//            "# tcp.server.accept-then-close\n" +
//            "accept tcp://localhost:7788\n" +
//            "accepted\n" +
//            "connected\n" +
//            "closed\n" +
//            "# tcp.client.connect-then-close\n" +
//            "connect tcp://localhost:7788\n" +
//            "connected\n" +
//            "close\n" +
//            "closed\n"
//            ;
//
//        AstScriptNode actual = doParse(script);
//        AstScriptNode expected;
//
//        if (!isCanonicalizedScript()) {
//            expected = new AstScriptNodeBuilder()
//                .addAcceptStream()
//                    .setNextLineInfo(2, 0)
//                    .setLocation(URI.create("tcp://localhost:7788"))
//                    .done()
//                .addAcceptedStream()
//                    .setNextLineInfo(1, 0)
//                    .addConnectedEvent()
//                        .setNextLineInfo(1, 0)
//                        .done()
//                    .addClosedEvent()
//                        .setNextLineInfo(1, 0)
//                        .done()
//                    .done()
//                .addConnectStream()
//                    .setNextLineInfo(2, 0)
//                    .setLocation(URI.create("tcp://localhost:7788"))
//                    .addConnectedEvent()
//                        .setNextLineInfo(1, 0)
//                        .done()
//                    .addCloseCommand()
//                        .setNextLineInfo(1, 0)
//                        .done()
//                    .addClosedEvent()
//                        .setNextLineInfo(1, 0)
//                        .done()
//                    .done()
//                .done();
//        } else {
//            expected = new AstScriptNodeBuilder()
//            .addAcceptStream()
//                .setNextLineInfo(2, 0)
//                .setLocation(URI.create("tcp://localhost:7788"))
//                .done()
//            .addAcceptedStream()
//                .setNextLineInfo(1, 0)
//                .addOpenedEvent()
//                    .setNextLineInfo(0, 0)
//                    .done()
//                .addBoundEvent()
//                    .setNextLineInfo(0, 0)
//                    .done()
//                .addConnectedEvent()
//                    .setNextLineInfo(1, 0)
//                    .done()
//                .addDisconnectedEvent()
//                    .setNextLineInfo(0, 0)
//                    .done()
//                .addUnboundEvent()
//                    .setNextLineInfo(0, 0)
//                    .done()
//                .addClosedEvent()
//                    .setNextLineInfo(1, 0)
//                    .done()
//                .done()
//            .addConnectStream()
//                .setNextLineInfo(2, 0)
//                .setLocation(URI.create("tcp://localhost:7788"))
//                .addOpenedEvent()
//                    .setNextLineInfo(0, 0)
//                    .done()
//                .addBoundEvent()
//                    .setNextLineInfo(0, 0)
//                    .done()
//                .addConnectedEvent()
//                    .setNextLineInfo(1, 0)
//                    .done()
//                .addCloseCommand()
//                    .setNextLineInfo(1, 0)
//                    .done()
//                .addDisconnectedEvent()
//                    .setNextLineInfo(0, 0)
//                    .done()
//                 .addUnboundEvent()
//                     .setNextLineInfo(0, 0)
//                     .done()
//                .addClosedEvent()
//                    .setNextLineInfo(1, 0)
//                    .done()
//                .done()
//            .done();
//
//            expected = transformScript(expected);
//
//        }
//
//        assertEquals(expected, actual);
//    }
//
//    @Test // see http://jira.kaazing.wan/NR-35
//    public void shouldParseNonClosingConnectScript()
//        throws Exception {
//
//        String script =
//            "# tcp.client.non-closing\n" +
//            "connect tcp://localhost:7788\n" +
//            "connected\n" +
//            "read \"foo\"\n" +
//            "write [0x01 0x02 0xff]\n" +
//            "closed\n";
//
//        AstScriptNode actual = doParse(script);
//        AstScriptNode expected;
//
//        if (!isCanonicalizedScript()) {
//            expected = new AstScriptNodeBuilder()
//            .addConnectStream()
//                .setNextLineInfo(2, 0)
//                .setLocation(URI.create("tcp://localhost:7788"))
//                .addConnectedEvent()
//                    .setNextLineInfo(1, 0)
//                    .done()
//                .addReadEvent()
//                    .setNextLineInfo(1, 0)
//                    .setExactText("foo")
//                    .done()
//                .addWriteCommand()
//                    .setNextLineInfo(1, 0)
//                    .setExactBytes(new byte[] { 0x01, 0x02, (byte) 0xff})
//                    .done()
//                .addClosedEvent()
//                    .setNextLineInfo(1, 0)
//                    .done()
//                .done()
//            .done();
//
//        } else {
//            expected = new AstScriptNodeBuilder()
//                .addConnectStream()
//                    .setNextLineInfo(2, 0)
//                    .setLocation(URI.create("tcp://localhost:7788"))
//                    .addOpenedEvent()
//                        .setNextLineInfo(0, 0)
//                        .done()
//                    .addBoundEvent()
//                        .setNextLineInfo(0, 0)
//                        .done()
//                    .addConnectedEvent()
//                        .setNextLineInfo(1, 0)
//                        .done()
//                    .addReadEvent()
//                        .setNextLineInfo(1, 0)
//                        .setExactText("foo")
//                        .done()
//                    .addReadNotifyBarrier()
//                        .setBarrierName("~read~write~1")
//                        .done()
//                    .addWriteAwaitBarrier()
//                        .setBarrierName("~read~write~1")
//                        .done()
//                    .addWriteCommand()
//                        .setNextLineInfo(1, 0)
//                        .setExactBytes(new byte[] { 0x01, 0x02, (byte) 0xff})
//                        .done()
//                    .addDisconnectedEvent()
//                        .setNextLineInfo(0, 0)
//                        .done()
//                    .addUnboundEvent()
//                        .setNextLineInfo(0, 0)
//                        .done()
//                    .addClosedEvent()
//                        .setNextLineInfo(1, 0)
//                        .done()
//                    .done()
//                .done();
//        }
//
//        assertEquals(expected, actual);
//    }
//
//    @Test
//    public void shouldParseEmptyScript()
//        throws Exception {
//
//        String script = "";
//
//        AstScriptNode actual = doParse(script);
//
//        AstScriptNode expected = new AstScriptNodeBuilder()
////            .setNextLineInfo(1, 0)
//            .done();
//
//        assertEquals(expected, actual);
//    }
//
//    @Test
//    public void shouldParseScriptWithCommentsOnly()
//        throws Exception {
//
//        String script = "# Comment 1\n" +
//            "# Comment 2\n" +
//            "# Comment 3\n";
//
//        AstScriptNode actual = doParse(script);
//
//        AstScriptNode expected = new AstScriptNode();
//
//        assertEquals(expected, actual);
//    }
//
//    @Test
//    public void shouldParseScriptWithCommentsAndWhitespace()
//        throws Exception {
//
//        String script = "# Comment 1\n" +
//            "\t\n" +
//            " # Comment 2\n" +
//            "\r\n" +
//            "# Comment 3\n";
//
//        AstScriptNode actual = doParse(script);
//
//        AstScriptNode expected = new AstScriptNode();
//
//        assertEquals(expected, actual);
//    }
//
//    @Test
//    public void shouldParseScript()
//        throws Exception {
//
//        /*TODO: This will fail because when canonicalized because the first unbound will
//         *      fail because the state is not DISCONNECTED. However, I can't currently disconnect
//         *      after a child open or child close.
//         */
//        org.junit.Assume.assumeFalse(isCanonicalizedScript());
//
//        String script =
//            "#\n" +
//            "# server\n" +
//            "#\n" +
//            "accept tcp://localhost:8000 as ACCEPT\n" +
//            "opened\n" +
//            "bound\n" +
//            "child opened\n" +
//            "child closed\n" +
//            "unbound\n" +
//            "closed\n" +
//            "#\n" +
//            "# child\n" +
//            "#\n" +
//            " accepted ACCEPT\n" +
//            "opened\n" +
//            " bound\n" +
//            "connected\n" +
//            " read ([0..32]:input)\n" +
//            "read notify BARRIER\n" +
//            "write await BARRIER\n" +
//            "write [ 0x01 0xfe ]\n" +
//            "close\n" +
//            "disconnected\n" +
//            "unbound\n" +
//            "closed\n" +
//            "#\n" +
//            "# client\n" +
//            "#\n" +
//            "connect tcp://localhost:8000\n" +
//            " opened\n" +
//            "bound\n" +
//            " connected\n" +
//            "write ${input}\n" +
//            " read [ 0x00 0xff ]\n" +
//            "close\n" +
//            "disconnected\n" +
//            "unbound\n" +
//            "closed";
//
//        ExpressionFactory factory = ExpressionFactory.newInstance();
//        ExpressionContext context = new ExpressionContext();
//
//        //parser.lex(new ByteArrayInputStream(script.getBytes(UTF_8)));
//        AstScriptNode actual = doParse(script, factory, context);
//
//        AstScriptNode expected = new AstScriptNodeBuilder()
//            .addAcceptStream()
//                .setNextLineInfo(4, 0)
//                .setLocation(URI.create("tcp://localhost:8000"))
//                .setAcceptName("ACCEPT")
//                .addOpenedEvent()
//                    .setNextLineInfo(1, 0)
//                    .done()
//                .addBoundEvent()
//                    .setNextLineInfo(1, 0)
//                    .done()
//                .addChildOpenedEvent()
//                    .setNextLineInfo(1, 0)
//                    .done()
//                .addChildClosedEvent()
//                    .setNextLineInfo(1, 0)
//                    .done()
//                .addUnboundEvent()
//                    .setNextLineInfo(1, 0)
//                    .done()
//                .addClosedEvent()
//                    .setNextLineInfo(1, 0)
//                    .done()
//                .done()
//            .addAcceptedStream()
//                .setNextLineInfo(4, 1)
//                .setAcceptName("ACCEPT")
//                .addOpenedEvent()
//                    .setNextLineInfo(1, 0)
//                    .done()
//                .addBoundEvent()
//                    .setNextLineInfo(1, 1)
//                    .done()
//                .addConnectedEvent()
//                    .setNextLineInfo(1, 0)
//                    .done()
//                .addReadEvent()
//                    .setNextLineInfo(1, 1)
//                    .setFixedLengthBytes(32, "input")
//                    .done()
//                .addReadNotifyBarrier()
//                    .setNextLineInfo(1, 0)
//                    .setBarrierName("BARRIER")
//                    .done()
//                .addWriteAwaitBarrier()
//                    .setNextLineInfo(1, 0)
//                    .setBarrierName("BARRIER")
//                    .done()
//                .addWriteCommand()
//                    .setNextLineInfo(1, 0)
//                    .setExactBytes(new byte[] { 0x01, -0x02 })
//                    .done()
//                .addCloseCommand()
//                    .setNextLineInfo(1, 0)
//                    .done()
//                .addDisconnectedEvent()
//                    .setNextLineInfo(1, 0)
//                    .done()
//                .addUnboundEvent()
//                    .setNextLineInfo(1, 0)
//                    .done()
//                .addClosedEvent()
//                    .setNextLineInfo(1, 0)
//                    .done()
//                .done()
//            .addConnectStream()
//                .setNextLineInfo(4, 0)
//                .setLocation(URI.create("tcp://localhost:8000"))
//                .addOpenedEvent()
//                    .setNextLineInfo(1, 1)
//                    .done()
//                .addBoundEvent()
//                    .setNextLineInfo(1, 0)
//                    .done()
//                .addConnectedEvent()
//                    .setNextLineInfo(1, 1)
//                    .done()
//                .addWriteCommand()
//                    .setNextLineInfo(1, 0)
//                    .setExpression(factory.createValueExpression(context, "${input}", byte[].class))
//                    .done()
//                .addReadEvent()
//                    .setNextLineInfo(1, 1)
//                    .setExactBytes(new byte[] { 0x00, -0x01 })
//                    .done()
//                .addCloseCommand()
//                    .setNextLineInfo(1, 0)
//                    .done()
//                .addDisconnectedEvent()
//                    .setNextLineInfo(1, 0)
//                    .done()
//                .addUnboundEvent()
//                    .setNextLineInfo(1, 0)
//                    .done()
//                .addClosedEvent()
//                    .setNextLineInfo(1, 0)
//                    .done()
//                .done()
//            .done();
//
//        assertEquals(expected, actual);
//    }
//
//    @Test(expected = ScriptParseException.class)
//    public void shouldNotParseScriptWithUnknownKeyword()
//        throws Exception {
//
//        String script = "written\n";
//
//        doParse(script);
//    }
//
//    @Test(expected = ScriptParseException.class)
//    public void shouldNotParseScriptWithReadBeforeConnect()
//        throws Exception {
//
//        String script =
//            "# tcp.client.connect-then-close\n" +
//            "read [0x01 0x02 0x03]\n" +
//            "connect tcp://localhost:7788\n" +
//            "connected\n" +
//            "close\n" +
//            "closed\n";
//
//        doParse(script);
//    }
//
//    // @formatter:on
//
// /*
// * TODO HACK. When parsing a script with accepted the parser will create a
// * new stream. However, the AssociateStreamsVisitor transforms this and puts
// * the acceptable nodes into the acceptable members. That is what this
// * method does for us. If you have a better way to do this please let me
// * know. I didn't want to use AssociateStreamsVisitor as that is part of the
// * unit under test.
// */
// private static AstScriptNode transformScript(AstScriptNode script) {
//
// /* Grab the streams for this script */
// ListIterator<AstStreamNode> iter = script.getStreams().listIterator();
//
// /*
// * This will be a reference to any acceptables the script has, assuming
// * there exists a AcceptNode. Note this will be a reference to the
// * acceptable list inside an AcceptNode
// */
// List<AstAcceptedNode> scriptAcceptableList = null;
//
// /*
// * Collect any Acceptable nodes and save them for adding to
// * scriptAcceptableList
// */
// List<AstAcceptedNode> collectAcceptableList = new LinkedList<AstAcceptedNode>();
//
// while (iter.hasNext()) {
// AstStreamNode node = iter.next();
//
// if (node.getClass().equals(AstAcceptNode.class)) {
//
// /* If its the first one */
// if (scriptAcceptableList == null) {
// scriptAcceptableList = ((AstAcceptNode) node)
// .getAcceptables();
// } else {
// /*
// * Otherwise a subsequent add all the acceptables found to
// * the last Node and grab a new list
// */
// scriptAcceptableList.addAll(collectAcceptableList);
// scriptAcceptableList = ((AstAcceptNode) node)
// .getAcceptables();
// collectAcceptableList = new LinkedList<AstAcceptedNode>();
// }
// } else if (node.getClass().equals(AstAcceptedNode.class)) {
// /* Remove the Acceptable */
// iter.remove();
// collectAcceptableList.add((AstAcceptedNode) node);
// }
// }
// /* Add all the acceptables to the last AcceptNode found */
// if (scriptAcceptableList != null) {
// scriptAcceptableList.addAll(collectAcceptableList);
// }
// return script;
// }
}
