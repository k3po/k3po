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
package org.kaazing.k3po.lang.internal.parser;

import static java.lang.Integer.parseInt;
import static java.util.Collections.emptyList;
import static org.kaazing.k3po.lang.internal.RegionInfo.newParallel;
import static org.kaazing.k3po.lang.internal.RegionInfo.newSequential;
import static org.kaazing.k3po.lang.internal.parser.ParserHelper.parseHexBytes;

import java.net.URI;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.el.ExpressionFactory;
import javax.el.ValueExpression;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Token;
import org.kaazing.k3po.lang.internal.RegionInfo;
import org.kaazing.k3po.lang.internal.ast.AstAcceptNode;
import org.kaazing.k3po.lang.internal.ast.AstAcceptedNode;
import org.kaazing.k3po.lang.internal.ast.AstBarrierNode;
import org.kaazing.k3po.lang.internal.ast.AstBoundNode;
import org.kaazing.k3po.lang.internal.ast.AstChildClosedNode;
import org.kaazing.k3po.lang.internal.ast.AstChildOpenedNode;
import org.kaazing.k3po.lang.internal.ast.AstCloseNode;
import org.kaazing.k3po.lang.internal.ast.AstClosedNode;
import org.kaazing.k3po.lang.internal.ast.AstCommandNode;
import org.kaazing.k3po.lang.internal.ast.AstConnectAbortNode;
import org.kaazing.k3po.lang.internal.ast.AstConnectAbortedNode;
import org.kaazing.k3po.lang.internal.ast.AstConnectNode;
import org.kaazing.k3po.lang.internal.ast.AstConnectedNode;
import org.kaazing.k3po.lang.internal.ast.AstDisconnectNode;
import org.kaazing.k3po.lang.internal.ast.AstDisconnectedNode;
import org.kaazing.k3po.lang.internal.ast.AstEventNode;
import org.kaazing.k3po.lang.internal.ast.AstNode;
import org.kaazing.k3po.lang.internal.ast.AstOpenedNode;
import org.kaazing.k3po.lang.internal.ast.AstPropertyNode;
import org.kaazing.k3po.lang.internal.ast.AstReadAbortNode;
import org.kaazing.k3po.lang.internal.ast.AstReadAbortedNode;
import org.kaazing.k3po.lang.internal.ast.AstReadAdviseNode;
import org.kaazing.k3po.lang.internal.ast.AstReadAdvisedNode;
import org.kaazing.k3po.lang.internal.ast.AstReadAwaitNode;
import org.kaazing.k3po.lang.internal.ast.AstReadClosedNode;
import org.kaazing.k3po.lang.internal.ast.AstReadConfigNode;
import org.kaazing.k3po.lang.internal.ast.AstReadNotifyNode;
import org.kaazing.k3po.lang.internal.ast.AstReadOptionNode;
import org.kaazing.k3po.lang.internal.ast.AstReadValueNode;
import org.kaazing.k3po.lang.internal.ast.AstRegion;
import org.kaazing.k3po.lang.internal.ast.AstRejectedNode;
import org.kaazing.k3po.lang.internal.ast.AstScriptNode;
import org.kaazing.k3po.lang.internal.ast.AstStreamNode;
import org.kaazing.k3po.lang.internal.ast.AstStreamableNode;
import org.kaazing.k3po.lang.internal.ast.AstUnbindNode;
import org.kaazing.k3po.lang.internal.ast.AstUnboundNode;
import org.kaazing.k3po.lang.internal.ast.AstWriteAbortNode;
import org.kaazing.k3po.lang.internal.ast.AstWriteAbortedNode;
import org.kaazing.k3po.lang.internal.ast.AstWriteAdviseNode;
import org.kaazing.k3po.lang.internal.ast.AstWriteAdvisedNode;
import org.kaazing.k3po.lang.internal.ast.AstWriteAwaitNode;
import org.kaazing.k3po.lang.internal.ast.AstWriteCloseNode;
import org.kaazing.k3po.lang.internal.ast.AstWriteConfigNode;
import org.kaazing.k3po.lang.internal.ast.AstWriteFlushNode;
import org.kaazing.k3po.lang.internal.ast.AstWriteNotifyNode;
import org.kaazing.k3po.lang.internal.ast.AstWriteOptionNode;
import org.kaazing.k3po.lang.internal.ast.AstWriteValueNode;
import org.kaazing.k3po.lang.internal.ast.matcher.AstByteLengthBytesMatcher;
import org.kaazing.k3po.lang.internal.ast.matcher.AstExactBytesMatcher;
import org.kaazing.k3po.lang.internal.ast.matcher.AstExactTextMatcher;
import org.kaazing.k3po.lang.internal.ast.matcher.AstExpressionMatcher;
import org.kaazing.k3po.lang.internal.ast.matcher.AstFixedLengthBytesMatcher;
import org.kaazing.k3po.lang.internal.ast.matcher.AstIntLengthBytesMatcher;
import org.kaazing.k3po.lang.internal.ast.matcher.AstLongLengthBytesMatcher;
import org.kaazing.k3po.lang.internal.ast.matcher.AstNumberMatcher;
import org.kaazing.k3po.lang.internal.ast.matcher.AstRegexMatcher;
import org.kaazing.k3po.lang.internal.ast.matcher.AstShortLengthBytesMatcher;
import org.kaazing.k3po.lang.internal.ast.matcher.AstValueMatcher;
import org.kaazing.k3po.lang.internal.ast.matcher.AstVariableLengthBytesMatcher;
import org.kaazing.k3po.lang.internal.ast.value.AstExpressionValue;
import org.kaazing.k3po.lang.internal.ast.value.AstLiteralByteValue;
import org.kaazing.k3po.lang.internal.ast.value.AstLiteralBytesValue;
import org.kaazing.k3po.lang.internal.ast.value.AstLiteralIntegerValue;
import org.kaazing.k3po.lang.internal.ast.value.AstLiteralLongValue;
import org.kaazing.k3po.lang.internal.ast.value.AstLiteralShortValue;
import org.kaazing.k3po.lang.internal.ast.value.AstLiteralTextValue;
import org.kaazing.k3po.lang.internal.ast.value.AstLiteralURIValue;
import org.kaazing.k3po.lang.internal.ast.value.AstValue;
import org.kaazing.k3po.lang.internal.el.ExpressionContext;
import org.kaazing.k3po.lang.internal.parser.types.TypeSystem;
import org.kaazing.k3po.lang.internal.regex.NamedGroupPattern;
import org.kaazing.k3po.lang.parser.v2.RobotBaseVisitor;
import org.kaazing.k3po.lang.parser.v2.RobotParser;
import org.kaazing.k3po.lang.parser.v2.RobotParser.AcceptNodeContext;
import org.kaazing.k3po.lang.parser.v2.RobotParser.AcceptOptionContext;
import org.kaazing.k3po.lang.parser.v2.RobotParser.AcceptedNodeContext;
import org.kaazing.k3po.lang.parser.v2.RobotParser.BarrierNodeContext;
import org.kaazing.k3po.lang.parser.v2.RobotParser.BoundNodeContext;
import org.kaazing.k3po.lang.parser.v2.RobotParser.ChildClosedNodeContext;
import org.kaazing.k3po.lang.parser.v2.RobotParser.ChildOpenedNodeContext;
import org.kaazing.k3po.lang.parser.v2.RobotParser.CloseNodeContext;
import org.kaazing.k3po.lang.parser.v2.RobotParser.ClosedNodeContext;
import org.kaazing.k3po.lang.parser.v2.RobotParser.CommandNodeContext;
import org.kaazing.k3po.lang.parser.v2.RobotParser.ConnectAbortNodeContext;
import org.kaazing.k3po.lang.parser.v2.RobotParser.ConnectAbortedNodeContext;
import org.kaazing.k3po.lang.parser.v2.RobotParser.ConnectNodeContext;
import org.kaazing.k3po.lang.parser.v2.RobotParser.ConnectOptionContext;
import org.kaazing.k3po.lang.parser.v2.RobotParser.ConnectedNodeContext;
import org.kaazing.k3po.lang.parser.v2.RobotParser.DisconnectNodeContext;
import org.kaazing.k3po.lang.parser.v2.RobotParser.DisconnectedNodeContext;
import org.kaazing.k3po.lang.parser.v2.RobotParser.EventNodeContext;
import org.kaazing.k3po.lang.parser.v2.RobotParser.ExactBytesMatcherContext;
import org.kaazing.k3po.lang.parser.v2.RobotParser.ExactTextMatcherContext;
import org.kaazing.k3po.lang.parser.v2.RobotParser.ExpressionMatcherContext;
import org.kaazing.k3po.lang.parser.v2.RobotParser.ExpressionValueContext;
import org.kaazing.k3po.lang.parser.v2.RobotParser.FixedLengthBytesMatcherContext;
import org.kaazing.k3po.lang.parser.v2.RobotParser.LiteralByteContext;
import org.kaazing.k3po.lang.parser.v2.RobotParser.LiteralBytesContext;
import org.kaazing.k3po.lang.parser.v2.RobotParser.LiteralIntegerContext;
import org.kaazing.k3po.lang.parser.v2.RobotParser.LiteralLongContext;
import org.kaazing.k3po.lang.parser.v2.RobotParser.LiteralShortContext;
import org.kaazing.k3po.lang.parser.v2.RobotParser.LiteralTextContext;
import org.kaazing.k3po.lang.parser.v2.RobotParser.MatcherContext;
import org.kaazing.k3po.lang.parser.v2.RobotParser.NumberMatcherContext;
import org.kaazing.k3po.lang.parser.v2.RobotParser.OpenedNodeContext;
import org.kaazing.k3po.lang.parser.v2.RobotParser.PropertyNodeContext;
import org.kaazing.k3po.lang.parser.v2.RobotParser.ReadAbortNodeContext;
import org.kaazing.k3po.lang.parser.v2.RobotParser.ReadAbortedNodeContext;
import org.kaazing.k3po.lang.parser.v2.RobotParser.ReadAdviseNodeContext;
import org.kaazing.k3po.lang.parser.v2.RobotParser.ReadAdvisedNodeContext;
import org.kaazing.k3po.lang.parser.v2.RobotParser.ReadAwaitNodeContext;
import org.kaazing.k3po.lang.parser.v2.RobotParser.ReadClosedNodeContext;
import org.kaazing.k3po.lang.parser.v2.RobotParser.ReadConfigNodeContext;
import org.kaazing.k3po.lang.parser.v2.RobotParser.ReadNodeContext;
import org.kaazing.k3po.lang.parser.v2.RobotParser.ReadNotifyNodeContext;
import org.kaazing.k3po.lang.parser.v2.RobotParser.ReadOptionNodeContext;
import org.kaazing.k3po.lang.parser.v2.RobotParser.RegexMatcherContext;
import org.kaazing.k3po.lang.parser.v2.RobotParser.RejectableNodeContext;
import org.kaazing.k3po.lang.parser.v2.RobotParser.RejectedNodeContext;
import org.kaazing.k3po.lang.parser.v2.RobotParser.ScriptNodeContext;
import org.kaazing.k3po.lang.parser.v2.RobotParser.ServerStreamableNodeContext;
import org.kaazing.k3po.lang.parser.v2.RobotParser.StreamNodeContext;
import org.kaazing.k3po.lang.parser.v2.RobotParser.StreamableNodeContext;
import org.kaazing.k3po.lang.parser.v2.RobotParser.UnbindNodeContext;
import org.kaazing.k3po.lang.parser.v2.RobotParser.UnboundNodeContext;
import org.kaazing.k3po.lang.parser.v2.RobotParser.VariableLengthBytesMatcherContext;
import org.kaazing.k3po.lang.parser.v2.RobotParser.WriteAbortNodeContext;
import org.kaazing.k3po.lang.parser.v2.RobotParser.WriteAbortedNodeContext;
import org.kaazing.k3po.lang.parser.v2.RobotParser.WriteAdviseNodeContext;
import org.kaazing.k3po.lang.parser.v2.RobotParser.WriteAdvisedNodeContext;
import org.kaazing.k3po.lang.parser.v2.RobotParser.WriteAwaitNodeContext;
import org.kaazing.k3po.lang.parser.v2.RobotParser.WriteCloseNodeContext;
import org.kaazing.k3po.lang.parser.v2.RobotParser.WriteConfigNodeContext;
import org.kaazing.k3po.lang.parser.v2.RobotParser.WriteFlushNodeContext;
import org.kaazing.k3po.lang.parser.v2.RobotParser.WriteNodeContext;
import org.kaazing.k3po.lang.parser.v2.RobotParser.WriteNotifyNodeContext;
import org.kaazing.k3po.lang.parser.v2.RobotParser.WriteOptionNodeContext;
import org.kaazing.k3po.lang.parser.v2.RobotParser.WriteValueContext;
import org.kaazing.k3po.lang.types.StructuredTypeInfo;
import org.kaazing.k3po.lang.types.TypeInfo;

public abstract class ScriptParseStrategy<T extends AstRegion> {

    public static final ScriptParseStrategy<AstScriptNode> SCRIPT = new ScriptParseStrategy<AstScriptNode>() {
        @Override
        public AstScriptNode parse(RobotParser parser, ExpressionFactory factory, ExpressionContext environment)
                throws RecognitionException {
            return new AstScriptNodeVisitor(factory, environment).visit(parser.scriptNode());
        }
    };

    public static final ScriptParseStrategy<AstPropertyNode> PROPERTY_NODE = new ScriptParseStrategy<AstPropertyNode>() {
        @Override
        public AstPropertyNode parse(RobotParser parser, ExpressionFactory factory, ExpressionContext environment)
                throws RecognitionException {
            return new AstPropertyNodeVisitor(factory, environment).visit(parser.propertyNode());
        }
    };

