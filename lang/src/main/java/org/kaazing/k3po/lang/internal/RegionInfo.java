/**
 * Copyright 2007-2015, Kaazing Corporation. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kaazing.k3po.lang.internal;

import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Objects;

public final class RegionInfo {

    private static final List<RegionInfo> EMPTY_CHILDREN = emptyList();

    public enum Kind { PARALLEL, SEQUENTIAL };

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
