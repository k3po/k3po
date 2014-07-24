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

package org.kaazing.robot.lang.parser;

import static java.lang.Integer.parseInt;
import static org.kaazing.robot.lang.parser.ParserHelper.parseHexBytes;

import java.net.URI;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

import javax.el.ExpressionFactory;
import javax.el.ValueExpression;

import org.antlr.v4.runtime.RecognitionException;
import org.kaazing.robot.lang.ast.AstAcceptNode;
import org.kaazing.robot.lang.ast.AstAcceptableNode;
import org.kaazing.robot.lang.ast.AstBarrierNode;
import org.kaazing.robot.lang.ast.AstBoundNode;
import org.kaazing.robot.lang.ast.AstChildClosedNode;
import org.kaazing.robot.lang.ast.AstChildOpenedNode;
import org.kaazing.robot.lang.ast.AstCloseHttpRequestNode;
import org.kaazing.robot.lang.ast.AstCloseHttpResponseNode;
import org.kaazing.robot.lang.ast.AstCloseNode;
import org.kaazing.robot.lang.ast.AstClosedNode;
import org.kaazing.robot.lang.ast.AstCommandNode;
import org.kaazing.robot.lang.ast.AstConnectNode;
import org.kaazing.robot.lang.ast.AstConnectedNode;
import org.kaazing.robot.lang.ast.AstDisconnectNode;
import org.kaazing.robot.lang.ast.AstDisconnectedNode;
import org.kaazing.robot.lang.ast.AstEventNode;
import org.kaazing.robot.lang.ast.AstNode;
import org.kaazing.robot.lang.ast.AstOpenedNode;
import org.kaazing.robot.lang.ast.AstOptionNode;
import org.kaazing.robot.lang.ast.AstReadAwaitNode;
import org.kaazing.robot.lang.ast.AstReadHttpHeaderNode;
import org.kaazing.robot.lang.ast.AstReadHttpMethodNode;
import org.kaazing.robot.lang.ast.AstReadHttpParameterNode;
import org.kaazing.robot.lang.ast.AstReadHttpStatusNode;
import org.kaazing.robot.lang.ast.AstReadHttpVersionNode;
import org.kaazing.robot.lang.ast.AstReadNotifyNode;
import org.kaazing.robot.lang.ast.AstReadOptionNode;
import org.kaazing.robot.lang.ast.AstReadValueNode;
import org.kaazing.robot.lang.ast.AstScriptNode;
import org.kaazing.robot.lang.ast.AstStreamNode;
import org.kaazing.robot.lang.ast.AstStreamableNode;
import org.kaazing.robot.lang.ast.AstUnbindNode;
import org.kaazing.robot.lang.ast.AstUnboundNode;
import org.kaazing.robot.lang.ast.AstWriteAwaitNode;
import org.kaazing.robot.lang.ast.AstWriteHttpContentLengthNode;
import org.kaazing.robot.lang.ast.AstWriteHttpHeaderNode;
import org.kaazing.robot.lang.ast.AstWriteHttpMethodNode;
import org.kaazing.robot.lang.ast.AstWriteHttpParameterNode;
import org.kaazing.robot.lang.ast.AstWriteHttpStatusNode;
import org.kaazing.robot.lang.ast.AstWriteHttpVersionNode;
import org.kaazing.robot.lang.ast.AstWriteNotifyNode;
import org.kaazing.robot.lang.ast.AstWriteOptionNode;
import org.kaazing.robot.lang.ast.AstWriteValueNode;
import org.kaazing.robot.lang.ast.matcher.AstByteLengthBytesMatcher;
import org.kaazing.robot.lang.ast.matcher.AstExactBytesMatcher;
import org.kaazing.robot.lang.ast.matcher.AstExactTextMatcher;
import org.kaazing.robot.lang.ast.matcher.AstExpressionMatcher;
import org.kaazing.robot.lang.ast.matcher.AstFixedLengthBytesMatcher;
import org.kaazing.robot.lang.ast.matcher.AstIntLengthBytesMatcher;
import org.kaazing.robot.lang.ast.matcher.AstLongLengthBytesMatcher;
import org.kaazing.robot.lang.ast.matcher.AstRegexMatcher;
import org.kaazing.robot.lang.ast.matcher.AstShortLengthBytesMatcher;
import org.kaazing.robot.lang.ast.matcher.AstValueMatcher;
import org.kaazing.robot.lang.ast.matcher.AstVariableLengthBytesMatcher;
import org.kaazing.robot.lang.ast.value.AstExpressionValue;
import org.kaazing.robot.lang.ast.value.AstLiteralBytesValue;
import org.kaazing.robot.lang.ast.value.AstLiteralTextValue;
import org.kaazing.robot.lang.ast.value.AstValue;
import org.kaazing.robot.lang.el.ExpressionContext;
import org.kaazing.robot.lang.parser.v2.RobotBaseVisitor;
import org.kaazing.robot.lang.parser.v2.RobotParser;
import org.kaazing.robot.lang.parser.v2.RobotParser.AcceptNodeContext;
import org.kaazing.robot.lang.parser.v2.RobotParser.AcceptableNodeContext;
import org.kaazing.robot.lang.parser.v2.RobotParser.BarrierNodeContext;
import org.kaazing.robot.lang.parser.v2.RobotParser.BoundNodeContext;
import org.kaazing.robot.lang.parser.v2.RobotParser.ChildClosedNodeContext;
import org.kaazing.robot.lang.parser.v2.RobotParser.ChildOpenedNodeContext;
import org.kaazing.robot.lang.parser.v2.RobotParser.CloseHttpRequestNodeContext;
import org.kaazing.robot.lang.parser.v2.RobotParser.CloseHttpResponseNodeContext;
import org.kaazing.robot.lang.parser.v2.RobotParser.CloseNodeContext;
import org.kaazing.robot.lang.parser.v2.RobotParser.ClosedNodeContext;
import org.kaazing.robot.lang.parser.v2.RobotParser.CommandNodeContext;
import org.kaazing.robot.lang.parser.v2.RobotParser.ConnectNodeContext;
import org.kaazing.robot.lang.parser.v2.RobotParser.ConnectedNodeContext;
import org.kaazing.robot.lang.parser.v2.RobotParser.DisconnectNodeContext;
import org.kaazing.robot.lang.parser.v2.RobotParser.DisconnectedNodeContext;
import org.kaazing.robot.lang.parser.v2.RobotParser.EventNodeContext;
import org.kaazing.robot.lang.parser.v2.RobotParser.ExactBytesMatcherContext;
import org.kaazing.robot.lang.parser.v2.RobotParser.ExactTextMatcherContext;
import org.kaazing.robot.lang.parser.v2.RobotParser.ExpressionMatcherContext;
import org.kaazing.robot.lang.parser.v2.RobotParser.ExpressionValueContext;
import org.kaazing.robot.lang.parser.v2.RobotParser.FixedLengthBytesMatcherContext;
import org.kaazing.robot.lang.parser.v2.RobotParser.LiteralBytesContext;
import org.kaazing.robot.lang.parser.v2.RobotParser.LiteralTextContext;
import org.kaazing.robot.lang.parser.v2.RobotParser.MatcherContext;
import org.kaazing.robot.lang.parser.v2.RobotParser.OpenedNodeContext;
import org.kaazing.robot.lang.parser.v2.RobotParser.OptionNodeContext;
import org.kaazing.robot.lang.parser.v2.RobotParser.ReadAwaitNodeContext;
import org.kaazing.robot.lang.parser.v2.RobotParser.ReadHttpHeaderNodeContext;
import org.kaazing.robot.lang.parser.v2.RobotParser.ReadHttpMethodNodeContext;
import org.kaazing.robot.lang.parser.v2.RobotParser.ReadHttpParameterNodeContext;
import org.kaazing.robot.lang.parser.v2.RobotParser.ReadHttpStatusNodeContext;
import org.kaazing.robot.lang.parser.v2.RobotParser.ReadHttpVersionNodeContext;
import org.kaazing.robot.lang.parser.v2.RobotParser.ReadNodeContext;
import org.kaazing.robot.lang.parser.v2.RobotParser.ReadNotifyNodeContext;
import org.kaazing.robot.lang.parser.v2.RobotParser.ReadOptionNodeContext;
import org.kaazing.robot.lang.parser.v2.RobotParser.RegexMatcherContext;
import org.kaazing.robot.lang.parser.v2.RobotParser.ScriptNodeContext;
import org.kaazing.robot.lang.parser.v2.RobotParser.ServerStreamableNodeContext;
import org.kaazing.robot.lang.parser.v2.RobotParser.StreamNodeContext;
import org.kaazing.robot.lang.parser.v2.RobotParser.StreamableNodeContext;
import org.kaazing.robot.lang.parser.v2.RobotParser.UnbindNodeContext;
import org.kaazing.robot.lang.parser.v2.RobotParser.UnboundNodeContext;
import org.kaazing.robot.lang.parser.v2.RobotParser.VariableLengthBytesMatcherContext;
import org.kaazing.robot.lang.parser.v2.RobotParser.WriteAwaitNodeContext;
import org.kaazing.robot.lang.parser.v2.RobotParser.WriteHttpContentLengthNodeContext;
import org.kaazing.robot.lang.parser.v2.RobotParser.WriteHttpHeaderNodeContext;
import org.kaazing.robot.lang.parser.v2.RobotParser.WriteHttpMethodNodeContext;
import org.kaazing.robot.lang.parser.v2.RobotParser.WriteHttpParameterNodeContext;
import org.kaazing.robot.lang.parser.v2.RobotParser.WriteHttpStatusNodeContext;
import org.kaazing.robot.lang.parser.v2.RobotParser.WriteHttpVersionNodeContext;
import org.kaazing.robot.lang.parser.v2.RobotParser.WriteNodeContext;
import org.kaazing.robot.lang.parser.v2.RobotParser.WriteNotifyNodeContext;
import org.kaazing.robot.lang.parser.v2.RobotParser.WriteOptionNodeContext;
import org.kaazing.robot.lang.parser.v2.RobotParser.WriteValueContext;
import org.kaazing.robot.lang.regex.NamedGroupPattern;

