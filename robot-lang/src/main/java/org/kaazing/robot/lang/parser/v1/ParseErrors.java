/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.lang.parser.v1;

import static java.lang.String.format;
import static java.util.Collections.unmodifiableList;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.kaazing.robot.lang.LocationInfo;
import org.kaazing.robot.lang.ast.AstNode;

public class ParseErrors {

    // By default, we want to capture a lot of errors
    private static final int DEFAULT_MAX_ERRORS = 30;

    private List<String> errors;
    private int maxErrorCount;

    private String getDesc(AstNode node) {
        LocationInfo loc = node.getLocationInfo();
        return String.format("line %d:%d: %s", loc.line, loc.column, node.toString());
    }

    public ParseErrors(int maxErrorCount) {
        if (maxErrorCount < 1) {
            throw new IllegalArgumentException(format("Maximum error count (%d) must be greater than zero", maxErrorCount));
        }

        errors = new ArrayList<String>(maxErrorCount);
        this.maxErrorCount = maxErrorCount;
    }

    public ParseErrors() {
        this(DEFAULT_MAX_ERRORS);
    }

    public void addError(String error) throws TooManyErrorsException {

        errors.add(error);

        if (errors.size() > maxErrorCount) {
            throw new TooManyErrorsException(format("Maximum number of parse errors (%d) encountered", maxErrorCount), errors);
        }
    }

    public void addError(String desc, String message) throws TooManyErrorsException {

        addError(String.format("%s: %s", desc, message));
    }

    public void addError(AstNode node, String message) throws TooManyErrorsException {

        addError(getDesc(node), message);
    }

    public void addError(Exception e) throws TooManyErrorsException {

        Throwable cause = e.getCause();
        if (cause != null) {
            addError(String.format("%s (%s)", e.getMessage(), e.getCause()));

        }
        else {
            addError(e.getMessage());
        }
    }

    public boolean isEmpty() {
        return errors.isEmpty();
    }

    public int size() {
        return errors.size();
    }

    public String getError(int idx) {
        return errors.get(idx);
    }

    public List<String> getErrors() {
        return unmodifiableList(errors);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        ListIterator<String> iter = errors.listIterator();
        while (iter.hasNext()) {
            String error = iter.next();
            sb.append(error).append("\n");
        }

        return sb.toString();
    }
}
