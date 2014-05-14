package org.kaazing.robot.control;

import java.net.URI;

public class MockRobotControlFactorySPI extends RobotControlFactorySPI {

    private RobotControl robotControl;

    @Override
    public RobotControl newClient(URI controlURI) throws Exception {
        return robotControl;
    }

    @Override
    public String getName() {
        return "Mock";
    }

    public void setRobotControl(RobotControl robotControl) {
        this.robotControl = robotControl;
    }
}
