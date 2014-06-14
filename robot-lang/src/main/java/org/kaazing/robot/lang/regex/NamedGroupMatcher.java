/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.lang.regex;

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
