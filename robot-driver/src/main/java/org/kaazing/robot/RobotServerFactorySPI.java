/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot;

public abstract class RobotServerFactorySPI implements RobotServerFactory {
    public abstract String getName();

    @Override
    public abstract RobotServer createRobotServer();

}
