/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.control.event;

public final class PreparedEvent extends CommandEvent {

    public Kind getKind() {
        return Kind.PREPARED;
    }

    @Override
    public int hashCode() {
        return hashTo();
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof PreparedEvent && equalTo((PreparedEvent) o);
    }
}