abstract class ScriptParseStrategy<T> {

    public static final ScriptParseStrategy<AstScriptNode> SCRIPT = new ScriptParseStrategy<AstScriptNode>() {
        @Override
        public AstScriptNode parse(RobotParser parser, ExpressionFactory elFactory, ExpressionContext elContext)
                throws RecognitionException {
            return new AstScriptNodeVisitor(elFactory, elContext).visit(parser.scriptNode());
        }
    };

    public static final ScriptParseStrategy<AstStreamNode> STREAM = new ScriptParseStrategy<AstStreamNode>() {
        @Override
        public AstStreamNode parse(RobotParser parser, ExpressionFactory elFactory, ExpressionContext elContext)
                throws RecognitionException {
            return new AstStreamNodeVisitor(elFactory, elContext).visit(parser.streamNode());
        }
    };

    public static final ScriptParseStrategy<AstStreamableNode> STREAMABLE = new ScriptParseStrategy<AstStreamableNode>() {
        @Override
        public AstStreamableNode parse(RobotParser parser, ExpressionFactory elFactory, ExpressionContext elContext)
                throws RecognitionException {
            return new AstStreamableNodeVisitor(elFactory, elContext).visit(parser.streamableNode());
        }
    };

    public static final ScriptParseStrategy<AstEventNode> EVENT = new ScriptParseStrategy<AstEventNode>() {
        @Override
        public AstEventNode parse(RobotParser parser, ExpressionFactory elFactory, ExpressionContext elContext)
                throws RecognitionException {
            return new AstEventNodeVisitor(elFactory, elContext).visit(parser.eventNode());
        }
    };

    public static final ScriptParseStrategy<AstCommandNode> COMMAND = new ScriptParseStrategy<AstCommandNode>() {
        @Override
        public AstCommandNode parse(RobotParser parser, ExpressionFactory elFactory, ExpressionContext elContext)
                throws RecognitionException {
            return new AstCommandNodeVisitor(elFactory, elContext).visit(parser.commandNode());
        }
    };

    public static final ScriptParseStrategy<AstBarrierNode> BARRIER = new ScriptParseStrategy<AstBarrierNode>() {
        @Override
        public AstBarrierNode parse(RobotParser parser, ExpressionFactory elFactory, ExpressionContext elContext)
                throws RecognitionException {
            return new AstBarrierNodeVisitor(elFactory, elContext).visit(parser.barrierNode());
        }
    };

    public static final ScriptParseStrategy<AstStreamableNode> SERVER_STREAMABLE = new ScriptParseStrategy<AstStreamableNode>() {
        @Override
        public AstStreamableNode parse(RobotParser parser, ExpressionFactory elFactory, ExpressionContext elContext)
                throws RecognitionException {
            return new AstStreamableNodeVisitor(elFactory, elContext).visit(parser.serverStreamableNode());
        }
    };

    public static final ScriptParseStrategy<AstEventNode> SERVER_EVENT = new ScriptParseStrategy<AstEventNode>() {
        @Override
        public AstEventNode parse(RobotParser parser, ExpressionFactory elFactory, ExpressionContext elContext)
                throws RecognitionException {
            return new AstEventNodeVisitor(elFactory, elContext).visit(parser.serverEventNode());
        }
    };

    public static final ScriptParseStrategy<AstCommandNode> SERVER_COMMAND = new ScriptParseStrategy<AstCommandNode>() {
        @Override
        public AstCommandNode parse(RobotParser parser, ExpressionFactory elFactory, ExpressionContext elContext)
                throws RecognitionException {
            return new AstCommandNodeVisitor(elFactory, elContext).visit(parser.serverCommandNode());
        }
    };

    public static final ScriptParseStrategy<AstAcceptNode> ACCEPT = new ScriptParseStrategy<AstAcceptNode>() {
        @Override
        public AstAcceptNode parse(RobotParser parser, ExpressionFactory elFactory, ExpressionContext elContext)
                throws RecognitionException {
            return new AstAcceptNodeVisitor(elFactory, elContext).visit(parser.acceptNode());
        }
    };

    public static final ScriptParseStrategy<AstAcceptableNode> ACCEPTABLE = new ScriptParseStrategy<AstAcceptableNode>() {
        @Override
        public AstAcceptableNode parse(RobotParser parser, ExpressionFactory elFactory, ExpressionContext elContext)
                throws RecognitionException {
            return new AstAcceptedNodeVisitor(elFactory, elContext).visit(parser.acceptableNode());
        }
    };

    public static final ScriptParseStrategy<AstConnectNode> CONNECT = new ScriptParseStrategy<AstConnectNode>() {
        @Override
        public AstConnectNode parse(RobotParser parser, ExpressionFactory elFactory, ExpressionContext elContext)
                throws RecognitionException {
            return new AstConnectNodeVisitor(elFactory, elContext).visit(parser.connectNode());
        }
    };

    public static final ScriptParseStrategy<AstCloseNode> CLOSE = new ScriptParseStrategy<AstCloseNode>() {
        @Override
        public AstCloseNode parse(RobotParser parser, ExpressionFactory elFactory, ExpressionContext elContext)
                throws RecognitionException {
            return new AstCloseNodeVisitor(elFactory, elContext).visit(parser.closeNode());
        }
    };

    public static final ScriptParseStrategy<AstDisconnectNode> DISCONNECT = new ScriptParseStrategy<AstDisconnectNode>() {
        @Override
        public AstDisconnectNode parse(RobotParser parser, ExpressionFactory elFactory, ExpressionContext elContext)
                throws RecognitionException {
            return new AstDisconnectNodeVisitor(elFactory, elContext).visit(parser.disconnectNode());
        }
    };

    public static final ScriptParseStrategy<AstUnbindNode> UNBIND = new ScriptParseStrategy<AstUnbindNode>() {
        @Override
        public AstUnbindNode parse(RobotParser parser, ExpressionFactory elFactory, ExpressionContext elContext)
                throws RecognitionException {
            return new AstUnbindNodeVisitor(elFactory, elContext).visit(parser.unbindNode());
        }
    };

    public static final ScriptParseStrategy<AstWriteValueNode> WRITE = new ScriptParseStrategy<AstWriteValueNode>() {
        @Override
        public AstWriteValueNode parse(RobotParser parser, ExpressionFactory elFactory, ExpressionContext elContext)
                throws RecognitionException {
            return new AstWriteValueNodeVisitor(elFactory, elContext).visit(parser.writeNode());
        }
    };

    public static final ScriptParseStrategy<AstChildOpenedNode> CHILD_OPENED = new ScriptParseStrategy<AstChildOpenedNode>() {
        @Override
        public AstChildOpenedNode parse(RobotParser parser, ExpressionFactory elFactory, ExpressionContext elContext)
                throws RecognitionException {
            return new AstChildOpenedNodeVisitor(elFactory, elContext).visit(parser.childOpenedNode());
        }
    };

    public static final ScriptParseStrategy<AstChildClosedNode> CHILD_CLOSED = new ScriptParseStrategy<AstChildClosedNode>() {
        @Override
        public AstChildClosedNode parse(RobotParser parser, ExpressionFactory elFactory, ExpressionContext elContext)
                throws RecognitionException {
            return new AstChildClosedNodeVisitor(elFactory, elContext).visit(parser.childClosedNode());
        }
    };

    public static final ScriptParseStrategy<AstBoundNode> BOUND = new ScriptParseStrategy<AstBoundNode>() {
        @Override
        public AstBoundNode parse(RobotParser parser, ExpressionFactory elFactory, ExpressionContext elContext)
                throws RecognitionException {
            return new AstBoundNodeVisitor(elFactory, elContext).visit(parser.boundNode());
        }
    };

    public static final ScriptParseStrategy<AstClosedNode> CLOSED = new ScriptParseStrategy<AstClosedNode>() {
        @Override
        public AstClosedNode parse(RobotParser parser, ExpressionFactory elFactory, ExpressionContext elContext)
                throws RecognitionException {
            return new AstClosedNodeVisitor(elFactory, elContext).visit(parser.closedNode());
        }
    };

    public static final ScriptParseStrategy<AstConnectedNode> CONNECTED = new ScriptParseStrategy<AstConnectedNode>() {
        @Override
        public AstConnectedNode parse(RobotParser parser, ExpressionFactory elFactory, ExpressionContext elContext)
                throws RecognitionException {
            return new AstConnectedNodeVisitor(elFactory, elContext).visit(parser.connectedNode());
        }
    };

    public static final ScriptParseStrategy<AstDisconnectedNode> DISCONNECTED = new ScriptParseStrategy<AstDisconnectedNode>() {
        @Override
        public AstDisconnectedNode parse(RobotParser parser, ExpressionFactory elFactory, ExpressionContext elContext)
                throws RecognitionException {
            return new AstDisconnectedNodeVisitor(elFactory, elContext).visit(parser.disconnectedNode());
        }
    };

    public static final ScriptParseStrategy<AstOpenedNode> OPENED = new ScriptParseStrategy<AstOpenedNode>() {
        @Override
        public AstOpenedNode parse(RobotParser parser, ExpressionFactory elFactory, ExpressionContext elContext)
                throws RecognitionException {
            return new AstOpenedNodeVisitor(elFactory, elContext).visit(parser.openedNode());
        }
    };

