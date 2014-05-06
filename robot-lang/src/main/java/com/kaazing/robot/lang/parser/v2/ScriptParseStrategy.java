/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package com.kaazing.robot.lang.parser.v2;

import java.util.List;

import org.antlr.runtime.RecognitionException;

import com.kaazing.robot.lang.ast.AstAcceptNode;
import com.kaazing.robot.lang.ast.AstAcceptableNode;
import com.kaazing.robot.lang.ast.AstBarrierNode;
import com.kaazing.robot.lang.ast.AstBoundNode;
import com.kaazing.robot.lang.ast.AstChildClosedNode;
import com.kaazing.robot.lang.ast.AstChildOpenedNode;
import com.kaazing.robot.lang.ast.AstCloseHttpRequestNode;
import com.kaazing.robot.lang.ast.AstCloseHttpResponseNode;
import com.kaazing.robot.lang.ast.AstCloseNode;
import com.kaazing.robot.lang.ast.AstClosedNode;
import com.kaazing.robot.lang.ast.AstCommandNode;
import com.kaazing.robot.lang.ast.AstConnectNode;
import com.kaazing.robot.lang.ast.AstConnectedNode;
import com.kaazing.robot.lang.ast.AstDisconnectNode;
import com.kaazing.robot.lang.ast.AstDisconnectedNode;
import com.kaazing.robot.lang.ast.AstEventNode;
import com.kaazing.robot.lang.ast.AstOpenedNode;
import com.kaazing.robot.lang.ast.AstReadAwaitNode;
import com.kaazing.robot.lang.ast.AstReadHttpHeaderNode;
import com.kaazing.robot.lang.ast.AstReadHttpMethodNode;
import com.kaazing.robot.lang.ast.AstReadHttpParameterNode;
import com.kaazing.robot.lang.ast.AstReadHttpStatusNode;
import com.kaazing.robot.lang.ast.AstReadHttpVersionNode;
import com.kaazing.robot.lang.ast.AstReadNotifyNode;
import com.kaazing.robot.lang.ast.AstReadValueNode;
import com.kaazing.robot.lang.ast.AstScriptNode;
import com.kaazing.robot.lang.ast.AstStreamNode;
import com.kaazing.robot.lang.ast.AstStreamableNode;
import com.kaazing.robot.lang.ast.AstUnbindNode;
import com.kaazing.robot.lang.ast.AstUnboundNode;
import com.kaazing.robot.lang.ast.AstWriteAwaitNode;
import com.kaazing.robot.lang.ast.AstWriteHttpContentLengthNode;
import com.kaazing.robot.lang.ast.AstWriteHttpHeaderNode;
import com.kaazing.robot.lang.ast.AstWriteHttpMethodNode;
import com.kaazing.robot.lang.ast.AstWriteHttpParameterNode;
import com.kaazing.robot.lang.ast.AstWriteHttpStatusNode;
import com.kaazing.robot.lang.ast.AstWriteHttpVersionNode;
import com.kaazing.robot.lang.ast.AstWriteNotifyNode;
import com.kaazing.robot.lang.ast.AstWriteValueNode;
import com.kaazing.robot.lang.ast.matcher.AstExactBytesMatcher;
import com.kaazing.robot.lang.ast.matcher.AstExactTextMatcher;
import com.kaazing.robot.lang.ast.matcher.AstExpressionMatcher;
import com.kaazing.robot.lang.ast.matcher.AstFixedLengthBytesMatcher;
import com.kaazing.robot.lang.ast.matcher.AstRegexMatcher;
import com.kaazing.robot.lang.ast.matcher.AstValueMatcher;
import com.kaazing.robot.lang.ast.matcher.AstVariableLengthBytesMatcher;
import com.kaazing.robot.lang.ast.value.AstExpressionValue;
import com.kaazing.robot.lang.ast.value.AstLiteralBytesValue;
import com.kaazing.robot.lang.ast.value.AstLiteralTextValue;
import com.kaazing.robot.lang.ast.value.AstValue;

abstract class ScriptParseStrategy<T> {