    public static final ScriptParseStrategy<AstStreamNode> STREAM = new ScriptParseStrategy<AstStreamNode>() {
        @Override
        public AstStreamNode parse(RobotParser parser, ExpressionFactory factory, ExpressionContext environment)
                throws RecognitionException {
            return new AstStreamNodeVisitor(factory, environment).visit(parser.streamNode());
        }
    };

    public static final ScriptParseStrategy<AstStreamableNode> STREAMABLE = new ScriptParseStrategy<AstStreamableNode>() {
        @Override
        public AstStreamableNode parse(RobotParser parser, ExpressionFactory factory, ExpressionContext environment)
                throws RecognitionException {
            return new AstStreamableNodeVisitor(factory, environment).visit(parser.streamableNode());
        }
    };

    public static final ScriptParseStrategy<AstEventNode> EVENT = new ScriptParseStrategy<AstEventNode>() {
        @Override
        public AstEventNode parse(RobotParser parser, ExpressionFactory factory, ExpressionContext environment)
                throws RecognitionException {
            return new AstEventNodeVisitor(factory, environment).visit(parser.eventNode());
        }
    };

    public static final ScriptParseStrategy<AstCommandNode> COMMAND = new ScriptParseStrategy<AstCommandNode>() {
        @Override
        public AstCommandNode parse(RobotParser parser, ExpressionFactory factory, ExpressionContext environment)
                throws RecognitionException {
            return new AstCommandNodeVisitor(factory, environment).visit(parser.commandNode());
        }
    };

    public static final ScriptParseStrategy<AstBarrierNode> BARRIER = new ScriptParseStrategy<AstBarrierNode>() {
        @Override
        public AstBarrierNode parse(RobotParser parser, ExpressionFactory factory, ExpressionContext environment)
                throws RecognitionException {
            return new AstBarrierNodeVisitor(factory, environment).visit(parser.barrierNode());
        }
    };

    public static final ScriptParseStrategy<AstStreamableNode> SERVER_STREAMABLE = new ScriptParseStrategy<AstStreamableNode>() {
        @Override
        public AstStreamableNode parse(RobotParser parser, ExpressionFactory factory, ExpressionContext environment)
                throws RecognitionException {
            return new AstStreamableNodeVisitor(factory, environment).visit(parser.serverStreamableNode());
        }
    };

    public static final ScriptParseStrategy<AstEventNode> SERVER_EVENT = new ScriptParseStrategy<AstEventNode>() {
        @Override
        public AstEventNode parse(RobotParser parser, ExpressionFactory factory, ExpressionContext environment)
                throws RecognitionException {
            return new AstEventNodeVisitor(factory, environment).visit(parser.serverEventNode());
        }
    };

    public static final ScriptParseStrategy<AstCommandNode> SERVER_COMMAND = new ScriptParseStrategy<AstCommandNode>() {
        @Override
        public AstCommandNode parse(RobotParser parser, ExpressionFactory factory, ExpressionContext environment)
                throws RecognitionException {
            return new AstCommandNodeVisitor(factory, environment).visit(parser.serverCommandNode());
        }
    };

    public static final ScriptParseStrategy<AstAcceptNode> ACCEPT = new ScriptParseStrategy<AstAcceptNode>() {
        @Override
        public AstAcceptNode parse(RobotParser parser, ExpressionFactory factory, ExpressionContext environment)
                throws RecognitionException {
            return new AstAcceptNodeVisitor(factory, environment).visit(parser.acceptNode());
        }
    };

    public static final ScriptParseStrategy<AstAcceptedNode> ACCEPTED = new ScriptParseStrategy<AstAcceptedNode>() {
        @Override
        public AstAcceptedNode parse(RobotParser parser, ExpressionFactory factory, ExpressionContext environment)
                throws RecognitionException {
            return new AstAcceptedNodeVisitor(factory, environment).visit(parser.acceptedNode());
        }
    };

    public static final ScriptParseStrategy<AstRejectedNode> REJECTED = new ScriptParseStrategy<AstRejectedNode>() {
        @Override
        public AstRejectedNode parse(RobotParser parser, ExpressionFactory factory, ExpressionContext environment)
                throws RecognitionException {
            return new AstRejectedNodeVisitor(factory, environment).visit(parser.rejectedNode());
        }
    };

    public static final ScriptParseStrategy<AstConnectNode> CONNECT = new ScriptParseStrategy<AstConnectNode>() {
        @Override
        public AstConnectNode parse(RobotParser parser, ExpressionFactory factory, ExpressionContext environment)
                throws RecognitionException {
            return new AstConnectNodeVisitor(factory, environment).visit(parser.connectNode());
        }
    };

    public static final ScriptParseStrategy<AstConnectAbortNode> CONNECT_ABORT = new ScriptParseStrategy<AstConnectAbortNode>() {
        @Override
        public AstConnectAbortNode parse(RobotParser parser, ExpressionFactory factory, ExpressionContext environment)
                throws RecognitionException {
            return new AstConnectAbortNodeVisitor(factory, environment).visit(parser.connectAbortNode());
        }
    };

    public static final ScriptParseStrategy<AstConnectAbortedNode> CONNECT_ABORTED = new ScriptParseStrategy<AstConnectAbortedNode>() {
        @Override
        public AstConnectAbortedNode parse(RobotParser parser, ExpressionFactory factory, ExpressionContext environment)
                throws RecognitionException {
            return new AstConnectAbortedNodeVisitor(factory, environment).visit(parser.connectAbortedNode());
        }
    };

    public static final ScriptParseStrategy<AstCloseNode> CLOSE = new ScriptParseStrategy<AstCloseNode>() {
        @Override
        public AstCloseNode parse(RobotParser parser, ExpressionFactory factory, ExpressionContext environment)
                throws RecognitionException {
            AstCloseNodeVisitor astCloseNodeVisitor = new AstCloseNodeVisitor(factory, environment);
            return astCloseNodeVisitor.visit(parser.closeNode());
        }
    };

    public static final ScriptParseStrategy<AstWriteAbortNode> WRITE_ABORT = new ScriptParseStrategy<AstWriteAbortNode>() {
        @Override
        public AstWriteAbortNode parse(RobotParser parser, ExpressionFactory factory, ExpressionContext environment)
                throws RecognitionException {
            return new AstWriteAbortNodeVisitor(factory, environment).visit(parser.writeAbortNode());
        }
    };

    public static final ScriptParseStrategy<AstReadAbortNode> READ_ABORT = new ScriptParseStrategy<AstReadAbortNode>() {
        @Override
        public AstReadAbortNode parse(RobotParser parser, ExpressionFactory factory, ExpressionContext environment)
                throws RecognitionException {
            return new AstReadAbortNodeVisitor(factory, environment).visit(parser.readAbortNode());
        }
    };

    public static final ScriptParseStrategy<AstDisconnectNode> DISCONNECT = new ScriptParseStrategy<AstDisconnectNode>() {
        @Override
        public AstDisconnectNode parse(RobotParser parser, ExpressionFactory factory, ExpressionContext environment)
                throws RecognitionException {
            return new AstDisconnectNodeVisitor(factory, environment).visit(parser.disconnectNode());
        }
    };

    public static final ScriptParseStrategy<AstUnbindNode> UNBIND = new ScriptParseStrategy<AstUnbindNode>() {
        @Override
        public AstUnbindNode parse(RobotParser parser, ExpressionFactory factory, ExpressionContext environment)
                throws RecognitionException {
            return new AstUnbindNodeVisitor(factory, environment).visit(parser.unbindNode());
        }
    };

    public static final ScriptParseStrategy<AstWriteValueNode> WRITE = new ScriptParseStrategy<AstWriteValueNode>() {
        @Override
        public AstWriteValueNode parse(RobotParser parser, ExpressionFactory factory, ExpressionContext environment)
                throws RecognitionException {
            return new AstWriteValueNodeVisitor(factory, environment).visit(parser.writeNode());
        }
    };

    public static final ScriptParseStrategy<AstChildOpenedNode> CHILD_OPENED = new ScriptParseStrategy<AstChildOpenedNode>() {
        @Override
        public AstChildOpenedNode parse(RobotParser parser, ExpressionFactory factory, ExpressionContext environment)
                throws RecognitionException {
            return new AstChildOpenedNodeVisitor(factory, environment).visit(parser.childOpenedNode());
        }
    };

    public static final ScriptParseStrategy<AstChildClosedNode> CHILD_CLOSED = new ScriptParseStrategy<AstChildClosedNode>() {
        @Override
        public AstChildClosedNode parse(RobotParser parser, ExpressionFactory factory, ExpressionContext environment)
                throws RecognitionException {
            return new AstChildClosedNodeVisitor(factory, environment).visit(parser.childClosedNode());
        }
    };

    public static final ScriptParseStrategy<AstBoundNode> BOUND = new ScriptParseStrategy<AstBoundNode>() {
        @Override
        public AstBoundNode parse(RobotParser parser, ExpressionFactory factory, ExpressionContext environment)
                throws RecognitionException {
            return new AstBoundNodeVisitor(factory, environment).visit(parser.boundNode());
        }
    };

    public static final ScriptParseStrategy<AstClosedNode> CLOSED = new ScriptParseStrategy<AstClosedNode>() {
        @Override
        public AstClosedNode parse(RobotParser parser, ExpressionFactory factory, ExpressionContext environment)
                throws RecognitionException {
            return new AstClosedNodeVisitor(factory, environment).visit(parser.closedNode());
        }
    };

    public static final ScriptParseStrategy<AstReadAbortedNode> READ_ABORTED = new ScriptParseStrategy<AstReadAbortedNode>() {
        @Override
        public AstReadAbortedNode parse(RobotParser parser, ExpressionFactory factory, ExpressionContext environment)
                throws RecognitionException {
            return new AstReadAbortedNodeVisitor(factory, environment).visit(parser.readAbortedNode());
        }
    };

    public static final ScriptParseStrategy<AstWriteAbortedNode> WRITE_ABORTED = new ScriptParseStrategy<AstWriteAbortedNode>() {
        @Override
        public AstWriteAbortedNode parse(RobotParser parser, ExpressionFactory factory, ExpressionContext environment)
                throws RecognitionException {
            return new AstWriteAbortedNodeVisitor(factory, environment).visit(parser.writeAbortedNode());
        }
    };

    public static final ScriptParseStrategy<AstConnectedNode> CONNECTED = new ScriptParseStrategy<AstConnectedNode>() {
        @Override
        public AstConnectedNode parse(RobotParser parser, ExpressionFactory factory, ExpressionContext environment)
                throws RecognitionException {
            return new AstConnectedNodeVisitor(factory, environment).visit(parser.connectedNode());
        }
    };

    public static final ScriptParseStrategy<AstDisconnectedNode> DISCONNECTED = new ScriptParseStrategy<AstDisconnectedNode>() {
        @Override
        public AstDisconnectedNode parse(RobotParser parser, ExpressionFactory factory, ExpressionContext environment)
                throws RecognitionException {
            return new AstDisconnectedNodeVisitor(factory, environment).visit(parser.disconnectedNode());
        }
    };

    public static final ScriptParseStrategy<AstOpenedNode> OPENED = new ScriptParseStrategy<AstOpenedNode>() {
        @Override
        public AstOpenedNode parse(RobotParser parser, ExpressionFactory factory, ExpressionContext environment)
                throws RecognitionException {
            return new AstOpenedNodeVisitor(factory, environment).visit(parser.openedNode());
        }
    };

    public static final ScriptParseStrategy<AstReadValueNode> READ = new ScriptParseStrategy<AstReadValueNode>() {
        @Override
        public AstReadValueNode parse(RobotParser parser, ExpressionFactory factory, ExpressionContext environment)
                throws RecognitionException {
            return new AstReadValueNodeVisitor(factory, environment).visit(parser.readNode());
        }
    };


    public static final ScriptParseStrategy<AstWriteFlushNode> WRITE_FLUSH = new ScriptParseStrategy<AstWriteFlushNode>() {
        @Override
        public AstWriteFlushNode parse(RobotParser parser, ExpressionFactory factory, ExpressionContext environment)
                throws RecognitionException {
            return new AstWriteFlushNodeVisitor(factory, environment).visit(parser.writeFlushNode());
        }
    };

    public static final ScriptParseStrategy<AstReadClosedNode> READ_CLOSED = new ScriptParseStrategy<AstReadClosedNode>() {
        @Override
        public AstReadClosedNode parse(RobotParser parser, ExpressionFactory factory, ExpressionContext environment)
                throws RecognitionException {
            return new AstReadClosedNodeVisitor(factory, environment).visit(parser.readClosedNode());
        }
    };

    public static final ScriptParseStrategy<AstWriteCloseNode> WRITE_CLOSE = new ScriptParseStrategy<AstWriteCloseNode>() {
        @Override
        public AstWriteCloseNode parse(RobotParser parser, ExpressionFactory factory, ExpressionContext environment)
                throws RecognitionException {
            return new AstWriteCloseNodeVisitor(factory, environment).visit(parser.writeCloseNode());
        }
    };

    public static final ScriptParseStrategy<AstUnboundNode> UNBOUND = new ScriptParseStrategy<AstUnboundNode>() {
        @Override
        public AstUnboundNode parse(RobotParser parser, ExpressionFactory factory, ExpressionContext environment)
                throws RecognitionException {
            return new AstUnboundNodeVisitor(factory, environment).visit(parser.unboundNode());
        }
    };

    public static final ScriptParseStrategy<AstReadAwaitNode> READ_AWAIT = new ScriptParseStrategy<AstReadAwaitNode>() {
        @Override
        public AstReadAwaitNode parse(RobotParser parser, ExpressionFactory factory, ExpressionContext environment)
                throws RecognitionException {
            return new AstReadAwaitNodeVisitor(factory, environment).visit(parser.readAwaitNode());
        }
    };

