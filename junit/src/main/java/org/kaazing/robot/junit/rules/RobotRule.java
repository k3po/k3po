/*
 * Copyright (c) 2014 "Kaazing Corporation," (www.kaazing.com)
 *
 * This file is part of Robot.
 *
 * Robot is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
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
            String versionToken = versionTokens[i];
            if (versionToken.contains("-")) {
                versionToken = versionToken.substring(0, versionToken.indexOf("-"));
            }
            versionsInt[i] = Integer.parseInt(versionToken);
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
