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
package org.kaazing.k3po.control.internal.event;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * FinishedEvent signaling that the robot hash completed the execution of one or more scripts.
 *
 */
public final class FinishedEvent extends CommandEvent {

    private String script;
    private final List<String> completedBarriers;
    private final List<String> incompleteBarriers;

    public FinishedEvent() {
        super();
        this.completedBarriers = new ArrayList<>();
        this.incompleteBarriers = new ArrayList<>();
    }

    @Override
    public Kind getKind() {
        return Kind.FINISHED;
    }

    /**
     * Sets the observed script behavior.
     * @param script the fully observed behavior by the scripts.
     */
    public void setScript(String script) {
        this.script = script;
    }

    /**
     * Gets a script showing the full observed behavior of a script.
     * @return the full behavior of a script.
     */
    public String getScript() {
        return script;
    }

    public List<String> getCompletedBarriers() {
        return completedBarriers;
    }

    public List<String> getIncompleteBarriers() {
        return incompleteBarriers;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getKind(), script);
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof FinishedEvent && equalTo((FinishedEvent) o);
    }

    protected boolean equalTo(FinishedEvent that) {
        return super.equalTo(that) && Objects.equals(this.script, that.script);
    }
}
