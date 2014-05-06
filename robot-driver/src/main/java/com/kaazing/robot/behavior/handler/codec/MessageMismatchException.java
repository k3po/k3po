/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package com.kaazing.robot.behavior.handler.codec;

import com.kaazing.robot.RobotException;

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
