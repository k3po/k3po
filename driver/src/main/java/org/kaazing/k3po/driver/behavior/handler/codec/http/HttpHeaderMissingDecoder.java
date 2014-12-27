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

package org.kaazing.k3po.driver.behavior.handler.codec.http;

import static java.lang.String.format;

import java.util.List;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.kaazing.k3po.driver.behavior.ScriptProgressException;
import org.kaazing.k3po.driver.behavior.handler.codec.AbstractConfigDecoder;
import org.kaazing.k3po.driver.netty.bootstrap.http.HttpChannelConfig;

public class HttpHeaderMissingDecoder extends AbstractConfigDecoder {

    private String name;

    public HttpHeaderMissingDecoder(String name) {
        this.name = name;
    }

    @Override
    public void decode(Channel channel) throws Exception {
        HttpChannelConfig httpConfig = (HttpChannelConfig) channel.getConfig();
        HttpHeaders headers = httpConfig.getReadHeaders();
        List<String> headerValues = headers.getAll(name);
        if (!headerValues.isEmpty()) {
            throw new ScriptProgressException(getRegionInfo(), format("HTTP header not missing: %s", name));
        }
    }

    @Override
    public String toString() {
        return format("http:header %s missing", name);
    }

}
