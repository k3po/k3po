/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package com.kaazing.netty.channel;

import java.net.SocketAddress;
import java.net.URI;

import org.jboss.netty.channel.ChannelException;

public class ChannelAddress extends SocketAddress {

    private static final long serialVersionUID = 1L;

    private final URI location;
    private final boolean secure;
    private final boolean ephemeral;

    private final ChannelAddress transport;

    public ChannelAddress(URI location) {
        this(location, false, null);
    }

    public ChannelAddress(URI location, boolean secure) {
        this(location, secure, null);
    }

    public ChannelAddress(URI location, ChannelAddress transport) {
        this(location, false, transport);
    }

    public ChannelAddress(URI location, boolean secure, ChannelAddress transport) {
        this(location, secure, transport, false);
    }

    private ChannelAddress(URI location, boolean secure, ChannelAddress transport, boolean ephemeral) {
        if (location == null) {
            throw new NullPointerException("location");
        }

        this.location = location;
        this.secure = secure;
        this.transport = transport;
        this.ephemeral = ephemeral;
    }

    public URI getLocation() {
        return location;
    }

    public boolean isSecure() {
        return secure;
    }

    public ChannelAddress getTransport() {
        return transport;
    }

    public ChannelAddress newEphemeralAddress() {
        if (ephemeral) {
            throw new ChannelException("Channel address is already ephemeral");
        }

        return new ChannelAddress(location, secure, transport, true);
    }

    @Override
    public int hashCode() {
        return location.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof ChannelAddress)) {
            return false;
        }

        if (this == o) {
            return true;
        }

        ChannelAddress that = (ChannelAddress) o;
        if (this.ephemeral ^ that.ephemeral) {
            return false;
        }

        return this.location.equals(that.location)
                && this.secure == that.secure
                && ((this.transport == null && that.transport == null) || (this.transport != null && this.transport
                        .equals(that.transport)));
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(location);
        if (transport != null) {
            sb.append(" @ ").append(transport);
        }
        return sb.toString();
    }
}
