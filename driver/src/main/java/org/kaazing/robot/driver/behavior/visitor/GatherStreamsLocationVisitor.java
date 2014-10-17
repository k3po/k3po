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

package org.kaazing.robot.driver.behavior.visitor;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.kaazing.robot.driver.behavior.visitor.GatherStreamsLocationVisitor.State;
import org.kaazing.robot.lang.LocationInfo;
import org.kaazing.robot.lang.ast.AstAcceptNode;
import org.kaazing.robot.lang.ast.AstAcceptableNode;
import org.kaazing.robot.lang.ast.AstBoundNode;
import org.kaazing.robot.lang.ast.AstChildClosedNode;
import org.kaazing.robot.lang.ast.AstChildOpenedNode;
import org.kaazing.robot.lang.ast.AstCloseNode;
import org.kaazing.robot.lang.ast.AstClosedNode;
import org.kaazing.robot.lang.ast.AstConnectNode;
import org.kaazing.robot.lang.ast.AstConnectedNode;
import org.kaazing.robot.lang.ast.AstDisconnectNode;
import org.kaazing.robot.lang.ast.AstDisconnectedNode;
import org.kaazing.robot.lang.ast.AstFlushNode;
import org.kaazing.robot.lang.ast.AstNode;
import org.kaazing.robot.lang.ast.AstOpenedNode;
import org.kaazing.robot.lang.ast.AstReadAwaitNode;
import org.kaazing.robot.lang.ast.AstReadClosedNode;
import org.kaazing.robot.lang.ast.AstReadConfigNode;
import org.kaazing.robot.lang.ast.AstReadNotifyNode;
import org.kaazing.robot.lang.ast.AstReadOptionNode;
import org.kaazing.robot.lang.ast.AstReadValueNode;
import org.kaazing.robot.lang.ast.AstScriptNode;
import org.kaazing.robot.lang.ast.AstStreamNode;
import org.kaazing.robot.lang.ast.AstStreamableNode;
import org.kaazing.robot.lang.ast.AstUnbindNode;
import org.kaazing.robot.lang.ast.AstUnboundNode;
import org.kaazing.robot.lang.ast.AstWriteAwaitNode;
import org.kaazing.robot.lang.ast.AstWriteCloseNode;
import org.kaazing.robot.lang.ast.AstWriteConfigNode;
import org.kaazing.robot.lang.ast.AstWriteNotifyNode;
import org.kaazing.robot.lang.ast.AstWriteOptionNode;
import org.kaazing.robot.lang.ast.AstWriteValueNode;

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
    public AstScriptNode visit(AstReadConfigNode node, State parameter) throws Exception {
        return null;
    }

    @Override
    public AstScriptNode visit(AstWriteConfigNode node, State parameter) throws Exception {
        return null;
    }

    @Override
    public AstScriptNode visit(AstReadClosedNode node, State parameter) throws Exception {
        return null;
    }

    @Override
    public AstScriptNode visit(AstWriteCloseNode node, State parameter) throws Exception {
        return null;
    }

    @Override
    public AstScriptNode visit(AstFlushNode node, State state) throws Exception {
        return null;
    }

    @Override
    public AstScriptNode visit(AstReadOptionNode node, State parameter) throws Exception {
        return null;
    }

    @Override
    public AstScriptNode visit(AstWriteOptionNode node, State parameter) throws Exception {
        return null;
    }
}
