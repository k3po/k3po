/*
 * Copyright 2014, Kaazing Corporation. All rights reserved.
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

package org.kaazing.k3po.lang.internal.ast;

import org.kaazing.k3po.lang.internal.RegionInfo;

public abstract class AstRegion {

    protected RegionInfo regionInfo;

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