    public static final ScriptParseStrategy<AstScriptNode> SCRIPT = new ScriptParseStrategy<AstScriptNode>() {
        @Override
        public AstScriptNode parse(RobotParser parser) throws RecognitionException {

            return parser.scriptNode();
        }
    };

    public static final ScriptParseStrategy<AstStreamNode> STREAM = new ScriptParseStrategy<AstStreamNode>() {
        @Override
        public AstStreamNode parse(RobotParser parser) throws RecognitionException {

            return parser.streamNode();
        }
    };

    public static final ScriptParseStrategy<AstStreamableNode> STREAMABLE = new ScriptParseStrategy<AstStreamableNode>() {
        @Override
        public AstStreamableNode parse(RobotParser parser) throws RecognitionException {

            return parser.streamableNode();
        }
    };

    public static final ScriptParseStrategy<AstEventNode> EVENT = new ScriptParseStrategy<AstEventNode>() {
        @Override
        public AstEventNode parse(RobotParser parser) throws RecognitionException {

            return parser.eventNode();
        }
    };

    public static final ScriptParseStrategy<AstCommandNode> COMMAND = new ScriptParseStrategy<AstCommandNode>() {
        @Override
        public AstCommandNode parse(RobotParser parser) throws RecognitionException {

            return parser.commandNode();
        }
    };

    public static final ScriptParseStrategy<AstBarrierNode> BARRIER = new ScriptParseStrategy<AstBarrierNode>() {
        @Override
        public AstBarrierNode parse(RobotParser parser) throws RecognitionException {

            return parser.barrierNode();
        }
    };

    public static final ScriptParseStrategy<AstStreamableNode> SERVER_STREAMABLE = new ScriptParseStrategy<AstStreamableNode>() {
        @Override
        public AstStreamableNode parse(RobotParser parser) throws RecognitionException {

            return parser.serverStreamableNode();
        }
    };

    public static final ScriptParseStrategy<AstEventNode> SERVER_EVENT = new ScriptParseStrategy<AstEventNode>() {
        @Override
        public AstEventNode parse(RobotParser parser) throws RecognitionException {

            return parser.serverEventNode();
        }
    };

    public static final ScriptParseStrategy<AstCommandNode> SERVER_COMMAND = new ScriptParseStrategy<AstCommandNode>() {
        @Override
        public AstCommandNode parse(RobotParser parser) throws RecognitionException {

            return parser.serverCommandNode();
        }
    };

    public static final ScriptParseStrategy<AstAcceptNode> ACCEPT = new ScriptParseStrategy<AstAcceptNode>() {
        @Override
        public AstAcceptNode parse(RobotParser parser) throws RecognitionException {

            return parser.acceptNode();
        }
    };

    public static final ScriptParseStrategy<AstAcceptableNode> ACCEPTABLE = new ScriptParseStrategy<AstAcceptableNode>() {
        @Override
        public AstAcceptableNode parse(RobotParser parser) throws RecognitionException {

            return parser.acceptableNode();
        }
    };

    public static final ScriptParseStrategy<AstConnectNode> CONNECT = new ScriptParseStrategy<AstConnectNode>() {
        @Override
        public AstConnectNode parse(RobotParser parser) throws RecognitionException {

            return parser.connectNode();
        }
    };

    public static final ScriptParseStrategy<AstCloseNode> CLOSE = new ScriptParseStrategy<AstCloseNode>() {
        @Override
        public AstCloseNode parse(RobotParser parser) throws RecognitionException {

            return parser.closeNode();
        }
    };

    public static final ScriptParseStrategy<AstDisconnectNode> DISCONNECT = new ScriptParseStrategy<AstDisconnectNode>() {
        @Override
        public AstDisconnectNode parse(RobotParser parser) throws RecognitionException {

            return parser.disconnectNode();
        }
    };

    public static final ScriptParseStrategy<AstUnbindNode> UNBIND = new ScriptParseStrategy<AstUnbindNode>() {
        @Override
        public AstUnbindNode parse(RobotParser parser) throws RecognitionException {

            return parser.unbindNode();
        }
    };

    public static final ScriptParseStrategy<AstWriteValueNode> WRITE = new ScriptParseStrategy<AstWriteValueNode>() {
        @Override
        public AstWriteValueNode parse(RobotParser parser) throws RecognitionException {

            return parser.writeNode();
        }
    };

