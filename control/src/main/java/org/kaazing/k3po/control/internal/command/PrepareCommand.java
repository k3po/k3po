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

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Prepares scripts for execution.
 *
 */
public final class PrepareCommand extends Command {

    private final List<String> names;
    private List<String> overriddenScriptProperties;

    /**
     * Constructs the Prepare Command.
     */
    public PrepareCommand() {
        this.names = new LinkedList<>();
    }

    /**
     * Set the fully qualified name of the script to use.
     * @param name of script to use
     */
    public void setName(String name) {
        this.names.clear();
        this.names.add(name);
    }

    /**
     * Sets the script names.
     * @param names of scripts to use
     */
    public void setNames(List<String> names) {
        this.names.clear();
        this.names.addAll(names);
    }

    /**
     * @return names
     */
    public List<String> getNames() {
        return names;
    }

    @Override
    public Kind getKind() {
        return Kind.PREPARE;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getKind(), names);
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof PrepareCommand && equalTo((PrepareCommand) o);
    }

    protected boolean equalTo(PrepareCommand that) {
        return super.equalTo(that) && Objects.equals(this.names, that.names);
    }

    public List<String> getOverriddenScriptProperties() {
        return overriddenScriptProperties;
    }

    public void setOverriddenScriptProperties(List<String> overriddenScriptProperties) {
        this.overriddenScriptProperties = overriddenScriptProperties;
    }

}