    public static final ScriptParseStrategy<AstReadNotifyNode> READ_NOTIFY = new ScriptParseStrategy<AstReadNotifyNode>() {
        @Override
        public AstReadNotifyNode parse(RobotParser parser, ExpressionFactory factory, ExpressionContext environment)
                throws RecognitionException {
            return new AstReadNotifyNodeVisitor(factory, environment).visit(parser.readNotifyNode());
        }
    };

    public static final ScriptParseStrategy<AstWriteAwaitNode> WRITE_AWAIT = new ScriptParseStrategy<AstWriteAwaitNode>() {
        @Override
        public AstWriteAwaitNode parse(RobotParser parser, ExpressionFactory factory, ExpressionContext environment)
                throws RecognitionException {
            return new AstWriteAwaitNodeVisitor(factory, environment).visit(parser.writeAwaitNode());
        }
    };

    public static final ScriptParseStrategy<AstWriteNotifyNode> WRITE_NOTIFY = new ScriptParseStrategy<AstWriteNotifyNode>() {
        @Override
        public AstWriteNotifyNode parse(RobotParser parser, ExpressionFactory factory, ExpressionContext environment)
                throws RecognitionException {
            return new AstWriteNotifyNodeVisitor(factory, environment).visit(parser.writeNotifyNode());
        }
    };

    public static final ScriptParseStrategy<AstValueMatcher> MATCHER = new ScriptParseStrategy<AstValueMatcher>() {
        @Override
        public AstValueMatcher parse(RobotParser parser, ExpressionFactory factory, ExpressionContext environment)
                throws RecognitionException {
            return new AstValueMatcherVisitor(factory, environment).visit(parser.matcher());
        }
    };

    public static final ScriptParseStrategy<AstExactTextMatcher> EXACT_TEXT_MATCHER =
        new ScriptParseStrategy<AstExactTextMatcher>() {
            @Override
            public AstExactTextMatcher parse(RobotParser parser, ExpressionFactory factory, ExpressionContext environment)
                    throws RecognitionException {
                return new AstExactTextMatcherVisitor(factory, environment).visit(parser.exactTextMatcher());
            }
        };

    public static final ScriptParseStrategy<AstExactBytesMatcher> EXACT_BYTES_MATCHER =
        new ScriptParseStrategy<AstExactBytesMatcher>() {
            @Override
            public AstExactBytesMatcher parse(RobotParser parser, ExpressionFactory factory, ExpressionContext environment)
                    throws RecognitionException {
                return new AstExactBytesMatcherVisitor(factory, environment).visit(parser.exactBytesMatcher());
            }
        };

        public static final ScriptParseStrategy<AstNumberMatcher> NUMBER_MATCHER =
                new ScriptParseStrategy<AstNumberMatcher>() {
                    @Override
                    public AstNumberMatcher parse(RobotParser parser, ExpressionFactory factory, ExpressionContext environment)
                            throws RecognitionException {
                        return new AstNumberMatcherVisitor(factory, environment).visit(parser.numberMatcher());
                    }
                };

    public static final ScriptParseStrategy<AstRegexMatcher> REGEX_MATCHER = new ScriptParseStrategy<AstRegexMatcher>() {
        @Override
        public AstRegexMatcher parse(RobotParser parser, ExpressionFactory factory, ExpressionContext environment)
                throws RecognitionException {
            return new AstRegexMatcherVisitor(factory, environment).visit(parser.regexMatcher());
        }
    };

    public static final ScriptParseStrategy<AstExpressionMatcher> EXPRESSION_MATCHER =
        new ScriptParseStrategy<AstExpressionMatcher>() {
            @Override
            public AstExpressionMatcher parse(RobotParser parser, ExpressionFactory factory, ExpressionContext environment)
                    throws RecognitionException {
                return new AstExpressionMatcherVisitor(factory, environment).visit(parser.expressionMatcher());
            }
        };

    public static final ScriptParseStrategy<AstFixedLengthBytesMatcher> FIXED_LENGTH_BYTES_MATCHER =
        new ScriptParseStrategy<AstFixedLengthBytesMatcher>() {
            @Override
            public AstFixedLengthBytesMatcher parse(RobotParser parser, ExpressionFactory factory,
                ExpressionContext environment) throws RecognitionException {
                return new AstFixedLengthBytesMatcherVisitor(factory, environment).visit(parser.fixedLengthBytesMatcher());
            }
        };

    public static final ScriptParseStrategy<AstVariableLengthBytesMatcher> VARIABLE_LENGTH_BYTES_MATCHER =
        new ScriptParseStrategy<AstVariableLengthBytesMatcher>() {
            @Override
            public AstVariableLengthBytesMatcher parse(RobotParser parser, ExpressionFactory factory,
                ExpressionContext environment) throws RecognitionException {
                return new AstVariableLengthBytesMatcherVisitor(factory, environment)
                        .visit(parser.variableLengthBytesMatcher());
            }
        };

    public static final ScriptParseStrategy<AstLiteralTextValue> LITERAL_TEXT_VALUE =
        new ScriptParseStrategy<AstLiteralTextValue>() {
            @Override
            public AstLiteralTextValue parse(RobotParser parser, ExpressionFactory factory, ExpressionContext environment)
                    throws RecognitionException {
                return new AstLiteralTextValueVisitor(factory, environment).visit(parser.literalText());
            }
        };

    public static final ScriptParseStrategy<AstLiteralBytesValue> LITERAL_BYTES_VALUE =
        new ScriptParseStrategy<AstLiteralBytesValue>() {
            @Override
            public AstLiteralBytesValue parse(RobotParser parser, ExpressionFactory factory, ExpressionContext environment)
                    throws RecognitionException {
                return new AstLiteralBytesValueVisitor(factory, environment).visit(parser.literalBytes());
            }
        };

    public static final ScriptParseStrategy<AstReadOptionNode> READ_OPTION = new ScriptParseStrategy<AstReadOptionNode>() {
        @Override
        public AstReadOptionNode parse(RobotParser parser, ExpressionFactory factory, ExpressionContext environment)
                throws RecognitionException {
            return new AstReadOptionNodeVisitor(factory, environment).visit(parser.readOptionNode());
        }
    };

    public static final ScriptParseStrategy<AstWriteOptionNode> WRITE_OPTION =
        new ScriptParseStrategy<AstWriteOptionNode>() {
            @Override
            public AstWriteOptionNode parse(RobotParser parser, ExpressionFactory factory, ExpressionContext environment)
                    throws RecognitionException {
                return new AstWriteOptionNodeVisitor(factory, environment)
                        .visit(parser.writeOptionNode());
            }
        };

    public static final ScriptParseStrategy<AstReadConfigNode> READ_CONFIG =
        new ScriptParseStrategy<AstReadConfigNode>() {
            @Override
            public AstReadConfigNode parse(RobotParser parser, ExpressionFactory factory, ExpressionContext environment)
                    throws RecognitionException {
                return new AstReadConfigNodeVisitor(factory, environment).visit(parser.readConfigNode());
            }
        };

    public static final ScriptParseStrategy<AstWriteConfigNode> WRITE_CONFIG =
        new ScriptParseStrategy<AstWriteConfigNode>() {
            @Override
            public AstWriteConfigNode parse(RobotParser parser, ExpressionFactory factory, ExpressionContext environment)
                    throws RecognitionException {
                return new AstWriteConfigNodeVisitor(factory, environment)
                        .visit(parser.writeConfigNode());
            }
        };

    public static final ScriptParseStrategy<AstReadAdviseNode> READ_ADVISE =
        new ScriptParseStrategy<AstReadAdviseNode>() {
            @Override
            public AstReadAdviseNode parse(RobotParser parser, ExpressionFactory factory, ExpressionContext environment)
                    throws RecognitionException {
                return new AstReadAdviseNodeVisitor(factory, environment)
                        .visit(parser.readAdviseNode());
            }
        };

    public static final ScriptParseStrategy<AstWriteAdviseNode> WRITE_ADVISE =
        new ScriptParseStrategy<AstWriteAdviseNode>() {
            @Override
            public AstWriteAdviseNode parse(RobotParser parser, ExpressionFactory factory, ExpressionContext environment)
                    throws RecognitionException {
                return new AstWriteAdviseNodeVisitor(factory, environment)
                        .visit(parser.writeAdviseNode());
            }
        };

    public static final ScriptParseStrategy<AstReadAdvisedNode> READ_ADVISED =
        new ScriptParseStrategy<AstReadAdvisedNode>() {
            @Override
            public AstReadAdvisedNode parse(RobotParser parser, ExpressionFactory factory, ExpressionContext environment)
                    throws RecognitionException {
                return new AstReadAdvisedNodeVisitor(factory, environment).visit(parser.readAdvisedNode());
            }
        };

    public static final ScriptParseStrategy<AstWriteAdvisedNode> WRITE_ADVISED =
        new ScriptParseStrategy<AstWriteAdvisedNode>() {
            @Override
            public AstWriteAdvisedNode parse(RobotParser parser, ExpressionFactory factory, ExpressionContext environment)
                    throws RecognitionException {
                return new AstWriteAdvisedNodeVisitor(factory, environment).visit(parser.writeAdvisedNode());
            }
        };


    public abstract T parse(RobotParser parser, ExpressionFactory factory, ExpressionContext environment)
            throws RecognitionException;

    private static class AstVisitor<T> extends RobotBaseVisitor<T> {
        private static final List<RegionInfo> EMPTY_CHILDREN = emptyList();

        protected List<RegionInfo> childInfos = EMPTY_CHILDREN;

        protected final ExpressionFactory factory;
        protected final ExpressionContext environment;

        protected AstVisitor(ExpressionFactory factory, ExpressionContext environment) {
            this.factory = factory;
            this.environment = environment;
        }

        protected List<RegionInfo> childInfos() {
            if (childInfos == EMPTY_CHILDREN) {
                childInfos = new LinkedList<>();
            }
            return childInfos;
        }
    }

    private static class AstNodeVisitor<T extends AstNode> extends AstVisitor<T> {

        protected T node;

        public AstNodeVisitor(ExpressionFactory factory, ExpressionContext environment) {
            super(factory, environment);
        }

        @Override
        protected T defaultResult() {
            return node;
        }

    }

    private static class AstScriptNodeVisitor extends AstNodeVisitor<AstScriptNode> {

        public AstScriptNodeVisitor(ExpressionFactory factory, ExpressionContext environment) {
            super(factory, environment);
        }

        @Override
        public AstScriptNode visitScriptNode(ScriptNodeContext ctx) {
            node = new AstScriptNode();
            super.visitScriptNode(ctx);
            node.setRegionInfo(asSequentialRegion(childInfos, ctx));
            return node;
        }

        @Override
        public AstScriptNode visitPropertyNode(PropertyNodeContext ctx) {
            AstPropertyNodeVisitor visitor = new AstPropertyNodeVisitor(factory, environment);
            AstPropertyNode propertyNode = visitor.visitPropertyNode(ctx);
            if (propertyNode != null) {
                node.getProperties().add(propertyNode);
                childInfos().add(propertyNode.getRegionInfo());
            }
            return node;
        }

        @Override
        public AstScriptNode visitStreamNode(StreamNodeContext ctx) {
            AstStreamNodeVisitor visitor = new AstStreamNodeVisitor(factory, environment);
            AstStreamNode streamNode = visitor.visitStreamNode(ctx);
            if (streamNode != null) {
                node.getStreams().add(streamNode);
                childInfos().add(streamNode.getRegionInfo());
            }
            return node;
        }

    }

    private static class AstPropertyNodeVisitor extends AstNodeVisitor<AstPropertyNode> {

        public AstPropertyNodeVisitor(ExpressionFactory factory, ExpressionContext environment) {
            super(factory, environment);
        }

        @Override
        public AstPropertyNode visitPropertyNode(PropertyNodeContext ctx) {

            AstValueVisitor<?> visitor = new AstValueVisitor<>(factory, environment, Object.class);
            AstValue<?> value = visitor.visit(ctx.value);
            childInfos().add(value.getRegionInfo());

            node = new AstPropertyNode();
            node.setRegionInfo(asSequentialRegion(childInfos, ctx));
            node.setPropertyName(ctx.name.getText());
            node.setPropertyValue(value);
            node.setEnvironment(environment);

            return node;
        }

    }

    private static class AstStreamNodeVisitor extends AstNodeVisitor<AstStreamNode> {

        public AstStreamNodeVisitor(ExpressionFactory factory, ExpressionContext environment) {
            super(factory, environment);
        }

        @Override
        public AstAcceptNode visitAcceptNode(AcceptNodeContext ctx) {
            AstAcceptNodeVisitor visitor = new AstAcceptNodeVisitor(factory, environment);
            AstAcceptNode acceptNode = visitor.visitAcceptNode(ctx);
            if (acceptNode != null) {
                childInfos().add(acceptNode.getRegionInfo());
            }
            return acceptNode;
        }

        @Override
        public AstAcceptedNode visitAcceptedNode(AcceptedNodeContext ctx) {
            AstAcceptedNodeVisitor visitor = new AstAcceptedNodeVisitor(factory, environment);
            AstAcceptedNode acceptedNode = visitor.visitAcceptedNode(ctx);
            if (acceptedNode != null) {
                childInfos().add(acceptedNode.getRegionInfo());
            }
            return acceptedNode;
        }

        @Override
        public AstRejectedNode visitRejectedNode(RejectedNodeContext ctx) {
            AstRejectedNodeVisitor visitor = new AstRejectedNodeVisitor(factory, environment);
            AstRejectedNode rejectedNode = visitor.visitRejectedNode(ctx);
            if (rejectedNode != null) {
                childInfos().add(rejectedNode.getRegionInfo());
            }
            return rejectedNode;
        }

        @Override
        public AstConnectNode visitConnectNode(ConnectNodeContext ctx) {
            AstConnectNodeVisitor visitor = new AstConnectNodeVisitor(factory, environment);
            AstConnectNode connectNode = visitor.visitConnectNode(ctx);
            if (connectNode != null) {
                childInfos().add(connectNode.getRegionInfo());
            }
            return connectNode;
        }

    }

