/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.control;

import java.net.URI;

public interface RobotControlFactory {

    RobotControl newClient(URI controlURI) throws Exception;
}
