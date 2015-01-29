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

import static java.lang.String.format;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.kaazing.k3po.lang.ast.AstAcceptNode;
import org.kaazing.k3po.lang.ast.AstAcceptableNode;
import org.kaazing.k3po.lang.ast.AstBarrierNode;
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
import org.kaazing.k3po.lang.parser.ScriptParseException;

public class ValidateBarriersVisitor implements AstNode.Visitor<Void, ValidateBarriersVisitor.State> {

    public static class State {
        Map<String, AstBarrierNode> awaitersByName = new HashMap<String, AstBarrierNode>();
        Collection<String> notifierNames = new HashSet<String>();
    }

    @Override
    public Void visit(AstScriptNode scriptNode, State state) throws Exception {

        for (AstPropertyNode property : scriptNode.getProperties()) {
            property.accept(this, state);
        }

        for (AstStreamNode stream : scriptNode.getStreams()) {
            stream.accept(this, state);
        }

        Collection<String> awaiterNames = state.awaitersByName.keySet();
        awaiterNames.removeAll(state.notifierNames);
        if (!awaiterNames.isEmpty()) {
            String awaiterName = awaiterNames.iterator().next();
            throw new ScriptParseException(format("barrier name '%s' not triggered by any 'notify' directives", awaiterName));
        }

        return null;
    }

    @Override
    public Void visit(AstPropertyNode propertyNode, State parameter) throws Exception {
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
    public Void visit(AstWriteFlushNode node, State state) throws Exception {

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
