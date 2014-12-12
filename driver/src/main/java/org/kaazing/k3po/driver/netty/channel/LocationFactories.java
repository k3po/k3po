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

package org.kaazing.k3po.driver.netty.channel;

import static java.lang.String.format;

import java.net.URI;
import java.net.URISyntaxException;

public abstract class LocationFactories {

    private LocationFactories() {
        // no-op
    }

    public static LocationFactory keepAuthorityOnly(String newScheme) {
        return new KeepAuthorityOnlyTransportFactory(newScheme);
    }

    public static LocationFactory changeSchemeOnly(String newScheme) {
        return new ChangeSchemeOnlyTransportFactory(newScheme);
    }

    private static final class KeepAuthorityOnlyTransportFactory extends LocationFactory {

        private final String newScheme;

        public KeepAuthorityOnlyTransportFactory(String newScheme) {
            this.newScheme = newScheme;
        }

        @Override
        public URI createURI(URI location) {
            return URI.create(format("%s://%s", newScheme, location.getAuthority()));
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