    public static final ScriptParseStrategy<AstReadValueNode> READ = new ScriptParseStrategy<AstReadValueNode>() {
        @Override
        public AstReadValueNode parse(RobotParser parser, ExpressionFactory elFactory, ExpressionContext elContext)
                throws RecognitionException {
            return new AstReadValueNodeVisitor(elFactory, elContext).visit(parser.readNode());
        }
    };

    public static final ScriptParseStrategy<AstReadHttpHeaderNode> READ_HTTP_HEADER = new ScriptParseStrategy<AstReadHttpHeaderNode>() {
        @Override
        public AstReadHttpHeaderNode parse(RobotParser parser, ExpressionFactory elFactory, ExpressionContext elContext)
                throws RecognitionException {
            return new AstReadHttpHeaderNodeVisitor(elFactory, elContext).visit(parser.readHttpHeaderNode());
        }
    };

    public static final ScriptParseStrategy<AstWriteHttpHeaderNode> WRITE_HTTP_HEADER = new ScriptParseStrategy<AstWriteHttpHeaderNode>() {
        @Override
        public AstWriteHttpHeaderNode parse(RobotParser parser, ExpressionFactory elFactory, ExpressionContext elContext)
                throws RecognitionException {
            return new AstWriteHttpHeaderNodeVisitor(elFactory, elContext).visit(parser.writeHttpHeaderNode());
        }
    };

    public static final ScriptParseStrategy<AstWriteHttpContentLengthNode> WRITE_HTTP_CONTENT_LENGTH = new ScriptParseStrategy<AstWriteHttpContentLengthNode>() {
        @Override
        public AstWriteHttpContentLengthNode parse(RobotParser parser,
                                                   ExpressionFactory elFactory,
                                                   ExpressionContext elContext) throws RecognitionException {
            return new AstWriteHttpContentLengthNodeVisitor(elFactory, elContext).visit(parser
                    .writeHttpContentLengthNode());
        }
    };

    public static final ScriptParseStrategy<AstReadHttpMethodNode> READ_HTTP_METHOD = new ScriptParseStrategy<AstReadHttpMethodNode>() {
        @Override
        public AstReadHttpMethodNode parse(RobotParser parser, ExpressionFactory elFactory, ExpressionContext elContext)
                throws RecognitionException {
            return new AstReadHttpMethodNodeVisitor(elFactory, elContext).visit(parser.readHttpMethodNode());
        }
    };

    public static final ScriptParseStrategy<AstWriteHttpMethodNode> WRITE_HTTP_METHOD = new ScriptParseStrategy<AstWriteHttpMethodNode>() {
        @Override
        public AstWriteHttpMethodNode parse(RobotParser parser, ExpressionFactory elFactory, ExpressionContext elContext)
                throws RecognitionException {
            return new AstWriteHttpMethodNodeVisitor(elFactory, elContext).visit(parser.writeHttpMethodNode());
        }
    };

    public static final ScriptParseStrategy<AstReadHttpParameterNode> READ_HTTP_PARAMETER = new ScriptParseStrategy<AstReadHttpParameterNode>() {
        @Override
        public AstReadHttpParameterNode parse(RobotParser parser,
                                              ExpressionFactory elFactory,
                                              ExpressionContext elContext) throws RecognitionException {
            return new AstReadHttpParameterNodeVisitor(elFactory, elContext).visit(parser.readHttpParameterNode());
        }
    };

    public static final ScriptParseStrategy<AstWriteHttpParameterNode> WRITE_HTTP_PARAMETER = new ScriptParseStrategy<AstWriteHttpParameterNode>() {
        @Override
        public AstWriteHttpParameterNode parse(RobotParser parser,
                                               ExpressionFactory elFactory,
                                               ExpressionContext elContext) throws RecognitionException {
            return new AstWriteHttpParameterNodeVisitor(elFactory, elContext).visit(parser.writeHttpParameterNode());
        }
    };

    public static final ScriptParseStrategy<AstReadHttpVersionNode> READ_HTTP_VERSION = new ScriptParseStrategy<AstReadHttpVersionNode>() {
        @Override
        public AstReadHttpVersionNode parse(RobotParser parser, ExpressionFactory elFactory, ExpressionContext elContext)
                throws RecognitionException {
            return new AstReadHttpVersionNodeVisitor(elFactory, elContext).visit(parser.readHttpVersionNode());
        }
    };

    public static final ScriptParseStrategy<AstWriteHttpVersionNode> WRITE_HTTP_VERSION = new ScriptParseStrategy<AstWriteHttpVersionNode>() {
        @Override
        public AstWriteHttpVersionNode parse(RobotParser parser,
                                             ExpressionFactory elFactory,
                                             ExpressionContext elContext) throws RecognitionException {
            return new AstWriteHttpVersionNodeVisitor(elFactory, elContext).visit(parser.writeHttpVersionNode());
        }
    };

    public static final ScriptParseStrategy<AstReadHttpStatusNode> READ_HTTP_STATUS = new ScriptParseStrategy<AstReadHttpStatusNode>() {
        @Override
        public AstReadHttpStatusNode parse(RobotParser parser, ExpressionFactory elFactory, ExpressionContext elContext)
                throws RecognitionException {
            return new AstReadHttpStatusNodeVisitor(elFactory, elContext).visit(parser.readHttpStatusNode());
        }
    };

    public static final ScriptParseStrategy<AstWriteHttpStatusNode> WRITE_HTTP_STATUS = new ScriptParseStrategy<AstWriteHttpStatusNode>() {
        @Override
        public AstWriteHttpStatusNode parse(RobotParser parser, ExpressionFactory elFactory, ExpressionContext elContext)
                throws RecognitionException {
            return new AstWriteHttpStatusNodeVisitor(elFactory, elContext).visit(parser.writeHttpStatusNode());
        }
    };

    public static final ScriptParseStrategy<AstCloseHttpRequestNode> CLOSE_HTTP_REQUEST = new ScriptParseStrategy<AstCloseHttpRequestNode>() {
        @Override
        public AstCloseHttpRequestNode parse(RobotParser parser,
                                             ExpressionFactory elFactory,
                                             ExpressionContext elContext) throws RecognitionException {
            return new AstCloseHttpRequestNodeVisitor(elFactory, elContext).visit(parser.closeHttpRequestNode());
        }
    };

    public static final ScriptParseStrategy<AstCloseHttpResponseNode> CLOSE_HTTP_RESPONSE = new ScriptParseStrategy<AstCloseHttpResponseNode>() {
        @Override
        public AstCloseHttpResponseNode parse(RobotParser parser,
                                              ExpressionFactory elFactory,
                                              ExpressionContext elContext) throws RecognitionException {
            return new AstCloseHttpResponseNodeVisitor(elFactory, elContext).visit(parser.closeHttpResponseNode());
        }
    };

    public static final ScriptParseStrategy<AstUnboundNode> UNBOUND = new ScriptParseStrategy<AstUnboundNode>() {
        @Override
        public AstUnboundNode parse(RobotParser parser, ExpressionFactory elFactory, ExpressionContext elContext)
                throws RecognitionException {
            return new AstUnboundNodeVisitor(elFactory, elContext).visit(parser.unboundNode());
        }
    };

    public static final ScriptParseStrategy<AstReadAwaitNode> READ_AWAIT = new ScriptParseStrategy<AstReadAwaitNode>() {
        @Override
        public AstReadAwaitNode parse(RobotParser parser, ExpressionFactory elFactory, ExpressionContext elContext)
                throws RecognitionException {
            return new AstReadAwaitNodeVisitor(elFactory, elContext).visit(parser.readAwaitNode());
        }
    };

    public static final ScriptParseStrategy<AstReadNotifyNode> READ_NOTIFY = new ScriptParseStrategy<AstReadNotifyNode>() {
        @Override
        public AstReadNotifyNode parse(RobotParser parser, ExpressionFactory elFactory, ExpressionContext elContext)
                throws RecognitionException {
            return new AstReadNotifyNodeVisitor(elFactory, elContext).visit(parser.readNotifyNode());
        }
    };

    public static final ScriptParseStrategy<AstWriteAwaitNode> WRITE_AWAIT = new ScriptParseStrategy<AstWriteAwaitNode>() {
        @Override
        public AstWriteAwaitNode parse(RobotParser parser, ExpressionFactory elFactory, ExpressionContext elContext)
                throws RecognitionException {
            return new AstWriteAwaitNodeVisitor(elFactory, elContext).visit(parser.writeAwaitNode());
        }
    };

    public static final ScriptParseStrategy<AstWriteNotifyNode> WRITE_NOTIFY = new ScriptParseStrategy<AstWriteNotifyNode>() {
        @Override
        public AstWriteNotifyNode parse(RobotParser parser, ExpressionFactory elFactory, ExpressionContext elContext)
                throws RecognitionException {
            return new AstWriteNotifyNodeVisitor(elFactory, elContext).visit(parser.writeNotifyNode());
        }
    };

    public static final ScriptParseStrategy<AstValueMatcher> MATCHER = new ScriptParseStrategy<AstValueMatcher>() {
        @Override
        public AstValueMatcher parse(RobotParser parser, ExpressionFactory elFactory, ExpressionContext elContext)
                throws RecognitionException {
            return new AstValueMatcherVisitor(elFactory, elContext).visit(parser.matcher());
        }
    };

