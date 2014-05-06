/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package com.kaazing.robot.behavior.handler.barrier;

import com.kaazing.robot.behavior.Barrier;
import com.kaazing.robot.behavior.handler.ExecutionHandler;

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
