/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.control.event;

public final class ErrorEvent extends CommandEvent {

    private String summary;
    private String description;

    public Kind getKind() {
        return Kind.ERROR;
    }

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
        int hashCode = hashTo();

        if (summary != null) {
            hashCode ^= summary.hashCode();
        }

        return hashCode;
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof ErrorEvent && equalTo((ErrorEvent) o);
    }

    protected boolean equalTo(ErrorEvent that) {
        return super.equalTo(that) &&
                (this.summary == that.summary || this.summary != null && this.summary.equals(that.summary)) &&
                (this.description == that.description || this.description != null && this.description.equals(that.description));
    }
}