    public static final ScriptParseStrategy<AstExactTextMatcher> EXACT_TEXT_MATCHER = new ScriptParseStrategy<AstExactTextMatcher>() {
        @Override
        public AstExactTextMatcher parse(RobotParser parser, ExpressionFactory elFactory, ExpressionContext elContext)
                throws RecognitionException {
            return new AstExactTextMatcherVisitor(elFactory, elContext).visit(parser.exactTextMatcher());
        }
    };

    public static final ScriptParseStrategy<AstExactBytesMatcher> EXACT_BYTES_MATCHER = new ScriptParseStrategy<AstExactBytesMatcher>() {
        @Override
        public AstExactBytesMatcher parse(RobotParser parser, ExpressionFactory elFactory, ExpressionContext elContext)
                throws RecognitionException {
            return new AstExactBytesMatcherVisitor(elFactory, elContext).visit(parser.exactBytesMatcher());
        }
    };

    public static final ScriptParseStrategy<AstRegexMatcher> REGEX_MATCHER = new ScriptParseStrategy<AstRegexMatcher>() {
        @Override
        public AstRegexMatcher parse(RobotParser parser, ExpressionFactory elFactory, ExpressionContext elContext)
                throws RecognitionException {
            return new AstRegexMatcherVisitor(elFactory, elContext).visit(parser.regexMatcher());
        }
    };

    public static final ScriptParseStrategy<AstExpressionMatcher> EXPRESSION_MATCHER = new ScriptParseStrategy<AstExpressionMatcher>() {
        @Override
        public AstExpressionMatcher parse(RobotParser parser, ExpressionFactory elFactory, ExpressionContext elContext)
                throws RecognitionException {
            return new AstExpressionMatcherVisitor(elFactory, elContext).visit(parser.expressionMatcher());
        }
    };

    public static final ScriptParseStrategy<AstFixedLengthBytesMatcher> FIXED_LENGTH_BYTES_MATCHER = new ScriptParseStrategy<AstFixedLengthBytesMatcher>() {
        @Override
        public AstFixedLengthBytesMatcher parse(RobotParser parser,
                                                ExpressionFactory elFactory,
                                                ExpressionContext elContext) throws RecognitionException {
            return new AstFixedLengthBytesMatcherVisitor(elFactory, elContext).visit(parser.fixedLengthBytesMatcher());
        }
    };

    public static final ScriptParseStrategy<AstVariableLengthBytesMatcher> VARIABLE_LENGTH_BYTES_MATCHER = new ScriptParseStrategy<AstVariableLengthBytesMatcher>() {
        @Override
        public AstVariableLengthBytesMatcher parse(RobotParser parser,
                                                   ExpressionFactory elFactory,
                                                   ExpressionContext elContext) throws RecognitionException {
            return new AstVariableLengthBytesMatcherVisitor(elFactory, elContext).visit(parser
                    .variableLengthBytesMatcher());
        }
    };

    public static final ScriptParseStrategy<AstValue> VALUE = new ScriptParseStrategy<AstValue>() {
        @Override
        public AstValue parse(RobotParser parser, ExpressionFactory elFactory, ExpressionContext elContext)
                throws RecognitionException {
            return new AstValueVisitor(elFactory, elContext).visit(parser.writeValue());
        }
    };

    public static final ScriptParseStrategy<AstLiteralTextValue> LITERAL_TEXT_VALUE = new ScriptParseStrategy<AstLiteralTextValue>() {
        @Override
        public AstLiteralTextValue parse(RobotParser parser, ExpressionFactory elFactory, ExpressionContext elContext)
                throws RecognitionException {
            return new AstLiteralTextValueVisitor(elFactory, elContext).visit(parser.literalText());
        }
    };

    public static final ScriptParseStrategy<AstLiteralBytesValue> LITERAL_BYTES_VALUE = new ScriptParseStrategy<AstLiteralBytesValue>() {
        @Override
        public AstLiteralBytesValue parse(RobotParser parser, ExpressionFactory elFactory, ExpressionContext elContext)
                throws RecognitionException {
            return new AstLiteralBytesValueVisitor(elFactory, elContext).visit(parser.literalBytes());
        }
    };

    public static final ScriptParseStrategy<AstExpressionValue> EXPRESSION_VALUE = new ScriptParseStrategy<AstExpressionValue>() {
        @Override
        public AstExpressionValue parse(RobotParser parser, ExpressionFactory elFactory, ExpressionContext elContext)
                throws RecognitionException {
            return new AstExpressionValueVisitor(elFactory, elContext).visit(parser.expressionValue());
        }
    };

    public static final ScriptParseStrategy<AstReadOptionNode> READ_OPTION = new ScriptParseStrategy<AstReadOptionNode>() {
        @Override
        public AstReadOptionNode parse(RobotParser parser, ExpressionFactory elFactory, ExpressionContext elContext)
                throws RecognitionException {
            return (AstReadOptionNode) new AstOptionNodeVisitor(elFactory, elContext).visit(parser.readOptionNode());
        }
    };

    public static final ScriptParseStrategy<AstWriteOptionNode> WRITE_OPTION = new ScriptParseStrategy<AstWriteOptionNode>() {
        @Override
        public AstWriteOptionNode parse(RobotParser parser, ExpressionFactory elFactory, ExpressionContext elContext)
                throws RecognitionException {
            return (AstWriteOptionNode) new AstOptionNodeVisitor(elFactory, elContext).visit(parser.writeOptionNode());
        }
    };

    public abstract T parse(RobotParser parser, ExpressionFactory elFactory, ExpressionContext elContext)
            throws RecognitionException;

    private static class AstVisitor<T> extends RobotBaseVisitor<T> {
        protected final ExpressionFactory elFactory;
        protected final ExpressionContext elContext;

        protected AstVisitor(ExpressionFactory elFactory, ExpressionContext elContext) {
            this.elFactory = elFactory;
            this.elContext = elContext;
        }
    }

    private static class AstNodeVisitor<T extends AstNode> extends AstVisitor<T> {
        protected T node;

        public AstNodeVisitor(ExpressionFactory elFactory, ExpressionContext elContext) {
            super(elFactory, elContext);
        }

        @Override
        protected T defaultResult() {
            return node;
        }
    }

    private static class AstScriptNodeVisitor extends AstNodeVisitor<AstScriptNode> {

        public AstScriptNodeVisitor(ExpressionFactory elFactory, ExpressionContext elContext) {
            super(elFactory, elContext);
        }

        @Override
        public AstScriptNode visitScriptNode(ScriptNodeContext ctx) {
            node = new AstScriptNode();
            return super.visitScriptNode(ctx);
        }

        @Override
        public AstScriptNode visitStreamNode(StreamNodeContext ctx) {
            AstStreamNodeVisitor visitor = new AstStreamNodeVisitor(elFactory, elContext);
            AstStreamNode streamNode = visitor.visitStreamNode(ctx);
            node.getStreams().add(streamNode);
            return node;
        }

    }

    private static class AstStreamNodeVisitor extends AstNodeVisitor<AstStreamNode> {

        public AstStreamNodeVisitor(ExpressionFactory elFactory, ExpressionContext elContext) {
            super(elFactory, elContext);
        }

        @Override
        public AstAcceptNode visitAcceptNode(AcceptNodeContext ctx) {
            return new AstAcceptNodeVisitor(elFactory, elContext).visitAcceptNode(ctx);
        }

        @Override
        public AstAcceptableNode visitAcceptableNode(AcceptableNodeContext ctx) {
            return new AstAcceptedNodeVisitor(elFactory, elContext).visitAcceptableNode(ctx);
        }

        @Override
        public AstConnectNode visitConnectNode(ConnectNodeContext ctx) {
            return new AstConnectNodeVisitor(elFactory, elContext).visitConnectNode(ctx);
        }

    }

    private static class AstAcceptNodeVisitor extends AstNodeVisitor<AstAcceptNode> {

        public AstAcceptNodeVisitor(ExpressionFactory elFactory, ExpressionContext elContext) {
            super(elFactory, elContext);
        }

        @Override
        public AstAcceptNode visitAcceptNode(AcceptNodeContext ctx) {
            node = new AstAcceptNode();
            node.setLocationInfo(ctx.k.getLine(), ctx.k.getCharPositionInLine());
            node.setLocation(URI.create(ctx.acceptURI.getText()));
            if (ctx.text != null) {
                node.setAcceptName(ctx.text.getText());
            }
            return super.visitAcceptNode(ctx);
        }

        @Override
        public AstAcceptNode visitServerStreamableNode(ServerStreamableNodeContext ctx) {
            AstStreamableNodeVisitor visitor = new AstStreamableNodeVisitor(elFactory, elContext);
            AstStreamableNode streamable = visitor.visitServerStreamableNode(ctx);
            node.getStreamables().add(streamable);
            return node;
        }

    }

    private static class AstAcceptedNodeVisitor extends AstNodeVisitor<AstAcceptableNode> {

        public AstAcceptedNodeVisitor(ExpressionFactory elFactory, ExpressionContext elContext) {
            super(elFactory, elContext);
        }

        @Override
        public AstAcceptableNode visitAcceptableNode(AcceptableNodeContext ctx) {
            node = new AstAcceptableNode();
            node.setLocationInfo(ctx.k.getLine(), ctx.k.getCharPositionInLine());
            if (ctx.text != null) {
                node.setAcceptName(ctx.text.getText());
            }
            return super.visitAcceptableNode(ctx);
        }

        @Override
        public AstAcceptableNode visitStreamableNode(StreamableNodeContext ctx) {
            AstStreamableNodeVisitor visitor = new AstStreamableNodeVisitor(elFactory, elContext);
            AstStreamableNode streamable = visitor.visitStreamableNode(ctx);
            node.getStreamables().add(streamable);
            return node;
        }

    }

