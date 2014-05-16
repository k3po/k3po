/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.cli.utils;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Collections.enumeration;

public class ServiceClassLoader extends ClassLoader {
    private final String servicePath;
    private final URL[] serviceURLs;

    public ServiceClassLoader(
            Class<?> serviceClass,
            URL... serviceURLs) {
        super();
        this.servicePath = format("META-INF/services/%s", serviceClass.getName());
        this.serviceURLs = serviceURLs;
    }

    public ServiceClassLoader(ClassLoader parent,
                              Class<?> serviceClass,
                              URL... serviceURLs) {
        super(parent);
        this.servicePath = format("META-INF/services/%s", serviceClass.getName());
        this.serviceURLs = serviceURLs;
    }

    @Override
    protected Enumeration<URL> findResources(String name)
            throws IOException {
        if (servicePath.equals(name)) {
            return enumeration(asList(serviceURLs));
        }
        return super.findResources(name);
    }
}