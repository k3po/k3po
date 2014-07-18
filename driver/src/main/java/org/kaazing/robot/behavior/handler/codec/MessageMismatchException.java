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

package org.kaazing.robot.behavior.handler.codec;

import org.kaazing.robot.RobotException;

@SuppressWarnings("serial")
public class MessageMismatchException extends RobotException {

    protected Object expected;
    protected Object observed;

    public MessageMismatchException(String msg, Object expected, Object observed) {
        super(msg);
        this.expected = expected;
        this.observed = observed;
    }

    public Object getExpected() {
        return expected;
    }

    public Object getObserved() {
        return observed;
    }
}
