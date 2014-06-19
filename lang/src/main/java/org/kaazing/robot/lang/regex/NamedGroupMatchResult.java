/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.lang.regex;

import java.util.Set;

public interface NamedGroupMatchResult {

    Set<String> groupNames();

    String group(String name);
}
