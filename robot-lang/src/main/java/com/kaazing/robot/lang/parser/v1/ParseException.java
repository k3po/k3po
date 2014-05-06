/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package com.kaazing.robot.lang.parser.v1;

import java.util.List;

public class ParseException extends Exception {

    private static final long serialVersionUID = 1L;

    protected List<String> errors;

    public ParseException(String msg) {
        super(msg);
    }

    public ParseException(String msg, List<String> errors) {
        this(msg);
        setErrors(errors);
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }
}
