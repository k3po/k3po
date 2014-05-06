/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.control;

public class ErrorMessage extends ControlMessage {

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
        int hashCode = super.hashTo();

        hashCode <<= 8;
        hashCode ^= (description != null) ? description.hashCode() : 0;

        hashCode <<= 8;
        hashCode ^= (summary != null) ? summary.hashCode() : 0;

        return hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        return (this == obj) || (obj instanceof ErrorMessage) && equals((ErrorMessage) obj);
    }

    protected final boolean equals(ErrorMessage that) {
        return super.equalTo(that)
                && (this.summary == that.summary || (this.summary != null && this.summary.equals(that.summary)))
                && (this.description == that.description || (this.description != null && this.description
                        .equals(that.description)));
    }

    @Override
    public Kind getKind() {
        return Kind.ERROR;
    }
}
