/**
 * Copyright 2007-2015, Kaazing Corporation. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kaazing.k3po.driver.internal.netty.channel.agrona;

import static org.kaazing.k3po.driver.internal.netty.channel.agrona.AgronaChannelAddress.OPTION_READER;
import static org.kaazing.k3po.driver.internal.netty.channel.agrona.AgronaChannelAddress.OPTION_WRITER;

import java.net.URI;
import java.util.Map;

import org.jboss.netty.channel.ChannelException;
import org.kaazing.k3po.driver.internal.netty.channel.ChannelAddress;
import org.kaazing.k3po.driver.internal.netty.channel.ChannelAddressFactorySpi;

public class AgronaChannelAddressFactorySpi extends ChannelAddressFactorySpi {

    @Override
    public String getSchemeName() {
        return "agrona";
    }

    @Override
    protected ChannelAddress newChannelAddress0(URI location, ChannelAddress transport, Map<String, Object> options) {

        String host = location.getHost();
        int port = location.getPort();
        String path = location.getPath();

        if (host == null) {
            throw new ChannelException(String.format("%s host missing", getSchemeName()));
        }

        if (port != -1) {
            throw new ChannelException(String.format("%s port unexpected", getSchemeName()));
        }

        if (path == null || path.isEmpty()) {
            throw new ChannelException(String.format("%s path missing", getSchemeName()));
        }

        if (!"stream".equals(host)) {
            throw new ChannelException(String.format("%s host is not 'stream'", getSchemeName()));
        }

        if (!"/bidirectional".equals(path)) {
            throw new ChannelException(String.format("%s path is not '/bidirectional'", getSchemeName()));
        }

        if (options == null || !options.containsKey(OPTION_READER)) {
            throw new ChannelException(String.format("%s reader option missing", getSchemeName()));
        }

        Object reader = options.get(OPTION_READER);
        if (!(reader instanceof ChannelReader)) {
            throw new ChannelException(String.format("%s reader option incorrect type", getSchemeName()));
        }

        if (options == null || !options.containsKey(OPTION_WRITER)) {
            throw new ChannelException(String.format("%s writer option missing", getSchemeName()));
        }

        Object writer = options.get(OPTION_WRITER);
        if (!(writer instanceof ChannelWriter)) {
            throw new ChannelException(String.format("%s reader option incorrect type", getSchemeName()));
        }

        return new AgronaChannelAddress(location, (ChannelReader) reader, (ChannelWriter) writer) ;
    }

}
