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

import static java.lang.String.format;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.kaazing.robot.lang.LocationInfo;
import org.kaazing.robot.lang.ast.AstAcceptNode;
import org.kaazing.robot.lang.ast.AstAcceptableNode;
import org.kaazing.robot.lang.ast.AstBarrierNode;
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
    public Void visit(AstFlushNode node, State state) throws Exception {

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
    public Void visit(AstReadConfigNode node, State parameter) throws Exception {
        // NOOP
        return null;
    }

    @Override
    public Void visit(AstWriteConfigNode node, State parameter) throws Exception {
        // NOOP
        return null;
    }

    @Override
    public Void visit(AstReadClosedNode node, State parameter) throws Exception {
        // NOOP
        return null;
    }

    @Override
    public Void visit(AstWriteCloseNode node, State parameter) throws Exception {
        // NOOP
        return null;
    }

    @Override
    public Void visit(AstReadOptionNode node, State state) throws Exception {
        // NOOP
        return null;
    }

    @Override
    public Void visit(AstWriteOptionNode node, State state) throws Exception {
        // NOOP
        return null;
    }
}
