/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package com.kaazing.robot.control;

public class FinishedMessage extends ControlMessage {

    private String observedScript = "";

    public String getObservedScript() {
        return observedScript;
    }

    public void setObservedScript(String observedScript) {
        this.observedScript = observedScript;
    }

    @Override
    public int hashCode() {
        int hashCode = super.hashTo();

        hashCode <<= 8;
        hashCode ^= (observedScript != null) ? observedScript.hashCode() : 0;

        return hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        return (this == obj) || (obj instanceof FinishedMessage) && equals((FinishedMessage) obj);
    }

    protected final boolean equals(FinishedMessage that) {
        return super.equalTo(that)
                && (this.observedScript == that.observedScript || (this.observedScript != null && this.observedScript
                        .equals(that.observedScript)));
    }

    @Override
    public Kind getKind() {
        return Kind.FINISHED;
    }

}