    private static class AstConnectNodeVisitor extends AstNodeVisitor<AstConnectNode> {

        public AstConnectNodeVisitor(ExpressionFactory elFactory, ExpressionContext elContext) {
            super(elFactory, elContext);
            // TODO Auto-generated constructor stub
        }

        @Override
        public AstConnectNode visitConnectNode(ConnectNodeContext ctx) {
            node = new AstConnectNode();
            node.setLocationInfo(ctx.k.getLine(), ctx.k.getCharPositionInLine());
            node.setLocation(URI.create(ctx.connectURI.getText()));
            return super.visitConnectNode(ctx);
        }

        @Override
        public AstConnectNode visitStreamableNode(StreamableNodeContext ctx) {
            AstStreamableNodeVisitor visitor = new AstStreamableNodeVisitor(elFactory, elContext);
            AstStreamableNode streamable = visitor.visitStreamableNode(ctx);
            node.getStreamables().add(streamable);
            return node;
        }

    }

    private static class AstStreamableNodeVisitor extends AstNodeVisitor<AstStreamableNode> {

        public AstStreamableNodeVisitor(ExpressionFactory elFactory, ExpressionContext elContext) {
            super(elFactory, elContext);
        }

        @Override
        public AstBarrierNode visitBarrierNode(BarrierNodeContext ctx) {
            return new AstBarrierNodeVisitor(elFactory, elContext).visitBarrierNode(ctx);
        }

        @Override
        public AstEventNode visitEventNode(EventNodeContext ctx) {
            return new AstEventNodeVisitor(elFactory, elContext).visitEventNode(ctx);
        }

        @Override
        public AstCommandNode visitCommandNode(CommandNodeContext ctx) {
            return new AstCommandNodeVisitor(elFactory, elContext).visitCommandNode(ctx);
        }

        @Override
        public AstOptionNode visitOptionNode(OptionNodeContext ctx) {
            return new AstOptionNodeVisitor(elFactory, elContext).visitOptionNode(ctx);

        }
    }

    // Not needed as of now as very similar to StreamableNodeVisitor except for unsupported features
    // private static class AstServerStreamableNodeVisitor extends AstNodeVisitor<AstStreamableNode> {
    //
    // public AstServerStreamableNodeVisitor(ExpressionFactory elFactory,
    // ExpressionContext elContext) {
    // super(elFactory, elContext);
    // }
    //
    // }

    private static class AstOptionNodeVisitor extends AstNodeVisitor<AstOptionNode> {

        public AstOptionNodeVisitor(ExpressionFactory elFactory, ExpressionContext elContext) {
            super(elFactory, elContext);
        }

        @Override
        public AstOptionNode visitReadOptionNode(ReadOptionNodeContext ctx) {
            node = new AstReadOptionNode();
            node.setLocationInfo(ctx.k.getLine(), ctx.k.getCharPositionInLine());
            node.setOptionName(ctx.name.getText());
            AstValue value = new AstValueVisitor(elFactory, elContext).visit(ctx);
            node.setOptionValue(value);
            return node;
        }

        @Override
        public AstOptionNode visitWriteOptionNode(WriteOptionNodeContext ctx) {
            node = new AstWriteOptionNode();
            node.setLocationInfo(ctx.k.getLine(), ctx.k.getCharPositionInLine());
            node.setOptionName(ctx.name.getText());
            AstValue value = new AstValueVisitor(elFactory, elContext).visit(ctx);
            node.setOptionValue(value);
            return node;
        }
    }

    private static class AstBarrierNodeVisitor extends AstNodeVisitor<AstBarrierNode> {

        public AstBarrierNodeVisitor(ExpressionFactory elFactory, ExpressionContext elContext) {
            super(elFactory, elContext);
        }

        @Override
        public AstReadAwaitNode visitReadAwaitNode(ReadAwaitNodeContext ctx) {
            return new AstReadAwaitNodeVisitor(elFactory, elContext).visitReadAwaitNode(ctx);
        }

        @Override
        public AstReadNotifyNode visitReadNotifyNode(ReadNotifyNodeContext ctx) {
            return new AstReadNotifyNodeVisitor(elFactory, elContext).visitReadNotifyNode(ctx);
        }

        @Override
        public AstWriteAwaitNode visitWriteAwaitNode(WriteAwaitNodeContext ctx) {
            return new AstWriteAwaitNodeVisitor(elFactory, elContext).visitWriteAwaitNode(ctx);
        }

        @Override
        public AstWriteNotifyNode visitWriteNotifyNode(WriteNotifyNodeContext ctx) {
            return new AstWriteNotifyNodeVisitor(elFactory, elContext).visitWriteNotifyNode(ctx);
        }

    }

    private static class AstEventNodeVisitor extends AstNodeVisitor<AstEventNode> {

        public AstEventNodeVisitor(ExpressionFactory elFactory, ExpressionContext elContext) {
            super(elFactory, elContext);
        }

        @Override
        public AstBoundNode visitBoundNode(BoundNodeContext ctx) {
            return new AstBoundNodeVisitor(elFactory, elContext).visitBoundNode(ctx);
        }

        @Override
        public AstClosedNode visitClosedNode(ClosedNodeContext ctx) {
            return new AstClosedNodeVisitor(elFactory, elContext).visitClosedNode(ctx);
        }

        @Override
        public AstConnectedNode visitConnectedNode(ConnectedNodeContext ctx) {
            return new AstConnectedNodeVisitor(elFactory, elContext).visitConnectedNode(ctx);
        }

        @Override
        public AstDisconnectedNode visitDisconnectedNode(DisconnectedNodeContext ctx) {
            return new AstDisconnectedNodeVisitor(elFactory, elContext).visitDisconnectedNode(ctx);
        }

        @Override
        public AstOpenedNode visitOpenedNode(OpenedNodeContext ctx) {
            return new AstOpenedNodeVisitor(elFactory, elContext).visitOpenedNode(ctx);
        }

        @Override
        public AstReadValueNode visitReadNode(ReadNodeContext ctx) {
            return new AstReadValueNodeVisitor(elFactory, elContext).visitReadNode(ctx);
        }

        @Override
        public AstUnboundNode visitUnboundNode(UnboundNodeContext ctx) {
            return new AstUnboundNodeVisitor(elFactory, elContext).visitUnboundNode(ctx);
        }

        // HTTP events

        @Override
        public AstReadHttpHeaderNode visitReadHttpHeaderNode(ReadHttpHeaderNodeContext ctx) {
            return new AstReadHttpHeaderNodeVisitor(elFactory, elContext).visitReadHttpHeaderNode(ctx);
        }

        @Override
        public AstReadHttpMethodNode visitReadHttpMethodNode(ReadHttpMethodNodeContext ctx) {
            return new AstReadHttpMethodNodeVisitor(elFactory, elContext).visitReadHttpMethodNode(ctx);
        }

        @Override
        public AstReadHttpParameterNode visitReadHttpParameterNode(ReadHttpParameterNodeContext ctx) {
            return new AstReadHttpParameterNodeVisitor(elFactory, elContext).visitReadHttpParameterNode(ctx);
        }

        @Override
        public AstReadHttpVersionNode visitReadHttpVersionNode(ReadHttpVersionNodeContext ctx) {
            return new AstReadHttpVersionNodeVisitor(elFactory, elContext).visitReadHttpVersionNode(ctx);
        }

        @Override
        public AstReadHttpStatusNode visitReadHttpStatusNode(ReadHttpStatusNodeContext ctx) {
            return new AstReadHttpStatusNodeVisitor(elFactory, elContext).visitReadHttpStatusNode(ctx);
        }

    }

    private static class AstCommandNodeVisitor extends AstNodeVisitor<AstCommandNode> {

        public AstCommandNodeVisitor(ExpressionFactory elFactory, ExpressionContext elContext) {
            super(elFactory, elContext);
        }

        @Override
        public AstUnbindNode visitUnbindNode(UnbindNodeContext ctx) {
            return new AstUnbindNodeVisitor(elFactory, elContext).visitUnbindNode(ctx);
        }

        @Override
        public AstWriteValueNode visitWriteNode(WriteNodeContext ctx) {
            return new AstWriteValueNodeVisitor(elFactory, elContext).visitWriteNode(ctx);
        }

        @Override
        public AstCloseNode visitCloseNode(CloseNodeContext ctx) {
            return new AstCloseNodeVisitor(elFactory, elContext).visitCloseNode(ctx);
        }

        // HTTP commands

        @Override
        public AstWriteHttpHeaderNode visitWriteHttpHeaderNode(WriteHttpHeaderNodeContext ctx) {
            return new AstWriteHttpHeaderNodeVisitor(elFactory, elContext).visitWriteHttpHeaderNode(ctx);
        }

        @Override
        public AstWriteHttpContentLengthNode visitWriteHttpContentLengthNode(WriteHttpContentLengthNodeContext ctx) {
            return new AstWriteHttpContentLengthNodeVisitor(elFactory, elContext).visitWriteHttpContentLengthNode(ctx);
        }

        @Override
        public AstWriteHttpMethodNode visitWriteHttpMethodNode(WriteHttpMethodNodeContext ctx) {
            return new AstWriteHttpMethodNodeVisitor(elFactory, elContext).visitWriteHttpMethodNode(ctx);
        }

        @Override
        public AstWriteHttpParameterNode visitWriteHttpParameterNode(WriteHttpParameterNodeContext ctx) {
            return new AstWriteHttpParameterNodeVisitor(elFactory, elContext).visitWriteHttpParameterNode(ctx);
        }

