/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.junit.rules;

import static org.junit.Assert.assertTrue;

import org.junit.rules.Verifier;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runners.model.Statement;

import org.kaazing.robot.junit.annotation.Robotic;

public final class RobotRule extends Verifier {

    /*
     * For some reason earlier versions of Junit will cause tests to either hang
     * or succeed incorrectly without ever talking to the Robot. I'm not sure
     * why but the apply method does not seem to be called. So we need to
     * require version 4.10 (I know 4.7 has the problem ... not sure about 4.8
     * and 4.9 but 4.10 works).
     */
    static {
        JUnitCore core = new JUnitCore();
        String version = core.getVersion();
        String[] versionTokens = version.split("\\.");
        Integer[] versionsInt = new Integer[versionTokens.length];
        for (int i = 0; i < versionTokens.length; i++) {
            versionsInt[i] = Integer.parseInt(versionTokens[i]);
        }
        if (versionsInt[0] < 5) {
            if (versionsInt.length == 1 || versionsInt[0] < 4 || versionsInt[1] < 10) {
                throw new AssertionError("Robot Junit library requires at least version 4.10. Found version " + version);
            }
        }
    }

    private final RoboticLatch latch;

    public RobotRule() {
        latch = new RoboticLatch();
    }

    @Override
    public Statement apply(Statement statement, Description description) {

        Robotic note = description.getAnnotation(Robotic.class);
        if (note != null) {
            // decorate with Robotic behavior only if @Robotic annotation is present
            statement = new RoboticStatement(description, statement, latch);
        }

        return super.apply(statement, description);
    }

    public void join() throws Exception {
        // script should already be prepared before @Robotic test can execute
        assertTrue("Did you call join() from outside @Robotic test?", latch.isPrepared());

        // notify script to start
        latch.notifyStartable();

        // wait for script to finish
        latch.awaitFinished();
    }
}
