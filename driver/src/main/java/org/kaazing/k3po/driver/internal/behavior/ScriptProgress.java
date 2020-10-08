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
package org.kaazing.k3po.driver.internal.behavior;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.requireNonNull;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.kaazing.k3po.lang.internal.RegionInfo;


public class ScriptProgress {

    private final String expectedScript;
    private final RegionInfo scriptInfo;
    private final Map<RegionInfo, String> failureInfos;
    private String observeredScript;

    public ScriptProgress(RegionInfo scriptInfo, String expectedScript) {
        this.expectedScript = expectedScript;
        this.scriptInfo = requireNonNull(scriptInfo);
        this.failureInfos = new ConcurrentHashMap<>();
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
            int numberOfFailures = failureInfos.size();
            if (numberOfFailures == 0) {
                // no failures
                observeredScript = expectedScript;
            } else {
                StringBuilder builder = new StringBuilder();
                processRegion(builder, scriptInfo, failureInfos);
                // Failures to unexpected events (e.g. channel close) are artificially added
                // potentially resulting in multiple failures on the same line with only one
                // being reported
                if (numberOfFailures <= failureInfos.size()) {
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
