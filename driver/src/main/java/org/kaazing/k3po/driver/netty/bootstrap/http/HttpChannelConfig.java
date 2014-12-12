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

package org.kaazing.k3po.driver.netty.bootstrap.http;

import org.jboss.netty.channel.ChannelConfig;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.jboss.netty.handler.codec.http.QueryStringDecoder;
import org.jboss.netty.handler.codec.http.QueryStringEncoder;

public interface HttpChannelConfig extends ChannelConfig {

    void setMethod(HttpMethod method);

    HttpMethod getMethod();

    void setVersion(HttpVersion version);

    HttpVersion getVersion();

    void setStatus(HttpResponseStatus status);

    HttpResponseStatus getStatus();

    boolean hasReadHeaders();

    HttpHeaders getReadHeaders();

    boolean hasWriteHeaders();

    HttpHeaders getWriteHeaders();

    void setMaximumBufferedContentLength(int maxValue);

    int getMaximumBufferedContentLength();

    void setReadQuery(QueryStringDecoder readQuery);

    QueryStringDecoder getReadQuery();

    void setWriteQuery(QueryStringEncoder writeQuery);

    QueryStringEncoder getWriteQuery();
}
