package org.kaazing.netty.utils; /**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

import java.net.URLStreamHandlerFactory;
import java.util.Collection;

/**
 * This a <i>Service Provider Interface</i> <em>(SPI)</em> class. Implementors
 * can create extensions of this class. At runtime, the extensions will be
 * instantiated using the {@link ServiceLoader} APIs using the META-INF/services
 * mechanism in the {@link URLFactory} implementation.
 * <p/>
 * {@link URLStreamHandlerFactory} is a singleton that is registered using the
 * static method
 * {@link URL#setURLStreamHandlerFactory(URLStreamHandlerFactory)}. Also,
 * the {@link URL} objects can only be created for the following protocols:
 * -- http, https, file, ftp, and jar. In order to install protocol handlers
 * for other protocols, one has to hijack or override the system's singleton
 * {@link URLStreamHandlerFactory} instance with a custom implementation. The
 * objective of this class is to make the {@link URLStreamHandler} registration
 * for other protocols such as ws, wss, etc. feasible without hijacking the
 * system's {@link URLStreamHandlerFactory}.
 * <p/>
 */
public abstract class URLStreamHandlerFactorySpi implements URLStreamHandlerFactory {

    /**
     * Returns a list of supported protocols. This can be used to instantiate
     * appropriate {@link URLStreamHandler} objects based on the protocol.
     *
     * @return list of supported protocols
     */
    public abstract Collection<String> getSupportedProtocols();
}