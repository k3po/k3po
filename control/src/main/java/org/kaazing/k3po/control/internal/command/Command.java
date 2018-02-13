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
package org.kaazing.k3po.control.internal.command;

import java.util.Objects;

/**
 * Abstract class for a Command to the robot.
 *
 */
public abstract class Command {

    /**
     * Kind of Command.
     *
     */
    public enum Kind {
        /**
         * Prepare command.
         */
        PREPARE,
        /**
         * Start command.
         */
        START,
        /**
         * Abort command.
         */
        ABORT,
        /**
         * Await command.
         */
        AWAIT,
        /**
         * Notify command.
         */
        NOTIFY,
        /**
         * Close command.
         */
        CLOSE
    }

    /**
     * @return Kind
     */
    public abstract Kind getKind();

    @Override
    public abstract int hashCode();

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof Command && equalTo((Command) o);
    }

    protected final boolean equalTo(Command that) {
        return Objects.equals(this.getKind(), that.getKind());
    }

}
