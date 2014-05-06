/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package com.kaazing.robot.control.command;

public final class AbortCommand extends Command {

    public Kind getKind() {
        return Kind.ABORT;
    }

    @Override
    public int hashCode() {
        return hashTo();
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof AbortCommand && equalTo((AbortCommand) o);
    }

}
