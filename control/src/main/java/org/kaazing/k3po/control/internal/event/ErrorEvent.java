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

import java.util.Objects;

/**
 * Error event signaling that an error has occured in the script execution.
 *
 */
public final class ErrorEvent extends CommandEvent {

    private String summary;
    private String description;

    @Override
    public Kind getKind() {
        return Kind.ERROR;
    }

    /**
     * Gets an abbreviated summary of why an error occurred during execution.
     * @return a summary of the error to occur.
     */
    public String getSummary() {
        return summary;
    }

    /**
     * Sets an abbreviated summary of the error occurred.
     * @param summary is the reason an error occurred.
     */
    public void setSummary(String summary) {
        this.summary = summary;
    }

    /**
     * Gets the full description of why the error occurred. Often this is the stack trace in an exception.
     * @return a full description of why the error occurred.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description on why an error occurred. Often this is the stack trace in an exception.
     * @param description of why the error occured.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public int hashCode() {
        return Objects.hash(summary, description);
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof ErrorEvent && equalTo((ErrorEvent) o);
    }

    protected boolean equalTo(ErrorEvent that) {
        return super.equalTo(that) && Objects.equals(this.summary, that.summary)
                && Objects.equals(this.description, that.description);
    }
}
