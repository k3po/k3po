/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package com.kaazing.netty.channel.tcp;

import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.Map;

import org.jboss.netty.channel.ChannelException;

import com.kaazing.netty.channel.ChannelAddress;
import com.kaazing.netty.channel.spi.ChannelAddressFactorySpi;

public class TcpChannelAddressFactorySpi extends ChannelAddressFactorySpi {

    @Override
    public String getSchemeName() {
        return "tcp";
    }

    @Override
    protected ChannelAddress newChannelAddress0(URI location, Map<String, Object> options) {

        String host = location.getHost();
        int port = location.getPort();
        String path = location.getPath();

        if (host == null) {
            throw new ChannelException(String.format("%s host missing", getSchemeName()));
        }

        if (port == -1) {
            throw new ChannelException(String.format("%s port missing", getSchemeName()));
        }

        if (path != null && !path.isEmpty()) {
            throw new ChannelException(String.format("%s path \"%s\" unexpected", getSchemeName(), path));
        }

        return super.newChannelAddress0(location, options);
    }

    public abstract static class TcpTransportable extends Transportable {

        @Override
        protected URI createTransportURI(URI location) {
            try {
                InetAddress inetAddress = InetAddress.getByName(location.getHost());
                String ipAddress = inetAddress.getHostAddress();
                int port = location.getPort();

                return URI.create(String.format("tcp://%s:%d", ipAddress, port));
            } catch (UnknownHostException e) {
                throw new ChannelException(e);
            }
        }

    }

}
