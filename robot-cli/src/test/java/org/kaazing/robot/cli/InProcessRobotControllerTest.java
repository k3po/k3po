package org.kaazing.robot.cli;

import org.kaazing.robot.control.RobotControlFactory;

public class InProcessRobotControllerTest extends AbstractRobotControllerTest {

    @Override
    public RobotController getRobotController() {
        return new InProcessRobotController(interpreter, getRobotControlFactory(), getRobotServerFactory());
    }
}
