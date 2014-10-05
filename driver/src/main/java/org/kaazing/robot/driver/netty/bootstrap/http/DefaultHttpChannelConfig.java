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

package org.kaazing.robot.driver.netty.bootstrap.http;

import org.jboss.netty.channel.DefaultChannelConfig;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;

public class DefaultHttpChannelConfig extends DefaultChannelConfig implements HttpChannelConfig {

    private HttpVersion version;
    private HttpMethod method;
    private HttpResponseStatus status;
    private HttpHeaders readHeaders;
    private HttpHeaders writeHeaders;
    private int maximumBufferedContentLength;

    @Override
    public void setMethod(HttpMethod method) {
        this.method = method;
    }

    @Override
    public HttpMethod getMethod() {
        return method;
    }

    @Override
    public void setVersion(HttpVersion version) {
        this.version = version;
    }

    @Override
    public HttpVersion getVersion() {
        return version;
    }

    @Override
    public void setStatus(HttpResponseStatus status) {
        this.status = status;
    }

    @Override
    public HttpResponseStatus getStatus() {
        return status;
    }

    @Override
    public void setReadHeaders(HttpHeaders readHeaders) {
        this.readHeaders = readHeaders;
    }

    @Override
    public HttpHeaders getReadHeaders() {
        return readHeaders;
    }

    @Override
    public void setWriteHeaders(HttpHeaders writeHeaders) {
        this.writeHeaders = writeHeaders;
    }

    @Override
    public HttpHeaders getWriteHeaders() {
        return writeHeaders;
    }

    @Override
    public void setMaximumBufferedContentLength(int maxValue) {
        maximumBufferedContentLength = maxValue;
    }

    @Override
    public int getMaximumBufferedContentLength() {
        return maximumBufferedContentLength;
    }
}
