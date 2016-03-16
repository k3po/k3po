/**
 * Copyright 2007-2015, Kaazing Corporation. All rights reserved.
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
package org.kaazing.k3po.lang.internal.ast;


public abstract class AstNode extends AstRegion {

    public abstract <R, P> R accept(Visitor<R, P> visitor, P parameter) throws Exception;

    public interface Visitor<R, P> {
        R visit(AstScriptNode node, P parameter) throws Exception;
        R visit(AstPropertyNode node, P parameter) throws Exception;
        R visit(AstAcceptNode node, P parameter) throws Exception;
        R visit(AstAcceptableNode node, P parameter) throws Exception;
        R visit(AstConnectNode node, P parameter) throws Exception;

        R visit(AstWriteFlushNode node, P parameter) throws Exception;
        R visit(AstWriteValueNode node, P parameter) throws Exception;
        R visit(AstWriteCloseNode node, P parameter) throws Exception;
        R visit(AstDisconnectNode node, P parameter) throws Exception;
        R visit(AstUnbindNode node, P parameter) throws Exception;
        R visit(AstCloseNode node, P parameter) throws Exception;
        R visit(AstAbortNode astAbortNode, P parameter) throws Exception;
        R visit(AstAbortedNode astAbortNode, P parameter) throws Exception;

        R visit(AstChildOpenedNode node, P parameter) throws Exception;
        R visit(AstChildClosedNode node, P parameter) throws Exception;
        R visit(AstOpenedNode node, P parameter) throws Exception;
        R visit(AstBoundNode node, P parameter) throws Exception;
        R visit(AstConnectedNode node, P parameter) throws Exception;
        R visit(AstReadValueNode node, P parameter) throws Exception;
        R visit(AstDisconnectedNode node, P parameter) throws Exception;
        R visit(AstUnboundNode node, P parameter) throws Exception;
        R visit(AstReadClosedNode node, P parameter) throws Exception;
        R visit(AstClosedNode node, P parameter) throws Exception;

        R visit(AstReadAwaitNode node, P parameter) throws Exception;
        R visit(AstWriteAwaitNode node, P parameter) throws Exception;
        R visit(AstReadNotifyNode node, P parameter) throws Exception;
        R visit(AstWriteNotifyNode node, P parameter) throws Exception;

        R visit(AstReadConfigNode node, P parameter) throws Exception;
        R visit(AstWriteConfigNode node, P parameter) throws Exception;

        R visit(AstReadOptionNode node, P parameter) throws Exception;
        R visit(AstWriteOptionNode node, P parameter) throws Exception;
    }
}
