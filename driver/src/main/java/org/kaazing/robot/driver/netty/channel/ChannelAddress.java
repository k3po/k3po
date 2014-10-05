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

package org.kaazing.robot.driver.netty.channel;

import java.net.SocketAddress;
import java.net.URI;
import java.util.Objects;

import org.jboss.netty.channel.ChannelException;

public class ChannelAddress extends SocketAddress {

    private static final long serialVersionUID = 1L;

    private final URI location;
    private final boolean ephemeral;

    private final ChannelAddress transport;

    public ChannelAddress(URI location) {
        this(location, null);
    }

    public ChannelAddress(URI location, ChannelAddress transport) {
        this(location, transport, false);
    }

    public ChannelAddress(URI location, ChannelAddress transport, boolean ephemeral) {
        if (location == null) {
            throw new NullPointerException("location");
        }

        this.location = location;
        this.transport = transport;
        this.ephemeral = ephemeral;
    }

    public URI getLocation() {
        return location;
    }

    public ChannelAddress getTransport() {
        return transport;
    }

    public ChannelAddress newEphemeralAddress() {
        if (ephemeral) {
            throw new ChannelException("Channel address is already ephemeral");
        }

        return new ChannelAddress(location, transport, true);
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

        return Objects.equals(this.location, that.location) &&
                Objects.equals(this.transport, that.transport);
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
