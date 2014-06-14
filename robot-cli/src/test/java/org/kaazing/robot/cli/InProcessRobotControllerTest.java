package org.kaazing.robot.cli;


public class InProcessRobotControllerTest extends AbstractRobotControllerTest {

    @Override
    public RobotController getRobotController() {
        return new InProcessRobotController(interpreter, getRobotControlFactory(), getRobotServerFactory());
    }
}
