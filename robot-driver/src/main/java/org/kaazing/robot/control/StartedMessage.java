/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.control;

public class StartedMessage extends ControlMessage {

    @Override
    public int hashCode() {
        return hashTo();
    }

    @Override
    public boolean equals(Object obj) {
        return (this == obj) || (obj instanceof StartedMessage) && equalTo((StartedMessage) obj);
    }

    @Override
    public Kind getKind() {
        return Kind.STARTED;
    }

}
