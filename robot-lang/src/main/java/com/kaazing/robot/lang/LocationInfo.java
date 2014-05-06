/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package com.kaazing.robot.lang;

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
