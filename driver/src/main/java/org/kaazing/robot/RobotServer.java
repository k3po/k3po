/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot;

import java.util.concurrent.TimeoutException;

public interface RobotServer {
    void start() throws Exception;

    void stop() throws TimeoutException;

    void join() throws InterruptedException;
}
