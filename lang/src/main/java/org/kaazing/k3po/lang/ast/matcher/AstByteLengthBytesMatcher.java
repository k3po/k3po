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

package org.kaazing.k3po.lang.ast.matcher;

import static java.lang.String.format;

public class AstByteLengthBytesMatcher extends AstFixedLengthBytesMatcher {

    @Deprecated
    public AstByteLengthBytesMatcher() {
        super(Byte.SIZE / Byte.SIZE);
    }

    public AstByteLengthBytesMatcher(String captureName) {
        super(Byte.SIZE / Byte.SIZE, captureName);
    }

    @Override
    public <R, P> R accept(Visitor<R, P> visitor, P parameter) throws Exception {
        return visitor.visit(this, parameter);
    }

    @Override
    protected void describe(StringBuilder buf) {
        String captureName = getCaptureName();
        if (captureName == null) {
            buf.append("byte");
        }
        else {
            buf.append(format("(byte:%s)", captureName));
        }
    }
}
