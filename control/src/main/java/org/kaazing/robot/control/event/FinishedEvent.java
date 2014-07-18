/*
 * Copyright (c) 2014 "Kaazing Corporation," (www.kaazing.com)
 *
 * This file is part of Robot.
 *
 * Robot is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
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
