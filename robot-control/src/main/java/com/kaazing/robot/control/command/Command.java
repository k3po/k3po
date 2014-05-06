/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package com.kaazing.robot.control.command;

public abstract class Command {

    public static enum Kind {
        PREPARE, START, ABORT
    }

    private String name;

    public abstract Kind getKind();

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    protected int hashTo() {
        return name != null ? name.hashCode() : 0;
    }

    protected boolean equalTo(Command that) {
        return this.name == that.name || this.name != null && this.name.equals(that.name);
    }

}
