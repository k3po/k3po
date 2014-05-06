/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package com.kaazing.robot.control.command;

public final class StartCommand extends Command {

    public Kind getKind() {
        return Kind.START;
    }

    @Override
    public int hashCode() {
        return hashTo();
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof StartCommand && equalTo((StartCommand) o);
    }

    protected boolean equalTo(StartCommand that) {
        return super.equalTo(that);
    }
}
