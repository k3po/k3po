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

package org.kaazing.k3po.driver.netty.bootstrap.bbosh;

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
