/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package com.kaazing.robot.behavior.visitor;

import static com.kaazing.robot.lang.ast.util.AstUtil.equivalent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.kaazing.robot.lang.LocationInfo;
import com.kaazing.robot.lang.ast.AstAcceptNode;
import com.kaazing.robot.lang.ast.AstAcceptableNode;
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
import com.kaazing.robot.lang.ast.AstNodeException;
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
import com.kaazing.robot.behavior.visitor.AssociateStreamsVisitor.State;

public class AssociateStreamsVisitor implements AstNode.Visitor<AstScriptNode, State> {

    public static enum ReadWriteState {
        NONE, READ, WRITE
    }

    public static final class State {
        private List<AstStreamNode> streams;
        private List<AstStreamableNode> streamables;
        private Map<String, AstAcceptNode> accepts = new HashMap<String, AstAcceptNode>();
        private String implicitAcceptName;
        private int implicitAcceptCount;
    }

    @Override
    public AstScriptNode visit(AstScriptNode script, State state) throws Exception {

        AstScriptNode newScript = new AstScriptNode();
        newScript.setLocationInfo(script.getLocationInfo());

        state.streams = newScript.getStreams();

        for (AstStreamNode stream : script.getStreams()) {
            stream.accept(this, state);
        }

        // remove all associated streams from the main script
        // But this appears to be a NOOP. Acceptables are not added as streams!
        for (AstAcceptNode accept : state.accepts.values()) {
            newScript.getStreams().removeAll(accept.getAcceptables());
        }

        return newScript;
    }

    @Override
    public AstScriptNode visit(AstAcceptNode acceptNode, State state) throws Exception {

        AstAcceptNode newAcceptNode = new AstAcceptNode();
        LocationInfo location = acceptNode.getLocationInfo();
        newAcceptNode.setLocationInfo(location);
        newAcceptNode.setLocation(acceptNode.getLocation());

        String acceptName = acceptNode.getAcceptName();
        String newAcceptName = acceptName != null ? acceptName : String.format("~accept~%d", ++state.implicitAcceptCount);

        state.accepts.put(newAcceptName, newAcceptNode);
        state.implicitAcceptName = newAcceptName;

        state.streamables = newAcceptNode.getStreamables();

        for (AstStreamableNode streamable : acceptNode.getStreamables()) {
            streamable.accept(this, state);
            if (streamable.getLocationInfo() != null) {
                location = streamable.getLocationInfo();
            }
        }

        for (AstAcceptableNode acceptable : acceptNode.getAcceptables()) {
            assert equivalent(acceptName, acceptable.getAcceptName());
            acceptable.accept(this, state);
            if (acceptable.getLocationInfo() != null) {
                location = acceptable.getLocationInfo();
            }
        }

        newAcceptNode.setEndLocation(location);

        state.streams.add(newAcceptNode);

        return null;
    }

    @Override
    public AstScriptNode visit(AstAcceptableNode acceptableNode, State state) throws Exception {

        AstAcceptableNode newAcceptableNode = new AstAcceptableNode();
        newAcceptableNode.setLocationInfo(acceptableNode.getLocationInfo());

        String acceptName = acceptableNode.getAcceptName();
        if (acceptName == null) {
            acceptName = state.implicitAcceptName;
        }

        AstAcceptNode acceptNode = state.accepts.get(acceptName);
        if (acceptNode == null) {
            LocationInfo locationInfo = acceptableNode.getLocationInfo();
            throw new AstNodeException("Accept not found for accepted").initLocationInfo(locationInfo);
        }

        LocationInfo endLocation = newAcceptableNode.getLocationInfo();
        state.streamables = newAcceptableNode.getStreamables();
        for (AstStreamableNode streamable : acceptableNode.getStreamables()) {
            streamable.accept(this, state);
            if (streamable.getLocationInfo() != null) {
                endLocation = streamable.getLocationInfo();
            }
        }
        newAcceptableNode.setEndLocation(endLocation);
        acceptNode.setEndLocation(endLocation);

        // associate accepted stream to corresponding accept
        acceptNode.getAcceptables().add(newAcceptableNode);

        return null;
    }

    @Override
    public AstScriptNode visit(AstConnectNode connectNode, State state) throws Exception {

        LocationInfo location = connectNode.getLocationInfo();

        AstConnectNode newConnectNode = new AstConnectNode();
        newConnectNode.setLocationInfo(location);
        newConnectNode.setLocation(connectNode.getLocation());

        state.streamables = newConnectNode.getStreamables();

        for (AstStreamableNode streamable : connectNode.getStreamables()) {
            streamable.accept(this, state);
            if (streamable.getLocationInfo() != null) {
                location = streamable.getLocationInfo();
            }
        }
        newConnectNode.setEndLocation(location);
        state.streams.add(newConnectNode);

        return null;
    }

