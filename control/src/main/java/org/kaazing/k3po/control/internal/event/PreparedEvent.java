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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * PreparedEvent is received in response to a PrepareCommand. It contains a script showing the expected robot behavior.
 *
 */
public final class PreparedEvent extends CommandEvent {

    private String script;
    private final List<String> barriers;

    public PreparedEvent() {
        barriers = new ArrayList<String>();
    }

    @Override
    public Kind getKind() {
        return Kind.PREPARED;
    }

    /**
     * Sets set resolved script value, i.e. the script to be executed.
     * @param script is the expected script behavior that will be executed by k3po.
     */
    public void setScript(String script) {
        this.script = script;
    }

    /**
     * Gets the script to be executed.
     * @return String of the combined script names provided in the PrepareCommand.
     */
    public String getScript() {
        return script;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getKind(), script);
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof PreparedEvent && equalTo((PreparedEvent) o);
    }

    protected boolean equalTo(PreparedEvent that) {
        return super.equalTo(that) && Objects.equals(this.script, that.script);
    }

    /**
     * The list of barriers in the script
     * @return
     */
    public List<String> getBarriers() {
        return barriers;
    }
}
