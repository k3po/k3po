/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package com.kaazing.robot.control;

public class PreparedMessage extends ControlMessage {

    private Kind compatibilityKind;

    @Override
    public int hashCode() {
        return hashTo();
    }

    @Override
    public boolean equals(Object obj) {
        return (this == obj) || (obj instanceof PreparedMessage) && equalTo((PreparedMessage) obj);
    }

    @Override
    public Kind getKind() {
        return Kind.PREPARED;
    }

    public Kind getCompatibilityKind() {
        return compatibilityKind;
    }

    public void setCompatibilityKind(Kind compatibilityKind) {
        this.compatibilityKind = compatibilityKind;
    }

}
