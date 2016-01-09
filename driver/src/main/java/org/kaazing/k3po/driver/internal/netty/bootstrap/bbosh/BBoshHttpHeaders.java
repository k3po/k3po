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
package org.kaazing.k3po.driver.internal.netty.bootstrap.bbosh;

import org.jboss.netty.handler.codec.http.HttpHeaders;


public final class BBoshHttpHeaders {

    private BBoshHttpHeaders() {
        // no instances
    }

    /**
     * BBOSH header names
     */
    public static final class Names {

        /**
         * {@code "Accept"}
         */
        public static final String ACCEPT = HttpHeaders.Names.ACCEPT;

        /**
         * {@code "Cache-Control"}
         */
        public static final String CACHE_CONTROL = HttpHeaders.Names.CACHE_CONTROL;

        /**
         * {@code "Content-Type"}
         */
        public static final String CONTENT_TYPE = HttpHeaders.Names.CONTENT_TYPE;

        /**
         * {@code "Location"}
         */
        public static final String LOCATION = HttpHeaders.Names.LOCATION;

        /**
         * {@code "X-Accept-Strategy"}
         */
        public static final String X_ACCEPT_STRATEGY = "X-Accept-Strategy";

        /**
         * {@code "X-Protocol"}
         */
        public static final String X_PROTOCOL = "X-Protocol";

        /**
         * {@code "X-Sequence-No"}
         */
        public static final String X_SEQUENCE_NO = "X-Sequence-No";

        /**
         * {@code "X-Strategy"}
         */
        public static final String X_STRATEGY = "X-Strategy";

    }

    /**
     * BBOSH header names
     */
    public static final class Values {

        /**
         * {@code "application/octet-stream"}
         */
        public static final String APPLICATION_OCTET_STREAM = "application/octet-stream";

        /**
         * {@code "bbosh/1.0"}
         */
        public static final String BBOSH_1_0 = "bbosh/1.0";

        /**
         * {@code "no-cache"}
         */
        public static final String NO_CACHE = HttpHeaders.Values.NO_CACHE;
    }

    public static int getIntHeader(HttpHeaders headers, String name) {
        String value = headers.get(name);
        if (value == null) {
            throw new NumberFormatException("header not found: " + name);
        }
        return Integer.parseInt(value);
    }
}
