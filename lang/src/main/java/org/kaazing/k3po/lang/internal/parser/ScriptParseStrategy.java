/*
 * Copyright 2014, Kaazing Corporation. All rights reserved.
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
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

import javax.el.ExpressionFactory;
import javax.el.ValueExpression;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Token;
import org.kaazing.k3po.lang.internal.RegionInfo;
import org.kaazing.k3po.lang.internal.ast.AstAcceptNode;
import org.kaazing.k3po.lang.internal.ast.AstAcceptableNode;
import org.kaazing.k3po.lang.internal.ast.AstBarrierNode;
import org.kaazing.k3po.lang.internal.ast.AstBoundNode;
import org.kaazing.k3po.lang.internal.ast.AstChildClosedNode;
import org.kaazing.k3po.lang.internal.ast.AstChildOpenedNode;
import org.kaazing.k3po.lang.internal.ast.AstCloseNode;
import org.kaazing.k3po.lang.internal.ast.AstClosedNode;
import org.kaazing.k3po.lang.internal.ast.AstCommandNode;
import org.kaazing.k3po.lang.internal.ast.AstConnectNode;
import org.kaazing.k3po.lang.internal.ast.AstConnectedNode;
import org.kaazing.k3po.lang.internal.ast.AstDisconnectNode;
import org.kaazing.k3po.lang.internal.ast.AstDisconnectedNode;
import org.kaazing.k3po.lang.internal.ast.AstEventNode;
import org.kaazing.k3po.lang.internal.ast.AstNode;
import org.kaazing.k3po.lang.internal.ast.AstOpenedNode;
import org.kaazing.k3po.lang.internal.ast.AstOptionNode;
import org.kaazing.k3po.lang.internal.ast.AstPropertyNode;
import org.kaazing.k3po.lang.internal.ast.AstReadAwaitNode;
import org.kaazing.k3po.lang.internal.ast.AstReadClosedNode;
import org.kaazing.k3po.lang.internal.ast.AstReadConfigNode;
import org.kaazing.k3po.lang.internal.ast.AstReadNotifyNode;
import org.kaazing.k3po.lang.internal.ast.AstReadOptionNode;
import org.kaazing.k3po.lang.internal.ast.AstReadValueNode;
import org.kaazing.k3po.lang.internal.ast.AstRegion;
import org.kaazing.k3po.lang.internal.ast.AstScriptNode;
import org.kaazing.k3po.lang.internal.ast.AstStreamNode;
import org.kaazing.k3po.lang.internal.ast.AstStreamableNode;
import org.kaazing.k3po.lang.internal.ast.AstUnbindNode;
import org.kaazing.k3po.lang.internal.ast.AstUnboundNode;
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
import org.kaazing.k3po.lang.internal.ast.matcher.AstRegexMatcher;
import org.kaazing.k3po.lang.internal.ast.matcher.AstShortLengthBytesMatcher;
import org.kaazing.k3po.lang.internal.ast.matcher.AstValueMatcher;
import org.kaazing.k3po.lang.internal.ast.matcher.AstVariableLengthBytesMatcher;
import org.kaazing.k3po.lang.internal.ast.value.AstExpressionValue;
import org.kaazing.k3po.lang.internal.ast.value.AstLiteralBytesValue;
import org.kaazing.k3po.lang.internal.ast.value.AstLiteralTextValue;
import org.kaazing.k3po.lang.internal.ast.value.AstLocation;
import org.kaazing.k3po.lang.internal.ast.value.AstLocationExpression;
import org.kaazing.k3po.lang.internal.ast.value.AstLocationLiteral;
import org.kaazing.k3po.lang.internal.ast.value.AstValue;
import org.kaazing.k3po.lang.internal.el.ExpressionContext;
import org.kaazing.k3po.lang.internal.regex.NamedGroupPattern;
import org.kaazing.k3po.lang.parser.v2.RobotBaseVisitor;
import org.kaazing.k3po.lang.parser.v2.RobotParser;
import org.kaazing.k3po.lang.parser.v2.RobotParser.AcceptNodeContext;
import org.kaazing.k3po.lang.parser.v2.RobotParser.AcceptableNodeContext;
import org.kaazing.k3po.lang.parser.v2.RobotParser.BarrierNodeContext;
import org.kaazing.k3po.lang.parser.v2.RobotParser.BoundNodeContext;
import org.kaazing.k3po.lang.parser.v2.RobotParser.ChildClosedNodeContext;
import org.kaazing.k3po.lang.parser.v2.RobotParser.ChildOpenedNodeContext;
import org.kaazing.k3po.lang.parser.v2.RobotParser.CloseNodeContext;
import org.kaazing.k3po.lang.parser.v2.RobotParser.ClosedNodeContext;
import org.kaazing.k3po.lang.parser.v2.RobotParser.CommandNodeContext;
import org.kaazing.k3po.lang.parser.v2.RobotParser.ConnectNodeContext;
import org.kaazing.k3po.lang.parser.v2.RobotParser.ConnectedNodeContext;
import org.kaazing.k3po.lang.parser.v2.RobotParser.DisconnectNodeContext;
import org.kaazing.k3po.lang.parser.v2.RobotParser.DisconnectedNodeContext;
import org.kaazing.k3po.lang.parser.v2.RobotParser.EventNodeContext;
import org.kaazing.k3po.lang.parser.v2.RobotParser.ExactBytesMatcherContext;
import org.kaazing.k3po.lang.parser.v2.RobotParser.ExactTextMatcherContext;
import org.kaazing.k3po.lang.parser.v2.RobotParser.ExpressionMatcherContext;
import org.kaazing.k3po.lang.parser.v2.RobotParser.ExpressionValueContext;
import org.kaazing.k3po.lang.parser.v2.RobotParser.FixedLengthBytesMatcherContext;
import org.kaazing.k3po.lang.parser.v2.RobotParser.LiteralBytesContext;
import org.kaazing.k3po.lang.parser.v2.RobotParser.LiteralTextContext;
import org.kaazing.k3po.lang.parser.v2.RobotParser.LocationContext;
import org.kaazing.k3po.lang.parser.v2.RobotParser.MatcherContext;
import org.kaazing.k3po.lang.parser.v2.RobotParser.OpenedNodeContext;
import org.kaazing.k3po.lang.parser.v2.RobotParser.OptionNodeContext;
import org.kaazing.k3po.lang.parser.v2.RobotParser.PropertyNodeContext;
import org.kaazing.k3po.lang.parser.v2.RobotParser.ReadAwaitNodeContext;
import org.kaazing.k3po.lang.parser.v2.RobotParser.ReadClosedNodeContext;
import org.kaazing.k3po.lang.parser.v2.RobotParser.ReadHttpHeaderNodeContext;
import org.kaazing.k3po.lang.parser.v2.RobotParser.ReadHttpMethodNodeContext;
import org.kaazing.k3po.lang.parser.v2.RobotParser.ReadHttpParameterNodeContext;
import org.kaazing.k3po.lang.parser.v2.RobotParser.ReadHttpStatusNodeContext;
import org.kaazing.k3po.lang.parser.v2.RobotParser.ReadHttpVersionNodeContext;
import org.kaazing.k3po.lang.parser.v2.RobotParser.ReadNodeContext;
import org.kaazing.k3po.lang.parser.v2.RobotParser.ReadNotifyNodeContext;
import org.kaazing.k3po.lang.parser.v2.RobotParser.ReadOptionNodeContext;
import org.kaazing.k3po.lang.parser.v2.RobotParser.RegexMatcherContext;
import org.kaazing.k3po.lang.parser.v2.RobotParser.ScriptNodeContext;
import org.kaazing.k3po.lang.parser.v2.RobotParser.ServerStreamableNodeContext;
import org.kaazing.k3po.lang.parser.v2.RobotParser.StreamNodeContext;
import org.kaazing.k3po.lang.parser.v2.RobotParser.StreamableNodeContext;
import org.kaazing.k3po.lang.parser.v2.RobotParser.UnbindNodeContext;
import org.kaazing.k3po.lang.parser.v2.RobotParser.UnboundNodeContext;
import org.kaazing.k3po.lang.parser.v2.RobotParser.UriValueContext;
import org.kaazing.k3po.lang.parser.v2.RobotParser.VariableLengthBytesMatcherContext;
import org.kaazing.k3po.lang.parser.v2.RobotParser.WriteAwaitNodeContext;
import org.kaazing.k3po.lang.parser.v2.RobotParser.WriteCloseNodeContext;
import org.kaazing.k3po.lang.parser.v2.RobotParser.WriteFlushNodeContext;
import org.kaazing.k3po.lang.parser.v2.RobotParser.WriteHttpContentLengthNodeContext;
import org.kaazing.k3po.lang.parser.v2.RobotParser.WriteHttpHeaderNodeContext;
import org.kaazing.k3po.lang.parser.v2.RobotParser.WriteHttpHostNodeContext;
import org.kaazing.k3po.lang.parser.v2.RobotParser.WriteHttpMethodNodeContext;
import org.kaazing.k3po.lang.parser.v2.RobotParser.WriteHttpParameterNodeContext;
import org.kaazing.k3po.lang.parser.v2.RobotParser.WriteHttpRequestNodeContext;
import org.kaazing.k3po.lang.parser.v2.RobotParser.WriteHttpStatusNodeContext;
import org.kaazing.k3po.lang.parser.v2.RobotParser.WriteHttpVersionNodeContext;
import org.kaazing.k3po.lang.parser.v2.RobotParser.WriteNodeContext;
import org.kaazing.k3po.lang.parser.v2.RobotParser.WriteNotifyNodeContext;
import org.kaazing.k3po.lang.parser.v2.RobotParser.WriteOptionNodeContext;
import org.kaazing.k3po.lang.parser.v2.RobotParser.WriteValueContext;

abstract class ScriptParseStrategy<T extends AstRegion> {

    public static final ScriptParseStrategy<AstScriptNode> SCRIPT = new ScriptParseStrategy<AstScriptNode>() {
        @Override
        public AstScriptNode parse(RobotParser parser, ExpressionFactory elFactory, ExpressionContext elContext)
                throws RecognitionException {
            return new AstScriptNodeVisitor(elFactory, elContext).visit(parser.scriptNode());
        }
    };

    public static final ScriptParseStrategy<AstPropertyNode> PROPERTY_NODE = new ScriptParseStrategy<AstPropertyNode>() {
        @Override
        public AstPropertyNode parse(RobotParser parser, ExpressionFactory elFactory, ExpressionContext elContext)
                throws RecognitionException {
            return new AstPropertyNodeVisitor(elFactory, elContext).visit(parser.propertyNode());
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

    public static final ScriptParseStrategy<AstReadConfigNode> READ_HTTP_HEADER = new ScriptParseStrategy<AstReadConfigNode>() {
        @Override
        public AstReadConfigNode parse(RobotParser parser, ExpressionFactory elFactory, ExpressionContext elContext)
                throws RecognitionException {
            return new AstReadHttpConfigNodeVisitor(elFactory, elContext).visit(parser.readHttpHeaderNode());
        }
    };

    public static final ScriptParseStrategy<AstWriteConfigNode> WRITE_HTTP_HEADER =
            new ScriptParseStrategy<AstWriteConfigNode>() {
                @Override
                public AstWriteConfigNode parse(RobotParser parser, ExpressionFactory elFactory, ExpressionContext elContext)
                        throws RecognitionException {
                    return new AstWriteConfigNodeVisitor(elFactory, elContext).visit(parser.writeHttpHeaderNode());
                }
            };

    public static final ScriptParseStrategy<AstWriteConfigNode> WRITE_HTTP_CONTENT_LENGTH =
            new ScriptParseStrategy<AstWriteConfigNode>() {
                @Override
                public AstWriteConfigNode parse(RobotParser parser, ExpressionFactory elFactory, ExpressionContext elContext)
                        throws RecognitionException {
                    return new AstWriteConfigNodeVisitor(elFactory, elContext).visit(parser.writeHttpContentLengthNode());
                }
            };

    public static final ScriptParseStrategy<AstWriteConfigNode> WRITE_HTTP_HOST = new ScriptParseStrategy<AstWriteConfigNode>() {
        @Override
        public AstWriteConfigNode parse(RobotParser parser, ExpressionFactory elFactory, ExpressionContext elContext)
                throws RecognitionException {
            return new AstWriteConfigNodeVisitor(elFactory, elContext).visit(parser.writeHttpHostNode());
        }
    };

    public static final ScriptParseStrategy<AstReadConfigNode> READ_HTTP_METHOD = new ScriptParseStrategy<AstReadConfigNode>() {
        @Override
        public AstReadConfigNode parse(RobotParser parser, ExpressionFactory elFactory, ExpressionContext elContext)
                throws RecognitionException {
            return new AstReadHttpConfigNodeVisitor(elFactory, elContext).visit(parser.readHttpMethodNode());
        }
    };

    public static final ScriptParseStrategy<AstWriteConfigNode> WRITE_HTTP_METHOD =
            new ScriptParseStrategy<AstWriteConfigNode>() {
                @Override
                public AstWriteConfigNode parse(RobotParser parser, ExpressionFactory elFactory, ExpressionContext elContext)
                        throws RecognitionException {
                    return new AstWriteConfigNodeVisitor(elFactory, elContext).visit(parser.writeHttpMethodNode());
                }
            };

    public static final ScriptParseStrategy<AstWriteConfigNode> WRITE_HTTP_REQUEST =
            new ScriptParseStrategy<AstWriteConfigNode>() {
                @Override
                public AstWriteConfigNode parse(RobotParser parser, ExpressionFactory elFactory, ExpressionContext elContext)
                        throws RecognitionException {
                    return new AstWriteConfigNodeVisitor(elFactory, elContext).visit(parser.writeHttpRequestNode());
                }
            };

    public static final ScriptParseStrategy<AstReadConfigNode> READ_HTTP_PARAMETER =
            new ScriptParseStrategy<AstReadConfigNode>() {
                @Override
                public AstReadConfigNode parse(RobotParser parser, ExpressionFactory elFactory, ExpressionContext elContext)
                        throws RecognitionException {
                    return new AstReadHttpConfigNodeVisitor(elFactory, elContext).visit(parser.readHttpParameterNode());
                }
            };

    public static final ScriptParseStrategy<AstWriteConfigNode> WRITE_HTTP_PARAMETER =
            new ScriptParseStrategy<AstWriteConfigNode>() {
                @Override
                public AstWriteConfigNode parse(RobotParser parser, ExpressionFactory elFactory, ExpressionContext elContext)
                        throws RecognitionException {
                    return new AstWriteConfigNodeVisitor(elFactory, elContext).visit(parser.writeHttpParameterNode());
                }
            };

    public static final ScriptParseStrategy<AstReadConfigNode> READ_HTTP_VERSION = new ScriptParseStrategy<AstReadConfigNode>() {
        @Override
        public AstReadConfigNode parse(RobotParser parser, ExpressionFactory elFactory, ExpressionContext elContext)
                throws RecognitionException {
            return new AstReadHttpConfigNodeVisitor(elFactory, elContext).visit(parser.readHttpVersionNode());
        }
    };

    public static final ScriptParseStrategy<AstWriteConfigNode> WRITE_HTTP_VERSION =
            new ScriptParseStrategy<AstWriteConfigNode>() {
                @Override
                public AstWriteConfigNode parse(RobotParser parser, ExpressionFactory elFactory, ExpressionContext elContext)
                        throws RecognitionException {
                    return new AstWriteConfigNodeVisitor(elFactory, elContext).visit(parser.writeHttpVersionNode());
                }
            };

    public static final ScriptParseStrategy<AstReadConfigNode> READ_HTTP_STATUS = new ScriptParseStrategy<AstReadConfigNode>() {
        @Override
        public AstReadConfigNode parse(RobotParser parser, ExpressionFactory elFactory, ExpressionContext elContext)
                throws RecognitionException {
            return new AstReadHttpConfigNodeVisitor(elFactory, elContext).visit(parser.readHttpStatusNode());
        }
    };

    public static final ScriptParseStrategy<AstWriteConfigNode> WRITE_HTTP_STATUS =
            new ScriptParseStrategy<AstWriteConfigNode>() {
                @Override
                public AstWriteConfigNode parse(RobotParser parser, ExpressionFactory elFactory, ExpressionContext elContext)
                        throws RecognitionException {
                    return new AstWriteConfigNodeVisitor(elFactory, elContext).visit(parser.writeHttpStatusNode());
                }
            };

    public static final ScriptParseStrategy<AstWriteFlushNode> WRITE_FLUSH = new ScriptParseStrategy<AstWriteFlushNode>() {
        @Override
        public AstWriteFlushNode parse(RobotParser parser, ExpressionFactory elFactory, ExpressionContext elContext)
                throws RecognitionException {
            return new AstWriteFlushNodeVisitor(elFactory, elContext).visit(parser.writeFlushNode());
        }
    };

    public static final ScriptParseStrategy<AstReadClosedNode> READ_CLOSED = new ScriptParseStrategy<AstReadClosedNode>() {
        @Override
        public AstReadClosedNode parse(RobotParser parser, ExpressionFactory elFactory, ExpressionContext elContext)
                throws RecognitionException {
            return new AstReadClosedNodeVisitor(elFactory, elContext).visit(parser.readClosedNode());
        }
    };

    public static final ScriptParseStrategy<AstWriteCloseNode> WRITE_CLOSE = new ScriptParseStrategy<AstWriteCloseNode>() {
        @Override
        public AstWriteCloseNode parse(RobotParser parser, ExpressionFactory elFactory, ExpressionContext elContext)
                throws RecognitionException {
            return new AstWriteCloseNodeVisitor(elFactory, elContext).visit(parser.writeCloseNode());
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

    public static final ScriptParseStrategy<AstExactTextMatcher> EXACT_TEXT_MATCHER =
            new ScriptParseStrategy<AstExactTextMatcher>() {
                @Override
                public AstExactTextMatcher parse(RobotParser parser, ExpressionFactory elFactory, ExpressionContext elContext)
                        throws RecognitionException {
                    return new AstExactTextMatcherVisitor(elFactory, elContext).visit(parser.exactTextMatcher());
                }
            };

    public static final ScriptParseStrategy<AstExactBytesMatcher> EXACT_BYTES_MATCHER =
            new ScriptParseStrategy<AstExactBytesMatcher>() {
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

    public static final ScriptParseStrategy<AstExpressionMatcher> EXPRESSION_MATCHER =
            new ScriptParseStrategy<AstExpressionMatcher>() {
                @Override
                public AstExpressionMatcher parse(RobotParser parser, ExpressionFactory elFactory, ExpressionContext elContext)
                        throws RecognitionException {
                    return new AstExpressionMatcherVisitor(elFactory, elContext).visit(parser.expressionMatcher());
                }
            };

    public static final ScriptParseStrategy<AstFixedLengthBytesMatcher> FIXED_LENGTH_BYTES_MATCHER =
            new ScriptParseStrategy<AstFixedLengthBytesMatcher>() {
                @Override
                public AstFixedLengthBytesMatcher parse(RobotParser parser, ExpressionFactory elFactory,
                    ExpressionContext elContext) throws RecognitionException {
                    return new AstFixedLengthBytesMatcherVisitor(elFactory, elContext).visit(parser.fixedLengthBytesMatcher());
                }
            };

    public static final ScriptParseStrategy<AstVariableLengthBytesMatcher> VARIABLE_LENGTH_BYTES_MATCHER =
            new ScriptParseStrategy<AstVariableLengthBytesMatcher>() {
                @Override
                public AstVariableLengthBytesMatcher parse(RobotParser parser, ExpressionFactory elFactory,
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

    public static final ScriptParseStrategy<AstLiteralTextValue> LITERAL_TEXT_VALUE =
            new ScriptParseStrategy<AstLiteralTextValue>() {
                @Override
                public AstLiteralTextValue parse(RobotParser parser, ExpressionFactory elFactory, ExpressionContext elContext)
                        throws RecognitionException {
                    return new AstLiteralTextValueVisitor(elFactory, elContext).visit(parser.literalText());
                }
            };

    public static final ScriptParseStrategy<AstLiteralBytesValue> LITERAL_BYTES_VALUE =
            new ScriptParseStrategy<AstLiteralBytesValue>() {
                @Override
                public AstLiteralBytesValue parse(RobotParser parser, ExpressionFactory elFactory, ExpressionContext elContext)
                        throws RecognitionException {
                    return new AstLiteralBytesValueVisitor(elFactory, elContext).visit(parser.literalBytes());
                }
            };

    public static final ScriptParseStrategy<AstExpressionValue> EXPRESSION_VALUE =
            new ScriptParseStrategy<AstExpressionValue>() {
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
        private static final List<RegionInfo> EMPTY_CHILDREN = emptyList();

        protected List<RegionInfo> childInfos = EMPTY_CHILDREN;

        protected final ExpressionFactory elFactory;
        protected final ExpressionContext elContext;

        protected AstVisitor(ExpressionFactory elFactory, ExpressionContext elContext) {
            this.elFactory = elFactory;
            this.elContext = elContext;
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
            super.visitScriptNode(ctx);
            node.setRegionInfo(asSequentialRegion(childInfos, ctx));
            return node;
        }

        @Override
        public AstScriptNode visitPropertyNode(PropertyNodeContext ctx) {
            AstPropertyNodeVisitor visitor = new AstPropertyNodeVisitor(elFactory, elContext);
            AstPropertyNode propertyNode = visitor.visitPropertyNode(ctx);
            if (propertyNode != null) {
                node.getProperties().add(propertyNode);
                childInfos().add(propertyNode.getRegionInfo());
            }
            return node;
        }

        @Override
        public AstScriptNode visitStreamNode(StreamNodeContext ctx) {
            AstStreamNodeVisitor visitor = new AstStreamNodeVisitor(elFactory, elContext);
            AstStreamNode streamNode = visitor.visitStreamNode(ctx);
            if (streamNode != null) {
                node.getStreams().add(streamNode);
                childInfos().add(streamNode.getRegionInfo());
            }
            return node;
        }

    }

    private static class AstPropertyNodeVisitor extends AstNodeVisitor<AstPropertyNode> {

        public AstPropertyNodeVisitor(ExpressionFactory elFactory, ExpressionContext elContext) {
            super(elFactory, elContext);
        }

        @Override
        public AstPropertyNode visitPropertyNode(PropertyNodeContext ctx) {

            AstValueVisitor visitor = new AstValueVisitor(elFactory, elContext, Object.class);
            AstValue value = visitor.visit(ctx.value);
            childInfos().add(value.getRegionInfo());

            node = new AstPropertyNode();
            node.setRegionInfo(asSequentialRegion(childInfos, ctx));
            node.setPropertyName(ctx.name.getText());
            node.setPropertyValue(value);
            node.setExpressionContext(elContext);

            return node;
        }

    }

    private static class AstStreamNodeVisitor extends AstNodeVisitor<AstStreamNode> {

        public AstStreamNodeVisitor(ExpressionFactory elFactory, ExpressionContext elContext) {
            super(elFactory, elContext);
        }

        @Override
        public AstAcceptNode visitAcceptNode(AcceptNodeContext ctx) {
            AstAcceptNodeVisitor visitor = new AstAcceptNodeVisitor(elFactory, elContext);
            AstAcceptNode acceptNode = visitor.visitAcceptNode(ctx);
            if (acceptNode != null) {
                childInfos().add(acceptNode.getRegionInfo());
            }
            return acceptNode;
        }

        @Override
        public AstAcceptableNode visitAcceptableNode(AcceptableNodeContext ctx) {
            AstAcceptedNodeVisitor visitor = new AstAcceptedNodeVisitor(elFactory, elContext);
            AstAcceptableNode acceptableNode = visitor.visitAcceptableNode(ctx);
            if (acceptableNode != null) {
                childInfos().add(acceptableNode.getRegionInfo());
            }
            return acceptableNode;
        }

        @Override
        public AstConnectNode visitConnectNode(ConnectNodeContext ctx) {
            AstConnectNodeVisitor visitor = new AstConnectNodeVisitor(elFactory, elContext);
            AstConnectNode connectNode = visitor.visitConnectNode(ctx);
            if (connectNode != null) {
                childInfos().add(connectNode.getRegionInfo());
            }
            return connectNode;
        }

    }

    private static class AstAcceptNodeVisitor extends AstNodeVisitor<AstAcceptNode> {

        public AstAcceptNodeVisitor(ExpressionFactory elFactory, ExpressionContext elContext) {
            super(elFactory, elContext);
        }

        @Override
        public AstAcceptNode visitAcceptNode(AcceptNodeContext ctx) {
            AstLocationVisitor locationVisitor = new AstLocationVisitor(elFactory, elContext);
            AstLocation location = locationVisitor.visit(ctx.acceptURI);
            node = new AstAcceptNode();
            node.setLocation(location);
            node.setEnvironment(elContext);
            if (ctx.text != null) {
                node.setAcceptName(ctx.text.getText());
            }
            LocationContext transport = ctx.value;
            if (transport != null) {
                AstLocationVisitor transportVisitor = new AstLocationVisitor(elFactory, elContext);
                AstLocation transportLocation = transportVisitor.visit(ctx.value);
                node.getOptions().put("transport", transportLocation);
            }
            super.visitAcceptNode(ctx);
            node.setRegionInfo(asParallelRegion(childInfos, ctx));
            return node;
        }

        @Override
        public AstAcceptNode visitServerStreamableNode(ServerStreamableNodeContext ctx) {
            AstStreamableNodeVisitor visitor = new AstStreamableNodeVisitor(elFactory, elContext);
            AstStreamableNode streamableNode = visitor.visitServerStreamableNode(ctx);
            if (streamableNode != null) {
                node.getStreamables().add(streamableNode);
                childInfos().add(streamableNode.getRegionInfo());
            }
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
            if (ctx.text != null) {
                node.setAcceptName(ctx.text.getText());
            }
            super.visitAcceptableNode(ctx);
            node.setRegionInfo(asParallelRegion(childInfos, ctx));
            return node;
        }

        @Override
        public AstAcceptableNode visitStreamableNode(StreamableNodeContext ctx) {
            AstStreamableNodeVisitor visitor = new AstStreamableNodeVisitor(elFactory, elContext);
            AstStreamableNode streamableNode = visitor.visitStreamableNode(ctx);
            if (streamableNode != null) {
                node.getStreamables().add(streamableNode);
                childInfos().add(streamableNode.getRegionInfo());
            }
            return node;
        }

    }

    private static class AstConnectNodeVisitor extends AstNodeVisitor<AstConnectNode> {

        public AstConnectNodeVisitor(ExpressionFactory elFactory, ExpressionContext elContext) {
            super(elFactory, elContext);
        }

        @Override
        public AstConnectNode visitConnectNode(ConnectNodeContext ctx) {
            AstLocationVisitor locationVisitor = new AstLocationVisitor(elFactory, elContext);
            AstLocation location = locationVisitor.visit(ctx.connectURI);
            node = new AstConnectNode();
            node.setLocation(location);
            node.setEnvironment(elContext);
            super.visitConnectNode(ctx);
            node.setRegionInfo(asParallelRegion(childInfos, ctx));
            Token barrier = ctx.barrier;
            if (barrier != null) {
                node.setBarrier(barrier.getText());
            }
            LocationContext transport = ctx.value;
            if (transport != null) {
                AstLocationVisitor transportVisitor = new AstLocationVisitor(elFactory, elContext);
                AstLocation transportLocation = transportVisitor.visit(ctx.value);
                node.getOptions().put("transport", transportLocation);
            }
            return node;
        }

        @Override
        public AstConnectNode visitStreamableNode(StreamableNodeContext ctx) {
            AstStreamableNodeVisitor visitor = new AstStreamableNodeVisitor(elFactory, elContext);
            AstStreamableNode streamableNode = visitor.visitStreamableNode(ctx);
            if (streamableNode != null) {
                node.getStreamables().add(streamableNode);
                childInfos().add(streamableNode.getRegionInfo());
            }
            return node;
        }

    }

    private static class AstStreamableNodeVisitor extends AstNodeVisitor<AstStreamableNode> {

        public AstStreamableNodeVisitor(ExpressionFactory elFactory, ExpressionContext elContext) {
            super(elFactory, elContext);
        }

        @Override
        public AstBarrierNode visitBarrierNode(BarrierNodeContext ctx) {
            AstBarrierNodeVisitor visitor = new AstBarrierNodeVisitor(elFactory, elContext);
            AstBarrierNode barrierNode = visitor.visitBarrierNode(ctx);
            if (barrierNode != null) {
                childInfos().add(barrierNode.getRegionInfo());
            }
            return barrierNode;
        }

        @Override
        public AstEventNode visitEventNode(EventNodeContext ctx) {
            AstEventNodeVisitor visitor = new AstEventNodeVisitor(elFactory, elContext);
            AstEventNode eventNode = visitor.visitEventNode(ctx);
            if (eventNode != null) {
                childInfos().add(eventNode.getRegionInfo());
            }
            return eventNode;
        }

        @Override
        public AstCommandNode visitCommandNode(CommandNodeContext ctx) {
            AstCommandNodeVisitor visitor = new AstCommandNodeVisitor(elFactory, elContext);
            AstCommandNode commandNode = visitor.visitCommandNode(ctx);
            if (commandNode != null) {
                childInfos().add(commandNode.getRegionInfo());
            }
            return commandNode;
        }

        @Override
        public AstOptionNode visitOptionNode(OptionNodeContext ctx) {
            AstOptionNodeVisitor visitor = new AstOptionNodeVisitor(elFactory, elContext);
            AstOptionNode optionNode = visitor.visitOptionNode(ctx);
            if (optionNode != null) {
                childInfos().add(optionNode.getRegionInfo());
            }
            return optionNode;
        }

    }

    private static class AstOptionNodeVisitor extends AstNodeVisitor<AstOptionNode> {

        public AstOptionNodeVisitor(ExpressionFactory elFactory, ExpressionContext elContext) {
            super(elFactory, elContext);
        }

        @Override
        public AstOptionNode visitReadOptionNode(ReadOptionNodeContext ctx) {

            AstValueVisitor visitor = new AstValueVisitor(elFactory, elContext);
            AstValue value = visitor.visit(ctx);
            childInfos().add(value.getRegionInfo());

            node = new AstReadOptionNode();
            node.setOptionName(ctx.name.getText());
            node.setOptionValue(value);
            node.setRegionInfo(asSequentialRegion(childInfos, ctx));

            return node;
        }

        @Override
        public AstOptionNode visitWriteOptionNode(WriteOptionNodeContext ctx) {

            AstValueVisitor visitor = new AstValueVisitor(elFactory, elContext);
            AstValue value = visitor.visit(ctx);
            childInfos().add(value.getRegionInfo());

            node = new AstWriteOptionNode();
            node.setOptionName(ctx.name.getText());
            node.setOptionValue(value);
            node.setRegionInfo(asSequentialRegion(childInfos, ctx));

            return node;
        }
    }

    private static class AstBarrierNodeVisitor extends AstNodeVisitor<AstBarrierNode> {

        public AstBarrierNodeVisitor(ExpressionFactory elFactory, ExpressionContext elContext) {
            super(elFactory, elContext);
        }

        @Override
        public AstReadAwaitNode visitReadAwaitNode(ReadAwaitNodeContext ctx) {

            AstReadAwaitNodeVisitor visitor = new AstReadAwaitNodeVisitor(elFactory, elContext);
            AstReadAwaitNode readAwaitNode = visitor.visitReadAwaitNode(ctx);
            if (readAwaitNode != null) {
                childInfos().add(readAwaitNode.getRegionInfo());
            }

            return readAwaitNode;
        }

        @Override
        public AstReadNotifyNode visitReadNotifyNode(ReadNotifyNodeContext ctx) {
            AstReadNotifyNodeVisitor visitor = new AstReadNotifyNodeVisitor(elFactory, elContext);
            AstReadNotifyNode readNotifyNode = visitor.visitReadNotifyNode(ctx);
            if (readNotifyNode != null) {
                childInfos().add(readNotifyNode.getRegionInfo());
            }
            return readNotifyNode;
        }

        @Override
        public AstWriteAwaitNode visitWriteAwaitNode(WriteAwaitNodeContext ctx) {

            AstWriteAwaitNodeVisitor visitor = new AstWriteAwaitNodeVisitor(elFactory, elContext);
            AstWriteAwaitNode writeAwaitNode = visitor.visitWriteAwaitNode(ctx);
            if (writeAwaitNode != null) {
                childInfos().add(writeAwaitNode.getRegionInfo());
            }

            return writeAwaitNode;
        }

        @Override
        public AstWriteNotifyNode visitWriteNotifyNode(WriteNotifyNodeContext ctx) {

            AstWriteNotifyNodeVisitor visitor = new AstWriteNotifyNodeVisitor(elFactory, elContext);
            AstWriteNotifyNode writeNotifyNode = visitor.visitWriteNotifyNode(ctx);
            if (writeNotifyNode != null) {
                childInfos().add(writeNotifyNode.getRegionInfo());
            }

            return writeNotifyNode;
        }

    }

    private static class AstEventNodeVisitor extends AstNodeVisitor<AstEventNode> {

        public AstEventNodeVisitor(ExpressionFactory elFactory, ExpressionContext elContext) {
            super(elFactory, elContext);
        }

        @Override
        public AstBoundNode visitBoundNode(BoundNodeContext ctx) {

            AstBoundNodeVisitor visitor = new AstBoundNodeVisitor(elFactory, elContext);
            AstBoundNode boundNode = visitor.visitBoundNode(ctx);
            if (boundNode != null) {
                childInfos().add(boundNode.getRegionInfo());
            }

            return boundNode;
        }

        @Override
        public AstClosedNode visitClosedNode(ClosedNodeContext ctx) {

            AstClosedNodeVisitor visitor = new AstClosedNodeVisitor(elFactory, elContext);
            AstClosedNode closedNode = visitor.visitClosedNode(ctx);
            if (closedNode != null) {
                childInfos().add(closedNode.getRegionInfo());
            }

            return closedNode;
        }

        @Override
        public AstConnectedNode visitConnectedNode(ConnectedNodeContext ctx) {

            AstConnectedNodeVisitor visitor = new AstConnectedNodeVisitor(elFactory, elContext);
            AstConnectedNode connectedNode = visitor.visitConnectedNode(ctx);
            if (connectedNode != null) {
                childInfos().add(connectedNode.getRegionInfo());
            }

            return connectedNode;
        }

        @Override
        public AstDisconnectedNode visitDisconnectedNode(DisconnectedNodeContext ctx) {

            AstDisconnectedNodeVisitor visitor = new AstDisconnectedNodeVisitor(elFactory, elContext);
            AstDisconnectedNode disconnectedNode = visitor.visitDisconnectedNode(ctx);
            if (disconnectedNode != null) {
                childInfos().add(disconnectedNode.getRegionInfo());
            }

            return disconnectedNode;
        }

        @Override
        public AstOpenedNode visitOpenedNode(OpenedNodeContext ctx) {

            AstOpenedNodeVisitor visitor = new AstOpenedNodeVisitor(elFactory, elContext);
            AstOpenedNode openedNode = visitor.visitOpenedNode(ctx);
            if (openedNode != null) {
                childInfos().add(openedNode.getRegionInfo());
            }

            return openedNode;
        }

        @Override
        public AstReadValueNode visitReadNode(ReadNodeContext ctx) {

            AstReadValueNodeVisitor visitor = new AstReadValueNodeVisitor(elFactory, elContext);
            AstReadValueNode readNode = visitor.visitReadNode(ctx);
            if (readNode != null) {
                childInfos().add(readNode.getRegionInfo());
            }

            return readNode;
        }

        @Override
        public AstReadClosedNode visitReadClosedNode(ReadClosedNodeContext ctx) {

            AstReadClosedNodeVisitor visitor = new AstReadClosedNodeVisitor(elFactory, elContext);
            AstReadClosedNode readClosedNode = visitor.visitReadClosedNode(ctx);
            if (readClosedNode != null) {
                childInfos().add(readClosedNode.getRegionInfo());
            }

            return readClosedNode;
        }

        @Override
        public AstUnboundNode visitUnboundNode(UnboundNodeContext ctx) {

            AstUnboundNodeVisitor visitor = new AstUnboundNodeVisitor(elFactory, elContext);
            AstUnboundNode unboundNode = visitor.visitUnboundNode(ctx);
            if (unboundNode != null) {
                childInfos().add(unboundNode.getRegionInfo());
            }

            return unboundNode;
        }

        // HTTP events

        @Override
        public AstReadConfigNode visitReadHttpHeaderNode(ReadHttpHeaderNodeContext ctx) {

            AstReadHttpConfigNodeVisitor visitor = new AstReadHttpConfigNodeVisitor(elFactory, elContext);
            AstReadConfigNode readHttpHeaderNode = visitor.visitReadHttpHeaderNode(ctx);
            if (readHttpHeaderNode != null) {
                childInfos().add(readHttpHeaderNode.getRegionInfo());
            }

            return readHttpHeaderNode;
        }

        @Override
        public AstReadConfigNode visitReadHttpMethodNode(ReadHttpMethodNodeContext ctx) {

            AstReadHttpConfigNodeVisitor visitor = new AstReadHttpConfigNodeVisitor(elFactory, elContext);
            AstReadConfigNode readHttpMethodNode = visitor.visitReadHttpMethodNode(ctx);
            if (readHttpMethodNode != null) {
                childInfos().add(readHttpMethodNode.getRegionInfo());
            }

            return readHttpMethodNode;
        }

        @Override
        public AstReadConfigNode visitReadHttpParameterNode(ReadHttpParameterNodeContext ctx) {

            AstReadHttpConfigNodeVisitor visitor = new AstReadHttpConfigNodeVisitor(elFactory, elContext);
            AstReadConfigNode readHttpParameterNode = visitor.visitReadHttpParameterNode(ctx);
            if (readHttpParameterNode != null) {
                childInfos().add(readHttpParameterNode.getRegionInfo());
            }

            return readHttpParameterNode;
        }

        @Override
        public AstReadConfigNode visitReadHttpVersionNode(ReadHttpVersionNodeContext ctx) {

            AstReadHttpConfigNodeVisitor visitor = new AstReadHttpConfigNodeVisitor(elFactory, elContext);
            AstReadConfigNode readHttpVersionNode = visitor.visitReadHttpVersionNode(ctx);
            if (readHttpVersionNode != null) {
                childInfos().add(readHttpVersionNode.getRegionInfo());
            }

            return readHttpVersionNode;
        }

        @Override
        public AstReadConfigNode visitReadHttpStatusNode(ReadHttpStatusNodeContext ctx) {

            AstReadHttpConfigNodeVisitor visitor = new AstReadHttpConfigNodeVisitor(elFactory, elContext);
            AstReadConfigNode readHttpStatusNode = visitor.visitReadHttpStatusNode(ctx);
            if (readHttpStatusNode != null) {
                childInfos().add(readHttpStatusNode.getRegionInfo());
            }

            return readHttpStatusNode;
        }

    }

    private static class AstCommandNodeVisitor extends AstNodeVisitor<AstCommandNode> {

        public AstCommandNodeVisitor(ExpressionFactory elFactory, ExpressionContext elContext) {
            super(elFactory, elContext);
        }

        @Override
        public AstUnbindNode visitUnbindNode(UnbindNodeContext ctx) {

            AstUnbindNodeVisitor visitor = new AstUnbindNodeVisitor(elFactory, elContext);
            AstUnbindNode unbindNode = visitor.visitUnbindNode(ctx);
            if (unbindNode != null) {
                childInfos().add(unbindNode.getRegionInfo());
            }

            return unbindNode;
        }

        @Override
        public AstWriteValueNode visitWriteNode(WriteNodeContext ctx) {
            AstWriteValueNodeVisitor visitor = new AstWriteValueNodeVisitor(elFactory, elContext);
            AstWriteValueNode writeNode = visitor.visitWriteNode(ctx);
            if (writeNode != null) {
                childInfos().add(writeNode.getRegionInfo());
            }
            return writeNode;
        }

        @Override
        public AstWriteFlushNode visitWriteFlushNode(WriteFlushNodeContext ctx) {

            AstWriteFlushNodeVisitor visitor = new AstWriteFlushNodeVisitor(elFactory, elContext);
            AstWriteFlushNode writeFlushNode = visitor.visitWriteFlushNode(ctx);
            if (writeFlushNode != null) {
                childInfos().add(writeFlushNode.getRegionInfo());
            }

            return writeFlushNode;
        }

        @Override
        public AstWriteCloseNode visitWriteCloseNode(WriteCloseNodeContext ctx) {

            AstWriteCloseNodeVisitor visitor = new AstWriteCloseNodeVisitor(elFactory, elContext);
            AstWriteCloseNode writeCloseNode = visitor.visitWriteCloseNode(ctx);
            if (writeCloseNode != null) {
                childInfos().add(writeCloseNode.getRegionInfo());
            }

            return writeCloseNode;
        }

        @Override
        public AstCloseNode visitCloseNode(CloseNodeContext ctx) {

            AstCloseNodeVisitor visitor = new AstCloseNodeVisitor(elFactory, elContext);
            AstCloseNode closeNode = visitor.visitCloseNode(ctx);
            if (closeNode != null) {
                childInfos().add(closeNode.getRegionInfo());
            }

            return closeNode;
        }

        // HTTP commands

        @Override
        public AstWriteConfigNode visitWriteHttpRequestNode(WriteHttpRequestNodeContext ctx) {

            AstWriteConfigNodeVisitor visitor = new AstWriteConfigNodeVisitor(elFactory, elContext);
            AstWriteConfigNode writeHttpRequestNode = visitor.visitWriteHttpRequestNode(ctx);
            if (writeHttpRequestNode != null) {
                childInfos().add(writeHttpRequestNode.getRegionInfo());
            }

            return writeHttpRequestNode;
        }

        @Override
        public AstWriteConfigNode visitWriteHttpHeaderNode(WriteHttpHeaderNodeContext ctx) {

            AstWriteConfigNodeVisitor visitor = new AstWriteConfigNodeVisitor(elFactory, elContext);
            AstWriteConfigNode writeHttpHeaderNode = visitor.visitWriteHttpHeaderNode(ctx);
            if (writeHttpHeaderNode != null) {
                childInfos().add(writeHttpHeaderNode.getRegionInfo());
            }

            return writeHttpHeaderNode;
        }

        @Override
        public AstWriteConfigNode visitWriteHttpContentLengthNode(WriteHttpContentLengthNodeContext ctx) {

            AstWriteConfigNodeVisitor visitor = new AstWriteConfigNodeVisitor(elFactory, elContext);
            AstWriteConfigNode writeHttpContentLengthNode = visitor.visitWriteHttpContentLengthNode(ctx);
            if (writeHttpContentLengthNode != null) {
                childInfos().add(writeHttpContentLengthNode.getRegionInfo());
            }

            return writeHttpContentLengthNode;
        }

        @Override
        public AstWriteConfigNode visitWriteHttpHostNode(WriteHttpHostNodeContext ctx) {

            AstWriteConfigNodeVisitor visitor = new AstWriteConfigNodeVisitor(elFactory, elContext);
            AstWriteConfigNode writeHttpHostNode = visitor.visitWriteHttpHostNode(ctx);
            if (writeHttpHostNode != null) {
                childInfos().add(writeHttpHostNode.getRegionInfo());
            }

            return writeHttpHostNode;
        }

        @Override
        public AstWriteConfigNode visitWriteHttpMethodNode(WriteHttpMethodNodeContext ctx) {

            AstWriteConfigNodeVisitor visitor = new AstWriteConfigNodeVisitor(elFactory, elContext);
            AstWriteConfigNode writeHttpMethodNode = visitor.visitWriteHttpMethodNode(ctx);
            if (writeHttpMethodNode != null) {
                childInfos().add(writeHttpMethodNode.getRegionInfo());
            }

            return writeHttpMethodNode;
        }

        @Override
        public AstWriteConfigNode visitWriteHttpParameterNode(WriteHttpParameterNodeContext ctx) {

            AstWriteConfigNodeVisitor visitor = new AstWriteConfigNodeVisitor(elFactory, elContext);
            AstWriteConfigNode writeHttpParameterNode = visitor.visitWriteHttpParameterNode(ctx);
            if (writeHttpParameterNode != null) {
                childInfos().add(writeHttpParameterNode.getRegionInfo());
            }

            return writeHttpParameterNode;
        }

        @Override
        public AstWriteConfigNode visitWriteHttpVersionNode(WriteHttpVersionNodeContext ctx) {

            AstWriteConfigNodeVisitor visitor = new AstWriteConfigNodeVisitor(elFactory, elContext);
            AstWriteConfigNode writeHttpVersionNode = visitor.visitWriteHttpVersionNode(ctx);
            if (writeHttpVersionNode != null) {
                childInfos().add(writeHttpVersionNode.getRegionInfo());
            }

            return writeHttpVersionNode;
        }

        @Override
        public AstWriteConfigNode visitWriteHttpStatusNode(WriteHttpStatusNodeContext ctx) {

            AstWriteConfigNodeVisitor visitor = new AstWriteConfigNodeVisitor(elFactory, elContext);
            AstWriteConfigNode writeHttpStatusNode = visitor.visitWriteHttpStatusNode(ctx);
            if (writeHttpStatusNode != null) {
                childInfos().add(writeHttpStatusNode.getRegionInfo());
            }

            return writeHttpStatusNode;
        }

    }

    private static class AstCloseNodeVisitor extends AstNodeVisitor<AstCloseNode> {

        public AstCloseNodeVisitor(ExpressionFactory elFactory, ExpressionContext elContext) {
            super(elFactory, elContext);
        }

        @Override
        public AstCloseNode visitCloseNode(CloseNodeContext ctx) {
            node = new AstCloseNode();
            node.setRegionInfo(asSequentialRegion(childInfos, ctx));
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
            node.setRegionInfo(asSequentialRegion(childInfos, ctx));
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
            node.setRegionInfo(asSequentialRegion(childInfos, ctx));
            return node;
        }

    }

    private static class AstWriteValueNodeVisitor extends AstNodeVisitor<AstWriteValueNode> {

        public AstWriteValueNodeVisitor(ExpressionFactory elFactory, ExpressionContext elContext) {
            super(elFactory, elContext);
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
            AstValueVisitor visitor = new AstValueVisitor(elFactory, elContext);
            AstValue value = visitor.visit(ctx);
            node.addValue(value);
            childInfos().add(value.getRegionInfo());
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
            node.setRegionInfo(asSequentialRegion(childInfos, ctx));
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
            node.setRegionInfo(asSequentialRegion(childInfos, ctx));
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
            node.setRegionInfo(asSequentialRegion(childInfos, ctx));
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
            node.setRegionInfo(asSequentialRegion(childInfos, ctx));
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
            node.setRegionInfo(asSequentialRegion(childInfos, ctx));
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
            node.setRegionInfo(asSequentialRegion(childInfos, ctx));
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
            node.setRegionInfo(asSequentialRegion(childInfos, ctx));
            return node;
        }

    }

    private static class AstReadValueNodeVisitor extends AstNodeVisitor<AstReadValueNode> {

        public AstReadValueNodeVisitor(ExpressionFactory elFactory, ExpressionContext elContext) {
            super(elFactory, elContext);
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
            AstValueMatcherVisitor visitor = new AstValueMatcherVisitor(elFactory, elContext);
            AstValueMatcher matcher = visitor.visit(ctx);
            node.addMatcher(matcher);
            childInfos().add(matcher.getRegionInfo());
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
            node.setRegionInfo(asSequentialRegion(childInfos, ctx));
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
            node.setRegionInfo(asSequentialRegion(childInfos, ctx));
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
            node.setRegionInfo(asSequentialRegion(childInfos, ctx));
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
            node.setRegionInfo(asSequentialRegion(childInfos, ctx));
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
            node.setRegionInfo(asSequentialRegion(childInfos, ctx));
            node.setBarrierName(ctx.barrier.getText());
            return node;
        }

    }

    private static class AstValueMatcherVisitor extends AstVisitor<AstValueMatcher> {

        public AstValueMatcherVisitor(ExpressionFactory elFactory, ExpressionContext elContext) {
            super(elFactory, elContext);
        }

        @Override
        public AstValueMatcher visitExactTextMatcher(ExactTextMatcherContext ctx) {

            AstExactTextMatcherVisitor visitor = new AstExactTextMatcherVisitor(elFactory, elContext);
            AstExactTextMatcher matcher = visitor.visit(ctx);
            if (matcher != null) {
                childInfos().add(matcher.getRegionInfo());
            }

            return matcher;
        }

        @Override
        public AstExactBytesMatcher visitExactBytesMatcher(ExactBytesMatcherContext ctx) {

            AstExactBytesMatcherVisitor visitor = new AstExactBytesMatcherVisitor(elFactory, elContext);
            AstExactBytesMatcher matcher = visitor.visit(ctx);
            if (matcher != null) {
                childInfos().add(matcher.getRegionInfo());
            }

            return matcher;
        }

        @Override
        public AstRegexMatcher visitRegexMatcher(RegexMatcherContext ctx) {

            AstRegexMatcherVisitor visitor = new AstRegexMatcherVisitor(elFactory, elContext);
            AstRegexMatcher matcher = visitor.visit(ctx);
            if (matcher != null) {
                childInfos().add(matcher.getRegionInfo());
            }

            return matcher;
        }

        @Override
        public AstExpressionMatcher visitExpressionMatcher(ExpressionMatcherContext ctx) {

            AstExpressionMatcherVisitor visitor = new AstExpressionMatcherVisitor(elFactory, elContext);
            AstExpressionMatcher matcher = visitor.visit(ctx);
            if (matcher != null) {
                childInfos().add(matcher.getRegionInfo());
            }

            return matcher;
        }

        @Override
        public AstFixedLengthBytesMatcher visitFixedLengthBytesMatcher(FixedLengthBytesMatcherContext ctx) {

            AstFixedLengthBytesMatcherVisitor visitor = new AstFixedLengthBytesMatcherVisitor(elFactory, elContext);
            AstFixedLengthBytesMatcher matcher = visitor.visit(ctx);
            if (matcher != null) {
                childInfos().add(matcher.getRegionInfo());
            }

            return matcher;
        }

        @Override
        public AstVariableLengthBytesMatcher visitVariableLengthBytesMatcher(VariableLengthBytesMatcherContext ctx) {

            AstVariableLengthBytesMatcherVisitor visitor = new AstVariableLengthBytesMatcherVisitor(elFactory, elContext);
            AstVariableLengthBytesMatcher matcher = visitor.visit(ctx);
            if (matcher != null) {
                childInfos().add(matcher.getRegionInfo());
            }

            return matcher;
        }

    }

    private static class AstExactTextMatcherVisitor extends AstVisitor<AstExactTextMatcher> {

        public AstExactTextMatcherVisitor(ExpressionFactory elFactory, ExpressionContext elContext) {
            super(elFactory, elContext);
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

        public AstExactBytesMatcherVisitor(ExpressionFactory elFactory, ExpressionContext elContext) {
            super(elFactory, elContext);
        }

        @Override
        public AstExactBytesMatcher visitExactBytesMatcher(ExactBytesMatcherContext ctx) {
            if (ctx.bytes != null) {
                byte[] array = parseHexBytes(ctx.bytes.getText());
                AstExactBytesMatcher matcher = new AstExactBytesMatcher(array, elContext);
                matcher.setRegionInfo(asSequentialRegion(childInfos, ctx));
                return matcher;
            } else if (ctx.byteLiteral != null) {
                byte[] array = parseHexBytes(ctx.byteLiteral.getText());
                AstExactBytesMatcher matcher = new AstExactBytesMatcher(array, elContext);
                matcher.setRegionInfo(asSequentialRegion(childInfos, ctx));
                return matcher;
            } else if (ctx.shortLiteral != null) {
                byte[] array = parseHexBytes(ctx.shortLiteral.getText());
                AstExactBytesMatcher matcher = new AstExactBytesMatcher(array, elContext);
                matcher.setRegionInfo(asSequentialRegion(childInfos, ctx));
                return matcher;
            } else if (ctx.longLiteral != null) {
                ByteBuffer buf = ByteBuffer.allocate(Long.SIZE / 8);
                buf.putLong(Long.parseLong(ctx.longLiteral.getText()));
                byte[] array = buf.array();
                AstExactBytesMatcher matcher = new AstExactBytesMatcher(array, elContext);
                matcher.setRegionInfo(asSequentialRegion(childInfos, ctx));
                return matcher;
            } else if (ctx.intLiteral != null) {
                ByteBuffer buf = ByteBuffer.allocate(Integer.SIZE / 8);
                buf.putInt(Integer.parseInt(ctx.intLiteral.getText()));
                byte[] array = buf.array();
                AstExactBytesMatcher matcher = new AstExactBytesMatcher(array, elContext);
                matcher.setRegionInfo(asSequentialRegion(childInfos, ctx));
                return matcher;
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
            String pattern = regex.substring(1, regex.length() - 1);
            AstRegexMatcher matcher = new AstRegexMatcher(NamedGroupPattern.compile(pattern), elContext);
            matcher.setRegionInfo(asSequentialRegion(childInfos, ctx));
            return matcher;
        }

    }

    private static class AstExpressionMatcherVisitor extends AstVisitor<AstExpressionMatcher> {

        public AstExpressionMatcherVisitor(ExpressionFactory elFactory, ExpressionContext elContext) {
            super(elFactory, elContext);
        }

        @Override
        public AstExpressionMatcher visitExpressionMatcher(ExpressionMatcherContext ctx) {
            ValueExpression expression = elFactory.createValueExpression(elContext, ctx.expression.getText(), byte[].class);
            AstExpressionMatcher matcher = new AstExpressionMatcher(expression, elContext);
            matcher.setRegionInfo(asSequentialRegion(childInfos, ctx));
            return matcher;
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
                    String captureName = capture.substring(1, capture.length());
                    AstFixedLengthBytesMatcher matcher =
                            new AstFixedLengthBytesMatcher(parseInt(lastIndex), captureName, elContext);
                    matcher.setRegionInfo(asSequentialRegion(childInfos, ctx));
                    return matcher;
                } else {
                    AstFixedLengthBytesMatcher matcher = new AstFixedLengthBytesMatcher(parseInt(lastIndex));
                    matcher.setRegionInfo(asSequentialRegion(childInfos, ctx));
                    return matcher;
                }
            } else if (ctx.byteCapture != null) {
                String byteCapture = ctx.byteCapture.getText();
                AstByteLengthBytesMatcher matcher = new AstByteLengthBytesMatcher(byteCapture.substring(1), elContext);
                matcher.setRegionInfo(asSequentialRegion(childInfos, ctx));
                return matcher;
            } else if (ctx.shortCapture != null) {
                String shortCapture = ctx.shortCapture.getText();
                AstShortLengthBytesMatcher matcher = new AstShortLengthBytesMatcher(shortCapture.substring(1), elContext);
                matcher.setRegionInfo(asSequentialRegion(childInfos, ctx));
                return matcher;
            } else if (ctx.intCapture != null) {
                String intCapture = ctx.intCapture.getText();
                AstIntLengthBytesMatcher matcher = new AstIntLengthBytesMatcher(intCapture.substring(1), elContext);
                matcher.setRegionInfo(asSequentialRegion(childInfos, ctx));
                return matcher;
            } else if (ctx.longCapture != null) {
                String longCapture = ctx.longCapture.getText();
                AstLongLengthBytesMatcher matcher = new AstLongLengthBytesMatcher(longCapture.substring(1), elContext);
                matcher.setRegionInfo(asSequentialRegion(childInfos, ctx));
                return matcher;
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
                String captureName = capture.substring(1);
                AstVariableLengthBytesMatcher matcher = new AstVariableLengthBytesMatcher(length, captureName, elContext);
                matcher.setRegionInfo(asSequentialRegion(childInfos, ctx));
                return matcher;
            } else {
                AstVariableLengthBytesMatcher matcher = new AstVariableLengthBytesMatcher(length, elContext);
                matcher.setRegionInfo(asSequentialRegion(childInfos, ctx));
                return matcher;
            }
        }
    }

    private static class AstLocationLiteralVisitor extends AstVisitor<AstLocationLiteral> {

        public AstLocationLiteralVisitor(ExpressionFactory elFactory, ExpressionContext elContext) {
            super(elFactory, elContext);
        }

        @Override
        public AstLocationLiteral visitUriValue(UriValueContext ctx) {
            String uriText = ctx.uri.getText();
            URI uri = URI.create(uriText);
            AstLocationLiteral value = new AstLocationLiteral(uri);
            value.setRegionInfo(asSequentialRegion(childInfos, ctx));
            return value;
        }

    }

    private static class AstLocationExpressionVisitor extends AstVisitor<AstLocationExpression> {

        protected AstLocationExpressionVisitor(ExpressionFactory elFactory, ExpressionContext elContext) {
            super(elFactory, elContext);
        }

        @Override
        public AstLocationExpression visitExpressionValue(ExpressionValueContext ctx) {
            ValueExpression expression = elFactory.createValueExpression(elContext, ctx.expression.getText(), URI.class);
            AstLocationExpression value = new AstLocationExpression(expression, elContext);
            value.setRegionInfo(asSequentialRegion(childInfos, ctx));
            return value;
        }
    }

    private static class AstLocationVisitor extends AstVisitor<AstLocation> {

        protected AstLocationVisitor(ExpressionFactory elFactory, ExpressionContext elContext) {
            super(elFactory, elContext);
        }

        @Override
        public AstLocation visitUriValue(UriValueContext ctx) {
            AstLocationLiteralVisitor visitor = new AstLocationLiteralVisitor(elFactory, elContext);
            AstLocationLiteral value = visitor.visit(ctx);

            if (value != null) {
                childInfos().add(value.getRegionInfo());
            }

            return value;
        }

        @Override
        public AstLocation visitExpressionValue(ExpressionValueContext ctx) {
            AstLocationExpressionVisitor visitor = new AstLocationExpressionVisitor(elFactory, elContext);
            AstLocationExpression value = visitor.visit(ctx);
            if (value != null) {
                childInfos().add(value.getRegionInfo());
            }

            return value;
        }
    }


    private static class AstValueVisitor extends AstVisitor<AstValue> {

        private final Class<?> expectedType;

        public AstValueVisitor(ExpressionFactory elFactory, ExpressionContext elContext) {
            this(elFactory, elContext, byte[].class);
        }

        public AstValueVisitor(ExpressionFactory elFactory, ExpressionContext elContext, Class<?> expectedType) {
            super(elFactory, elContext);
            this.expectedType = expectedType;
        }

        @Override
        public AstValue visitLiteralBytes(LiteralBytesContext ctx) {

            AstLiteralBytesValueVisitor visitor = new AstLiteralBytesValueVisitor(elFactory, elContext);
            AstLiteralBytesValue value = visitor.visit(ctx);
            if (value != null) {
                childInfos().add(value.getRegionInfo());
            }

            return value;
        }

        @Override
        public AstValue visitLiteralText(LiteralTextContext ctx) {

            AstLiteralTextValueVisitor visitor = new AstLiteralTextValueVisitor(elFactory, elContext);
            AstLiteralTextValue value = visitor.visit(ctx);
            if (value != null) {
                childInfos().add(value.getRegionInfo());
            }

            return value;
        }

        @Override
        public AstValue visitExpressionValue(ExpressionValueContext ctx) {

            AstExpressionValueVisitor visitor = new AstExpressionValueVisitor(elFactory, elContext, expectedType);
            AstExpressionValue value = visitor.visit(ctx);
            if (value != null) {
                childInfos().add(value.getRegionInfo());
            }

            return value;
        }

    }

    private static class AstLiteralTextValueVisitor extends AstVisitor<AstLiteralTextValue> {

        public AstLiteralTextValueVisitor(ExpressionFactory elFactory, ExpressionContext elContext) {
            super(elFactory, elContext);
        }

        @Override
        public AstLiteralTextValue visitLiteralText(LiteralTextContext ctx) {
            String text = ctx.text.getText();
            String textWithoutQuotes = text.substring(1, text.length() - 1);
            String escapedText = escapeString(textWithoutQuotes);
            AstLiteralTextValue value = new AstLiteralTextValue(escapedText);
            value.setRegionInfo(asSequentialRegion(childInfos, ctx));
            return value;
        }

    }

    private static class AstLiteralBytesValueVisitor extends AstVisitor<AstLiteralBytesValue> {

        public AstLiteralBytesValueVisitor(ExpressionFactory elFactory, ExpressionContext elContext) {
            super(elFactory, elContext);
        }

        @Override
        public AstLiteralBytesValue visitLiteralBytes(LiteralBytesContext ctx) {
            String bytes = ctx.bytes.getText();
            AstLiteralBytesValue value = new AstLiteralBytesValue(parseHexBytes(bytes));
            value.setRegionInfo(asSequentialRegion(childInfos, ctx));
            return value;
        }

    }

    private static class AstExpressionValueVisitor extends AstVisitor<AstExpressionValue> {

        private final Class<?> expectedType;

        public AstExpressionValueVisitor(ExpressionFactory elFactory, ExpressionContext elContext) {
            this(elFactory, elContext, byte[].class);
        }

        public AstExpressionValueVisitor(ExpressionFactory elFactory, ExpressionContext elContext, Class<?> expectedType) {
            super(elFactory, elContext);
            this.expectedType = expectedType;
        }

        @Override
        public AstExpressionValue visitExpressionValue(ExpressionValueContext ctx) {
            ValueExpression expression = elFactory.createValueExpression(elContext, ctx.expression.getText(), expectedType);
            AstExpressionValue value = new AstExpressionValue(expression, elContext);
            value.setRegionInfo(asSequentialRegion(childInfos, ctx));
            return value;
        }

    }

    // HTTP visitors

    private static class AstReadHttpConfigNodeVisitor extends AstNodeVisitor<AstReadConfigNode> {

        public AstReadHttpConfigNodeVisitor(ExpressionFactory elFactory, ExpressionContext elContext) {
            super(elFactory, elContext);
        }

        @Override
        public AstReadConfigNode visitReadHttpMethodNode(ReadHttpMethodNodeContext ctx) {

            AstValueMatcherVisitor visitor = new AstValueMatcherVisitor(elFactory, elContext);
            AstValueMatcher value = visitor.visit(ctx.method);
            childInfos().add(value.getRegionInfo());

            node = new AstReadConfigNode();
            node.setRegionInfo(asSequentialRegion(childInfos, ctx));
            node.setType("method");
            node.setMatcher("name", value);

            return node;
        }

        @Override
        public AstReadConfigNode visitReadHttpHeaderNode(ReadHttpHeaderNodeContext ctx) {

            AstLiteralTextValueVisitor visitor = new AstLiteralTextValueVisitor(elFactory, elContext);
            AstLiteralTextValue value = visitor.visit(ctx.name);
            childInfos().add(value.getRegionInfo());

            node = new AstReadConfigNode();
            node.setType(ctx.HttpMissingKeyword() != null ? "header missing" : "header");
            node.setValue("name", value);
            super.visitReadHttpHeaderNode(ctx);
            node.setRegionInfo(asSequentialRegion(childInfos, ctx));

            return node;
        }

        @Override
        public AstReadConfigNode visitReadHttpParameterNode(ReadHttpParameterNodeContext ctx) {

            AstLiteralTextValueVisitor visitor = new AstLiteralTextValueVisitor(elFactory, elContext);
            AstLiteralTextValue value = visitor.visit(ctx.name);
            childInfos().add(value.getRegionInfo());

            node = new AstReadConfigNode();
            node.setType("parameter");
            node.setValue("name", value);

            super.visitReadHttpParameterNode(ctx);

            node.setRegionInfo(asSequentialRegion(childInfos, ctx));

            return node;
        }

        @Override
        public AstReadConfigNode visitReadHttpStatusNode(ReadHttpStatusNodeContext ctx) {

            AstValueMatcherVisitor codeVisitor = new AstValueMatcherVisitor(elFactory, elContext);
            AstValueMatcherVisitor reasonVisitor = new AstValueMatcherVisitor(elFactory, elContext);
            AstValueMatcher codeMatcher = codeVisitor.visit(ctx.code);
            AstValueMatcher reasonMatcher = reasonVisitor.visit(ctx.reason);

            childInfos().add(codeMatcher.getRegionInfo());
            childInfos().add(reasonMatcher.getRegionInfo());
            node = new AstReadConfigNode();
            node.setRegionInfo(asSequentialRegion(childInfos, ctx));
            node.setType("status");
            node.setMatcher("code", codeMatcher);
            node.setMatcher("reason", reasonMatcher);

            return node;
        }

        @Override
        public AstReadConfigNode visitReadHttpVersionNode(ReadHttpVersionNodeContext ctx) {

            AstValueMatcherVisitor visitor = new AstValueMatcherVisitor(elFactory, elContext);
            AstValueMatcher value = visitor.visit(ctx.version);
            childInfos().add(value.getRegionInfo());

            node = new AstReadConfigNode();
            node.setRegionInfo(asSequentialRegion(childInfos, ctx));
            node.setType("version");
            node.setMatcher("version", value);

            return node;
        }

        @Override
        public AstReadConfigNode visitMatcher(MatcherContext ctx) {

            AstValueMatcherVisitor visitor = new AstValueMatcherVisitor(elFactory, elContext);
            AstValueMatcher matcher = visitor.visit(ctx);

            if (matcher != null) {
                node.addMatcher(matcher);
                childInfos().add(matcher.getRegionInfo());
            }

            return node;
        }

    }

    private static class AstWriteConfigNodeVisitor extends AstNodeVisitor<AstWriteConfigNode> {

        public AstWriteConfigNodeVisitor(ExpressionFactory elFactory, ExpressionContext elContext) {
            super(elFactory, elContext);
        }

        @Override
        public AstWriteConfigNode visitWriteHttpRequestNode(WriteHttpRequestNodeContext ctx) {

            AstValueVisitor visitor = new AstValueVisitor(elFactory, elContext);
            AstValue value = visitor.visit(ctx.form);
            childInfos().add(value.getRegionInfo());

            node = new AstWriteConfigNode();
            node.setRegionInfo(asSequentialRegion(childInfos, ctx));
            node.setType("request");
            node.setValue("form", value);

            return node;
        }

        @Override
        public AstWriteConfigNode visitWriteHttpHeaderNode(WriteHttpHeaderNodeContext ctx) {

            AstValueVisitor visitor = new AstValueVisitor(elFactory, elContext);
            AstValue value = visitor.visit(ctx.name);
            childInfos().add(value.getRegionInfo());

            node = new AstWriteConfigNode();
            node.setRegionInfo(asSequentialRegion(childInfos, ctx));
            node.setType("header");
            node.setName("name", value);

            super.visitWriteHttpHeaderNode(ctx);

            return node;
        }

        @Override
        public AstWriteConfigNode visitWriteHttpContentLengthNode(WriteHttpContentLengthNodeContext ctx) {

            node = new AstWriteConfigNode();
            node.setRegionInfo(asSequentialRegion(childInfos, ctx));
            node.setType("content-length");

            return node;
        }

        @Override
        public AstWriteConfigNode visitWriteHttpHostNode(WriteHttpHostNodeContext ctx) {

            node = new AstWriteConfigNode();
            node.setRegionInfo(asSequentialRegion(childInfos, ctx));
            node.setType("host");

            return node;
        }

        @Override
        public AstWriteConfigNode visitWriteHttpMethodNode(WriteHttpMethodNodeContext ctx) {

            node = new AstWriteConfigNode();
            node.setRegionInfo(asSequentialRegion(childInfos, ctx));
            node.setType("method");

            super.visitWriteHttpMethodNode(ctx);

            return node;
        }

        @Override
        public AstWriteConfigNode visitWriteHttpParameterNode(WriteHttpParameterNodeContext ctx) {

            AstValueVisitor visitor = new AstValueVisitor(elFactory, elContext);
            AstValue value = visitor.visit(ctx.name);
            childInfos().add(value.getRegionInfo());

            node = new AstWriteConfigNode();
            node.setRegionInfo(asSequentialRegion(childInfos, ctx));
            node.setType("parameter");
            node.setName("name", value);

            super.visitWriteHttpParameterNode(ctx);

            return node;
        }

        @Override
        public AstWriteConfigNode visitWriteHttpVersionNode(WriteHttpVersionNodeContext ctx) {

            node = new AstWriteConfigNode();
            node.setType("version");
            node.setRegionInfo(asSequentialRegion(childInfos, ctx));

            super.visitWriteHttpVersionNode(ctx);

            return node;
        }

        @Override
        public AstWriteConfigNode visitWriteHttpStatusNode(WriteHttpStatusNodeContext ctx) {

            AstValueVisitor codeVisitor = new AstValueVisitor(elFactory, elContext);
            AstValueVisitor reasonVisitor = new AstValueVisitor(elFactory, elContext);

            AstValue codeValue = codeVisitor.visit(ctx.code);
            childInfos().add(codeValue.getRegionInfo());

            AstValue reasonValue = reasonVisitor.visit(ctx.reason);
            childInfos().add(reasonValue.getRegionInfo());

            node = new AstWriteConfigNode();
            node.setRegionInfo(asSequentialRegion(childInfos, ctx));
            node.setType("status");
            node.setValue("code", codeValue);
            node.setValue("reason", reasonValue);

            return node;
        }

        @Override
        public AstWriteConfigNode visitWriteValue(WriteValueContext ctx) {

            AstValueVisitor visitor = new AstValueVisitor(elFactory, elContext);
            AstValue value = visitor.visit(ctx);

            if (value != null) {
                node.addValue(value);
                childInfos().add(value.getRegionInfo());
            }

            return node;
        }
    }

    private static class AstWriteFlushNodeVisitor extends AstNodeVisitor<AstWriteFlushNode> {

        public AstWriteFlushNodeVisitor(ExpressionFactory elFactory, ExpressionContext elContext) {
            super(elFactory, elContext);
        }

        @Override
        public AstWriteFlushNode visitWriteFlushNode(WriteFlushNodeContext ctx) {

            node = new AstWriteFlushNode();
            node.setRegionInfo(asSequentialRegion(childInfos, ctx));

            return node;
        }

    }

    private static class AstReadClosedNodeVisitor extends AstNodeVisitor<AstReadClosedNode> {

        public AstReadClosedNodeVisitor(ExpressionFactory elFactory, ExpressionContext elContext) {
            super(elFactory, elContext);
        }

        @Override
        public AstReadClosedNode visitReadClosedNode(ReadClosedNodeContext ctx) {

            node = new AstReadClosedNode();
            node.setRegionInfo(asSequentialRegion(childInfos, ctx));

            return node;
        }

    }

    private static class AstWriteCloseNodeVisitor extends AstNodeVisitor<AstWriteCloseNode> {

        public AstWriteCloseNodeVisitor(ExpressionFactory elFactory, ExpressionContext elContext) {
            super(elFactory, elContext);
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

}