        @Override
        public AstWriteHttpVersionNode visitWriteHttpVersionNode(WriteHttpVersionNodeContext ctx) {
            return new AstWriteHttpVersionNodeVisitor(elFactory, elContext).visitWriteHttpVersionNode(ctx);
        }

        @Override
        public AstWriteHttpStatusNode visitWriteHttpStatusNode(WriteHttpStatusNodeContext ctx) {
            return new AstWriteHttpStatusNodeVisitor(elFactory, elContext).visitWriteHttpStatusNode(ctx);
        }

        @Override
        public AstCloseHttpRequestNode visitCloseHttpRequestNode(CloseHttpRequestNodeContext ctx) {
            return new AstCloseHttpRequestNodeVisitor(elFactory, elContext).visitCloseHttpRequestNode(ctx);
        }

        @Override
        public AstCloseHttpResponseNode visitCloseHttpResponseNode(CloseHttpResponseNodeContext ctx) {
            return new AstCloseHttpResponseNodeVisitor(elFactory, elContext).visitCloseHttpResponseNode(ctx);
        }

    }

    private static class AstCloseNodeVisitor extends AstNodeVisitor<AstCloseNode> {

        public AstCloseNodeVisitor(ExpressionFactory elFactory, ExpressionContext elContext) {
            super(elFactory, elContext);
        }

        @Override
        public AstCloseNode visitCloseNode(CloseNodeContext ctx) {
            node = new AstCloseNode();
            node.setLocationInfo(ctx.k.getLine(), ctx.k.getCharPositionInLine());
            return node;
        }

    }

    private static class AstDisconnectNodeVisitor extends AstNodeVisitor<AstDisconnectNode> {

        public AstDisconnectNodeVisitor(ExpressionFactory elFactory, ExpressionContext elContext) {
            super(elFactory, elContext);
        }

        @Override
        public AstDisconnectNode visitDisconnectNode(DisconnectNodeContext ctx) {
            node = new AstDisconnectNode();
            node.setLocationInfo(ctx.k.getLine(), ctx.k.getCharPositionInLine());
            return node;
        }

    }

    private static class AstUnbindNodeVisitor extends AstNodeVisitor<AstUnbindNode> {

        public AstUnbindNodeVisitor(ExpressionFactory elFactory, ExpressionContext elContext) {
            super(elFactory, elContext);
        }

        @Override
        public AstUnbindNode visitUnbindNode(UnbindNodeContext ctx) {
            node = new AstUnbindNode();
            node.setLocationInfo(ctx.k.getLine(), ctx.k.getCharPositionInLine());
            return node;
        }

    }

    private static class AstWriteValueNodeVisitor extends AstNodeVisitor<AstWriteValueNode> {

        public AstWriteValueNodeVisitor(ExpressionFactory elFactory, ExpressionContext elContext) {
            super(elFactory, elContext);
        }

        private List<AstValue> values;

        @Override
        public AstWriteValueNode visitWriteNode(WriteNodeContext ctx) {
            node = new AstWriteValueNode();
            node.setLocationInfo(ctx.k.getLine(), ctx.k.getCharPositionInLine());
            values = new LinkedList<>();
            AstWriteValueNode result = super.visitWriteNode(ctx);
            node.setValues(values);
            return result;
        }

        @Override
        public AstWriteValueNode visitWriteValue(WriteValueContext ctx) {
            AstValue value = new AstValueVisitor(elFactory, elContext).visit(ctx);
            values.add(value);
            return node;
        }
    }

    private static class AstChildOpenedNodeVisitor extends AstNodeVisitor<AstChildOpenedNode> {

        public AstChildOpenedNodeVisitor(ExpressionFactory elFactory, ExpressionContext elContext) {
            super(elFactory, elContext);
        }

        @Override
        public AstChildOpenedNode visitChildOpenedNode(ChildOpenedNodeContext ctx) {
            node = new AstChildOpenedNode();
            node.setLocationInfo(ctx.k.getLine(), ctx.k.getCharPositionInLine());
            return node;
        }

    }

    private static class AstChildClosedNodeVisitor extends AstNodeVisitor<AstChildClosedNode> {

        public AstChildClosedNodeVisitor(ExpressionFactory elFactory, ExpressionContext elContext) {
            super(elFactory, elContext);
        }

        @Override
        public AstChildClosedNode visitChildClosedNode(ChildClosedNodeContext ctx) {
            node = new AstChildClosedNode();
            node.setLocationInfo(ctx.k.getLine(), ctx.k.getCharPositionInLine());
            return node;
        }

    }

    private static class AstBoundNodeVisitor extends AstNodeVisitor<AstBoundNode> {

        public AstBoundNodeVisitor(ExpressionFactory elFactory, ExpressionContext elContext) {
            super(elFactory, elContext);
        }

        @Override
        public AstBoundNode visitBoundNode(BoundNodeContext ctx) {
            node = new AstBoundNode();
            node.setLocationInfo(ctx.k.getLine(), ctx.k.getCharPositionInLine());
            return node;
        }

    }

    private static class AstClosedNodeVisitor extends AstNodeVisitor<AstClosedNode> {

        public AstClosedNodeVisitor(ExpressionFactory elFactory, ExpressionContext elContext) {
            super(elFactory, elContext);
        }

        @Override
        public AstClosedNode visitClosedNode(ClosedNodeContext ctx) {
            node = new AstClosedNode();
            node.setLocationInfo(ctx.k.getLine(), ctx.k.getCharPositionInLine());
            return node;
        }

    }

    private static class AstConnectedNodeVisitor extends AstNodeVisitor<AstConnectedNode> {

        public AstConnectedNodeVisitor(ExpressionFactory elFactory, ExpressionContext elContext) {
            super(elFactory, elContext);
        }

        @Override
        public AstConnectedNode visitConnectedNode(ConnectedNodeContext ctx) {
            node = new AstConnectedNode();
            node.setLocationInfo(ctx.k.getLine(), ctx.k.getCharPositionInLine());
            return node;
        }

    }

    private static class AstDisconnectedNodeVisitor extends AstNodeVisitor<AstDisconnectedNode> {

        public AstDisconnectedNodeVisitor(ExpressionFactory elFactory, ExpressionContext elContext) {
            super(elFactory, elContext);
        }

        @Override
        public AstDisconnectedNode visitDisconnectedNode(DisconnectedNodeContext ctx) {
            node = new AstDisconnectedNode();
            node.setLocationInfo(ctx.k.getLine(), ctx.k.getCharPositionInLine());
            return node;
        }

    }

    private static class AstOpenedNodeVisitor extends AstNodeVisitor<AstOpenedNode> {

        public AstOpenedNodeVisitor(ExpressionFactory elFactory, ExpressionContext elContext) {
            super(elFactory, elContext);
        }

        @Override
        public AstOpenedNode visitOpenedNode(OpenedNodeContext ctx) {
            node = new AstOpenedNode();
            node.setLocationInfo(ctx.k.getLine(), ctx.k.getCharPositionInLine());
            return node;
        }

    }

    private static class AstReadValueNodeVisitor extends AstNodeVisitor<AstReadValueNode> {

        private List<AstValueMatcher> matchers;

        public AstReadValueNodeVisitor(ExpressionFactory elFactory, ExpressionContext elContext) {
            super(elFactory, elContext);
        }

        @Override
        public AstReadValueNode visitReadNode(ReadNodeContext ctx) {
            node = new AstReadValueNode();
            node.setLocationInfo(ctx.k.getLine(), ctx.k.getCharPositionInLine());
            matchers = new LinkedList<>();
            AstReadValueNode result = super.visitReadNode(ctx);
            node.setMatchers(matchers);
            return result;
        }

        @Override
        public AstReadValueNode visitMatcher(MatcherContext ctx) {
            AstValueMatcher matcher = new AstValueMatcherVisitor(elFactory, elContext).visit(ctx);
            matchers.add(matcher);
            return node;
        }

    }

    private static class AstUnboundNodeVisitor extends AstNodeVisitor<AstUnboundNode> {

        public AstUnboundNodeVisitor(ExpressionFactory elFactory, ExpressionContext elContext) {
            super(elFactory, elContext);
        }

        @Override
        public AstUnboundNode visitUnboundNode(UnboundNodeContext ctx) {
            node = new AstUnboundNode();
            node.setLocationInfo(ctx.k.getLine(), ctx.k.getCharPositionInLine());
            return node;
        }

    }

    private static class AstReadAwaitNodeVisitor extends AstNodeVisitor<AstReadAwaitNode> {

        public AstReadAwaitNodeVisitor(ExpressionFactory elFactory, ExpressionContext elContext) {
            super(elFactory, elContext);
        }

        @Override
        public AstReadAwaitNode visitReadAwaitNode(ReadAwaitNodeContext ctx) {
            node = new AstReadAwaitNode();
            node.setLocationInfo(ctx.k.getLine(), ctx.k.getCharPositionInLine());
            node.setBarrierName(ctx.barrier.getText());
            return node;
        }

    }

    private static class AstReadNotifyNodeVisitor extends AstNodeVisitor<AstReadNotifyNode> {

        public AstReadNotifyNodeVisitor(ExpressionFactory elFactory, ExpressionContext elContext) {
            super(elFactory, elContext);
        }

        @Override
        public AstReadNotifyNode visitReadNotifyNode(ReadNotifyNodeContext ctx) {
            node = new AstReadNotifyNode();
            node.setLocationInfo(ctx.k.getLine(), ctx.k.getCharPositionInLine());
            node.setBarrierName(ctx.barrier.getText());
            return node;
        }

    }

    private static class AstWriteAwaitNodeVisitor extends AstNodeVisitor<AstWriteAwaitNode> {

