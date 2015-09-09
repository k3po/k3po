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

public abstract class ControlMessage {

    public static enum Kind {
        PREPARE, PREPARED, START, STARTED, ERROR, ABORT, FINISHED, AWAIT, NOTIFY, NOTIFIED
    }

    public abstract Kind getKind();

    public abstract int hashCode();

    public abstract boolean equals(Object obj);

    protected final boolean equalTo(ControlMessage that) {
        return this.getKind() == that.getKind();
    }

    @Override
    public String toString() {
        return String.format("%s", getKind());
    }
}
