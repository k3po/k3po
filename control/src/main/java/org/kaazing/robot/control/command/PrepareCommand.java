/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.control.command;

public final class PrepareCommand extends Command {

    private String script;

    public Kind getKind() {
        return Kind.PREPARE;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public String getScript() {
        return script;
    }

    @Override
    public int hashCode() {
        return hashTo();
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof PrepareCommand && equalTo((PrepareCommand) o);
    }

    protected boolean equalTo(PrepareCommand that) {
        return super.equalTo(that) &&
                this.script == that.script || this.script != null && this.script.equals(that.script);
    }
}
