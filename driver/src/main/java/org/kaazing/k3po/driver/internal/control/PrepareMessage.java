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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PrepareMessage extends ControlMessage {

    private List<String> names;
    private String version;

    public PrepareMessage() {
        this.names = new ArrayList<String>(5);
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getVersion() {
        return version;
    }

    public List<String> getNames() {
        return names;
    }

    public void setNames(List<String> names) {
        this.names.clear();
        this.names.addAll(names);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getKind(), names);
    }

    @Override
    public boolean equals(Object obj) {
        return (this == obj) || (obj instanceof PrepareMessage) && equals((PrepareMessage) obj);
    }

    @Override
    public Kind getKind() {
        return Kind.PREPARE;
    }

    protected final boolean equals(PrepareMessage that) {
        return super.equalTo(that) && Objects.equals(this.names, that.names);
    }

}