    private static class AstAcceptNodeVisitor extends AstNodeVisitor<AstAcceptNode> {

        public AstAcceptNodeVisitor(ExpressionFactory factory, ExpressionContext environment) {
            super(factory, environment);
        }

        @Override
        public AstAcceptNode visitAcceptNode(AcceptNodeContext ctx) {
            AstLocationVisitor locationVisitor = new AstLocationVisitor(factory, environment);
            AstValue<URI> location = locationVisitor.visit(ctx.acceptURI);
            node = new AstAcceptNode();
            node.setLocation(location);
            if (ctx.as != null) {
                node.setAcceptName(ctx.as.getText());
            }
            if (ctx.notify != null) {
                node.setNotifyName(ctx.notify.getText());
            }
            super.visitAcceptNode(ctx);
            node.setRegionInfo(asParallelRegion(childInfos, ctx));
            return node;
        }

        @Override
        public AstAcceptNode visitAcceptOption(AcceptOptionContext ctx) {

            String optionQName = ctx.optionName().getText();
            TypeInfo<?> optionType = TYPE_SYSTEM.acceptOption(optionQName);
            String optionName = optionType.getName();
            Class<?> expectedType = optionType.getType();
            AstValueVisitor<?> valueVisitor = new AstValueVisitor<>(factory, environment, expectedType);
            AstValue<?> optionValue = valueVisitor.visit(ctx.writeValue());

            Map<String, Object> options = node.getOptions();
            options.put(optionName, optionValue);

            return super.visitAcceptOption(ctx);
        }

        @Override
        public AstAcceptNode visitServerStreamableNode(ServerStreamableNodeContext ctx) {
            AstStreamableNodeVisitor visitor = new AstStreamableNodeVisitor(factory, environment);
            AstStreamableNode streamableNode = visitor.visitServerStreamableNode(ctx);
            if (streamableNode != null) {
                node.getStreamables().add(streamableNode);
                childInfos().add(streamableNode.getRegionInfo());
            }
            return node;
        }

    }

    private static class AstAcceptedNodeVisitor extends AstNodeVisitor<AstAcceptedNode> {

        public AstAcceptedNodeVisitor(ExpressionFactory factory, ExpressionContext environment) {
            super(factory, environment);
        }

        @Override
        public AstAcceptedNode visitAcceptedNode(AcceptedNodeContext ctx) {
            node = new AstAcceptedNode();
            if (ctx.text != null) {
                node.setAcceptName(ctx.text.getText());
            }
            super.visitAcceptedNode(ctx);
            node.setRegionInfo(asParallelRegion(childInfos, ctx));
            return node;
        }

        @Override
        public AstAcceptedNode visitStreamableNode(StreamableNodeContext ctx) {
            AstStreamableNodeVisitor visitor = new AstStreamableNodeVisitor(factory, environment);
            AstStreamableNode streamableNode = visitor.visitStreamableNode(ctx);
            if (streamableNode != null) {
                node.getStreamables().add(streamableNode);
                childInfos().add(streamableNode.getRegionInfo());
            }
            return node;
        }

    }

    private static class AstRejectedNodeVisitor extends AstNodeVisitor<AstRejectedNode> {

        public AstRejectedNodeVisitor(ExpressionFactory factory, ExpressionContext environment) {
            super(factory, environment);
        }

        @Override
        public AstRejectedNode visitRejectedNode(RejectedNodeContext ctx) {
            node = new AstRejectedNode();
            if (ctx.text != null) {
                node.setAcceptName(ctx.text.getText());
            }
            super.visitRejectedNode(ctx);
            node.setRegionInfo(asParallelRegion(childInfos, ctx));
            return node;
        }

        @Override
        public AstRejectedNode visitRejectableNode(RejectableNodeContext ctx) {
            AstStreamableNodeVisitor visitor = new AstStreamableNodeVisitor(factory, environment);
            AstStreamableNode rejectableNode = visitor.visitRejectableNode(ctx);
            if (rejectableNode != null) {
                node.getStreamables().add(rejectableNode);
                childInfos().add(rejectableNode.getRegionInfo());
            }
            return node;
        }

    }

    private static class AstConnectNodeVisitor extends AstNodeVisitor<AstConnectNode> {

        public AstConnectNodeVisitor(ExpressionFactory factory, ExpressionContext environment) {
            super(factory, environment);
        }

        @Override
        public AstConnectNode visitConnectNode(ConnectNodeContext ctx) {
            AstLocationVisitor locationVisitor = new AstLocationVisitor(factory, environment);
            AstValue<URI> location = locationVisitor.visit(ctx.connectURI);
            node = new AstConnectNode();
            node.setLocation(location);
            super.visitConnectNode(ctx);
            node.setRegionInfo(asParallelRegion(childInfos, ctx));
            if (ctx.await != null) {
                node.setAwaitName(ctx.await.getText());
            }
            return node;
        }

        @Override
        public AstConnectNode visitConnectOption(ConnectOptionContext ctx) {
            String optionQName = ctx.optionName().getText();
            TypeInfo<?> optionType = TYPE_SYSTEM.connectOption(optionQName);
            String optionName = optionType.getName();
            Class<?> expectedType = optionType.getType();
            AstValueVisitor<?> valueVisitor = new AstValueVisitor<>(factory, environment, expectedType);
            AstValue<?> optionValue = valueVisitor.visit(ctx.writeValue());

            Map<String, Object> options = node.getOptions();
            options.put(optionName, optionValue);

            return super.visitConnectOption(ctx);
        }

        @Override
        public AstConnectNode visitStreamableNode(StreamableNodeContext ctx) {
            AstStreamableNodeVisitor visitor = new AstStreamableNodeVisitor(factory, environment);
            AstStreamableNode streamableNode = visitor.visitStreamableNode(ctx);
            if (streamableNode != null) {
                node.getStreamables().add(streamableNode);
                childInfos().add(streamableNode.getRegionInfo());
            }
            return node;
        }

    }

    private static class AstConnectAbortNodeVisitor extends AstNodeVisitor<AstConnectAbortNode> {

        public AstConnectAbortNodeVisitor(ExpressionFactory factory, ExpressionContext environment) {
            super(factory, environment);
        }

        @Override
        public AstConnectAbortNode visitConnectAbortNode(ConnectAbortNodeContext ctx) {
            node = new AstConnectAbortNode();
            super.visitConnectAbortNode(ctx);
            node.setRegionInfo(asParallelRegion(childInfos, ctx));
            return node;
        }

    }

    private static class AstConnectAbortedNodeVisitor extends AstNodeVisitor<AstConnectAbortedNode> {

        public AstConnectAbortedNodeVisitor(ExpressionFactory factory, ExpressionContext environment) {
            super(factory, environment);
        }

        @Override
        public AstConnectAbortedNode visitConnectAbortedNode(ConnectAbortedNodeContext ctx) {
            node = new AstConnectAbortedNode();
            super.visitConnectAbortedNode(ctx);
            node.setRegionInfo(asParallelRegion(childInfos, ctx));
            return node;
        }

    }

    private static class AstStreamableNodeVisitor extends AstNodeVisitor<AstStreamableNode> {

        public AstStreamableNodeVisitor(ExpressionFactory factory, ExpressionContext environment) {
            super(factory, environment);
        }

        @Override
        public AstBarrierNode visitBarrierNode(BarrierNodeContext ctx) {
            AstBarrierNodeVisitor visitor = new AstBarrierNodeVisitor(factory, environment);
            AstBarrierNode barrierNode = visitor.visitBarrierNode(ctx);
            if (barrierNode != null) {
                childInfos().add(barrierNode.getRegionInfo());
            }
            return barrierNode;
        }

        @Override
        public AstEventNode visitEventNode(EventNodeContext ctx) {
            AstEventNodeVisitor visitor = new AstEventNodeVisitor(factory, environment);
            AstEventNode eventNode = visitor.visitEventNode(ctx);
            if (eventNode != null) {
                childInfos().add(eventNode.getRegionInfo());
            }
            return eventNode;
        }

        @Override
        public AstCommandNode visitCommandNode(CommandNodeContext ctx) {
            AstCommandNodeVisitor visitor = new AstCommandNodeVisitor(factory, environment);
            AstCommandNode commandNode = visitor.visitCommandNode(ctx);
            if (commandNode != null) {
                childInfos().add(commandNode.getRegionInfo());
            }
            return commandNode;
        }

        @Override
        public AstReadOptionNode visitReadOptionNode(ReadOptionNodeContext ctx) {
            AstReadOptionNodeVisitor visitor = new AstReadOptionNodeVisitor(factory, environment);
            AstReadOptionNode optionNode = visitor.visitReadOptionNode(ctx);
            if (optionNode != null) {
                childInfos().add(optionNode.getRegionInfo());
            }
            return optionNode;
        }

        @Override
        public AstWriteOptionNode visitWriteOptionNode(WriteOptionNodeContext ctx) {
            AstWriteOptionNodeVisitor visitor = new AstWriteOptionNodeVisitor(factory, environment);
            AstWriteOptionNode optionNode = visitor.visitWriteOptionNode(ctx);
            if (optionNode != null) {
                childInfos().add(optionNode.getRegionInfo());
            }
            return optionNode;
        }
    }

    private static class AstReadOptionNodeVisitor extends AstNodeVisitor<AstReadOptionNode> {

        public AstReadOptionNodeVisitor(ExpressionFactory factory, ExpressionContext environment) {
            super(factory, environment);
        }

        @Override
        public AstReadOptionNode visitReadOptionNode(ReadOptionNodeContext ctx) {

            String optionQName = ctx.optionName().getText();
            TypeInfo<?> optionType = TYPE_SYSTEM.readOption(optionQName);
            Class<?> expectedType = optionType.getType();
            AstValueVisitor<?> visitor = new AstValueVisitor<>(factory, environment, expectedType);
            AstValue<?> optionValue = visitor.visit(ctx);
            childInfos().add(optionValue.getRegionInfo());

            node = new AstReadOptionNode();
            node.setOptionType(optionType);
            node.setOptionName(optionQName);
            node.setOptionValue(optionValue);
            node.setRegionInfo(asSequentialRegion(childInfos, ctx));

            return node;
        }
    }

    private static class AstWriteOptionNodeVisitor extends AstNodeVisitor<AstWriteOptionNode> {

        public AstWriteOptionNodeVisitor(ExpressionFactory factory, ExpressionContext environment) {
            super(factory, environment);
        }

        @Override
        public AstWriteOptionNode visitWriteOptionNode(WriteOptionNodeContext ctx) {

            String optionQName = ctx.optionName().getText();
            TypeInfo<?> optionType = TYPE_SYSTEM.writeOption(optionQName);
            Class<?> expectedType = optionType.getType();
            AstValueVisitor<?> visitor = new AstValueVisitor<>(factory, environment, expectedType);
            AstValue<?> optionValue = visitor.visit(ctx);
            childInfos().add(optionValue.getRegionInfo());

            node = new AstWriteOptionNode();
            node.setOptionType(optionType);
            node.setOptionName(optionQName);
            node.setOptionValue(optionValue);
            node.setRegionInfo(asSequentialRegion(childInfos, ctx));

            return node;
        }
    }

    private static class AstBarrierNodeVisitor extends AstNodeVisitor<AstBarrierNode> {

        public AstBarrierNodeVisitor(ExpressionFactory factory, ExpressionContext environment) {
            super(factory, environment);
        }

        @Override
        public AstReadAwaitNode visitReadAwaitNode(ReadAwaitNodeContext ctx) {

            AstReadAwaitNodeVisitor visitor = new AstReadAwaitNodeVisitor(factory, environment);
            AstReadAwaitNode readAwaitNode = visitor.visitReadAwaitNode(ctx);
            if (readAwaitNode != null) {
                childInfos().add(readAwaitNode.getRegionInfo());
            }

            return readAwaitNode;
        }

        @Override
        public AstReadNotifyNode visitReadNotifyNode(ReadNotifyNodeContext ctx) {
            AstReadNotifyNodeVisitor visitor = new AstReadNotifyNodeVisitor(factory, environment);
            AstReadNotifyNode readNotifyNode = visitor.visitReadNotifyNode(ctx);
            if (readNotifyNode != null) {
                childInfos().add(readNotifyNode.getRegionInfo());
            }
            return readNotifyNode;
        }

        @Override
        public AstWriteAwaitNode visitWriteAwaitNode(WriteAwaitNodeContext ctx) {

            AstWriteAwaitNodeVisitor visitor = new AstWriteAwaitNodeVisitor(factory, environment);
            AstWriteAwaitNode writeAwaitNode = visitor.visitWriteAwaitNode(ctx);
            if (writeAwaitNode != null) {
                childInfos().add(writeAwaitNode.getRegionInfo());
            }

            return writeAwaitNode;
        }

        @Override
        public AstWriteNotifyNode visitWriteNotifyNode(WriteNotifyNodeContext ctx) {

            AstWriteNotifyNodeVisitor visitor = new AstWriteNotifyNodeVisitor(factory, environment);
            AstWriteNotifyNode writeNotifyNode = visitor.visitWriteNotifyNode(ctx);
            if (writeNotifyNode != null) {
                childInfos().add(writeNotifyNode.getRegionInfo());
            }

            return writeNotifyNode;
        }

    }

    private static class AstEventNodeVisitor extends AstNodeVisitor<AstEventNode> {

        public AstEventNodeVisitor(ExpressionFactory factory, ExpressionContext environment) {
            super(factory, environment);
        }

        @Override
        public AstBoundNode visitBoundNode(BoundNodeContext ctx) {

            AstBoundNodeVisitor visitor = new AstBoundNodeVisitor(factory, environment);
            AstBoundNode boundNode = visitor.visitBoundNode(ctx);
            if (boundNode != null) {
                childInfos().add(boundNode.getRegionInfo());
            }

            return boundNode;
        }

