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

package org.kaazing.k3po.driver.behavior;

import static java.util.Objects.requireNonNull;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.kaazing.k3po.lang.RegionInfo;


public class ScriptProgress {

    private final String expectedScript;
    private final RegionInfo scriptInfo;
    private final Map<RegionInfo, String> failureInfos;
    private String observeredScript;

    public ScriptProgress(RegionInfo scriptInfo, String expectedScript) {
        this.expectedScript = expectedScript;
        this.scriptInfo = requireNonNull(scriptInfo);
        this.failureInfos = new ConcurrentHashMap<RegionInfo, String>();
    }

    public void addScriptFailure(RegionInfo regionInfo) {
        failureInfos.put(regionInfo, "");
    }

    public void addScriptFailure(RegionInfo regionInfo, String message) {
        failureInfos.put(regionInfo, message);
    }

    public String getExpectedScript() {
        return expectedScript;
    }

    public RegionInfo getScriptInfo() {
        return scriptInfo;
    }

    public String getObservedScript() {
        if (observeredScript == null) {
            if (failureInfos.size() == 0) {
                // no failures
                observeredScript = expectedScript;
            } else {
                StringBuilder builder = new StringBuilder();
                processRegion(builder, scriptInfo, failureInfos);
                if (!failureInfos.isEmpty()) {
                    throw new RuntimeException("Script failure detected but not located");
                }
                observeredScript = builder.toString();
            }
        }
        return observeredScript;
    }

    private boolean processRegion(StringBuilder builder, RegionInfo regionInfo, Map<RegionInfo, String> failureInfos) {

        String failure = failureInfos.remove(regionInfo);
        if (failure != null) {
            builder.append(failure);
            if (regionInfo.kind == RegionInfo.Kind.PARALLEL) {
                return true;
            }
            return false;
        }

        List<RegionInfo> childInfos = regionInfo.children;
        int previousEnd = regionInfo.start;
        for (Iterator<RegionInfo> $i = childInfos.iterator(); $i.hasNext();) {
            RegionInfo childInfo = $i.next();

            builder.append(expectedScript.substring(previousEnd, childInfo.start));
            previousEnd = childInfo.end;

            boolean status = processRegion(builder, childInfo, failureInfos);
            if (!status) {
                if (regionInfo.kind == RegionInfo.Kind.PARALLEL) {
                    while ($i.hasNext()) {
                        childInfo = $i.next();
                        previousEnd = childInfo.end;
                    }
                    break;
                }
                return false;
            }
        }
        builder.append(expectedScript.substring(previousEnd, regionInfo.end));
        return true;
    }
}
