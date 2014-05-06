/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.control;

public abstract class ControlMessage {

    public static enum Kind {
        PREPARE, PREPARED, START, STARTED, ERROR, ABORT, FINISHED
    };

    private String scriptName;

    public String getScriptName() {
        return scriptName;
    }

    public void setScriptName(String scriptName) {
        this.scriptName = scriptName;
    }

    public abstract Kind getKind();

    public abstract int hashCode();

    public abstract boolean equals(Object obj);

    protected int hashTo() {
        return (scriptName != null) ? scriptName.hashCode() : 0;
    }

    protected final boolean equalTo(ControlMessage that) {
        return this.getKind() == that.getKind() &&
                (this.scriptName == that.scriptName ||
                (this.scriptName != null && this.scriptName.equals(that.scriptName)));
    }

    @Override
    public String toString() {
        return String.format("%s %s", getKind(), getScriptName());
    }
}
