/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package com.kaazing.robot.behavior.visitor;

import static java.lang.String.format;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import com.kaazing.robot.lang.LocationInfo;
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
import com.kaazing.robot.lang.ast.AstConnectNode;
import com.kaazing.robot.lang.ast.AstConnectedNode;
import com.kaazing.robot.lang.ast.AstDisconnectNode;
import com.kaazing.robot.lang.ast.AstDisconnectedNode;
import com.kaazing.robot.lang.ast.AstEndOfHttpHeadersNode;
import com.kaazing.robot.lang.ast.AstNode;
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

public class ValidateBarriersVisitor implements AstNode.Visitor<Void, ValidateBarriersVisitor.State> {

    public static class State {
        Map<String, AstBarrierNode> awaitersByName = new HashMap<String, AstBarrierNode>();
        Collection<String> notifierNames = new HashSet<String>();
    }

    @Override
    public Void visit(AstScriptNode node, State state) throws Exception {

        for (AstStreamNode stream : node.getStreams()) {
            stream.accept(this, state);
        }

        Collection<String> awaiterNames = state.awaitersByName.keySet();
        awaiterNames.removeAll(state.notifierNames);
        if (!awaiterNames.isEmpty()) {
            String awaiterName = awaiterNames.iterator().next();
            AstBarrierNode awaiter = state.awaitersByName.get(awaiterName);
            LocationInfo locationInfo = awaiter.getLocationInfo();
            String lineInfo = String.format("line %d:%d", locationInfo.line, locationInfo.column);

            throw new IllegalStateException(format("%s : barrier name '%s' not triggered by any 'notify' directives", lineInfo,
                    awaiterName));
        }

        return null;
    }

    @Override
    public Void visit(AstAcceptNode acceptNode, State state) throws Exception {

        for (AstStreamableNode streamable : acceptNode.getStreamables()) {
            streamable.accept(this, state);
        }

        for (AstStreamNode acceptedStream : acceptNode.getAcceptables()) {
            acceptedStream.accept(this, state);
        }

        return null;
    }

    @Override
    public Void visit(AstAcceptableNode acceptableNode, State state) throws Exception {

        for (AstStreamableNode streamable : acceptableNode.getStreamables()) {
            streamable.accept(this, state);
        }

        return null;
    }

    @Override
    public Void visit(AstConnectNode connectNode, State state) throws Exception {

        for (AstStreamableNode streamable : connectNode.getStreamables()) {
            streamable.accept(this, state);
        }

        return null;
    }

    @Override
    public Void visit(AstReadAwaitNode node, State state) throws Exception {

        Map<String, AstBarrierNode> awaitersByName = state.awaitersByName;
        String barrierName = node.getBarrierName();
        AstBarrierNode barrier = awaitersByName.get(barrierName);
        if (barrier == null) {
            awaitersByName.put(barrierName, node);
        }

        return null;
    }

    @Override
    public Void visit(AstWriteAwaitNode node, State state) throws Exception {

        Map<String, AstBarrierNode> awaitersByName = state.awaitersByName;
        String barrierName = node.getBarrierName();
        AstBarrierNode barrier = awaitersByName.get(barrierName);
        if (barrier == null) {
            awaitersByName.put(barrierName, node);
        }

        return null;
    }

    @Override
    public Void visit(AstReadNotifyNode node, State state) throws Exception {

        state.notifierNames.add(node.getBarrierName());
        return null;
    }

    @Override
    public Void visit(AstWriteNotifyNode node, State state) throws Exception {

        state.notifierNames.add(node.getBarrierName());
        return null;
    }

    @Override
    public Void visit(AstWriteValueNode node, State state) throws Exception {

        // NOOP
        return null;
    }

    @Override
    public Void visit(AstDisconnectNode node, State state) throws Exception {

        // NOOP
        return null;
    }

    @Override
    public Void visit(AstUnbindNode node, State state) throws Exception {

        // NOOP
        return null;
    }

    @Override
    public Void visit(AstCloseNode node, State state) throws Exception {

        // NOOP
        return null;
    }

    @Override
    public Void visit(AstChildOpenedNode node, State state) throws Exception {

        // NOOP
        return null;
    }

    @Override
    public Void visit(AstChildClosedNode node, State parameter) throws Exception {

        // NOOP
        return null;
    }

    @Override
    public Void visit(AstOpenedNode node, State state) throws Exception {

        // NOOP
        return null;
    }

    @Override
    public Void visit(AstBoundNode node, State state) throws Exception {

        // NOOP
        return null;
    }

    @Override
    public Void visit(AstConnectedNode node, State state) throws Exception {

        // NOOP
        return null;
    }

    @Override
    public Void visit(AstReadValueNode node, State state) throws Exception {

        // NOOP
        return null;
    }

    @Override
    public Void visit(AstDisconnectedNode node, State state) throws Exception {

        // NOOP
        return null;
    }

    @Override
    public Void visit(AstUnboundNode node, State state) throws Exception {

        // NOOP
        return null;
    }

    @Override
    public Void visit(AstClosedNode node, State state) throws Exception {

        // NOOP
        return null;
    }

    @Override
    public Void visit(AstReadHttpHeaderNode node, State parameter) throws Exception {
        // NOOP
        return null;
    }

    @Override
    public Void visit(AstWriteHttpHeaderNode node, State parameter) throws Exception {
        // NOOP
        return null;
    }

    @Override
    public Void visit(AstWriteHttpContentLengthNode node, State parameter) throws Exception {
        // NOOP
        return null;
    }

    @Override
    public Void visit(AstReadHttpMethodNode node, State parameter) throws Exception {
        // NOOP
        return null;
    }

    @Override
    public Void visit(AstWriteHttpMethodNode node, State parameter) throws Exception {
        // NOOP
        return null;
    }

    @Override
    public Void visit(AstReadHttpParameterNode node, State parameter) throws Exception {
        // NOOP
        return null;
    }

    @Override
    public Void visit(AstWriteHttpParameterNode node, State parameter) throws Exception {
        // NOOP
        return null;
    }

    @Override
    public Void visit(AstReadHttpVersionNode node, State parameter) throws Exception {
        // NOOP
        return null;
    }

    @Override
    public Void visit(AstWriteHttpVersionNode node, State parameter) throws Exception {
        // NOOP
        return null;
    }

    @Override
    public Void visit(AstReadHttpStatusNode node, State parameter) throws Exception {
        // NOOP
        return null;
    }

    @Override
    public Void visit(AstWriteHttpStatusNode node, State parameter) throws Exception {
        // NOOP
        return null;
    }

    @Override
    public Void visit(AstCloseHttpRequestNode node, State parameter) throws Exception {
        // NOOP
        return null;
    }

    @Override
    public Void visit(AstCloseHttpResponseNode node, State parameter) throws Exception {
        // NOOP
        return null;
    }

    @Override
    public Void visit(AstEndOfHttpHeadersNode node, State parameter) throws Exception {
        // NOOP
        return null;
    }
}
