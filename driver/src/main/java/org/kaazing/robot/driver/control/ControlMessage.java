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

package org.kaazing.robot.driver.control;

public abstract class ControlMessage {

    public static enum Kind {
        PREPARE, PREPARED, START, STARTED, ERROR, ABORT, FINISHED
    }

    public abstract Kind getKind();

    public abstract int hashCode();

    public abstract boolean equals(Object obj);

    protected final boolean equalTo(ControlMessage that) {
        return this.getKind() == that.getKind();
    }

    @Override
    public String toString() {
        return String.format("%s", getKind());
    }
}