        @Override
        public AstClosedNode visitClosedNode(ClosedNodeContext ctx) {

            AstClosedNodeVisitor visitor = new AstClosedNodeVisitor(factory, environment);
            AstClosedNode closedNode = visitor.visitClosedNode(ctx);
            if (closedNode != null) {
                childInfos().add(closedNode.getRegionInfo());
            }

            return closedNode;
        }

        @Override
        public AstConnectAbortedNode visitConnectAbortedNode(ConnectAbortedNodeContext ctx) {
            AstConnectAbortedNodeVisitor visitor = new AstConnectAbortedNodeVisitor(factory, environment);
            AstConnectAbortedNode connectAbortedNode = visitor.visitConnectAbortedNode(ctx);
            if (connectAbortedNode != null) {
                childInfos().add(connectAbortedNode.getRegionInfo());
            }
            return connectAbortedNode;
        }

        @Override
        public AstConnectedNode visitConnectedNode(ConnectedNodeContext ctx) {

            AstConnectedNodeVisitor visitor = new AstConnectedNodeVisitor(factory, environment);
            AstConnectedNode connectedNode = visitor.visitConnectedNode(ctx);
            if (connectedNode != null) {
                childInfos().add(connectedNode.getRegionInfo());
            }

            return connectedNode;
        }

        @Override
        public AstDisconnectedNode visitDisconnectedNode(DisconnectedNodeContext ctx) {

            AstDisconnectedNodeVisitor visitor = new AstDisconnectedNodeVisitor(factory, environment);
            AstDisconnectedNode disconnectedNode = visitor.visitDisconnectedNode(ctx);
            if (disconnectedNode != null) {
                childInfos().add(disconnectedNode.getRegionInfo());
            }

            return disconnectedNode;
        }

        @Override
        public AstOpenedNode visitOpenedNode(OpenedNodeContext ctx) {

            AstOpenedNodeVisitor visitor = new AstOpenedNodeVisitor(factory, environment);
            AstOpenedNode openedNode = visitor.visitOpenedNode(ctx);
            if (openedNode != null) {
                childInfos().add(openedNode.getRegionInfo());
            }

            return openedNode;
        }

        @Override
        public AstReadAdvisedNode visitReadAdvisedNode(ReadAdvisedNodeContext ctx) {

            AstReadAdvisedNodeVisitor visitor = new AstReadAdvisedNodeVisitor(factory, environment);
            AstReadAdvisedNode readAdvisedNode = visitor.visitReadAdvisedNode(ctx);
            if (readAdvisedNode != null) {
                childInfos().add(readAdvisedNode.getRegionInfo());
            }

            return readAdvisedNode;
        }

        @Override
        public AstWriteAdvisedNode visitWriteAdvisedNode(WriteAdvisedNodeContext ctx) {

            AstWriteAdvisedNodeVisitor visitor = new AstWriteAdvisedNodeVisitor(factory, environment);
            AstWriteAdvisedNode writeAdvisedNode = visitor.visitWriteAdvisedNode(ctx);
            if (writeAdvisedNode != null) {
                childInfos().add(writeAdvisedNode.getRegionInfo());
            }

            return writeAdvisedNode;
        }

        @Override
        public AstReadConfigNode visitReadConfigNode(ReadConfigNodeContext ctx) {

            AstReadConfigNodeVisitor visitor = new AstReadConfigNodeVisitor(factory, environment);
            AstReadConfigNode readConfigNode = visitor.visitReadConfigNode(ctx);
            if (readConfigNode != null) {
                childInfos().add(readConfigNode.getRegionInfo());
            }

            return readConfigNode;
        }

        @Override
        public AstReadValueNode visitReadNode(ReadNodeContext ctx) {

            AstReadValueNodeVisitor visitor = new AstReadValueNodeVisitor(factory, environment);
            AstReadValueNode readNode = visitor.visitReadNode(ctx);
            if (readNode != null) {
                childInfos().add(readNode.getRegionInfo());
            }

            return readNode;
        }

        @Override
        public AstReadClosedNode visitReadClosedNode(ReadClosedNodeContext ctx) {

            AstReadClosedNodeVisitor visitor = new AstReadClosedNodeVisitor(factory, environment);
            AstReadClosedNode readClosedNode = visitor.visitReadClosedNode(ctx);
            if (readClosedNode != null) {
                childInfos().add(readClosedNode.getRegionInfo());
            }

            return readClosedNode;
        }

        @Override
        public AstUnboundNode visitUnboundNode(UnboundNodeContext ctx) {

            AstUnboundNodeVisitor visitor = new AstUnboundNodeVisitor(factory, environment);
            AstUnboundNode unboundNode = visitor.visitUnboundNode(ctx);
            if (unboundNode != null) {
                childInfos().add(unboundNode.getRegionInfo());
            }

            return unboundNode;
        }

        @Override
        public AstReadAbortedNode visitReadAbortedNode(ReadAbortedNodeContext ctx) {

            AstReadAbortedNodeVisitor visitor = new AstReadAbortedNodeVisitor(factory, environment);
            AstReadAbortedNode abortedNode = visitor.visitReadAbortedNode(ctx);
            if (abortedNode != null) {
                childInfos().add(abortedNode.getRegionInfo());
            }

            return abortedNode;
        }

        @Override
        public AstWriteAbortedNode visitWriteAbortedNode(WriteAbortedNodeContext ctx) {

            AstWriteAbortedNodeVisitor visitor = new AstWriteAbortedNodeVisitor(factory, environment);
            AstWriteAbortedNode abortedNode = visitor.visitWriteAbortedNode(ctx);
            if (abortedNode != null) {
                childInfos().add(abortedNode.getRegionInfo());
            }

            return abortedNode;
        }
    }

    private static class AstCommandNodeVisitor extends AstNodeVisitor<AstCommandNode> {

        public AstCommandNodeVisitor(ExpressionFactory factory, ExpressionContext environment) {
            super(factory, environment);
        }

        @Override
        public AstConnectAbortNode visitConnectAbortNode(ConnectAbortNodeContext ctx) {
            AstConnectAbortNodeVisitor visitor = new AstConnectAbortNodeVisitor(factory, environment);
            AstConnectAbortNode connectAbortNode = visitor.visitConnectAbortNode(ctx);
            if (connectAbortNode != null) {
                childInfos().add(connectAbortNode.getRegionInfo());
            }
            return connectAbortNode;
        }

        @Override
        public AstUnbindNode visitUnbindNode(UnbindNodeContext ctx) {

            AstUnbindNodeVisitor visitor = new AstUnbindNodeVisitor(factory, environment);
            AstUnbindNode unbindNode = visitor.visitUnbindNode(ctx);
            if (unbindNode != null) {
                childInfos().add(unbindNode.getRegionInfo());
            }

            return unbindNode;
        }

        @Override
        public AstReadAdviseNode visitReadAdviseNode(ReadAdviseNodeContext ctx) {

            AstReadAdviseNodeVisitor visitor = new AstReadAdviseNodeVisitor(factory, environment);
            AstReadAdviseNode readAdviseNode = visitor.visitReadAdviseNode(ctx);
            if (readAdviseNode != null) {
                childInfos().add(readAdviseNode.getRegionInfo());
            }

            return readAdviseNode;
        }

        @Override
        public AstWriteAdviseNode visitWriteAdviseNode(WriteAdviseNodeContext ctx) {

            AstWriteAdviseNodeVisitor visitor = new AstWriteAdviseNodeVisitor(factory, environment);
            AstWriteAdviseNode writeAdviseNode = visitor.visitWriteAdviseNode(ctx);
            if (writeAdviseNode != null) {
                childInfos().add(writeAdviseNode.getRegionInfo());
            }

            return writeAdviseNode;
        }

        @Override
        public AstWriteConfigNode visitWriteConfigNode(WriteConfigNodeContext ctx) {

            AstWriteConfigNodeVisitor visitor = new AstWriteConfigNodeVisitor(factory, environment);
            AstWriteConfigNode writeConfigNode = visitor.visitWriteConfigNode(ctx);
            if (writeConfigNode != null) {
                childInfos().add(writeConfigNode.getRegionInfo());
            }

            return writeConfigNode;
        }

        @Override
        public AstWriteValueNode visitWriteNode(WriteNodeContext ctx) {
            AstWriteValueNodeVisitor visitor = new AstWriteValueNodeVisitor(factory, environment);
            AstWriteValueNode writeNode = visitor.visitWriteNode(ctx);
            if (writeNode != null) {
                childInfos().add(writeNode.getRegionInfo());
            }
            return writeNode;
        }

        @Override
        public AstWriteFlushNode visitWriteFlushNode(WriteFlushNodeContext ctx) {

            AstWriteFlushNodeVisitor visitor = new AstWriteFlushNodeVisitor(factory, environment);
            AstWriteFlushNode writeFlushNode = visitor.visitWriteFlushNode(ctx);
            if (writeFlushNode != null) {
                childInfos().add(writeFlushNode.getRegionInfo());
            }

            return writeFlushNode;
        }

        @Override
        public AstWriteCloseNode visitWriteCloseNode(WriteCloseNodeContext ctx) {

            AstWriteCloseNodeVisitor visitor = new AstWriteCloseNodeVisitor(factory, environment);
            AstWriteCloseNode writeCloseNode = visitor.visitWriteCloseNode(ctx);
            if (writeCloseNode != null) {
                childInfos().add(writeCloseNode.getRegionInfo());
            }

            return writeCloseNode;
        }

        @Override
        public AstCloseNode visitCloseNode(CloseNodeContext ctx) {

            AstCloseNodeVisitor visitor = new AstCloseNodeVisitor(factory, environment);
            AstCloseNode closeNode = visitor.visitCloseNode(ctx);
            if (closeNode != null) {
                childInfos().add(closeNode.getRegionInfo());
            }

            return closeNode;
        }

        @Override
        public AstWriteAbortNode visitWriteAbortNode(WriteAbortNodeContext ctx) {

            AstWriteAbortNodeVisitor visitor = new AstWriteAbortNodeVisitor(factory, environment);
            AstWriteAbortNode abortNode = visitor.visitWriteAbortNode(ctx);
            if (abortNode != null) {
                childInfos().add(abortNode.getRegionInfo());
            }

            return abortNode;
        }

        @Override
        public AstReadAbortNode visitReadAbortNode(ReadAbortNodeContext ctx) {

            AstReadAbortNodeVisitor visitor = new AstReadAbortNodeVisitor(factory, environment);
            AstReadAbortNode abortNode = visitor.visitReadAbortNode(ctx);
            if (abortNode != null) {
                childInfos().add(abortNode.getRegionInfo());
            }

            return abortNode;
        }
    }

    private static class AstCloseNodeVisitor extends AstNodeVisitor<AstCloseNode> {

        public AstCloseNodeVisitor(ExpressionFactory factory, ExpressionContext environment) {
            super(factory, environment);
        }

        @Override
        public AstCloseNode visitCloseNode(CloseNodeContext ctx) {
            node = new AstCloseNode();
            node.setRegionInfo(asSequentialRegion(childInfos, ctx));
            return node;
        }

    }

    private static class AstWriteAbortNodeVisitor extends AstNodeVisitor<AstWriteAbortNode> {

        public AstWriteAbortNodeVisitor(ExpressionFactory factory, ExpressionContext environment) {
            super(factory, environment);
        }

        @Override
        public AstWriteAbortNode visitWriteAbortNode(WriteAbortNodeContext ctx) {
            node = new AstWriteAbortNode();
            node.setRegionInfo(asSequentialRegion(childInfos, ctx));
            return node;
        }

    }

    private static class AstReadAbortNodeVisitor extends AstNodeVisitor<AstReadAbortNode> {

        public AstReadAbortNodeVisitor(ExpressionFactory factory, ExpressionContext environment) {
            super(factory, environment);
        }

        @Override
        public AstReadAbortNode visitReadAbortNode(ReadAbortNodeContext ctx) {
            node = new AstReadAbortNode();
            node.setRegionInfo(asSequentialRegion(childInfos, ctx));
            return node;
        }

    }

    private static class AstDisconnectNodeVisitor extends AstNodeVisitor<AstDisconnectNode> {

        public AstDisconnectNodeVisitor(ExpressionFactory factory, ExpressionContext environment) {
            super(factory, environment);
        }

        @Override
        public AstDisconnectNode visitDisconnectNode(DisconnectNodeContext ctx) {
            node = new AstDisconnectNode();
            node.setRegionInfo(asSequentialRegion(childInfos, ctx));
            return node;
        }

    }

    private static class AstUnbindNodeVisitor extends AstNodeVisitor<AstUnbindNode> {

        public AstUnbindNodeVisitor(ExpressionFactory factory, ExpressionContext environment) {
            super(factory, environment);
        }

        @Override
        public AstUnbindNode visitUnbindNode(UnbindNodeContext ctx) {
            node = new AstUnbindNode();
            node.setRegionInfo(asSequentialRegion(childInfos, ctx));
            return node;
        }

    }

    private static class AstWriteValueNodeVisitor extends AstNodeVisitor<AstWriteValueNode> {

        public AstWriteValueNodeVisitor(ExpressionFactory factory, ExpressionContext environment) {
            super(factory, environment);
        }

        @Override
        public AstWriteValueNode visitWriteNode(WriteNodeContext ctx) {
            node = new AstWriteValueNode();
            super.visitWriteNode(ctx);
            node.setRegionInfo(asSequentialRegion(childInfos, ctx));
            return node;
        }

        @Override
        public AstWriteValueNode visitWriteValue(WriteValueContext ctx) {

            AstValueVisitor<?> visitor = new AstValueVisitor<>(factory, environment, Object.class);
            AstValue<?> value = visitor.visit(ctx);
            node.addValue(value);
            childInfos().add(value.getRegionInfo());
            return node;
        }
    }

