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

package org.kaazing.k3po.driver.internal.behavior.visitor;

import static java.lang.String.format;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.kaazing.k3po.lang.internal.ast.AstAcceptNode;
import org.kaazing.k3po.lang.internal.ast.AstAcceptableNode;
import org.kaazing.k3po.lang.internal.ast.AstBarrierNode;
import org.kaazing.k3po.lang.internal.ast.AstBoundNode;
import org.kaazing.k3po.lang.internal.ast.AstChildClosedNode;
import org.kaazing.k3po.lang.internal.ast.AstChildOpenedNode;
import org.kaazing.k3po.lang.internal.ast.AstCloseNode;
import org.kaazing.k3po.lang.internal.ast.AstClosedNode;
import org.kaazing.k3po.lang.internal.ast.AstConnectNode;
import org.kaazing.k3po.lang.internal.ast.AstConnectedNode;
import org.kaazing.k3po.lang.internal.ast.AstDisconnectNode;
import org.kaazing.k3po.lang.internal.ast.AstDisconnectedNode;
import org.kaazing.k3po.lang.internal.ast.AstNode;
import org.kaazing.k3po.lang.internal.ast.AstOpenedNode;
import org.kaazing.k3po.lang.internal.ast.AstPropertyNode;
import org.kaazing.k3po.lang.internal.ast.AstReadAwaitNode;
import org.kaazing.k3po.lang.internal.ast.AstReadClosedNode;
import org.kaazing.k3po.lang.internal.ast.AstReadConfigNode;
import org.kaazing.k3po.lang.internal.ast.AstReadNotifyNode;
import org.kaazing.k3po.lang.internal.ast.AstReadOptionMaskNode;
import org.kaazing.k3po.lang.internal.ast.AstReadValueNode;
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
import org.kaazing.k3po.lang.internal.ast.AstWriteOptionMaskNode;
import org.kaazing.k3po.lang.internal.ast.AstWriteValueNode;
import org.kaazing.k3po.lang.internal.parser.ScriptParseException;

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
    public Void visit(AstReadOptionMaskNode node, State state) throws Exception {
        // NOOP
        return null;
    }

    @Override
    public Void visit(AstWriteOptionMaskNode node, State state) throws Exception {
        // NOOP
        return null;
    }
}
