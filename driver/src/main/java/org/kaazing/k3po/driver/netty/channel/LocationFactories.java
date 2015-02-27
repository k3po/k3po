/*
 * Copyright 2014, Kaazing Corporation. All rights reserved.
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

package org.kaazing.k3po.driver.netty.channel;

import static java.lang.String.format;

import java.net.URI;
import java.net.URISyntaxException;

public abstract class LocationFactories {

    private LocationFactories() {
        // no-op
    }

    public static LocationFactory keepAuthorityOnly(String newScheme, String oldScheme) {
        return new KeepAuthorityOnlyTransportFactory(newScheme, oldScheme);
    }

    public static LocationFactory changeSchemeOnly(String newScheme) {
        return new ChangeSchemeOnlyTransportFactory(newScheme);
    }

    private static final class KeepAuthorityOnlyTransportFactory extends LocationFactory {

        private final String newScheme;
        private String oldScheme;

        public KeepAuthorityOnlyTransportFactory(String newScheme, String oldScheme) {
            this.newScheme = newScheme;
            this.oldScheme = oldScheme;
        }

        @Override
        public URI createURI(URI location) {
            URI result = null;
            switch (oldScheme) {
            case "http":
                if (location.getPort() == -1) {
                    result = URI.create(format("%s://%s:80", newScheme, location.getAuthority()));
                }
                break;
            case "https":
                if (location.getPort() == -1) {
                    result = URI.create(format("%s://%s:443", newScheme, location.getAuthority()));
                }
                break;
            default:
            }
            if (result == null) {
                result = URI.create(format("%s://%s", newScheme, location.getAuthority()));
            }
            return result;
        }
    }

    private static final class ChangeSchemeOnlyTransportFactory extends LocationFactory {

        private final String newScheme;

        public ChangeSchemeOnlyTransportFactory(String newScheme) {
            this.newScheme = newScheme;
        }

        @Override
        public URI createURI(URI location) {
            String scheme = location.getScheme();
            if (newScheme.equals(scheme)) {
                return location;
            }
            String authority = location.getAuthority();
            String path = location.getPath();
            String query = location.getQuery();
            String fragment = location.getFragment();

            try {
                return new URI(newScheme, authority, path, query, fragment);
            }
            catch (URISyntaxException x) {
                IllegalArgumentException y = new IllegalArgumentException();
                y.initCause(x);
                throw y;
            }
        }
    }
}
