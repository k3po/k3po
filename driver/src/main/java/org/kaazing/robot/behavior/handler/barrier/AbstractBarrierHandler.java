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

package org.kaazing.robot.behavior.handler.barrier;

import org.kaazing.robot.behavior.Barrier;
import org.kaazing.robot.behavior.handler.ExecutionHandler;

public abstract class AbstractBarrierHandler extends ExecutionHandler {

    private final Barrier barrier;

    public AbstractBarrierHandler(Barrier barrier) {
        if (barrier == null) {
            throw new NullPointerException("barrier");
        }
        this.barrier = barrier;
    }

    public Barrier getBarrier() {
        return barrier;
    }

}
