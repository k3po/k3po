/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package com.kaazing.netty.bootstrap;

public class BootstrapException
    extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public BootstrapException() {
        super();
    }

    public BootstrapException(String message) {
        super(message);
    }

    public BootstrapException(Throwable cause) {
        super(cause);
    }

    public BootstrapException(String message,
                              Throwable cause) {
        super(message, cause);
    }
}
