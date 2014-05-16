/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot;

import java.net.URI;

public class TcpControlledRobotServerFactorySPI extends RobotServerFactorySPI {

    @Override
    public RobotServer createRobotServer(URI uri, boolean verbose) {
        return new TcpControlledRobotServer(uri, verbose);
    }

    @Override
    public String getSchemeName() {
        return "tcp";
    }
}