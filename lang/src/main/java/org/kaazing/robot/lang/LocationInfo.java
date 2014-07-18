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

package org.kaazing.robot.lang;

public final class LocationInfo implements Comparable<LocationInfo> {
    public final int line;
    public final int column;

    public LocationInfo(int line, int column) {
        this.line = line;
        this.column = column;
    }

    @Override
    public int hashCode() {
        return line ^ column;
    }

    @Override
    public boolean equals(Object obj) {
        return (this == obj) || ((obj instanceof LocationInfo) && equals((LocationInfo) obj));
    }

    @Override
    public String toString() {
        return String.format("%d:%d", line, column);
    }

    private boolean equals(LocationInfo that) {
        return this.line == that.line && this.column == that.column;
    }

    @Override
    public int compareTo(LocationInfo that) {
        final int LESS = -1;
        final int EQUAL = 0;
        final int MORE = 1;

        if (this.equals(that)) {
            return 0;
        }
        if (this.line < that.line) {
            return LESS;
        } else if (this.line > that.line) {
            return MORE;
        } else if (this.column < that.column) {
            return LESS;
        } else if (this.column > that.column) {
            return MORE;
        }

        assert this.equals(that) : "compareTo inconsitant with equal";
        return EQUAL;
    }

    /* Inclusive. */
    public boolean isBetween(LocationInfo start, LocationInfo end) {
        assert start.compareTo(end) <= 0 : String.format("end |%s| before start |%s|", end, start);
        return start.compareTo(this) <= 0 && end.compareTo(this) >= 0;
    }

}
