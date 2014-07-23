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

package org.kaazing.robot.driver.netty.utils;


import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * {@link URLFactory} supports static methods to instantiate URL objects that
 * support custom protocols/schemes. Since {@link URL} by default only
 * guarantees protocol handlers for
 * <p/>
 * {@code
 * http, https, ftp, file, and jar
 * }
 * <p/>
 * and the {@link URLStreamHandlerFactory} registration is not extensible,
 * URLFactory will allow application developers to create URL objects for
 * protocols such as
 * <p/>
 * {@code
 * ws, wse, wsn, wss, wse+ssl
 * }
 * <p/>
 * like this:
 * <p/>
 * {@code
 * URL url = URLFactory.createURL("ws://<hostname>:<port>/<serviceName>");
 * }
 */
public class URLFactory {
    private static final Map<String, URLStreamHandlerFactorySpi> _factories;

    static {
        Class<URLStreamHandlerFactorySpi> clazz = URLStreamHandlerFactorySpi.class;
        ServiceLoader<URLStreamHandlerFactorySpi> loader = ServiceLoader.load(clazz);
        _factories = new HashMap<String, URLStreamHandlerFactorySpi>();

        for (URLStreamHandlerFactorySpi factory : loader) {
            Collection<String> protocols = factory.getSupportedProtocols();

            if (protocols != null && !protocols.isEmpty()) {
                for (String protocol : protocols) {
                    _factories.put(protocol, factory);
                }
            }
        }
    }

    /**
     * Creates a URL object from the String representation.
     *
     * @param spec the String to parse as a URL.
     * @return URL representing the passed in String
     * @throws MalformedURLException if no protocol is specified, or an
     *                               unknown protocol is found, or spec is null
     */
    public static URL createURL(String spec) throws MalformedURLException {
        return createURL(null, spec);
    }

    /**
     * Creates a URL by parsing the given spec within a specified context. The
     * new URL is created from the given context URL and the spec argument as
     * described in RFC2396 "Uniform Resource Identifiers : Generic Syntax" :
     * <p/>
     * {@code
     * <scheme>://<authority><path>?<query>#<fragment>
     * }
     * <p/>
     * The reference is parsed into the scheme, authority, path, query and
     * fragment parts. If the path component is empty and the scheme, authority,
     * and query components are undefined, then the new URL is a reference to
     * the current document. Otherwise, the fragment and query parts present in
     * the spec are used in the new URL.
     * <p/>
     * If the scheme component is defined in the given spec and does not match
     * the scheme of the context, then the new URL is created as an absolute URL
     * based on the spec alone. Otherwise the scheme component is inherited from
     * the context URL.
     * <p/>
     * If the authority component is present in the spec then the spec is
     * treated as absolute and the spec authority and path will replace the
     * context authority and path. If the authority component is absent in the
     * spec then the authority of the new URL will be inherited from the context.
     * <p/>
     * If the spec's path component begins with a slash character "/" then the
     * path is treated as absolute and the spec path replaces the context path.
     * <p/>
     * Otherwise, the path is treated as a relative path and is appended to the
     * context path, as described in RFC2396. Also, in this case, the path is
     * canonicalized through the removal of directory changes made by
     * occurrences of ".." and ".".
     * <p/>
     * For a more detailed description of URL parsing, refer to RFC2396.
     *
     * @param context the context in which to parse the specification
     * @param spec    the String to parse as a URL
     * @return URL created using the spec within the specified context
     * @throws MalformedURLException if no protocol is specified, or an unknown
     *                               protocol is found, or spec is null.
     */
    public static URL createURL(URL context, String spec)
            throws MalformedURLException {
        if ((spec == null) || (spec.trim().length() == 0)) {
            return new URL(context, spec);
        }

        String protocol = URI.create(spec).getScheme();
        URLStreamHandlerFactory factory = _factories.get(protocol);

        // If there is no URLStreamHandlerFactory registered for the
        // scheme/protocol, then we just use the regular URL constructor.
        if (factory == null) {
            return new URL(context, spec);
        }

        // If there is a URLStreamHandlerFactory associated for the
        // scheme/protocol, then we create a URLStreamHandler. And, then use
        // then use the URLStreamHandler to create a URL.
        URLStreamHandler handler = factory.createURLStreamHandler(protocol);
        return new URL(context, spec, handler);
    }

    /**
     * Creates a URL from the specified protocol name, host name, and file name.
     * The default port for the specified protocol is used.
     * <p/>
     * This method is equivalent to calling the four-argument method with
     * the arguments being protocol, host, -1, and file. No validation of the
     * inputs is performed by this method.
     *
     * @param protocol the name of the protocol to use
     * @param host     the name of the host
     * @param file     the file on the host
     * @return URL created using specified protocol, host, and file
     * @throws MalformedURLException if an unknown protocol is specified
     */
    public static URL createURL(String protocol, String host, String file)
            throws MalformedURLException {
        return createURL(protocol, host, -1, file);

    }

    /**
     * Creates a URL from the specified protocol name, host name, port number,
     * and file name.
     * <p/>
     * No validation of the inputs is performed by this method.
     *
     * @param protocol the name of the protocol to use
     * @param host     the name of the host
     * @param port     the port number
     * @param file     the file on the host
     * @return URL created using specified protocol, host, and file
     * @throws MalformedURLException if an unknown protocol is specified
     */
    public static URL createURL(String protocol,
                                String host,
                                int port,
                                String file) throws MalformedURLException {
        URLStreamHandlerFactory factory = _factories.get(protocol);

        // If there is no URLStreamHandlerFactory registered for the
        // scheme/protocol, then we just use the regular URL constructor.
        if (factory == null) {
            return new URL(protocol, host, port, file);
        }

        // If there is a URLStreamHandlerFactory associated for the
        // scheme/protocol, then we create a URLStreamHandler. And, then use
        // then use the URLStreamHandler to create a URL.
        URLStreamHandler handler = factory.createURLStreamHandler(protocol);
        return new URL(protocol, host, port, file, handler);
    }
}