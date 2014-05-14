/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot;

public class MockRobotServerFactorySPI extends RobotServerFactorySPI {

    public RobotServer robotServer;

    @Override
    public String getName() {
        return "Mock";
    }

    @Override
    public RobotServer createRobotServer() {
        return null;
    }

    public void setRobotServer(RobotServer robotServer){
        this.robotServer = robotServer;
    }
}
