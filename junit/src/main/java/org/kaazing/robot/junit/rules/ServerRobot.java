/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.junit.rules;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

@Deprecated
public class ServerRobot implements TestRule {

    private final RobotRule robot;

    public ServerRobot() {
        this.robot = new RobotRule();
    }

    @Override
    public Statement apply(Statement base, Description description) {
        return robot.apply(base, description);
    }

    public void awaitFinish() {
        try {
            robot.join();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
