/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot;

import java.net.URI;

public abstract class RobotServerFactorySPI implements RobotServerFactory {
    @Override
    public abstract RobotServer createRobotServer(URI uri, boolean verbose);

    /**
     * Returns the name of the scheme provided by factories using this
     * service provider.
     */
    public abstract String getSchemeName();


}
