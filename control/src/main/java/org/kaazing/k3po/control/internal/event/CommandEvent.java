/*
 * Copyright (c) 2007-2014 Kaazing Corporation. All rights reserved.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.kaazing.k3po.control.internal.event;

import java.util.Objects;

/**
 * Abstract class for receiving an Event from the robot.
 *
 */
public abstract class CommandEvent {

    /**
     * Enum for the kind of command event.
     *
     */
    public enum Kind {
        /**
         * Event for if the scripts are prepared.
         */
        PREPARED,
        /**
         * Event for if k3po has started the scripts.
         */
        STARTED,
        /**
         * Event for if k3po has finished the scripts.
         */
        FINISHED,
        /**
         * Event for if there is an error in the execution or preparation of the scripts.
         */
        ERROR,
        /**
         * Event for if a barrier has been triggered via a notify command
         */
        NOTIFIED,
        /**
         * Event for when the K3po Driver has been disposed of
         */
        DISPOSED
    }

    /**
     * Returns the Kind of event.
     * @return the event Kind
     */
    public abstract Kind getKind();

    @Override
    public abstract int hashCode();

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof CommandEvent && equalTo((CommandEvent) o);
    }

    protected final boolean equalTo(CommandEvent that) {
        return Objects.equals(this.getKind(), that.getKind());
    }
}
