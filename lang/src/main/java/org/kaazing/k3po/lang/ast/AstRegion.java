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

package org.kaazing.k3po.lang.ast;

import org.kaazing.k3po.lang.RegionInfo;

public abstract class AstRegion {

    private RegionInfo regionInfo;

    public RegionInfo getRegionInfo() {
        return regionInfo;
    }

    public void setRegionInfo(RegionInfo regionInfo) {
        this.regionInfo = regionInfo;
    }

    @Override
    public final int hashCode() {
        return hashTo();
    }

    @Override
    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (obj == null || !getClass().isAssignableFrom(obj.getClass())) {
            return false;
        }

        AstRegion that = (AstRegion) obj;
        return equalTo(that);
    }

    public final String toString() {
        StringBuilder sb = new StringBuilder();
        describe(sb);
        return sb.toString();
    }

    protected abstract int hashTo();

    protected abstract boolean equalTo(AstRegion that);

    protected void describe(StringBuilder buf) {
    }

}
