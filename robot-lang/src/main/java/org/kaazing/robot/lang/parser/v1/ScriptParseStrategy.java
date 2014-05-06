/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.lang.parser.v1;

import org.antlr.runtime.RecognitionException;

import org.kaazing.robot.lang.ast.AstAcceptNode;
import org.kaazing.robot.lang.ast.AstAcceptableNode;
import org.kaazing.robot.lang.ast.AstBarrierNode;
import org.kaazing.robot.lang.ast.AstBoundNode;
import org.kaazing.robot.lang.ast.AstChildClosedNode;
import org.kaazing.robot.lang.ast.AstChildOpenedNode;
import org.kaazing.robot.lang.ast.AstCloseNode;
import org.kaazing.robot.lang.ast.AstClosedNode;
import org.kaazing.robot.lang.ast.AstCommandNode;
import org.kaazing.robot.lang.ast.AstConnectNode;
import org.kaazing.robot.lang.ast.AstConnectedNode;
import org.kaazing.robot.lang.ast.AstDisconnectNode;
import org.kaazing.robot.lang.ast.AstDisconnectedNode;
import org.kaazing.robot.lang.ast.AstEventNode;
import org.kaazing.robot.lang.ast.AstOpenedNode;
import org.kaazing.robot.lang.ast.AstReadAwaitNode;
import org.kaazing.robot.lang.ast.AstReadNotifyNode;
import org.kaazing.robot.lang.ast.AstReadValueNode;
import org.kaazing.robot.lang.ast.AstScriptNode;
import org.kaazing.robot.lang.ast.AstStreamNode;
import org.kaazing.robot.lang.ast.AstStreamableNode;
import org.kaazing.robot.lang.ast.AstUnbindNode;
import org.kaazing.robot.lang.ast.AstUnboundNode;
import org.kaazing.robot.lang.ast.AstWriteAwaitNode;
import org.kaazing.robot.lang.ast.AstWriteNotifyNode;
import org.kaazing.robot.lang.ast.AstWriteValueNode;
import org.kaazing.robot.lang.ast.matcher.AstExactBytesMatcher;
import org.kaazing.robot.lang.ast.matcher.AstExactTextMatcher;
import org.kaazing.robot.lang.ast.matcher.AstExpressionMatcher;
import org.kaazing.robot.lang.ast.matcher.AstFixedLengthBytesMatcher;
import org.kaazing.robot.lang.ast.matcher.AstRegexMatcher;
import org.kaazing.robot.lang.ast.matcher.AstValueMatcher;
import org.kaazing.robot.lang.ast.matcher.AstVariableLengthBytesMatcher;
import org.kaazing.robot.lang.ast.value.AstExpressionValue;
import org.kaazing.robot.lang.ast.value.AstLiteralBytesValue;
import org.kaazing.robot.lang.ast.value.AstLiteralTextValue;
import org.kaazing.robot.lang.ast.value.AstValue;

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

            return parser.value();
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

    public abstract T parse(RobotParser parser) throws RecognitionException;
}