    private static class AstChildOpenedNodeVisitor extends AstNodeVisitor<AstChildOpenedNode> {

        public AstChildOpenedNodeVisitor(ExpressionFactory factory, ExpressionContext environment) {
            super(factory, environment);
        }

        @Override
        public AstChildOpenedNode visitChildOpenedNode(ChildOpenedNodeContext ctx) {
            node = new AstChildOpenedNode();
            node.setRegionInfo(asSequentialRegion(childInfos, ctx));
            return node;
        }

    }

    private static class AstChildClosedNodeVisitor extends AstNodeVisitor<AstChildClosedNode> {

        public AstChildClosedNodeVisitor(ExpressionFactory factory, ExpressionContext environment) {
            super(factory, environment);
        }

        @Override
        public AstChildClosedNode visitChildClosedNode(ChildClosedNodeContext ctx) {
            node = new AstChildClosedNode();
            node.setRegionInfo(asSequentialRegion(childInfos, ctx));
            return node;
        }

    }

    private static class AstBoundNodeVisitor extends AstNodeVisitor<AstBoundNode> {

        public AstBoundNodeVisitor(ExpressionFactory factory, ExpressionContext environment) {
            super(factory, environment);
        }

        @Override
        public AstBoundNode visitBoundNode(BoundNodeContext ctx) {
            node = new AstBoundNode();
            node.setRegionInfo(asSequentialRegion(childInfos, ctx));
            return node;
        }

    }

    private static class AstClosedNodeVisitor extends AstNodeVisitor<AstClosedNode> {

        public AstClosedNodeVisitor(ExpressionFactory factory, ExpressionContext environment) {
            super(factory, environment);
        }

        @Override
        public AstClosedNode visitClosedNode(ClosedNodeContext ctx) {
            node = new AstClosedNode();
            node.setRegionInfo(asSequentialRegion(childInfos, ctx));
            return node;
        }

    }


    private static class AstReadAbortedNodeVisitor extends AstNodeVisitor<AstReadAbortedNode> {

        public AstReadAbortedNodeVisitor(ExpressionFactory factory, ExpressionContext environment) {
            super(factory, environment);
        }

        @Override
        public AstReadAbortedNode visitReadAbortedNode(ReadAbortedNodeContext ctx) {
            node = new AstReadAbortedNode();
            node.setRegionInfo(asSequentialRegion(childInfos, ctx));
            return node;
        }

    }

    private static class AstWriteAbortedNodeVisitor extends AstNodeVisitor<AstWriteAbortedNode> {

        public AstWriteAbortedNodeVisitor(ExpressionFactory factory, ExpressionContext environment) {
            super(factory, environment);
        }

        @Override
        public AstWriteAbortedNode visitWriteAbortedNode(WriteAbortedNodeContext ctx) {
            node = new AstWriteAbortedNode();
            node.setRegionInfo(asSequentialRegion(childInfos, ctx));
            return node;
        }

    }

    private static class AstConnectedNodeVisitor extends AstNodeVisitor<AstConnectedNode> {

        public AstConnectedNodeVisitor(ExpressionFactory factory, ExpressionContext environment) {
            super(factory, environment);
        }

        @Override
        public AstConnectedNode visitConnectedNode(ConnectedNodeContext ctx) {
            node = new AstConnectedNode();
            node.setRegionInfo(asSequentialRegion(childInfos, ctx));
            return node;
        }

    }

    private static class AstDisconnectedNodeVisitor extends AstNodeVisitor<AstDisconnectedNode> {

        public AstDisconnectedNodeVisitor(ExpressionFactory factory, ExpressionContext environment) {
            super(factory, environment);
        }

        @Override
        public AstDisconnectedNode visitDisconnectedNode(DisconnectedNodeContext ctx) {
            node = new AstDisconnectedNode();
            node.setRegionInfo(asSequentialRegion(childInfos, ctx));
            return node;
        }

    }

    private static class AstOpenedNodeVisitor extends AstNodeVisitor<AstOpenedNode> {

        public AstOpenedNodeVisitor(ExpressionFactory factory, ExpressionContext environment) {
            super(factory, environment);
        }

        @Override
        public AstOpenedNode visitOpenedNode(OpenedNodeContext ctx) {
            node = new AstOpenedNode();
            node.setRegionInfo(asSequentialRegion(childInfos, ctx));
            return node;
        }

    }

    private static class AstReadValueNodeVisitor extends AstNodeVisitor<AstReadValueNode> {

        public AstReadValueNodeVisitor(ExpressionFactory factory, ExpressionContext environment) {
            super(factory, environment);
        }

        @Override
        public AstReadValueNode visitReadNode(ReadNodeContext ctx) {
            node = new AstReadValueNode();
            super.visitReadNode(ctx);
            node.setRegionInfo(asSequentialRegion(childInfos, ctx));
            return node;
        }

        @Override
        public AstReadValueNode visitMatcher(MatcherContext ctx) {
            AstValueMatcherVisitor visitor = new AstValueMatcherVisitor(factory, environment);
            AstValueMatcher matcher = visitor.visit(ctx);
            node.addMatcher(matcher);
            childInfos().add(matcher.getRegionInfo());
            return node;
        }

    }

    private static class AstUnboundNodeVisitor extends AstNodeVisitor<AstUnboundNode> {

        public AstUnboundNodeVisitor(ExpressionFactory factory, ExpressionContext environment) {
            super(factory, environment);
        }

        @Override
        public AstUnboundNode visitUnboundNode(UnboundNodeContext ctx) {
            node = new AstUnboundNode();
            node.setRegionInfo(asSequentialRegion(childInfos, ctx));
            return node;
        }

    }

    private static class AstReadAwaitNodeVisitor extends AstNodeVisitor<AstReadAwaitNode> {

        public AstReadAwaitNodeVisitor(ExpressionFactory factory, ExpressionContext environment) {
            super(factory, environment);
        }

        @Override
        public AstReadAwaitNode visitReadAwaitNode(ReadAwaitNodeContext ctx) {
            node = new AstReadAwaitNode();
            node.setRegionInfo(asSequentialRegion(childInfos, ctx));
            node.setBarrierName(ctx.Name().getText());
            return node;
        }

    }

    private static class AstReadNotifyNodeVisitor extends AstNodeVisitor<AstReadNotifyNode> {

        public AstReadNotifyNodeVisitor(ExpressionFactory factory, ExpressionContext environment) {
            super(factory, environment);
        }

        @Override
        public AstReadNotifyNode visitReadNotifyNode(ReadNotifyNodeContext ctx) {
            node = new AstReadNotifyNode();
            node.setRegionInfo(asSequentialRegion(childInfos, ctx));
            node.setBarrierName(ctx.Name().getText());
            return node;
        }

    }

    private static class AstWriteAwaitNodeVisitor extends AstNodeVisitor<AstWriteAwaitNode> {

        public AstWriteAwaitNodeVisitor(ExpressionFactory factory, ExpressionContext environment) {
            super(factory, environment);
        }

        @Override
        public AstWriteAwaitNode visitWriteAwaitNode(WriteAwaitNodeContext ctx) {
            node = new AstWriteAwaitNode();
            node.setRegionInfo(asSequentialRegion(childInfos, ctx));
            node.setBarrierName(ctx.Name().getText());
            return node;
        }

    }

    private static class AstWriteNotifyNodeVisitor extends AstNodeVisitor<AstWriteNotifyNode> {

        public AstWriteNotifyNodeVisitor(ExpressionFactory factory, ExpressionContext environment) {
            super(factory, environment);
        }

        @Override
        public AstWriteNotifyNode visitWriteNotifyNode(WriteNotifyNodeContext ctx) {
            node = new AstWriteNotifyNode();
            node.setRegionInfo(asSequentialRegion(childInfos, ctx));
            node.setBarrierName(ctx.Name().getText());
            return node;
        }

    }

    private static class AstValueMatcherVisitor extends AstVisitor<AstValueMatcher> {

        public AstValueMatcherVisitor(ExpressionFactory factory, ExpressionContext environment) {
            super(factory, environment);
        }

        @Override
        public AstValueMatcher visitExactTextMatcher(ExactTextMatcherContext ctx) {

            AstExactTextMatcherVisitor visitor = new AstExactTextMatcherVisitor(factory, environment);
            AstExactTextMatcher matcher = visitor.visit(ctx);
            if (matcher != null) {
                childInfos().add(matcher.getRegionInfo());
            }

            return matcher;
        }

        @Override
        public AstExactBytesMatcher visitExactBytesMatcher(ExactBytesMatcherContext ctx) {

            AstExactBytesMatcherVisitor visitor = new AstExactBytesMatcherVisitor(factory, environment);
            AstExactBytesMatcher matcher = visitor.visit(ctx);
            if (matcher != null) {
                childInfos().add(matcher.getRegionInfo());
            }

            return matcher;
        }

        @Override
        public AstValueMatcher visitNumberMatcher(NumberMatcherContext ctx) {

            AstNumberMatcherVisitor visitor = new AstNumberMatcherVisitor(factory, environment);
            AstNumberMatcher matcher = visitor.visit(ctx);
            if (matcher != null) {
                childInfos().add(matcher.getRegionInfo());
            }

            return matcher;
        }

        @Override
        public AstRegexMatcher visitRegexMatcher(RegexMatcherContext ctx) {

            AstRegexMatcherVisitor visitor = new AstRegexMatcherVisitor(factory, environment);
            AstRegexMatcher matcher = visitor.visit(ctx);
            if (matcher != null) {
                childInfos().add(matcher.getRegionInfo());
            }

            return matcher;
        }

        @Override
        public AstExpressionMatcher visitExpressionMatcher(ExpressionMatcherContext ctx) {

            AstExpressionMatcherVisitor visitor = new AstExpressionMatcherVisitor(factory, environment);
            AstExpressionMatcher matcher = visitor.visit(ctx);
            if (matcher != null) {
                childInfos().add(matcher.getRegionInfo());
            }

            return matcher;
        }

        @Override
        public AstFixedLengthBytesMatcher visitFixedLengthBytesMatcher(FixedLengthBytesMatcherContext ctx) {

            AstFixedLengthBytesMatcherVisitor visitor = new AstFixedLengthBytesMatcherVisitor(factory, environment);
            AstFixedLengthBytesMatcher matcher = visitor.visit(ctx);
            if (matcher != null) {
                childInfos().add(matcher.getRegionInfo());
            }

            return matcher;
        }

        @Override
        public AstVariableLengthBytesMatcher visitVariableLengthBytesMatcher(VariableLengthBytesMatcherContext ctx) {

            AstVariableLengthBytesMatcherVisitor visitor = new AstVariableLengthBytesMatcherVisitor(factory, environment);
            AstVariableLengthBytesMatcher matcher = visitor.visit(ctx);
            if (matcher != null) {
                childInfos().add(matcher.getRegionInfo());
            }

            return matcher;
        }

    }

    private static class AstExactTextMatcherVisitor extends AstVisitor<AstExactTextMatcher> {

        public AstExactTextMatcherVisitor(ExpressionFactory factory, ExpressionContext environment) {
            super(factory, environment);
        }

        @Override
        public AstExactTextMatcher visitExactTextMatcher(ExactTextMatcherContext ctx) {
            String text = ctx.text.getText();
            String textWithoutQuote = text.substring(1, text.length() - 1);
            String escapedText = escapeString(textWithoutQuote);
            AstExactTextMatcher matcher = new AstExactTextMatcher(escapedText);
            matcher.setRegionInfo(asSequentialRegion(childInfos, ctx));
            return matcher;
        }

    }

    private static class AstExactBytesMatcherVisitor extends AstVisitor<AstExactBytesMatcher> {

        public AstExactBytesMatcherVisitor(ExpressionFactory factory, ExpressionContext environment) {
            super(factory, environment);
        }

        @Override
        public AstExactBytesMatcher visitExactBytesMatcher(ExactBytesMatcherContext ctx) {
            if (ctx.bytes != null) {
                byte[] array = parseHexBytes(ctx.bytes.getText());
                AstExactBytesMatcher matcher = new AstExactBytesMatcher(array);
                matcher.setRegionInfo(asSequentialRegion(childInfos, ctx));
                return matcher;
            }

            return null;
        }

    }

    private static class AstNumberMatcherVisitor extends AstVisitor<AstNumberMatcher> {

        public AstNumberMatcherVisitor(ExpressionFactory factory, ExpressionContext environment) {
            super(factory, environment);
        }

        @Override
        public AstNumberMatcher visitNumberMatcher(NumberMatcherContext ctx) {
            if (ctx.longLiteral != null) {
                Long literal = Long.decode(ctx.longLiteral.getText().replaceAll("\\_", ""));
                AstNumberMatcher matcher = new AstNumberMatcher(literal);
                matcher.setRegionInfo(asSequentialRegion(childInfos, ctx));
                return matcher;
            } else if (ctx.intLiteral != null) {
                Integer literal = Integer.decode(ctx.intLiteral.getText().replaceAll("\\_", ""));
                AstNumberMatcher matcher = new AstNumberMatcher(literal);
                matcher.setRegionInfo(asSequentialRegion(childInfos, ctx));
                return matcher;
            } else if (ctx.shortLiteral != null) {
                Short literal = Short.decode(ctx.shortLiteral.getText().replaceAll("\\_", ""));
                AstNumberMatcher matcher = new AstNumberMatcher(literal);
                matcher.setRegionInfo(asSequentialRegion(childInfos, ctx));
                return matcher;
            } else if (ctx.byteLiteral != null) {
                Byte literal = Byte.decode(ctx.byteLiteral.getText().replaceAll("\\_", ""));
                AstNumberMatcher matcher = new AstNumberMatcher(literal);
                matcher.setRegionInfo(asSequentialRegion(childInfos, ctx));
                return matcher;
            }

            return null;
        }
    }

