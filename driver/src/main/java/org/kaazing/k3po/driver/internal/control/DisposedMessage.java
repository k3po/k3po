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

package org.kaazing.k3po.driver.internal.control;

import static org.kaazing.k3po.driver.internal.control.ControlMessage.Kind.DISPOSED;

import java.util.Objects;

public class DisposedMessage extends ControlMessage {

    @Override
    public int hashCode() {
        return Objects.hashCode(getKind());
    }

    @Override
    public boolean equals(Object obj) {
        return (this == obj) || (obj instanceof DisposedMessage) && equalTo((DisposedMessage) obj);
    }

    @Override
    public Kind getKind() {
        return DISPOSED;
    }
}
