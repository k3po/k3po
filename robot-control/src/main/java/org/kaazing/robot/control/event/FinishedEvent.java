/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.control.event;

public final class FinishedEvent extends CommandEvent {

    private String script;

    public Kind getKind() {
        return Kind.FINISHED;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public String getScript() {
        return script;
    }

    @Override
    public int hashCode() {
        int hashCode = hashTo();

        if (script != null) {
            hashCode ^= script.hashCode();
        }

        return hashCode;
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof FinishedEvent && equalTo((FinishedEvent) o);
    }

    protected boolean equalTo(FinishedEvent that) {
        return super.equalTo(that) &&
                this.script == that.script || this.script != null && this.script.equals(that.script);
    }
}
