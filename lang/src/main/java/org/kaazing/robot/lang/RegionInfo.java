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

import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Objects;

public final class RegionInfo {

    private static final List<RegionInfo> EMPTY_CHILDREN = emptyList();

    public static enum Kind { PARALLEL, SEQUENTIAL };

    public final List<RegionInfo> children;
    public final int start;
    public final int end;
    public final Kind kind;

    public static RegionInfo newParallel(int start, int end) {
        return newParallel(EMPTY_CHILDREN, start, end);
    }

    public static RegionInfo newParallel(List<RegionInfo> children, int start, int end) {
        return new RegionInfo(Kind.PARALLEL, children, start, end);
    }

    public static RegionInfo newSequential(int start, int end) {
        return newSequential(EMPTY_CHILDREN, start, end);
    }

    public static RegionInfo newSequential(List<RegionInfo> children, int start, int end) {
        return new RegionInfo(Kind.SEQUENTIAL, children, start, end);
    }

    @Override
    public int hashCode() {
        return Objects.hash(children, start, end);
    }

    @Override
    public boolean equals(Object obj) {
        return (this == obj) || ((obj instanceof RegionInfo) && equalTo((RegionInfo) obj));
    }

    public int size() {
        return end - start;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        appendString(0, sb);
        return sb.toString();
    }

    private RegionInfo(Kind kind, List<RegionInfo> children, int start, int end) {
        this.kind = requireNonNull(kind);
        this.children = requireNonNull(children);
        if (end < start) {
            throw new IllegalArgumentException("end < start");
        }
        this.start = start;
        this.end = end;
    }

    private void appendString(int level, StringBuffer sb) {
        for (int i = 0; i < level; i++) {
            sb.append("  ");
        }
        sb.append(format("[%d,%d) %s", start, end, kind));
        for (RegionInfo child : children) {
            sb.append('\n');
            child.appendString(level + 1, sb);
        }
    }

    private boolean equalTo(RegionInfo that) {
        return this.start == that.start && this.end == that.end &&
                Objects.equals(this.children, that.children);
    }

}