        public AstWriteAwaitNodeVisitor(ExpressionFactory elFactory, ExpressionContext elContext) {
            super(elFactory, elContext);
        }

        @Override
        public AstWriteAwaitNode visitWriteAwaitNode(WriteAwaitNodeContext ctx) {
            node = new AstWriteAwaitNode();
            node.setLocationInfo(ctx.k.getLine(), ctx.k.getCharPositionInLine());
            node.setBarrierName(ctx.barrier.getText());
            return node;
        }

    }

    private static class AstWriteNotifyNodeVisitor extends AstNodeVisitor<AstWriteNotifyNode> {

        public AstWriteNotifyNodeVisitor(ExpressionFactory elFactory, ExpressionContext elContext) {
            super(elFactory, elContext);
        }

        @Override
        public AstWriteNotifyNode visitWriteNotifyNode(WriteNotifyNodeContext ctx) {
            node = new AstWriteNotifyNode();
            node.setLocationInfo(ctx.k.getLine(), ctx.k.getCharPositionInLine());
            node.setBarrierName(ctx.barrier.getText());
            return node;
        }

    }

    private static class AstValueMatcherVisitor extends AstVisitor<AstValueMatcher> {

        public AstValueMatcherVisitor(ExpressionFactory elFactory, ExpressionContext elContext) {
            super(elFactory, elContext);
        }

        @Override
        public AstExactTextMatcher visitExactTextMatcher(ExactTextMatcherContext ctx) {
            return new AstExactTextMatcherVisitor(elFactory, elContext).visit(ctx);
        }

        @Override
        public AstExactBytesMatcher visitExactBytesMatcher(ExactBytesMatcherContext ctx) {
            return new AstExactBytesMatcherVisitor(elFactory, elContext).visit(ctx);
        }

        @Override
        public AstRegexMatcher visitRegexMatcher(RegexMatcherContext ctx) {
            return new AstRegexMatcherVisitor(elFactory, elContext).visit(ctx);
        }

        @Override
        public AstExpressionMatcher visitExpressionMatcher(ExpressionMatcherContext ctx) {
            return new AstExpressionMatcherVisitor(elFactory, elContext).visit(ctx);
        }

        @Override
        public AstFixedLengthBytesMatcher visitFixedLengthBytesMatcher(FixedLengthBytesMatcherContext ctx) {
            return new AstFixedLengthBytesMatcherVisitor(elFactory, elContext).visit(ctx);
        }

        @Override
        public AstVariableLengthBytesMatcher visitVariableLengthBytesMatcher(VariableLengthBytesMatcherContext ctx) {
            return new AstVariableLengthBytesMatcherVisitor(elFactory, elContext).visit(ctx);
        }

    }

    private static class AstExactTextMatcherVisitor extends AstVisitor<AstExactTextMatcher> {

        public AstExactTextMatcherVisitor(ExpressionFactory elFactory, ExpressionContext elContext) {
            super(elFactory, elContext);
        }

        @Override
        public AstExactTextMatcher visitExactTextMatcher(ExactTextMatcherContext ctx) {
            String text = ctx.text.getText();
            return new AstExactTextMatcher(text.substring(1, text.length() - 1));
        }

    }

    private static class AstExactBytesMatcherVisitor extends AstVisitor<AstExactBytesMatcher> {

        public AstExactBytesMatcherVisitor(ExpressionFactory elFactory, ExpressionContext elContext) {
            super(elFactory, elContext);
        }

        @Override
        public AstExactBytesMatcher visitExactBytesMatcher(ExactBytesMatcherContext ctx) {
            if (ctx.bytes != null) {
                byte[] array = parseHexBytes(ctx.bytes.getText());
                return new AstExactBytesMatcher(array);
            } else if (ctx.byteLiteral != null) {
                byte[] array = parseHexBytes(ctx.byteLiteral.getText());
                return new AstExactBytesMatcher(array);
            } else if (ctx.shortLiteral != null) {
                byte[] array = parseHexBytes(ctx.shortLiteral.getText());
                return new AstExactBytesMatcher(array);
            } else if (ctx.longLiteral != null) {
                ByteBuffer buf = ByteBuffer.allocate(Long.SIZE / 8);
                buf.putLong(Long.parseLong(ctx.longLiteral.getText()));
                byte[] array = buf.array();
                return new AstExactBytesMatcher(array);
            } else if (ctx.intLiteral != null) {
                ByteBuffer buf = ByteBuffer.allocate(Integer.SIZE / 8);
                buf.putInt(Integer.parseInt(ctx.intLiteral.getText()));
                byte[] array = buf.array();
                return new AstExactBytesMatcher(array);
            }

            return null;
        }

    }

    private static class AstRegexMatcherVisitor extends AstVisitor<AstRegexMatcher> {

        public AstRegexMatcherVisitor(ExpressionFactory elFactory, ExpressionContext elContext) {
            super(elFactory, elContext);
        }

        @Override
        public AstRegexMatcher visitRegexMatcher(RegexMatcherContext ctx) {
            String regex = ctx.regex.getText();
            return new AstRegexMatcher(NamedGroupPattern.compile(regex));
        }

    }

    private static class AstExpressionMatcherVisitor extends AstVisitor<AstExpressionMatcher> {

        public AstExpressionMatcherVisitor(ExpressionFactory elFactory, ExpressionContext elContext) {
            super(elFactory, elContext);
        }

        @Override
        public AstExpressionMatcher visitExpressionMatcher(ExpressionMatcherContext ctx) {
            ValueExpression expression = elFactory.createValueExpression(elContext, ctx.expression.getText(),
                    byte[].class);
            return new AstExpressionMatcher(expression);
        }

    }

    private static class AstFixedLengthBytesMatcherVisitor extends AstVisitor<AstFixedLengthBytesMatcher> {

        public AstFixedLengthBytesMatcherVisitor(ExpressionFactory elFactory, ExpressionContext elContext) {
            super(elFactory, elContext);
        }

        @Override
        public AstFixedLengthBytesMatcher visitFixedLengthBytesMatcher(FixedLengthBytesMatcherContext ctx) {
            if (ctx.lastIndex != null) {
                String lastIndex = ctx.lastIndex.getText();
                if (ctx.capture != null) {
                    String capture = ctx.capture.getText();
                    return new AstFixedLengthBytesMatcher(parseInt(lastIndex), capture.substring(1, capture.length()));
                } else {
                    return new AstFixedLengthBytesMatcher(parseInt(lastIndex));
                }
            } else if (ctx.byteCapture != null) {
                String byteCapture = ctx.byteCapture.getText();
                return new AstByteLengthBytesMatcher(byteCapture.substring(1));
            } else if (ctx.shortCapture != null) {
                String shortCapture = ctx.shortCapture.getText();
                return new AstShortLengthBytesMatcher(shortCapture.substring(1));
            } else if (ctx.intCapture != null) {
                String intCapture = ctx.intCapture.getText();
                return new AstIntLengthBytesMatcher(intCapture.substring(1));
            } else if (ctx.longCapture != null) {
                String longCapture = ctx.longCapture.getText();
                return new AstLongLengthBytesMatcher(longCapture.substring(1));
            }

            return null;
        }

    }

    private static class AstVariableLengthBytesMatcherVisitor extends AstVisitor<AstVariableLengthBytesMatcher> {

        public AstVariableLengthBytesMatcherVisitor(ExpressionFactory elFactory, ExpressionContext elContext) {
            super(elFactory, elContext);
        }

        @Override
        public AstVariableLengthBytesMatcher visitVariableLengthBytesMatcher(VariableLengthBytesMatcherContext ctx) {
            ValueExpression length = elFactory.createValueExpression(elContext, ctx.length.getText(), Integer.class);
            if (ctx.capture != null) {
                String capture = ctx.capture.getText();
                return new AstVariableLengthBytesMatcher(length, capture.substring(1));
            } else {
                return new AstVariableLengthBytesMatcher(length);
            }
        }
    }

    private static class AstValueVisitor extends AstVisitor<AstValue> {

        public AstValueVisitor(ExpressionFactory elFactory, ExpressionContext elContext) {
            super(elFactory, elContext);
        }

        @Override
        public AstValue visitLiteralBytes(LiteralBytesContext ctx) {
            return new AstLiteralBytesValueVisitor(elFactory, elContext).visit(ctx);
        }

        @Override
        public AstValue visitLiteralText(LiteralTextContext ctx) {
            return new AstLiteralTextValueVisitor(elFactory, elContext).visit(ctx);
        }

        @Override
        public AstValue visitExpressionValue(ExpressionValueContext ctx) {
            return new AstExpressionValueVisitor(elFactory, elContext).visit(ctx);
        }

    }

    private static class AstLiteralTextValueVisitor extends AstVisitor<AstLiteralTextValue> {

        public AstLiteralTextValueVisitor(ExpressionFactory elFactory, ExpressionContext elContext) {
            super(elFactory, elContext);
        }

        @Override
        public AstLiteralTextValue visitLiteralText(LiteralTextContext ctx) {
            String text = ctx.text.getText();
            return new AstLiteralTextValue(text.substring(1, text.length() - 1));
        }

    }

    private static class AstLiteralBytesValueVisitor extends AstVisitor<AstLiteralBytesValue> {

        public AstLiteralBytesValueVisitor(ExpressionFactory elFactory, ExpressionContext elContext) {
            super(elFactory, elContext);
        }

        @Override
        public AstLiteralBytesValue visitLiteralBytes(LiteralBytesContext ctx) {
            String bytes = ctx.bytes.getText();
            return new AstLiteralBytesValue(parseHexBytes(bytes));
        }

    }