    private static class AstRegexMatcherVisitor extends AstVisitor<AstRegexMatcher> {

        public AstRegexMatcherVisitor(ExpressionFactory factory, ExpressionContext environment) {
            super(factory, environment);
        }

        @Override
        public AstRegexMatcher visitRegexMatcher(RegexMatcherContext ctx) {
            String regex = ctx.regex.getText();
            String pattern = regex.substring(1, regex.length() - 1);
            AstRegexMatcher matcher = new AstRegexMatcher(NamedGroupPattern.compile(pattern), environment);
            matcher.setRegionInfo(asSequentialRegion(childInfos, ctx));
            return matcher;
        }

    }

    private static class AstExpressionMatcherVisitor extends AstVisitor<AstExpressionMatcher> {

        public AstExpressionMatcherVisitor(ExpressionFactory factory, ExpressionContext environment) {
            super(factory, environment);
        }

        @Override
        public AstExpressionMatcher visitExpressionMatcher(ExpressionMatcherContext ctx) {
            ValueExpression expression = factory.createValueExpression(environment, ctx.expression.getText(), Object.class);
            AstExpressionMatcher matcher = new AstExpressionMatcher(expression, environment);
            matcher.setRegionInfo(asSequentialRegion(childInfos, ctx));
            return matcher;
        }

    }

    private static class AstFixedLengthBytesMatcherVisitor extends AstVisitor<AstFixedLengthBytesMatcher> {

        public AstFixedLengthBytesMatcherVisitor(ExpressionFactory factory, ExpressionContext environment) {
            super(factory, environment);
        }

        @Override
        public AstFixedLengthBytesMatcher visitFixedLengthBytesMatcher(FixedLengthBytesMatcherContext ctx) {
            if (ctx.lastIndex != null) {
                String lastIndex = ctx.lastIndex.getText();
                if (ctx.capture != null) {
                    String capture = ctx.capture.getText();
                    String captureName = capture.substring(1, capture.length());
                    AstFixedLengthBytesMatcher matcher =
                            new AstFixedLengthBytesMatcher(parseInt(lastIndex), captureName, environment);
                    matcher.setRegionInfo(asSequentialRegion(childInfos, ctx));
                    return matcher;
                } else {
                    AstFixedLengthBytesMatcher matcher = new AstFixedLengthBytesMatcher(parseInt(lastIndex));
                    matcher.setRegionInfo(asSequentialRegion(childInfos, ctx));
                    return matcher;
                }
            } else if (ctx.byteCapture != null) {
                String byteCapture = ctx.byteCapture.getText();
                AstByteLengthBytesMatcher matcher = new AstByteLengthBytesMatcher(byteCapture.substring(1), environment);
                matcher.setRegionInfo(asSequentialRegion(childInfos, ctx));
                return matcher;
            } else if (ctx.shortCapture != null) {
                String shortCapture = ctx.shortCapture.getText();
                AstShortLengthBytesMatcher matcher = new AstShortLengthBytesMatcher(shortCapture.substring(1), environment);
                matcher.setRegionInfo(asSequentialRegion(childInfos, ctx));
                return matcher;
            } else if (ctx.intCapture != null) {
                String intCapture = ctx.intCapture.getText();
                AstIntLengthBytesMatcher matcher = new AstIntLengthBytesMatcher(intCapture.substring(1), environment);
                matcher.setRegionInfo(asSequentialRegion(childInfos, ctx));
                return matcher;
            } else if (ctx.longCapture != null) {
                String longCapture = ctx.longCapture.getText();
                AstLongLengthBytesMatcher matcher = new AstLongLengthBytesMatcher(longCapture.substring(1), environment);
                matcher.setRegionInfo(asSequentialRegion(childInfos, ctx));
                return matcher;
            }

            return null;
        }

    }

    private static class AstVariableLengthBytesMatcherVisitor extends AstVisitor<AstVariableLengthBytesMatcher> {

        public AstVariableLengthBytesMatcherVisitor(ExpressionFactory factory, ExpressionContext environment) {
            super(factory, environment);
        }

        @Override
        public AstVariableLengthBytesMatcher visitVariableLengthBytesMatcher(VariableLengthBytesMatcherContext ctx) {
            ValueExpression length = factory.createValueExpression(environment, ctx.length.getText(), Integer.class);
            if (ctx.capture != null) {
                String capture = ctx.capture.getText();
                String captureName = capture.substring(1);
                AstVariableLengthBytesMatcher matcher = new AstVariableLengthBytesMatcher(length, captureName, environment);
                matcher.setRegionInfo(asSequentialRegion(childInfos, ctx));
                return matcher;
            } else {
                AstVariableLengthBytesMatcher matcher = new AstVariableLengthBytesMatcher(length, environment);
                matcher.setRegionInfo(asSequentialRegion(childInfos, ctx));
                return matcher;
            }
        }
    }

    private static class AstLocationVisitor extends AstVisitor<AstValue<URI>> {

        protected AstLocationVisitor(ExpressionFactory factory, ExpressionContext environment) {
            super(factory, environment);
        }

        @Override
        public AstValue<URI> visitLiteralText(LiteralTextContext ctx) {
            AstLiteralURIValueVisitor visitor = new AstLiteralURIValueVisitor(factory, environment);
            AstLiteralURIValue value = visitor.visit(ctx);

            if (value != null) {
                childInfos().add(value.getRegionInfo());
            }

            return value;
        }

        @Override
        public AstValue<URI> visitExpressionValue(ExpressionValueContext ctx) {
            AstExpressionValueVisitor<URI> visitor = new AstExpressionValueVisitor<>(factory, environment, URI.class);
            AstExpressionValue<URI> value = visitor.visit(ctx);
            if (value != null) {
                childInfos().add(value.getRegionInfo());
            }

            return value;
        }
    }

    @SuppressWarnings("unchecked")
    private static class AstValueVisitor<T> extends AstVisitor<AstValue<T>> {

        private final Class<T> expectedType;

        public AstValueVisitor(ExpressionFactory factory, ExpressionContext environment, Class<T> expectedType) {
            super(factory, environment);
            this.expectedType = expectedType;
        }

        @Override
        public AstValue<T> visitLiteralBytes(LiteralBytesContext ctx) {

            AstLiteralBytesValueVisitor visitor = new AstLiteralBytesValueVisitor(factory, environment);
            AstLiteralBytesValue value = visitor.visit(ctx);
            if (value != null) {
                childInfos().add(value.getRegionInfo());
            }

            return (AstValue<T>) value;
        }

        @Override
        public AstValue<T> visitLiteralByte(LiteralByteContext ctx) {

            AstLiteralByteValueVisitor visitor = new AstLiteralByteValueVisitor(factory, environment);
            AstLiteralByteValue value = visitor.visit(ctx);
            if (value != null) {
                childInfos().add(value.getRegionInfo());
            }

            return (AstValue<T>) value;
        }

        @Override
        public AstValue<T> visitLiteralShort(LiteralShortContext ctx) {

            AstLiteralShortValueVisitor visitor = new AstLiteralShortValueVisitor(factory, environment);
            AstLiteralShortValue value = visitor.visit(ctx);
            if (value != null) {
                childInfos().add(value.getRegionInfo());
            }

            return (AstValue<T>) value;
        }

        @Override
        public AstValue<T> visitLiteralInteger(LiteralIntegerContext ctx) {

            AstLiteralIntegerValueVisitor visitor = new AstLiteralIntegerValueVisitor(factory, environment);
            AstLiteralIntegerValue literal = visitor.visit(ctx);

            AstValue<?> value = literal;
            if (expectedType == long.class || expectedType == Long.class) {
                value = new AstLiteralLongValue(literal.getValue().longValue());
            }

            if (value != null) {
                childInfos().add(value.getRegionInfo());
            }

            return (AstValue<T>) value;
        }

        @Override
        public AstValue<T> visitLiteralLong(LiteralLongContext ctx) {

            AstLiteralLongValueVisitor visitor = new AstLiteralLongValueVisitor(factory, environment);
            AstLiteralLongValue value = visitor.visit(ctx);
            if (value != null) {
                childInfos().add(value.getRegionInfo());
            }

            return (AstValue<T>) value;
        }

        @Override
        public AstValue<T> visitLiteralText(LiteralTextContext ctx) {

            if (expectedType == URI.class) {
                AstLiteralURIValueVisitor visitor = new AstLiteralURIValueVisitor(factory, environment);
                AstLiteralURIValue value = visitor.visit(ctx);
                if (value != null) {
                    childInfos().add(value.getRegionInfo());
                }

                return (AstValue<T>) value;
            }

            AstLiteralTextValueVisitor visitor = new AstLiteralTextValueVisitor(factory, environment);
            AstLiteralTextValue value = visitor.visit(ctx);
            if (value != null) {
                childInfos().add(value.getRegionInfo());
            }

            return (AstValue<T>) value;
        }

        @Override
        public AstValue<T> visitExpressionValue(ExpressionValueContext ctx) {

            AstExpressionValueVisitor<T> visitor = new AstExpressionValueVisitor<>(factory, environment, expectedType);
            AstExpressionValue<T> value = visitor.visit(ctx);
            if (value != null) {
                childInfos().add(value.getRegionInfo());
            }

            return value;
        }

    }

    private static class AstLiteralTextValueVisitor extends AstVisitor<AstLiteralTextValue> {

        public AstLiteralTextValueVisitor(ExpressionFactory factory, ExpressionContext environment) {
            super(factory, environment);
        }

        @Override
        public AstLiteralTextValue visitLiteralText(LiteralTextContext ctx) {
            String text = ctx.literal.getText();
            String textWithoutQuotes = text.substring(1, text.length() - 1);
            String escapedText = escapeString(textWithoutQuotes);
            AstLiteralTextValue value = new AstLiteralTextValue(escapedText);
            value.setRegionInfo(asSequentialRegion(childInfos, ctx));
            return value;
        }
    }

    private static class AstLiteralURIValueVisitor extends AstVisitor<AstLiteralURIValue> {

        public AstLiteralURIValueVisitor(ExpressionFactory factory, ExpressionContext environment) {
            super(factory, environment);
        }

        @Override
        public AstLiteralURIValue visitLiteralText(LiteralTextContext ctx) {
            String literal = ctx.literal.getText();
            String textWithoutQuotes = literal.substring(1, literal.length() - 1);
            String escapedText = escapeString(textWithoutQuotes);
            AstLiteralURIValue value = new AstLiteralURIValue(URI.create(escapedText));
            value.setRegionInfo(asSequentialRegion(childInfos, ctx));
            return value;
        }

    }

    private static class AstLiteralBytesValueVisitor extends AstVisitor<AstLiteralBytesValue> {

        public AstLiteralBytesValueVisitor(ExpressionFactory factory, ExpressionContext environment) {
            super(factory, environment);
        }

        @Override
        public AstLiteralBytesValue visitLiteralBytes(LiteralBytesContext ctx) {
            String literal = ctx.literal.getText();
            AstLiteralBytesValue value = new AstLiteralBytesValue(parseHexBytes(literal));
            value.setRegionInfo(asSequentialRegion(childInfos, ctx));
            return value;
        }

    }

    private static class AstLiteralByteValueVisitor extends AstVisitor<AstLiteralByteValue> {

        public AstLiteralByteValueVisitor(ExpressionFactory factory, ExpressionContext environment) {
            super(factory, environment);
        }

        @Override
        public AstLiteralByteValue visitLiteralByte(LiteralByteContext ctx) {
            String literal = ctx.literal.getText().replaceAll("_", "");
            AstLiteralByteValue value = new AstLiteralByteValue(Byte.decode(literal));
            value.setRegionInfo(asSequentialRegion(childInfos, ctx));
            return value;
        }

    }

    private static class AstLiteralShortValueVisitor extends AstVisitor<AstLiteralShortValue> {

        public AstLiteralShortValueVisitor(ExpressionFactory factory, ExpressionContext environment) {
            super(factory, environment);
        }

        @Override
        public AstLiteralShortValue visitLiteralShort(LiteralShortContext ctx) {
            String literal = ctx.literal.getText().replaceAll("_", "");
            AstLiteralShortValue value = new AstLiteralShortValue(Short.decode(literal));
            value.setRegionInfo(asSequentialRegion(childInfos, ctx));
            return value;
        }

    }

    private static class AstLiteralIntegerValueVisitor extends AstVisitor<AstLiteralIntegerValue> {

        public AstLiteralIntegerValueVisitor(ExpressionFactory factory, ExpressionContext environment) {
            super(factory, environment);
        }

        @Override
        public AstLiteralIntegerValue visitLiteralInteger(LiteralIntegerContext ctx) {
            String literal = ctx.literal.getText().replaceAll("_", "");
            AstLiteralIntegerValue value = new AstLiteralIntegerValue(Integer.decode(literal));
            value.setRegionInfo(asSequentialRegion(childInfos, ctx));
            return value;
        }

    }

    private static class AstLiteralLongValueVisitor extends AstVisitor<AstLiteralLongValue> {

        public AstLiteralLongValueVisitor(ExpressionFactory factory, ExpressionContext environment) {
            super(factory, environment);
        }

        @Override
        public AstLiteralLongValue visitLiteralLong(LiteralLongContext ctx) {
            String literal = ctx.literal.getText().replaceAll("_", "");
            AstLiteralLongValue value = new AstLiteralLongValue(Long.decode(literal));
            value.setRegionInfo(asSequentialRegion(childInfos, ctx));
            return value;
        }

    }

    private static class AstExpressionValueVisitor<T> extends AstVisitor<AstExpressionValue<T>> {

        private final Class<T> expectedType;

        public AstExpressionValueVisitor(ExpressionFactory factory, ExpressionContext environment, Class<T> expectedType) {
            super(factory, environment);
            this.expectedType = expectedType;
        }

