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
