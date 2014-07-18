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

public abstract class CommandEvent {

    public enum Kind {
        PREPARED, STARTED, FINISHED, ERROR
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

    protected boolean equalTo(CommandEvent that) {
        return this.name == that.name || this.name != null && this.name.equals(that.name);
    }
}
