/*
 * Copyright (c) 2014 "Kaazing Corporation," (www.kaazing.com)
 *
 * This file is part of Robot.
 *
 * Robot is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
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