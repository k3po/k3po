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

package org.kaazing.robot.control.command;

public final class PrepareCommand extends Command {

    private String scriptPath;

    public Kind getKind() {
        return Kind.PREPARE;
    }

    public void setScriptPath(String scriptPath) {
        this.scriptPath = scriptPath;
    }

    public String getScriptPath() {
        return scriptPath;
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
                this.scriptPath == that.scriptPath || this.scriptPath != null && this.scriptPath.equals(that.scriptPath);
    }
}
