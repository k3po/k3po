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

package org.kaazing.robot.control;

import org.kaazing.robot.control.command.Command;
import org.kaazing.robot.control.event.CommandEvent;

import java.util.concurrent.TimeUnit;

public interface RobotControl {
    void connect() throws Exception;

    void disconnect() throws Exception;

    void writeCommand(Command command) throws Exception;

    CommandEvent readEvent() throws Exception;

    CommandEvent readEvent(int timeout, TimeUnit unit) throws Exception;
}
