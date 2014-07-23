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

package org.kaazing.robot.driver.netty.channel.tcp;

import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.Map;

import org.jboss.netty.channel.ChannelException;

import org.kaazing.robot.driver.netty.channel.ChannelAddress;
import org.kaazing.robot.driver.netty.channel.spi.ChannelAddressFactorySpi;

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