        @Override
        public AstExpressionValue<T> visitExpressionValue(ExpressionValueContext ctx) {
            ValueExpression expression = factory.createValueExpression(environment, ctx.expression.getText(), expectedType);
            AstExpressionValue<T> value = new AstExpressionValue<>(expression, environment);
            value.setRegionInfo(asSequentialRegion(childInfos, ctx));
            return value;
        }

    }

    private static class AstReadAdviseNodeVisitor extends AstNodeVisitor<AstReadAdviseNode> {

        private Iterator<TypeInfo<?>> namedFields;
        private int anonymousFields;

        public AstReadAdviseNodeVisitor(ExpressionFactory factory, ExpressionContext environment) {
            super(factory, environment);
        }

        @Override
        public AstReadAdviseNode visitReadAdviseNode(ReadAdviseNodeContext ctx) {

            String advisoryQName = ctx.QualifiedName().getText();

            node = new AstReadAdviseNode();

            StructuredTypeInfo advisoryType = TYPE_SYSTEM.readAdvisory(advisoryQName);
            namedFields = advisoryType.getNamedFields().iterator();
            anonymousFields = advisoryType.getAnonymousFields();

            node.setType(advisoryType);

            super.visitReadAdviseNode(ctx);

            node.setRegionInfo(asSequentialRegion(childInfos, ctx));

            return node;
        }

        @Override
        public AstReadAdviseNode visitWriteValue(WriteValueContext ctx) {

            AstValueVisitor<?> visitor = new AstValueVisitor<>(factory, environment, Object.class);
            AstValue<?> value = visitor.visit(ctx);

            if (value != null) {

                if (namedFields.hasNext()) {
                    TypeInfo<?> field = namedFields.next();
                    node.setValue(field.getName(), value);
                }
                else if (anonymousFields > 0) {
                    anonymousFields--;
                    node.addValue(value);
                }
                else {
                    throw new IllegalStateException(String.format("Unexpected %s syntax", node.getType()));
                }

                childInfos().add(value.getRegionInfo());
            }

            return node;
        }
    }

    private static class AstWriteAdviseNodeVisitor extends AstNodeVisitor<AstWriteAdviseNode> {

        private Iterator<TypeInfo<?>> namedFields;
        private int anonymousFields;

        public AstWriteAdviseNodeVisitor(ExpressionFactory factory, ExpressionContext environment) {
            super(factory, environment);
        }

        @Override
        public AstWriteAdviseNode visitWriteAdviseNode(WriteAdviseNodeContext ctx) {

            String advisoryQName = ctx.QualifiedName().getText();

            node = new AstWriteAdviseNode();

            StructuredTypeInfo advisoryType = TYPE_SYSTEM.writeAdvisory(advisoryQName);
            namedFields = advisoryType.getNamedFields().iterator();
            anonymousFields = advisoryType.getAnonymousFields();

            node.setType(advisoryType);

            super.visitWriteAdviseNode(ctx);

            node.setRegionInfo(asSequentialRegion(childInfos, ctx));

            return node;
        }

        @Override
        public AstWriteAdviseNode visitWriteValue(WriteValueContext ctx) {

            AstValueVisitor<?> visitor = new AstValueVisitor<>(factory, environment, Object.class);
            AstValue<?> value = visitor.visit(ctx);

            if (value != null) {

                if (namedFields.hasNext()) {
                    TypeInfo<?> field = namedFields.next();
                    node.setValue(field.getName(), value);
                }
                else if (anonymousFields > 0) {
                    anonymousFields--;
                    node.addValue(value);
                }
                else {
                    throw new IllegalStateException(String.format("Unexpected %s syntax", node.getType()));
                }

                childInfos().add(value.getRegionInfo());
            }

            return node;
        }
    }

    private static class AstReadAdvisedNodeVisitor extends AstNodeVisitor<AstReadAdvisedNode> {

        private Iterator<TypeInfo<?>> namedFields;
        private int anonymousFields;

        public AstReadAdvisedNodeVisitor(ExpressionFactory factory, ExpressionContext environment) {
            super(factory, environment);
        }

        @Override
        public AstReadAdvisedNode visitReadAdvisedNode(ReadAdvisedNodeContext ctx) {

            String advistoryQName = ctx.QualifiedName().getText();
            boolean missing = ctx.MissingKeyword() != null;

            node = new AstReadAdvisedNode();
            node.setMissing(missing);

            StructuredTypeInfo advisoryType = TYPE_SYSTEM.writeAdvisory(advistoryQName);
            namedFields = advisoryType.getNamedFields().iterator();
            anonymousFields = advisoryType.getAnonymousFields();

            node.setType(advisoryType);

            super.visitReadAdvisedNode(ctx);

            node.setRegionInfo(asSequentialRegion(childInfos, ctx));

            return node;
        }

        @Override
        public AstReadAdvisedNode visitMatcher(MatcherContext ctx) {

            AstValueMatcherVisitor visitor = new AstValueMatcherVisitor(factory, environment);
            AstValueMatcher matcher = visitor.visit(ctx);

            if (matcher != null) {

                if (namedFields.hasNext()) {
                    TypeInfo<?> field = namedFields.next();
                    node.setMatcher(field.getName(), matcher);
                }
                else if (anonymousFields > 0) {
                    anonymousFields--;
                    node.addMatcher(matcher);
                }
                else {
                    throw new IllegalStateException(String.format("Unexpected %s syntax", node.getType()));
                }

                childInfos().add(matcher.getRegionInfo());
            }

            return node;
        }
    }

    private static class AstWriteAdvisedNodeVisitor extends AstNodeVisitor<AstWriteAdvisedNode> {

        private Iterator<TypeInfo<?>> namedFields;
        private int anonymousFields;

        public AstWriteAdvisedNodeVisitor(ExpressionFactory factory, ExpressionContext environment) {
            super(factory, environment);
        }

        @Override
        public AstWriteAdvisedNode visitWriteAdvisedNode(WriteAdvisedNodeContext ctx) {

            String advisoryQName = ctx.QualifiedName().getText();
            boolean missing = ctx.MissingKeyword() != null;

            node = new AstWriteAdvisedNode();
            node.setMissing(missing);

            StructuredTypeInfo advisoryType = TYPE_SYSTEM.readAdvisory(advisoryQName);
            namedFields = advisoryType.getNamedFields().iterator();
            anonymousFields = advisoryType.getAnonymousFields();

            node.setType(advisoryType);

            super.visitWriteAdvisedNode(ctx);

            node.setRegionInfo(asSequentialRegion(childInfos, ctx));

            return node;
        }

        @Override
        public AstWriteAdvisedNode visitMatcher(MatcherContext ctx) {

            AstValueMatcherVisitor visitor = new AstValueMatcherVisitor(factory, environment);
            AstValueMatcher matcher = visitor.visit(ctx);

            if (matcher != null) {

                if (namedFields.hasNext()) {
                    TypeInfo<?> field = namedFields.next();
                    node.setMatcher(field.getName(), matcher);
                }
                else if (anonymousFields > 0) {
                    anonymousFields--;
                    node.addMatcher(matcher);
                }
                else {
                    throw new IllegalStateException(String.format("Unexpected %s syntax", node.getType()));
                }

                childInfos().add(matcher.getRegionInfo());
            }

            return node;
        }
    }

    private static class AstReadConfigNodeVisitor extends AstNodeVisitor<AstReadConfigNode> {

        private Iterator<TypeInfo<?>> namedFields;
        private int anonymousFields;

        public AstReadConfigNodeVisitor(ExpressionFactory factory, ExpressionContext environment) {
            super(factory, environment);
        }

        @Override
        public AstReadConfigNode visitReadConfigNode(ReadConfigNodeContext ctx) {

            String configQName = ctx.QualifiedName().getText();
            boolean missing = ctx.MissingKeyword() != null;

            node = new AstReadConfigNode();
            node.setMissing(missing);

            StructuredTypeInfo configType = TYPE_SYSTEM.readConfig(configQName);
            namedFields = configType.getNamedFields().iterator();
            anonymousFields = configType.getAnonymousFields();

            node.setType(configType);

            super.visitReadConfigNode(ctx);

            node.setRegionInfo(asSequentialRegion(childInfos, ctx));

            return node;
        }

        @Override
        public AstReadConfigNode visitMatcher(MatcherContext ctx) {

            AstValueMatcherVisitor visitor = new AstValueMatcherVisitor(factory, environment);
            AstValueMatcher matcher = visitor.visit(ctx);

            if (matcher != null) {

                if (namedFields.hasNext()) {
                    TypeInfo<?> field = namedFields.next();
                    node.setMatcher(field.getName(), matcher);
                }
                else if (anonymousFields > 0) {
                    anonymousFields--;
                    node.addMatcher(matcher);
                }
                else {
                    throw new IllegalStateException(String.format("Unexpected %s syntax", node.getType()));
                }

                childInfos().add(matcher.getRegionInfo());
            }

            return node;
        }
    }

    private static class AstWriteConfigNodeVisitor extends AstNodeVisitor<AstWriteConfigNode> {

        private Iterator<TypeInfo<?>> namedFields;
        private int anonymousFields;

        public AstWriteConfigNodeVisitor(ExpressionFactory factory, ExpressionContext environment) {
            super(factory, environment);
        }

        @Override
        public AstWriteConfigNode visitWriteConfigNode(WriteConfigNodeContext ctx) {

            String configQName = ctx.QualifiedName().getText();

            node = new AstWriteConfigNode();

            StructuredTypeInfo configType = TYPE_SYSTEM.writeConfig(configQName);
            namedFields = configType.getNamedFields().iterator();
            anonymousFields = configType.getAnonymousFields();

            node.setType(configType);

            super.visitWriteConfigNode(ctx);

            node.setRegionInfo(asSequentialRegion(childInfos, ctx));

            return node;
        }

        @Override
        public AstWriteConfigNode visitWriteValue(WriteValueContext ctx) {

            AstValueVisitor<?> visitor = new AstValueVisitor<>(factory, environment, Object.class);
            AstValue<?> value = visitor.visit(ctx);

            if (value != null) {

                if (namedFields.hasNext()) {
                    TypeInfo<?> field = namedFields.next();
                    node.setValue(field.getName(), value);
                }
                else if (anonymousFields > 0) {
                    anonymousFields--;
                    node.addValue(value);
                }
                else {
                    throw new IllegalStateException(String.format("Unexpected %s syntax", node.getType()));
                }

                childInfos().add(value.getRegionInfo());
            }

            return node;
        }
    }

    private static class AstWriteFlushNodeVisitor extends AstNodeVisitor<AstWriteFlushNode> {

        public AstWriteFlushNodeVisitor(ExpressionFactory factory, ExpressionContext environment) {
            super(factory, environment);
        }

        @Override
        public AstWriteFlushNode visitWriteFlushNode(WriteFlushNodeContext ctx) {

            node = new AstWriteFlushNode();
            node.setRegionInfo(asSequentialRegion(childInfos, ctx));

            return node;
        }

    }

    private static class AstReadClosedNodeVisitor extends AstNodeVisitor<AstReadClosedNode> {

        public AstReadClosedNodeVisitor(ExpressionFactory factory, ExpressionContext environment) {
            super(factory, environment);
        }

        @Override
        public AstReadClosedNode visitReadClosedNode(ReadClosedNodeContext ctx) {

            node = new AstReadClosedNode();
            node.setRegionInfo(asSequentialRegion(childInfos, ctx));

            return node;
        }

    }

    private static class AstWriteCloseNodeVisitor extends AstNodeVisitor<AstWriteCloseNode> {

        public AstWriteCloseNodeVisitor(ExpressionFactory factory, ExpressionContext environment) {
            super(factory, environment);
        }

        @Override
        public AstWriteCloseNode visitWriteCloseNode(WriteCloseNodeContext ctx) {

            node = new AstWriteCloseNode();
            node.setRegionInfo(asSequentialRegion(childInfos, ctx));

            return node;
        }

    }

    private static String escapeString(String toEscape) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < toEscape.length(); i++) {
            char current = toEscape.charAt(i);
            if (current == '\\') {
                char next = toEscape.charAt(i + 1);
                switch (next) {
                case 'b':
                    sb.append('\b');
                    break;
                case 'f':
                    sb.append('\f');
                    break;
                case 'r':
                    sb.append('\r');
                    break;
                case 'n':
                    sb.append('\n');
                    break;
                case 't':
                    sb.append('\t');
                    break;
                case '"':
                    sb.append('"');
                    break;
                case '\'':
                    sb.append('\'');
                    break;
                case '\\':
                    sb.append('\\');
                    break;
                default:
                    // parser will handle out of bounds errors here so we don't need to check
                    // throw new Exception("escaping unescapable character"); will ruin the method signature
                    // of the visitor
                    assert false;
                }
                i++;
            } else {
                sb.append(current);
            }
        }
        return sb.toString();
    }

    private static RegionInfo asSequentialRegion(List<RegionInfo> childInfos, ParserRuleContext ctx) {
        if (childInfos.isEmpty()) {
            return newSequential(startIndex(ctx.start), stopIndex(ctx.stop) + 1);
        }

        return newSequential(childInfos, startIndex(ctx.start), stopIndex(ctx.stop) + 1);
    }

    private static RegionInfo asParallelRegion(List<RegionInfo> childInfos, ParserRuleContext ctx) {
        if (childInfos.isEmpty()) {
            return newParallel(startIndex(ctx.start), stopIndex(ctx.stop) + 1);
        }

        return newParallel(childInfos, startIndex(ctx.start), stopIndex(ctx.stop) + 1);
    }

    private static int startIndex(Token token) {
        return (token != null && token.getType() != Token.EOF) ? token.getStartIndex() : 0;
    }

    private static int stopIndex(Token token) {
        return (token != null) ? token.getStopIndex() : 0;
    }

    private static final TypeSystem TYPE_SYSTEM = TypeSystem.newInstance();
}
