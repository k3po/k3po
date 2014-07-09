/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.junit;

public class RoboticException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public RoboticException() {
        super();
    }

    public RoboticException(String msg) {
        super(msg);
    }

    public RoboticException(Throwable cause) {
        super(cause);
    }
}
