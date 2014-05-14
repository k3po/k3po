/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.cli;

import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;
import org.kaazing.robot.control.RobotControl;
import org.kaazing.robot.control.RobotControlFactories;
import org.kaazing.robot.control.RobotControlFactory;
//import org.kaazing.robot.control.MockRobotControlFactorySPI;

public abstract class AbstractRobotControllerTest {

    static Interpreter interpreter;
    static RobotControlFactory robotControlFactory;
    static Mockery context;
    static RobotControl robotControl;

    public abstract RobotController getRobotController();

    @Before
    public void setup() {
        context = new Mockery();
        robotControl = context.mock(RobotControl.class);
        robotControlFactory = RobotControlFactories.createRobotServerFactory("Mock");
//        assert robotControlFactory instanceof  MockRobotControlFactorySPI
        interpreter = context.mock(Interpreter.class);
    }

    @Test
    public void testStart() throws Exception {
//        robotController = getRobotController();
//        context.checking(
//                new Expectations() {
//                    {
//                        oneOf(robotControl).start();
//                    }
//                });
//        robotController.start();
//        context.assertIsSatisfied();
    }

    @Test
    public void testStartWithURI() {

    }

    @Test
    public void testGetRobotClient() {

    }

    @Test
    public void testExceptionWhenRobotAlreadyStarted() {

    }

    @Test
    public void testRunsRobotTestSuccess() {

    }

    @Test
    public void testRunsRobotTestFail() {

    }

    @Test
    public void testRunsRobotTestTimeout() {

    }
}
