/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package com.kaazing.robot.control.event;

public final class StartedEvent extends CommandEvent {

    public Kind getKind() {
        return Kind.STARTED;
    }

    @Override
    public int hashCode() {
        return hashTo();
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof StartedEvent && equalTo((StartedEvent) o);
    }
}
