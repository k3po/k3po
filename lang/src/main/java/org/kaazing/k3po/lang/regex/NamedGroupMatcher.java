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

package org.kaazing.k3po.lang.regex;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;

public class NamedGroupMatcher implements NamedGroupMatchResult {

    private final Matcher matcher;
    private final Set<String> groupNames = new HashSet<String>();

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