    public static final ScriptParseStrategy<AstChildOpenedNode> CHILD_OPENED = new ScriptParseStrategy<AstChildOpenedNode>() {
        @Override
        public AstChildOpenedNode parse(RobotParser parser) throws RecognitionException {

            return parser.childOpenedNode();
        }
    };

    public static final ScriptParseStrategy<AstChildClosedNode> CHILD_CLOSED = new ScriptParseStrategy<AstChildClosedNode>() {
        @Override
        public AstChildClosedNode parse(RobotParser parser) throws RecognitionException {

            return parser.childClosedNode();
        }
    };

    public static final ScriptParseStrategy<AstBoundNode> BOUND = new ScriptParseStrategy<AstBoundNode>() {
        @Override
        public AstBoundNode parse(RobotParser parser) throws RecognitionException {

            return parser.boundNode();
        }
    };

    public static final ScriptParseStrategy<AstClosedNode> CLOSED = new ScriptParseStrategy<AstClosedNode>() {
        @Override
        public AstClosedNode parse(RobotParser parser) throws RecognitionException {

            return parser.closedNode();
        }
    };

    public static final ScriptParseStrategy<AstConnectedNode> CONNECTED = new ScriptParseStrategy<AstConnectedNode>() {
        @Override
        public AstConnectedNode parse(RobotParser parser) throws RecognitionException {

            return parser.connectedNode();
        }
    };

    public static final ScriptParseStrategy<AstDisconnectedNode> DISCONNECTED = new ScriptParseStrategy<AstDisconnectedNode>() {
        @Override
        public AstDisconnectedNode parse(RobotParser parser) throws RecognitionException {

            return parser.disconnectedNode();
        }
    };

    public static final ScriptParseStrategy<AstOpenedNode> OPENED = new ScriptParseStrategy<AstOpenedNode>() {
        @Override
        public AstOpenedNode parse(RobotParser parser) throws RecognitionException {

            return parser.openedNode();
        }
    };

    public static final ScriptParseStrategy<AstReadValueNode> READ = new ScriptParseStrategy<AstReadValueNode>() {
        @Override
        public AstReadValueNode parse(RobotParser parser) throws RecognitionException {

            return parser.readNode();
        }
    };

    public static final ScriptParseStrategy<AstReadHttpHeaderNode> READ_HTTP_HEADER =
            new ScriptParseStrategy<AstReadHttpHeaderNode>() {
        @Override
        public AstReadHttpHeaderNode parse(RobotParser parser) throws RecognitionException {

            return parser.readHttpHeaderNode();
        }
    };

    public static final ScriptParseStrategy<AstWriteHttpHeaderNode> WRITE_HTTP_HEADER =
            new ScriptParseStrategy<AstWriteHttpHeaderNode>() {
        @Override
        public AstWriteHttpHeaderNode parse(RobotParser parser) throws RecognitionException {

            return parser.writeHttpHeaderNode();
        }
    };

    public static final ScriptParseStrategy<AstWriteHttpContentLengthNode> WRITE_HTTP_CONTENT_LENGTH =
            new ScriptParseStrategy<AstWriteHttpContentLengthNode>() {
        @Override
        public AstWriteHttpContentLengthNode parse(RobotParser parser) throws RecognitionException {

            return parser.writeHttpContentLengthNode();
        }
    };

    public static final ScriptParseStrategy<AstReadHttpMethodNode> READ_HTTP_METHOD =
            new ScriptParseStrategy<AstReadHttpMethodNode>() {
        @Override
        public AstReadHttpMethodNode parse(RobotParser parser) throws RecognitionException {

            return parser.readHttpMethodNode();
        }
    };

    public static final ScriptParseStrategy<AstWriteHttpMethodNode> WRITE_HTTP_METHOD =
            new ScriptParseStrategy<AstWriteHttpMethodNode>() {
        @Override
        public AstWriteHttpMethodNode parse(RobotParser parser) throws RecognitionException {

            return parser.writeHttpMethodNode();
        }
    };

