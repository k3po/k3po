/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.lang.regex;

import java.util.regex.MatchResult;

public interface NamedGroupMatchResult extends MatchResult {

    String groupName(int group);
}
