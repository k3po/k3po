/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.control;

import java.net.URI;

public abstract class RobotControlFactorySPI implements RobotControlFactory {

    public abstract RobotControl newClient(URI controlURI) throws Exception;

    public abstract String getName();
}
