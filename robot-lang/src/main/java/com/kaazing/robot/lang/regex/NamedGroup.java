/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package com.kaazing.robot.lang.regex;

public class NamedGroup {
    final String name;
    final int start;

    public NamedGroup(String name, int start) {
        this.name = name;
        this.start = start;
    }

    @Override
    public String toString() {
        return String.format("%s @ %d", name, start);
    }
}
