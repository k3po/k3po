/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package com.kaazing.robot.lang.parser;

public class ScriptParseException
    extends Exception {

    private static final long serialVersionUID = 1L;

    public ScriptParseException() {
        super();
    }

    public ScriptParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public ScriptParseException(String message) {
        super(message);
    }

    public ScriptParseException(Throwable cause) {
        super(cause);
    }
}