    public static final ScriptParseStrategy<AstReadHttpParameterNode> READ_HTTP_PARAMETER =
            new ScriptParseStrategy<AstReadHttpParameterNode>() {
        @Override
        public AstReadHttpParameterNode parse(RobotParser parser) throws RecognitionException {

            return parser.readHttpParameterNode();
        }
    };

    public static final ScriptParseStrategy<AstWriteHttpParameterNode> WRITE_HTTP_PARAMETER =
            new ScriptParseStrategy<AstWriteHttpParameterNode>() {
        @Override
        public AstWriteHttpParameterNode parse(RobotParser parser) throws RecognitionException {

            return parser.writeHttpParameterNode();
        }
    };

    public static final ScriptParseStrategy<AstReadHttpVersionNode> READ_HTTP_VERSION =
            new ScriptParseStrategy<AstReadHttpVersionNode>() {
        @Override
        public AstReadHttpVersionNode parse(RobotParser parser) throws RecognitionException {

            return parser.readHttpVersionNode();
        }
    };

    public static final ScriptParseStrategy<AstWriteHttpVersionNode> WRITE_HTTP_VERSION =
            new ScriptParseStrategy<AstWriteHttpVersionNode>() {
        @Override
        public AstWriteHttpVersionNode parse(RobotParser parser) throws RecognitionException {

            return parser.writeHttpVersionNode();
        }
    };

    public static final ScriptParseStrategy<AstReadHttpStatusNode> READ_HTTP_STATUS =
            new ScriptParseStrategy<AstReadHttpStatusNode>() {
        @Override
        public AstReadHttpStatusNode parse(RobotParser parser) throws RecognitionException {

            return parser.readHttpStatusNode();
        }
    };

    public static final ScriptParseStrategy<AstWriteHttpStatusNode> WRITE_HTTP_STATUS =
            new ScriptParseStrategy<AstWriteHttpStatusNode>() {
        @Override
        public AstWriteHttpStatusNode parse(RobotParser parser) throws RecognitionException {

            return parser.writeHttpStatusNode();
        }
    };

    public static final ScriptParseStrategy<AstCloseHttpRequestNode> CLOSE_HTTP_REQUEST =
            new ScriptParseStrategy<AstCloseHttpRequestNode>() {
        @Override
        public AstCloseHttpRequestNode parse(RobotParser parser) throws RecognitionException {

            return parser.closeHttpRequestNode();
        }
    };

    public static final ScriptParseStrategy<AstCloseHttpResponseNode> CLOSE_HTTP_RESPONSE =
            new ScriptParseStrategy<AstCloseHttpResponseNode>() {
        @Override
        public AstCloseHttpResponseNode parse(RobotParser parser) throws RecognitionException {

            return parser.closeHttpResponseNode();
        }
    };

    public static final ScriptParseStrategy<AstUnboundNode> UNBOUND = new ScriptParseStrategy<AstUnboundNode>() {
        @Override
        public AstUnboundNode parse(RobotParser parser) throws RecognitionException {

            return parser.unboundNode();
        }
    };

    public static final ScriptParseStrategy<AstReadAwaitNode> READ_AWAIT = new ScriptParseStrategy<AstReadAwaitNode>() {
        @Override
        public AstReadAwaitNode parse(RobotParser parser) throws RecognitionException {

            return parser.readAwaitNode();
        }
    };

    public static final ScriptParseStrategy<AstReadNotifyNode> READ_NOTIFY = new ScriptParseStrategy<AstReadNotifyNode>() {
        @Override
        public AstReadNotifyNode parse(RobotParser parser) throws RecognitionException {

            return parser.readNotifyNode();
        }
    };

    public static final ScriptParseStrategy<AstWriteAwaitNode> WRITE_AWAIT = new ScriptParseStrategy<AstWriteAwaitNode>() {
        @Override
        public AstWriteAwaitNode parse(RobotParser parser) throws RecognitionException {

            return parser.writeAwaitNode();
        }
    };

    public static final ScriptParseStrategy<AstWriteNotifyNode> WRITE_NOTIFY = new ScriptParseStrategy<AstWriteNotifyNode>() {
        @Override
        public AstWriteNotifyNode parse(RobotParser parser) throws RecognitionException {

            return parser.writeNotifyNode();
        }
    };

