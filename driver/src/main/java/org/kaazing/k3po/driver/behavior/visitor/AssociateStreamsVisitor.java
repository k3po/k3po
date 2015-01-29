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

package org.kaazing.k3po.driver.behavior.visitor;

import static org.kaazing.k3po.lang.ast.util.AstUtil.equivalent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kaazing.k3po.driver.behavior.visitor.AssociateStreamsVisitor.State;
import org.kaazing.k3po.lang.RegionInfo;
import org.kaazing.k3po.lang.ast.AstAcceptNode;
import org.kaazing.k3po.lang.ast.AstAcceptableNode;
import org.kaazing.k3po.lang.ast.AstBoundNode;
import org.kaazing.k3po.lang.ast.AstChildClosedNode;
import org.kaazing.k3po.lang.ast.AstChildOpenedNode;
import org.kaazing.k3po.lang.ast.AstCloseNode;
import org.kaazing.k3po.lang.ast.AstClosedNode;
import org.kaazing.k3po.lang.ast.AstConnectNode;
import org.kaazing.k3po.lang.ast.AstConnectedNode;
import org.kaazing.k3po.lang.ast.AstDisconnectNode;
import org.kaazing.k3po.lang.ast.AstDisconnectedNode;
import org.kaazing.k3po.lang.ast.AstWriteFlushNode;
import org.kaazing.k3po.lang.ast.AstNode;
import org.kaazing.k3po.lang.ast.AstOpenedNode;
import org.kaazing.k3po.lang.ast.AstPropertyNode;
import org.kaazing.k3po.lang.ast.AstReadAwaitNode;
import org.kaazing.k3po.lang.ast.AstReadClosedNode;
import org.kaazing.k3po.lang.ast.AstReadConfigNode;
import org.kaazing.k3po.lang.ast.AstReadNotifyNode;
import org.kaazing.k3po.lang.ast.AstReadOptionNode;
import org.kaazing.k3po.lang.ast.AstReadValueNode;
import org.kaazing.k3po.lang.ast.AstRegionException;
import org.kaazing.k3po.lang.ast.AstScriptNode;
import org.kaazing.k3po.lang.ast.AstStreamNode;
import org.kaazing.k3po.lang.ast.AstStreamableNode;
import org.kaazing.k3po.lang.ast.AstUnbindNode;
import org.kaazing.k3po.lang.ast.AstUnboundNode;
import org.kaazing.k3po.lang.ast.AstWriteAwaitNode;
import org.kaazing.k3po.lang.ast.AstWriteCloseNode;
import org.kaazing.k3po.lang.ast.AstWriteConfigNode;
import org.kaazing.k3po.lang.ast.AstWriteNotifyNode;
import org.kaazing.k3po.lang.ast.AstWriteOptionNode;
import org.kaazing.k3po.lang.ast.AstWriteValueNode;

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
        newScript.setRegionInfo(script.getRegionInfo());
        newScript.getProperties().addAll(script.getProperties());

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
    public AstScriptNode visit(AstPropertyNode propertyNode, State state) throws Exception {
        return null;
    }

    @Override
    public AstScriptNode visit(AstAcceptNode acceptNode, State state) throws Exception {

        AstAcceptNode newAcceptNode = new AstAcceptNode();
        newAcceptNode.setRegionInfo(acceptNode.getRegionInfo());
        newAcceptNode.setLocation(acceptNode.getLocation());

        String acceptName = acceptNode.getAcceptName();
        String newAcceptName = acceptName != null ? acceptName : String.format("~accept~%d", ++state.implicitAcceptCount);

        state.accepts.put(newAcceptName, newAcceptNode);
        state.implicitAcceptName = newAcceptName;

        state.streamables = newAcceptNode.getStreamables();

        for (AstStreamableNode streamable : acceptNode.getStreamables()) {
            streamable.accept(this, state);
        }

        for (AstAcceptableNode acceptable : acceptNode.getAcceptables()) {
            assert equivalent(acceptName, acceptable.getAcceptName());
            acceptable.accept(this, state);
        }

        state.streams.add(newAcceptNode);

        return null;
    }

    @Override
    public AstScriptNode visit(AstAcceptableNode acceptableNode, State state) throws Exception {

        AstAcceptableNode newAcceptableNode = new AstAcceptableNode();
        newAcceptableNode.setRegionInfo(acceptableNode.getRegionInfo());

        String acceptName = acceptableNode.getAcceptName();
        if (acceptName == null) {
            acceptName = state.implicitAcceptName;
        }

        AstAcceptNode acceptNode = state.accepts.get(acceptName);
        if (acceptNode == null) {
            RegionInfo regionInfo = acceptableNode.getRegionInfo();
            throw new AstRegionException("Accept not found for accepted").initRegionInfo(regionInfo);
        }

        state.streamables = newAcceptableNode.getStreamables();
        for (AstStreamableNode streamable : acceptableNode.getStreamables()) {
            streamable.accept(this, state);
        }

        // associate accepted stream to corresponding accept
        acceptNode.getAcceptables().add(newAcceptableNode);

        return null;
    }

    @Override
    public AstScriptNode visit(AstConnectNode connectNode, State state) throws Exception {

        AstConnectNode newConnectNode = new AstConnectNode();
        newConnectNode.setRegionInfo(connectNode.getRegionInfo());
        newConnectNode.setLocation(connectNode.getLocation());

        state.streamables = newConnectNode.getStreamables();

        for (AstStreamableNode streamable : connectNode.getStreamables()) {
            streamable.accept(this, state);
        }

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
    public AstScriptNode visit(AstReadConfigNode node, State state) throws Exception {
        state.streamables.add(node);
        return null;
    }

    @Override
    public AstScriptNode visit(AstWriteConfigNode node, State state) throws Exception {
        state.streamables.add(node);
        return null;
    }

    @Override
    public AstScriptNode visit(AstReadClosedNode node, State state) throws Exception {
        state.streamables.add(node);
        return null;
    }

    @Override
    public AstScriptNode visit(AstWriteCloseNode node, State state) throws Exception {
        state.streamables.add(node);
        return null;
    }

    @Override
    public AstScriptNode visit(AstWriteFlushNode node, State state) throws Exception {
        state.streamables.add(node);
        return null;
    }

    @Override
    public AstScriptNode visit(AstReadOptionNode node, State state) throws Exception {
        state.streamables.add(node);
        return null;
    }

    @Override
    public AstScriptNode visit(AstWriteOptionNode node, State state) throws Exception {
        state.streamables.add(node);
        return null;
    }
}
