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

package org.kaazing.robot.driver.behavior;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.DefaultChannelFuture;

public class RobotCompletionFutureImpl extends DefaultChannelFuture implements RobotCompletionFuture {

    private String observedScript;
    private String expectedScript;

    public RobotCompletionFutureImpl(Channel channel, boolean cancellable) {
        super(channel, cancellable);
    }

    // Note default setSuccess can be used for the empty script
    // @Override
    // private boolean setSuccess() {
    // super.setSuccess();
    // }

    public boolean setSuccess(String observed, String expected) {
        boolean alreadyDone = isDone();
        if (!alreadyDone) {
            observedScript = observed;
            expectedScript = expected;
        }
        return super.setSuccess();
    }

    @Override
    public String getObservedScript() {
        return observedScript;
    }

    @Override
    public String getExpectedScript() {
        return expectedScript;
    }

    public void setExpectedScript(String expectedScript) {
        this.expectedScript = expectedScript;
    }

    public void setObservedScript(String observedScript) {
        this.observedScript = observedScript;

    }
}
