/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.control;

import org.kaazing.robot.control.command.Command;
import org.kaazing.robot.control.event.CommandEvent;

import java.util.concurrent.TimeUnit;

public interface RobotControl {
    void connect() throws Exception;

    void disconnect() throws Exception;

    void writeCommand(Command command) throws Exception;

    CommandEvent readEvent() throws Exception;

    CommandEvent readEvent(int timeout, TimeUnit unit) throws Exception;
}
