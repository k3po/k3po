/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.lang.regex;

import java.util.List;
import java.util.regex.Matcher;

public class NamedGroupMatcher implements NamedGroupMatchResult {

    private final Matcher matcher;
    private final List<String> groupNames;

    NamedGroupMatcher(Matcher matcher, List<String> groupNames) {
        this.matcher = matcher;
        this.groupNames = groupNames;
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

    @Override
    public int start() {
        return matcher.start();
    }

    @Override
    public int start(int group) {
        return matcher.start(group);
    }

    @Override
    public int end() {
        return matcher.end();
    }

    @Override
    public int end(int group) {
        return matcher.end(group);
    }

    @Override
    public String group() {
        return matcher.group();
    }

    @Override
    public String group(int group) {
        // We don't use group 0
        return matcher.group(group + 1);
    }

    @Override
    public int groupCount() {
        return matcher.groupCount();
    }

    @Override
    public String groupName(int group) {
        return (groupNames == null || group >= groupNames.size()) ? null : groupNames.get(group);
    }
}
