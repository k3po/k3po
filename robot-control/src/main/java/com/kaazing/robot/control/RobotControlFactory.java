/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package com.kaazing.robot.control;

import java.net.URI;
import java.net.URL;

public class RobotControlFactory {

    public RobotControl newClient(URI controlURI) throws Exception {

        String scheme = controlURI.getScheme();
        if (!"tcp".equals(scheme)) {
            throw new IllegalArgumentException("Unrecognized scheme: " + scheme);
        }

        URL location = new URL(null, controlURI.toASCIIString(), new TcpURLStreamHandler());

        return new RobotControl(location);
    }
}
