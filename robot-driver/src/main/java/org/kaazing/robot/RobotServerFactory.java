/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot;

import java.net.URI;

public interface RobotServerFactory {
    RobotServer createRobotServer(URI uri, boolean verbose);
}