    @Override
    public AstScriptNode visit(AstReadAwaitNode node, State state) throws Exception {

        state.streamables.add(node);

        return null;
    }

    @Override
    public AstScriptNode visit(AstWriteAwaitNode node, State state) throws Exception {

        state.streamables.add(node);
        return null;
    }

    @Override
    public AstScriptNode visit(AstReadNotifyNode node, State state) throws Exception {

        state.streamables.add(node);
        return null;
    }

    @Override
    public AstScriptNode visit(AstWriteNotifyNode node, State state) throws Exception {

        state.streamables.add(node);
        return null;
    }

    @Override
    public AstScriptNode visit(AstWriteValueNode node, State state) throws Exception {

        state.streamables.add(node);

        return null;
    }

    @Override
    public AstScriptNode visit(AstDisconnectNode node, State state) throws Exception {

        state.streamables.add(node);
        return null;
    }

    @Override
    public AstScriptNode visit(AstUnbindNode node, State state) throws Exception {

        state.streamables.add(node);
        return null;
    }

    @Override
    public AstScriptNode visit(AstCloseNode node, State state) throws Exception {

        state.streamables.add(node);
        return null;
    }

    @Override
    public AstScriptNode visit(AstChildOpenedNode childOpenedNode, State state) throws Exception {

        state.streamables.add(childOpenedNode);
        return null;
    }

    @Override
    public AstScriptNode visit(AstChildClosedNode childClosedNode, State state) throws Exception {

        state.streamables.add(childClosedNode);
        return null;
    }

    @Override
    public AstScriptNode visit(AstOpenedNode openedNode, State state) throws Exception {

        state.streamables.add(openedNode);
        return null;
    }

    @Override
    public AstScriptNode visit(AstBoundNode boundNode, State state) throws Exception {

        state.streamables.add(boundNode);
        return null;
    }

    @Override
    public AstScriptNode visit(AstConnectedNode connectedNode, State state) throws Exception {

        state.streamables.add(connectedNode);
        return null;
    }

    @Override
    public AstScriptNode visit(AstReadValueNode node, State state) throws Exception {

        state.streamables.add(node);
        return null;
    }

    @Override
    public AstScriptNode visit(AstDisconnectedNode disconnectedNode, State state) throws Exception {

        state.streamables.add(disconnectedNode);
        return null;
    }

    @Override
    public AstScriptNode visit(AstUnboundNode unboundNode, State state) throws Exception {

        state.streamables.add(unboundNode);
        return null;
    }

    @Override
    public AstScriptNode visit(AstClosedNode closedNode, State state) throws Exception {

        state.streamables.add(closedNode);
        return null;
    }

    @Override
    public AstScriptNode visit(AstReadHttpHeaderNode node, State state) throws Exception {
        state.streamables.add(node);
        return null;
    }

    @Override
    public AstScriptNode visit(AstWriteHttpHeaderNode node, State state) throws Exception {
        state.streamables.add(node);
        return null;
    }

    @Override
    public AstScriptNode visit(AstWriteHttpContentLengthNode node, State state) throws Exception {
        state.streamables.add(node);
        return null;
    }

    @Override
    public AstScriptNode visit(AstReadHttpMethodNode node, State state) throws Exception {
        state.streamables.add(node);
        return null;
    }

    @Override
    public AstScriptNode visit(AstWriteHttpMethodNode node, State state) throws Exception {
        state.streamables.add(node);
        return null;
    }

    @Override
    public AstScriptNode visit(AstReadHttpParameterNode node, State state) throws Exception {
        state.streamables.add(node);
        return null;
    }

    @Override
    public AstScriptNode visit(AstWriteHttpParameterNode node, State state) throws Exception {
        state.streamables.add(node);
        return null;
    }

    @Override
    public AstScriptNode visit(AstReadHttpVersionNode node, State state) throws Exception {
        state.streamables.add(node);
        return null;
    }

    @Override
    public AstScriptNode visit(AstWriteHttpVersionNode node, State state) throws Exception {
        state.streamables.add(node);
        return null;
    }

    @Override
    public AstScriptNode visit(AstReadHttpStatusNode node, State state) throws Exception {
        state.streamables.add(node);
        return null;
    }

    @Override
    public AstScriptNode visit(AstWriteHttpStatusNode node, State state) throws Exception {
        state.streamables.add(node);
        return null;
    }

    @Override
    public AstScriptNode visit(AstCloseHttpRequestNode node, State state) throws Exception {
        state.streamables.add(node);
        return null;
    }

    @Override
    public AstScriptNode visit(AstCloseHttpResponseNode node, State state) throws Exception {
        state.streamables.add(node);
        return null;
    }

    @Override
    public AstScriptNode visit(AstEndOfHttpHeadersNode node, State state) throws Exception {
        state.streamables.add(node);
        return null;
    }
}
