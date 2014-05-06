/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package com.kaazing.robot.behavior.visitor;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
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
import com.kaazing.robot.behavior.visitor.GatherStreamsLocationVisitor.State;

public class GatherStreamsLocationVisitor implements AstNode.Visitor<AstScriptNode, State> {

    public static final class StreamResultLocationInfo {
        public final LocationInfo start;
        public final LocationInfo observed;
        public final LocationInfo end;

        public StreamResultLocationInfo(LocationInfo start, LocationInfo end, LocationInfo observed) {
            // We could just sort them and put them in the right place. But I
            // think the assertions will help catch errors.
            if (observed != null) {
                assert observed.isBetween(start, end) : String.format("observed %s is not between start %s and end %s",
                        observed, start, end);
            }
            else {
                assert start.compareTo(end) <= 0 : String.format("start %s is greater than end %s", start, end);
            }
            this.start = start;
            this.end = end;
            this.observed = observed;
        }
    }

    public static final class State {
        public final List<StreamResultLocationInfo> results = new LinkedList<StreamResultLocationInfo>();

        private final List<LocationInfo> streamEndPoints;
        private final Map<LocationInfo, Object> serverLocations;
        private int currentIndex;

        /* Convenience constructor */
        public State(List<LocationInfo> streamEndPoints) {
            this(streamEndPoints, new HashMap<LocationInfo, Object>(0, 100));
        }

        public State(List<LocationInfo> streamEndPoints, Map<LocationInfo, Object> serverLocations) {
            this.streamEndPoints = streamEndPoints;
            this.serverLocations = serverLocations;
            Collections.sort(this.streamEndPoints);
        }

    }

    @Override
    public AstScriptNode visit(AstScriptNode script, State state) throws Exception {

        for (AstStreamNode stream : script.getStreams()) {
            stream.accept(this, state);
        }

        assert state.currentIndex == state.streamEndPoints.size() : "Too many end points";

        return null;
    }

    @Override
    public AstScriptNode visit(AstAcceptNode acceptNode, State state) throws Exception {

        LocationInfo start = acceptNode.getLocationInfo();

        LocationInfo end = acceptNode.getEndLocation();

        /*
         * If the observed is between start and end then we observed at least
         * one stream within this accept. Initially I was thinking that in this
         * case we didn't need a stream result. However this was not good
         * because it wouldn't show up in our observed script.
         */

        /* The bind must have succeeded so add a StreamResultInfo .. */
        if (state.serverLocations.containsKey(start)) {
            /*
             * Accept's don't have an observed location. There may be more than
             * one.
             */
            state.results.add(new StreamResultLocationInfo(start, end, null));
        }
        else {
            /* Otherwise just a sanity check */
            if (state.currentIndex < state.streamEndPoints.size()) {
                assert !state.streamEndPoints.get(state.currentIndex).isBetween(start, end) :
                    "Bind failed but one of the streams associated with it succeeded???";
            }
        }

        for (AstStreamableNode streamable : acceptNode.getStreamables()) {
            streamable.accept(this, state);
        }

        for (AstAcceptableNode acceptable : acceptNode.getAcceptables()) {
            acceptable.accept(this, state);
        }
        return null;
    }

    @Override
    public AstScriptNode visit(AstAcceptableNode acceptableNode, State state) throws Exception {

        recordResults(acceptableNode, state);
        /*
         * I don't think we have to traverse further for (AstStreamableNode
         * streamable : acceptableNode.getStreamables()) {
         * streamable.accept(this, state);
         *
         * }
         */

        return null;
    }

    @Override
    public AstScriptNode visit(AstConnectNode connectNode, State state) throws Exception {

        recordResults(connectNode, state);

        /*
         * I don't think we have to traverse further for (AstStreamableNode
         * streamable : connectNode.getStreamables()) { streamable.accept(this,
         * state);
         *
         * }
         */
        return null;
    }

    @Override
    public AstScriptNode visit(AstReadAwaitNode node, State state) throws Exception {
        return null;
    }

    @Override
    public AstScriptNode visit(AstWriteAwaitNode node, State state) throws Exception {
        return null;
    }

