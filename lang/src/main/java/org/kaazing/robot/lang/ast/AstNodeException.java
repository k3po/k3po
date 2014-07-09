/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.lang.ast;

import org.kaazing.robot.lang.LocationInfo;

public class AstNodeException extends Exception {

    private static final long serialVersionUID = 1L;

    private LocationInfo locationInfo;

    public AstNodeException() {
        super();
    }

    public AstNodeException(String message, Throwable cause) {
        super(message, cause);
    }

    public AstNodeException(String message) {
        super(message);
    }

    public AstNodeException(Throwable cause) {
        super(cause);
    }

    public LocationInfo getLocationInfo() {
        return locationInfo;
    }

    public AstNodeException initLocationInfo(LocationInfo locationInfo) {
        this.locationInfo = locationInfo;
        return this;
    }

}
