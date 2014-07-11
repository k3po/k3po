/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.behavior;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.DefaultChannelFuture;

public class RobotCompletionFutureImpl extends DefaultChannelFuture implements RobotCompletionFuture {

    private String observedScript;

    public RobotCompletionFutureImpl(Channel channel, boolean cancellable) {
        super(channel, cancellable);
    }

    // Note default setSuccess can be used for the empty script
    // @Override
    // private boolean setSuccess() {
    // super.setSuccess();
    // }

    public boolean setSuccess(String observed) {
        boolean alreadyDone = isDone();
        if (!alreadyDone) {
            observedScript = observed;
        }
        return super.setSuccess();
    }

    @Override
    public String getObservedScript() {
        return observedScript;
    }

    public void setObservedScript(String observedScript) {
        this.observedScript = observedScript;

    }
}
