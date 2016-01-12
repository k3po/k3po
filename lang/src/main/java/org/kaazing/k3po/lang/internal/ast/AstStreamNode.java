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

import static org.kaazing.k3po.lang.internal.ast.util.AstUtil.equivalent;

import java.util.LinkedList;
import java.util.List;

public abstract class AstStreamNode extends AstNode {

    private List<AstStreamableNode> streamables;

    public List<AstStreamableNode> getStreamables() {
        if (streamables == null) {
            streamables = new LinkedList<>();
        }
        return streamables;
    }

    @Override
    protected int hashTo() {
        int hashCode = getClass().hashCode();

        if (streamables != null) {
            hashCode <<= 4;
            hashCode ^= streamables.hashCode();
        }

        return hashCode;
    }

    protected boolean equalTo(AstStreamNode that) {
        return equivalent(this.streamables, that.streamables);
    }

    @Override
    protected void describe(StringBuilder buf) {
        describeLine(buf);
        if (streamables != null) {
            for (AstRegion streamable : streamables) {
                streamable.describe(buf);
            }
        }
    }

    protected void describeLine(StringBuilder sb) {
        super.describe(sb);
    }
}
