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

    public abstract <R, P> R accept(Visitor<R, P> visitor, P parameter);

    public interface Visitor<R, P> {
        R visit(AstScriptNode node, P parameter);
        R visit(AstPropertyNode node, P parameter);
        R visit(AstAcceptNode node, P parameter);
        R visit(AstAcceptedNode node, P parameter);
        R visit(AstRejectedNode node, P parameter);
        R visit(AstConnectNode node, P parameter);

        R visit(AstConnectAbortNode node, P parameter);
        R visit(AstConnectAbortedNode node, P parameter);

        R visit(AstWriteFlushNode node, P parameter);
        R visit(AstWriteValueNode node, P parameter);
        R visit(AstWriteCloseNode node, P parameter);
        R visit(AstDisconnectNode node, P parameter);
        R visit(AstUnbindNode node, P parameter);
        R visit(AstCloseNode node, P parameter);

        R visit(AstWriteAbortNode node, P parameter);
        R visit(AstReadAbortedNode node, P parameter);

        R visit(AstReadAbortNode node, P parameter);
        R visit(AstWriteAbortedNode node, P parameter);

        R visit(AstChildOpenedNode node, P parameter);
        R visit(AstChildClosedNode node, P parameter);
        R visit(AstOpenedNode node, P parameter);
        R visit(AstBoundNode node, P parameter);
        R visit(AstConnectedNode node, P parameter);
        R visit(AstReadValueNode node, P parameter);
        R visit(AstDisconnectedNode node, P parameter);
        R visit(AstUnboundNode node, P parameter);
        R visit(AstReadClosedNode node, P parameter);
        R visit(AstClosedNode node, P parameter);

        R visit(AstReadAwaitNode node, P parameter);
        R visit(AstWriteAwaitNode node, P parameter);
        R visit(AstReadNotifyNode node, P parameter);
        R visit(AstWriteNotifyNode node, P parameter);

        R visit(AstReadConfigNode node, P parameter);
        R visit(AstWriteConfigNode node, P parameter);

        R visit(AstReadOptionNode node, P parameter);
        R visit(AstWriteOptionNode node, P parameter);

        R visit(AstReadAdviseNode node, P parameter);
        R visit(AstWriteAdviseNode node, P parameter);
        R visit(AstReadAdvisedNode node, P parameter);
        R visit(AstWriteAdvisedNode node, P parameter);
    }
}
