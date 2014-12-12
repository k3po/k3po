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

package org.kaazing.k3po.driver.netty.channel.http;

import static org.kaazing.k3po.driver.netty.channel.LocationFactories.keepAuthorityOnly;

import java.net.URI;
import java.util.Map;

import org.jboss.netty.channel.ChannelException;
import org.kaazing.k3po.driver.netty.channel.ChannelAddress;
import org.kaazing.k3po.driver.netty.channel.ChannelAddressFactorySpi;
import org.kaazing.k3po.driver.netty.channel.LocationFactory;

public class HttpChannelAddressFactorySpi extends ChannelAddressFactorySpi {

    private static final LocationFactory TRANSPORT_FACTORY = keepAuthorityOnly("tcp");

    @Override
    public String getSchemeName() {
        return "http";
    }

    @Override
    protected LocationFactory getTransportFactory() {
        return TRANSPORT_FACTORY;
    }

    @Override
    protected ChannelAddress newChannelAddress0(URI location, ChannelAddress transport, Map<String, Object> options) {

        String host = location.getHost();
        int port = location.getPort();
        String path = location.getPath();

        if (host == null) {
            throw new ChannelException(String.format("%s host missing", getSchemeName()));
        }

        if (port == -1) {
            throw new ChannelException(String.format("%s port missing", getSchemeName()));
        }

        if (path == null || path.isEmpty()) {
            throw new ChannelException(String.format("%s path missing", getSchemeName()));
        }

        return super.newChannelAddress0(location, transport, options);
    }
}
