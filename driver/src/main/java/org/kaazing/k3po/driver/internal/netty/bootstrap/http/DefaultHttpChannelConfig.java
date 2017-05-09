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
package org.kaazing.k3po.driver.internal.netty.bootstrap.http;

import org.jboss.netty.handler.codec.http.DefaultHttpHeaders;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.jboss.netty.handler.codec.http.QueryStringDecoder;
import org.kaazing.k3po.driver.internal.behavior.handler.codec.http.QueryStringEncoder;
import org.kaazing.k3po.driver.internal.netty.bootstrap.channel.DefaultChannelConfig;

public class DefaultHttpChannelConfig extends DefaultChannelConfig implements HttpChannelConfig {

    private HttpVersion version;
    private HttpMethod method;
    private HttpRequestForm requestForm;
    private HttpResponseStatus status;
    private HttpHeaders readHeaders;
    private HttpHeaders writeHeaders;
    private int maximumBufferedContentLength;
    private QueryStringDecoder readQuery;
    private QueryStringEncoder writeQuery;
    private HttpHeaders writeTrailers;

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
    public void setRequestForm(HttpRequestForm requestForm) {
        this.requestForm = requestForm;
    }

    @Override
    public HttpRequestForm getRequestForm() {
        return requestForm;
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
    public boolean hasReadHeaders() {
        return readHeaders != null;
    }

    @Override
    public HttpHeaders getReadHeaders() {
        if (readHeaders == null) {
            readHeaders = new DefaultHttpHeaders();
        }
        return readHeaders;
    }

    @Override
    public boolean hasWriteHeaders() {
        return writeHeaders != null;
    }

    @Override
    public HttpHeaders getWriteHeaders() {
        if (writeHeaders == null) {
            writeHeaders = new DefaultHttpHeaders();
        }
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

    @Override
    public void setReadQuery(QueryStringDecoder readQuery) {
        this.readQuery = readQuery;
    }

    @Override
    public QueryStringDecoder getReadQuery() {
        return readQuery;
    }

    @Override
    public void setWriteQuery(QueryStringEncoder writeQuery) {
        this.writeQuery = writeQuery;
    }

    @Override
    public QueryStringEncoder getWriteQuery() {
        return writeQuery;
    }

    @Override
    public HttpHeaders getWriteTrailers() {
        if (writeTrailers == null) {
            writeTrailers = new DefaultHttpHeaders();
        }
        return writeTrailers;
    }

    @Override
    public HttpHeaders getReadTrailers() {
        if (readHeaders == null) {
            readHeaders = new DefaultHttpHeaders();
        }
        return readHeaders;
    }
}