    @Override
    public AstScriptNode visit(AstReadNotifyNode node, State state) throws Exception {
        return null;
    }

    @Override
    public AstScriptNode visit(AstWriteNotifyNode node, State state) throws Exception {
        return null;
    }

    @Override
    public AstScriptNode visit(AstWriteValueNode node, State state) throws Exception {
        return null;
    }

    @Override
    public AstScriptNode visit(AstDisconnectNode node, State state) throws Exception {
        return null;
    }

    @Override
    public AstScriptNode visit(AstUnbindNode node, State state) throws Exception {
        return null;
    }

    @Override
    public AstScriptNode visit(AstCloseNode node, State state) throws Exception {
        return null;
    }

    @Override
    public AstScriptNode visit(AstChildOpenedNode childOpenedNode, State state) throws Exception {
        return null;
    }

    @Override
    public AstScriptNode visit(AstChildClosedNode childClosedNode, State state) throws Exception {
        return null;
    }

    @Override
    public AstScriptNode visit(AstOpenedNode openedNode, State state) throws Exception {
        return null;
    }

    @Override
    public AstScriptNode visit(AstBoundNode boundNode, State state) throws Exception {
        return null;
    }

    @Override
    public AstScriptNode visit(AstConnectedNode connectedNode, State state) throws Exception {
        return null;
    }

    @Override
    public AstScriptNode visit(AstReadValueNode node, State state) throws Exception {
        return null;
    }

    @Override
    public AstScriptNode visit(AstDisconnectedNode disconnectedNode, State state) throws Exception {
        return null;
    }

    @Override
    public AstScriptNode visit(AstUnboundNode unboundNode, State state) throws Exception {
        return null;
    }

    @Override
    public AstScriptNode visit(AstClosedNode closedNode, State state) throws Exception {
        return null;
    }

    private static void recordResults(AstStreamNode node, State state) {
        // Then we have already found them all
        if (state.streamEndPoints.size() == state.currentIndex) {
            return;
        }
        LocationInfo start = node.getLocationInfo();
        LocationInfo observed = state.streamEndPoints.get(state.currentIndex);
        LocationInfo end = node.getEndLocation();
        if (observed.isBetween(start, end)) {
            state.results.add(new StreamResultLocationInfo(start, end, observed));
            state.currentIndex++;
        }
    }

    @Override
    public AstScriptNode visit(AstReadHttpHeaderNode node, State parameter) throws Exception {
        return null;
    }

    @Override
    public AstScriptNode visit(AstWriteHttpHeaderNode node, State parameter) throws Exception {
        return null;
    }

    @Override
    public AstScriptNode visit(AstWriteHttpContentLengthNode node, State parameter) throws Exception {
        return null;
    }

    @Override
    public AstScriptNode visit(AstReadHttpMethodNode node, State parameter) throws Exception {
        return null;
    }

    @Override
    public AstScriptNode visit(AstWriteHttpMethodNode node, State parameter) throws Exception {
        return null;
    }

    @Override
    public AstScriptNode visit(AstReadHttpParameterNode node, State parameter) throws Exception {
        return null;
    }

    @Override
    public AstScriptNode visit(AstWriteHttpParameterNode node, State parameter) throws Exception {
        return null;
    }

    @Override
    public AstScriptNode visit(AstReadHttpVersionNode node, State parameter) throws Exception {
        return null;
    }

    @Override
    public AstScriptNode visit(AstWriteHttpVersionNode node, State parameter) throws Exception {
        return null;
    }

    @Override
    public AstScriptNode visit(AstReadHttpStatusNode node, State parameter) throws Exception {
        return null;
    }

    @Override
    public AstScriptNode visit(AstWriteHttpStatusNode node, State parameter) throws Exception {
        return null;
    }

    @Override
    public AstScriptNode visit(AstCloseHttpRequestNode node, State parameter) throws Exception {
        return null;
    }

    @Override
    public AstScriptNode visit(AstCloseHttpResponseNode node, State parameter) throws Exception {
        return null;
    }

    @Override
    public AstScriptNode visit(AstEndOfHttpHeadersNode node, State parameter) throws Exception {
        return null;
    }
}
