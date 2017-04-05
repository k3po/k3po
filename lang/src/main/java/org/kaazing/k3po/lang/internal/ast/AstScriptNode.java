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

public class AstScriptNode extends AstNode {

    private List<AstPropertyNode> properties;
    private List<AstStreamNode> streams;

    public List<AstPropertyNode> getProperties() {
        if (properties == null) {
            properties = new LinkedList<>();
        }

        return properties;
    }

    public List<AstStreamNode> getStreams() {
        if (streams == null) {
            streams = new LinkedList<>();
        }

        return streams;
    }

    @Override
    public <R, P> R accept(Visitor<R, P> visitor, P parameter) {
        return visitor.visit(this, parameter);
    }

    @Override
    protected int hashTo() {
        int hashCode = getClass().hashCode();

        if (streams != null) {
            hashCode <<= 4;
            hashCode ^= streams.hashCode();
        }

        return hashCode;
    }

    @Override
    protected boolean equalTo(AstRegion that) {
        return that instanceof AstScriptNode && equalTo((AstScriptNode) that);
    }

    protected boolean equalTo(AstScriptNode that) {
        return equivalent(this.streams, that.streams);
    }

    @Override
    protected void describe(StringBuilder buf) {
        if (properties != null) {
            for (AstPropertyNode property : properties) {
                property.describe(buf);
            }
        }
        if (streams != null) {
            for (AstStreamNode stream : streams) {
                stream.describe(buf);
            }
        }
    }
}
