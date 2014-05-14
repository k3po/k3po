/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot;

import java.net.URI;
import java.util.concurrent.TimeoutException;

public interface RobotServer {
    void setAccept(URI acceptURI);

    void setVerbose(boolean verbose);

    void start() throws Exception;

    void start(String format) throws Exception;

    void stop() throws TimeoutException;

    void join() throws InterruptedException;
}
