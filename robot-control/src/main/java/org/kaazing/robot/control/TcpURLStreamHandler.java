/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.control;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

// note: would be better as part of URLFactory.createURL(String)
final class TcpURLStreamHandler extends URLStreamHandler {

    @Override
    protected URLConnection openConnection(URL location) throws IOException {
        return new TcpURLConnection(location);
    }

    private static final class TcpURLConnection extends URLConnection implements Closeable {

        private final Socket socket;
        private final InetSocketAddress endpoint;

        public TcpURLConnection(URL location) throws IOException {
            super(location);

            String protocol = location.getProtocol();
            if (!"tcp".equals(protocol)) {
                throw new IllegalArgumentException("Unrecognized protocol: " + protocol);
            }

            String path = location.getPath();
            if (!path.isEmpty()) {
                throw new IllegalArgumentException("Unexpected path: " + path);
            }

            String hostname = location.getHost();
            if (hostname == null || hostname.isEmpty()) {
                throw new IllegalArgumentException("Expected hostname: " + hostname);
            }

            int port = location.getPort();
            if (port == -1) {
                throw new IllegalArgumentException("Expected port: " + port);
            }

            socket = new Socket();
            endpoint = new InetSocketAddress(hostname, port);
        }

        @Override
        public void connect() throws IOException {
            socket.connect(endpoint);
        }

        @Override
        public void setReadTimeout(int timeout) {
            try {
                socket.setSoTimeout(timeout);
            }
            catch (SocketException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public int getReadTimeout() {
            try {
                return socket.getSoTimeout();
            }
            catch (SocketException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return socket.getInputStream();
        }

        @Override
        public OutputStream getOutputStream() throws IOException {
            return socket.getOutputStream();
        }

        @Override
        public void close() throws IOException {
            socket.close();
        }
    }
}
