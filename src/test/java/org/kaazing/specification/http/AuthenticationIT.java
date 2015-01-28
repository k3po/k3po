/**
 * Copyright (c) 2007-2014 Kaazing Corporation. All rights reserved.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kaazing.specification.http;

import org.junit.Ignore;
import org.junit.Test;

/**
 * rfc7235
 *
 */
public class AuthenticationIT {

    @Test
    @Ignore("Not Implemented")
    public void secureServerShould401ToAnyUnAuthorizedRequest() {
        // this includes 101

        // A server generating a 401 (Unauthorized) response MUST send a
        // WWW-Authenticate header field containing at least one challenge. A
        // server MAY generate a WWW-Authenticate header field in other response
        // messages to indicate that supplying credentials (or different
        // credentials) might affect the response.
    }

    @Test
    @Ignore("Not Implemented")
    public void proxyMustNotModifyWWWAuthenticateHeader() {

    }

    @Test
    @Ignore("Not Implemented")
    public void clientMaySendAuthenticationHeaderToServerAfter401() {
        // The "Authorization" header field allows a user agent to authenticate
        // itself with an origin server -- usually, but not necessarily, after
        // receiving a 401 (Unauthorized) response. Its value consists of
        // credentials containing the authentication information of the user
        // agent for the realm of the resource being requested.
    }

    @Test
    @Ignore("Not Implemented")
    public void clientMaySendAuthenticationHeaderToServerWithout401() {
        // Send authorization header that passes and should be allowed
        // to resource
    }

    @Test
    @Ignore("Not Implemented")
    public void proxyMustNotAlterAuthenticationHeader() {

    }

    @Test
    @Ignore("Not Implemented")
    public void secureProxyShouldSend407ToAnyUnAuthorizedRequest() {
        // The "Proxy-Authenticate" header field consists of at least one
        // challenge that indicates the authentication scheme(s) and parameters
        // applicable to the proxy for this effective request URI (Section 5.5
        // of [RFC7230]). A proxy MUST send at least one Proxy-Authenticate
        // header field in each 407 (Proxy Authentication Required) response
        // that it generates.
    }

    @Test
    @Ignore("Not Implemented")
    public void clientMaySendProxyAuthorizationHeaderInResponseTo407() {
        // The "Proxy-Authorization" header field allows the client to identify
        // itself (or its user) to a proxy that requires authentication. Its
        // value consists of credentials containing the authentication
        // information of the client for the proxy and/or realm of the resource
        // being requested.
    }

}
