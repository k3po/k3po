/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.maven.plugins.logging;

import org.apache.maven.plugin.logging.Log;

import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.logging.InternalLoggerFactory;

public class MavenLoggerFactory extends InternalLoggerFactory {

    private final Log logger;

    public MavenLoggerFactory(Log logger) {
        this.logger = logger;
    }

    @Override
    public InternalLogger newInstance(String name) {
        return new MavenLogger(logger);
    }
}
