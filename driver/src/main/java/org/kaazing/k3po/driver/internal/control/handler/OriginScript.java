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
package org.kaazing.k3po.driver.internal.control.handler;

import static java.lang.String.format;

import java.net.URI;
import java.net.URL;

public final class OriginScript {

    private OriginScript() {
        // Utility Class
    }

    public static String get(String origin) throws Exception {
        URI originURI = new URI(origin);
        String scheme = originURI.getScheme();
        switch (scheme) {
        case "http":
            URL url = originURI.toURL();
            // @formatter:off
                return ""
                        + "# This script is used for loading a browser origin page\n"
                        + "\n"
                        + "accept http://" + url.getHost() + ":" + url.getPort() + "/" + url.getPath() + "\n"
                        + "accepted\n"
                        + "connected\n"
                        + "read method \"GET\"\n"
                        + "read closed\n"
                        + "write version \"HTTP/1.1\"\n"
                        + "write status \"200\" \"OK\"\n"
                        + "write header content-length\n"
                        + "write \"<!DOCTYPE html>\"\n"
                        + "write \"<link rel=\\\"icon\\\" href=\\\"data:;base64,=\\\">\"\n"
                        + "write close\n\n\n";
                // @formatter:on
        default:
            throw new Exception(format("Could not find an origin script for scheme: %s", scheme));
        }
    }
}
