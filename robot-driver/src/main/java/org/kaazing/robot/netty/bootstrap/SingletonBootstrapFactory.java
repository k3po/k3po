/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.netty.bootstrap;

import org.kaazing.netty.bootstrap.BootstrapFactory;

public final class SingletonBootstrapFactory {

    private static BootstrapFactory factory;

    public static BootstrapFactory getInstance() {
        if (factory == null) {
            factory = BootstrapFactory.newBootstrapFactory();
        }
        return factory;
    }

    private SingletonBootstrapFactory() {
    }
}
