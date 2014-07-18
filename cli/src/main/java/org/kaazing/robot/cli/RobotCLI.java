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

package org.kaazing.robot.cli;

import org.kaazing.robot.RobotServerFactories;
import org.kaazing.robot.control.RobotControlFactories;

public final class RobotCLI {

    private RobotCLI() {

    }

    public static void main(String... args) throws Exception {

        if (args.length == 0) {
            // interactive
            Interpreter interpreter = new InteractiveInterpreter();
            AbstractRobotController controller = new InProcessRobotController(interpreter,
                    RobotControlFactories.createRobotControlFactory(),
                    RobotServerFactories.createRobotServerFactory());
            interpreter.run(controller);
        } else {
            // non interactive
            Interpreter interpreter = new NonInteractiveInterpreter(args);
            // may use FileDrivenRobotController when completed, this will save temporary data
            AbstractRobotController controller = new InProcessRobotController(interpreter,
                    RobotControlFactories.createRobotControlFactory(),
                    RobotServerFactories.createRobotServerFactory());
            interpreter.run(controller);
        }

    }
}
