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
package org.kaazing.k3po.lang.internal.regex;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;

public class NamedGroupMatcher implements NamedGroupMatchResult {

    private final Matcher matcher;
    private final Set<String> groupNames = new HashSet<>();

    NamedGroupMatcher(Matcher matcher, List<String> groupNames) {
        this.matcher = matcher;
        this.groupNames.clear();
        this.groupNames.addAll(groupNames);
    }

    public boolean matches() {
        return matcher.matches();
    }

    public boolean lookingAt() {
        return matcher.lookingAt();
    }

    public boolean hitEnd() {
        return matcher.hitEnd();
    }

//    @Override
//    public int start() {
//        return matcher.start();
//    }
//
//    @Override
//    public int start(int group) {
//        return matcher.start(group);
//    }
//
    public int end() {
        return matcher.end();
    }

//
//    @Override
//    public String group() {
//        return matcher.group();
//    }

    @Override
    public String group(String name) {
        return matcher.group(name);
    }

//    @Override
//    public int groupCount() {
//        return matcher.groupCount();
//    }

    @Override
    public Set<String> groupNames() {
        return groupNames;
    }

}
