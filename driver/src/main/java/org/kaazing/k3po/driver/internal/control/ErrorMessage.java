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
package org.kaazing.k3po.driver.internal.control;

import java.util.Objects;

public final class ErrorMessage extends ControlMessage {

    private String summary;
    private String description;

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getKind(), description, summary);
    }

    @Override
    public boolean equals(Object obj) {
        return (this == obj) || (obj instanceof ErrorMessage) && equalTo((ErrorMessage) obj);
    }

    @Override
    public Kind getKind() {
        return Kind.ERROR;
    }

    protected boolean equalTo(ErrorMessage that) {
        return super.equalTo(that) && Objects.equals(this.summary, that.summary)
                && Objects.equals(this.description, that.description);
    }
}