    private static class AstExpressionValueVisitor extends AstVisitor<AstExpressionValue> {

        public AstExpressionValueVisitor(ExpressionFactory elFactory, ExpressionContext elContext) {
            super(elFactory, elContext);
        }

        @Override
        public AstExpressionValue visitExpressionValue(ExpressionValueContext ctx) {
            ValueExpression expression = elFactory.createValueExpression(elContext, ctx.expression.getText(),
                    byte[].class);
            return new AstExpressionValue(expression);
        }

    }

    // HTTP visitors

    private static class AstReadHttpHeaderNodeVisitor extends AstNodeVisitor<AstReadHttpHeaderNode> {

        public AstReadHttpHeaderNodeVisitor(ExpressionFactory elFactory, ExpressionContext elContext) {
            super(elFactory, elContext);
        }

        @Override
        public AstReadHttpHeaderNode visitReadHttpHeaderNode(ReadHttpHeaderNodeContext ctx) {
            node = new AstReadHttpHeaderNode();
            node.setLocationInfo(ctx.k.getLine(), ctx.k.getCharPositionInLine());
            node.setName(new AstLiteralTextValueVisitor(elFactory, elContext).visit(ctx.name));
            node.setValue(new AstValueMatcherVisitor(elFactory, elContext).visit(ctx.value));
            return node;
        }

    }

    private static class AstWriteHttpHeaderNodeVisitor extends AstNodeVisitor<AstWriteHttpHeaderNode> {

        public AstWriteHttpHeaderNodeVisitor(ExpressionFactory elFactory, ExpressionContext elContext) {
            super(elFactory, elContext);
        }

        @Override
        public AstWriteHttpHeaderNode visitWriteHttpHeaderNode(WriteHttpHeaderNodeContext ctx) {
            node = new AstWriteHttpHeaderNode();
            node.setLocationInfo(ctx.k.getLine(), ctx.k.getCharPositionInLine());
            node.setName(new AstValueVisitor(elFactory, elContext).visit(ctx.name));
            node.setValue(new AstValueVisitor(elFactory, elContext).visit(ctx.value));
            return node;
        }

    }

    private static class AstWriteHttpContentLengthNodeVisitor extends AstNodeVisitor<AstWriteHttpContentLengthNode> {

        public AstWriteHttpContentLengthNodeVisitor(ExpressionFactory elFactory, ExpressionContext elContext) {
            super(elFactory, elContext);
        }

        @Override
        public AstWriteHttpContentLengthNode visitWriteHttpContentLengthNode(WriteHttpContentLengthNodeContext ctx) {
            node = new AstWriteHttpContentLengthNode();
            node.setLocationInfo(ctx.k.getLine(), ctx.k.getCharPositionInLine());
            return node;
        }

    }

    private static class AstReadHttpMethodNodeVisitor extends AstNodeVisitor<AstReadHttpMethodNode> {

        public AstReadHttpMethodNodeVisitor(ExpressionFactory elFactory, ExpressionContext elContext) {
            super(elFactory, elContext);
        }

        @Override
        public AstReadHttpMethodNode visitReadHttpMethodNode(ReadHttpMethodNodeContext ctx) {
            node = new AstReadHttpMethodNode();
            node.setLocationInfo(ctx.k.getLine(), ctx.k.getCharPositionInLine());
            node.setMethod(new AstValueMatcherVisitor(elFactory, elContext).visit(ctx.method));
            return node;
        }

    }

    private static class AstWriteHttpMethodNodeVisitor extends AstNodeVisitor<AstWriteHttpMethodNode> {

        public AstWriteHttpMethodNodeVisitor(ExpressionFactory elFactory, ExpressionContext elContext) {
            super(elFactory, elContext);
        }

        @Override
        public AstWriteHttpMethodNode visitWriteHttpMethodNode(WriteHttpMethodNodeContext ctx) {
            node = new AstWriteHttpMethodNode();
            node.setLocationInfo(ctx.k.getLine(), ctx.k.getCharPositionInLine());
            node.setMethod(new AstValueVisitor(elFactory, elContext).visit(ctx.method));
            return node;
        }

    }

    private static class AstReadHttpParameterNodeVisitor extends AstNodeVisitor<AstReadHttpParameterNode> {

        public AstReadHttpParameterNodeVisitor(ExpressionFactory elFactory, ExpressionContext elContext) {
            super(elFactory, elContext);
        }

        @Override
        public AstReadHttpParameterNode visitReadHttpParameterNode(ReadHttpParameterNodeContext ctx) {
            node = new AstReadHttpParameterNode();
            node.setLocationInfo(ctx.k.getLine(), ctx.k.getCharPositionInLine());
            node.setKey(new AstLiteralTextValueVisitor(elFactory, elContext).visit(ctx.name));
            node.setValue(new AstValueMatcherVisitor(elFactory, elContext).visit(ctx.value));
            return node;
        }

    }

    private static class AstWriteHttpParameterNodeVisitor extends AstNodeVisitor<AstWriteHttpParameterNode> {

        public AstWriteHttpParameterNodeVisitor(ExpressionFactory elFactory, ExpressionContext elContext) {
            super(elFactory, elContext);
        }

        @Override
        public AstWriteHttpParameterNode visitWriteHttpParameterNode(WriteHttpParameterNodeContext ctx) {
            node = new AstWriteHttpParameterNode();
            node.setLocationInfo(ctx.k.getLine(), ctx.k.getCharPositionInLine());
            node.setKey(new AstValueVisitor(elFactory, elContext).visit(ctx.name));
            node.setValue(new AstValueVisitor(elFactory, elContext).visit(ctx.value));
            return node;
        }

    }

    private static class AstReadHttpVersionNodeVisitor extends AstNodeVisitor<AstReadHttpVersionNode> {

        public AstReadHttpVersionNodeVisitor(ExpressionFactory elFactory, ExpressionContext elContext) {
            super(elFactory, elContext);
        }

        @Override
        public AstReadHttpVersionNode visitReadHttpVersionNode(ReadHttpVersionNodeContext ctx) {
            node = new AstReadHttpVersionNode();
            node.setLocationInfo(ctx.k.getLine(), ctx.k.getCharPositionInLine());
            node.setVersion(new AstValueMatcherVisitor(elFactory, elContext).visit(ctx.version));
            return node;
        }

    }

    private static class AstWriteHttpVersionNodeVisitor extends AstNodeVisitor<AstWriteHttpVersionNode> {

        public AstWriteHttpVersionNodeVisitor(ExpressionFactory elFactory, ExpressionContext elContext) {
            super(elFactory, elContext);
        }

        @Override
        public AstWriteHttpVersionNode visitWriteHttpVersionNode(WriteHttpVersionNodeContext ctx) {
            node = new AstWriteHttpVersionNode();
            node.setLocationInfo(ctx.k.getLine(), ctx.k.getCharPositionInLine());
            node.setVersion(new AstValueVisitor(elFactory, elContext).visit(ctx.version));
            return node;
        }

    }

    private static class AstReadHttpStatusNodeVisitor extends AstNodeVisitor<AstReadHttpStatusNode> {

        public AstReadHttpStatusNodeVisitor(ExpressionFactory elFactory, ExpressionContext elContext) {
            super(elFactory, elContext);
        }

        @Override
        public AstReadHttpStatusNode visitReadHttpStatusNode(ReadHttpStatusNodeContext ctx) {
            node = new AstReadHttpStatusNode();
            node.setLocationInfo(ctx.k.getLine(), ctx.k.getCharPositionInLine());
            node.setCode(new AstValueMatcherVisitor(elFactory, elContext).visit(ctx.code));
            node.setReason(new AstValueMatcherVisitor(elFactory, elContext).visit(ctx.reason));
            return node;
        }

    }

    private static class AstWriteHttpStatusNodeVisitor extends AstNodeVisitor<AstWriteHttpStatusNode> {

        public AstWriteHttpStatusNodeVisitor(ExpressionFactory elFactory, ExpressionContext elContext) {
            super(elFactory, elContext);
        }

        @Override
        public AstWriteHttpStatusNode visitWriteHttpStatusNode(WriteHttpStatusNodeContext ctx) {
            node = new AstWriteHttpStatusNode();
            node.setLocationInfo(ctx.k.getLine(), ctx.k.getCharPositionInLine());
            node.setCode(new AstValueVisitor(elFactory, elContext).visit(ctx.code));
            node.setReason(new AstValueVisitor(elFactory, elContext).visit(ctx.reason));
            return node;
        }

    }

    private static class AstCloseHttpRequestNodeVisitor extends AstNodeVisitor<AstCloseHttpRequestNode> {

        public AstCloseHttpRequestNodeVisitor(ExpressionFactory elFactory, ExpressionContext elContext) {
            super(elFactory, elContext);
        }

        @Override
        public AstCloseHttpRequestNode visitCloseHttpRequestNode(CloseHttpRequestNodeContext ctx) {
            node = new AstCloseHttpRequestNode();
            node.setLocationInfo(ctx.k.getLine(), ctx.k.getCharPositionInLine());
            return node;
        }

    }

    private static class AstCloseHttpResponseNodeVisitor extends AstNodeVisitor<AstCloseHttpResponseNode> {

        public AstCloseHttpResponseNodeVisitor(ExpressionFactory elFactory, ExpressionContext elContext) {
            super(elFactory, elContext);
        }

        @Override
        public AstCloseHttpResponseNode visitCloseHttpResponseNode(CloseHttpResponseNodeContext ctx) {
            node = new AstCloseHttpResponseNode();
            node.setLocationInfo(ctx.k.getLine(), ctx.k.getCharPositionInLine());
            return node;
        }

    }

}