    public static final ScriptParseStrategy<AstValueMatcher> MATCHER = new ScriptParseStrategy<AstValueMatcher>() {
        @Override
        public AstValueMatcher parse(RobotParser parser) throws RecognitionException {

            return parser.matcher();
        }
    };

    public static final ScriptParseStrategy<AstExactTextMatcher> EXACT_TEXT_MATCHER =
            new ScriptParseStrategy<AstExactTextMatcher>() {
                @Override
                public AstExactTextMatcher parse(RobotParser parser) throws RecognitionException {

                    return parser.exactTextMatcher();
                }
            };

    public static final ScriptParseStrategy<AstExactBytesMatcher> EXACT_BYTES_MATCHER =
            new ScriptParseStrategy<AstExactBytesMatcher>() {
                @Override
                public AstExactBytesMatcher parse(RobotParser parser) throws RecognitionException {

                    return parser.exactBytesMatcher();
                }
            };

    public static final ScriptParseStrategy<AstRegexMatcher> REGEX_MATCHER = new ScriptParseStrategy<AstRegexMatcher>() {
        @Override
        public AstRegexMatcher parse(RobotParser parser) throws RecognitionException {

            return parser.regexMatcher();
        }
    };

    public static final ScriptParseStrategy<AstExpressionMatcher> EXPRESSION_MATCHER =
            new ScriptParseStrategy<AstExpressionMatcher>() {
                @Override
                public AstExpressionMatcher parse(RobotParser parser) throws RecognitionException {

                    return parser.expressionMatcher();
                }
            };

    public static final ScriptParseStrategy<AstFixedLengthBytesMatcher> FIXED_LENGTH_BYTES_MATCHER =
            new ScriptParseStrategy<AstFixedLengthBytesMatcher>() {
                @Override
                public AstFixedLengthBytesMatcher parse(RobotParser parser) throws RecognitionException {

                    return parser.fixedLengthBytesMatcher();
                }
            };

    public static final ScriptParseStrategy<AstVariableLengthBytesMatcher> VARIABLE_LENGTH_BYTES_MATCHER =
            new ScriptParseStrategy<AstVariableLengthBytesMatcher>() {
                @Override
                public AstVariableLengthBytesMatcher parse(RobotParser parser) throws RecognitionException {

                    return parser.variableLengthBytesMatcher();
                }
            };

    public static final ScriptParseStrategy<AstValue> VALUE = new ScriptParseStrategy<AstValue>() {
        @Override
        public AstValue parse(RobotParser parser) throws RecognitionException {

            return parser.writeValue();
        }
    };

    public static final ScriptParseStrategy<AstLiteralTextValue> LITERAL_TEXT_VALUE =
            new ScriptParseStrategy<AstLiteralTextValue>() {
                @Override
                public AstLiteralTextValue parse(RobotParser parser) throws RecognitionException {

                    return parser.literalText();
                }
            };

    public static final ScriptParseStrategy<AstLiteralBytesValue> LITERAL_BYTES_VALUE =
            new ScriptParseStrategy<AstLiteralBytesValue>() {
                @Override
                public AstLiteralBytesValue parse(RobotParser parser) throws RecognitionException {

                    return parser.literalBytes();
                }
            };

    public static final ScriptParseStrategy<AstExpressionValue> EXPRESSION_VALUE =
            new ScriptParseStrategy<AstExpressionValue>() {
                @Override
                public AstExpressionValue parse(RobotParser parser) throws RecognitionException {

                    return parser.expressionValue();
                }
            };

    public static final ScriptParseStrategy<List<AstValueMatcher>> MATCHER_LIST =
            new ScriptParseStrategy<List<AstValueMatcher>>() {
                @Override
                public List<AstValueMatcher> parse(RobotParser parser) throws RecognitionException {
                    return parser.matcherList();
                }
    };

    public static final ScriptParseStrategy<List<AstValue>> WRITE_VALUE_LIST = new ScriptParseStrategy<List<AstValue>>() {
        @Override
        public List<AstValue> parse(RobotParser parser) throws RecognitionException {
            return parser.writeValueList();
        }
    };

    public abstract T parse(RobotParser parser) throws RecognitionException;
}
