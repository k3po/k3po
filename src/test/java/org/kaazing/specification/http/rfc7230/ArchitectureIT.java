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
package org.kaazing.specification.http.rfc7230;

import org.junit.Ignore;
import org.junit.Test;

/**
 * Test to validate behavior as specified in <a href="https://tools.ietf.org/html/rfc7230#section-2">RFC 7230 section 2:
 * Architecture</a>.
 */
public class ArchitectureIT {

    /**
     * See <a href="https://tools.ietf.org/html/rfc7230#section-2.6">RFC 7230 section 2.6: Protocol Versioning</a>.
     */
    @Test
    @Ignore("Not Implemented")
    public void downstreamMustSendHttpVersion() {

    }

    /**
     * See <a href="https://tools.ietf.org/html/rfc7230#section-2.6">RFC 7230 section 2.6: Protocol Versioning</a>.
     */
    @Test
    @Ignore("Not Implemented")
    public void upstreamShouldFailOnAnyLowerCaseHttpInVersion() {
        // return 505 HTTP Version Not Supported
    }

    /**
     * See <a href="https://tools.ietf.org/html/rfc7230#section-2.6">RFC 7230 section 2.6: Protocol Versioning</a>.
     */
    @Test
    @Ignore("Not Implemented")
    public void upstreamMustFailOnVersionMissingDot() {
        // return 505
    }

    /**
     * See <a href="https://tools.ietf.org/html/rfc7230#section-2.6">RFC 7230 section 2.6: Protocol Versioning</a>.
     */
    @Test
    @Ignore("Not Implemented")
    public void upstreamMustFailOnVersionHavingTwoDots() {
        // return 505
    }

    /**
     * See <a href="https://tools.ietf.org/html/rfc7230#section-2.6">RFC 7230 section 2.6: Protocol Versioning</a>.
     */
    @Test
    @Ignore("Not Implemented")
    public void inboundMustSend505OnUnparseableVersion() {
    }

    /**
     * See <a href="https://tools.ietf.org/html/rfc7230#section-2.6">RFC 7230 section 2.6: Protocol Versioning</a>.
     */
    @Test
    @Ignore("Not Implemented")
    public void inboundMustReplyWithVersionOneDotOneWhenReceivedVersionWithMajorVersionOneAndMinorVersionGreaterThanOne() {
        // return response with 1.1
    }

    /**
     * See <a href="https://tools.ietf.org/html/rfc7230#section-2.6">RFC 7230 section 2.6: Protocol Versioning</a>.
     */
    @Test
    @Ignore("Not Implemented")
    public void originServerShouldSend505OnMajorVersionNotEqualToOne() {

    }

    /**
     * See <a href="https://tools.ietf.org/html/rfc7230#section-2.7">RFC 7230 section 2.7: Uniform Resource
     * Identifiers</a>.
     */
    @Test
    @Ignore("Not Implemented")
    public void clientMustSendHostIdentifier() {

    }

    /**
     * See <a href="https://tools.ietf.org/html/rfc7230#section-2.7">RFC 7230 section 2.7: Uniform Resource
     * Identifiers</a>.
     */
    @Test
    @Ignore("Not Implemented")
    public void inboundMustRejectRequestsMissingHostIdentifier() {
        // 400 Bad Request
    }

    /**
     * See <a href="https://tools.ietf.org/html/rfc7230#section-2.7">RFC 7230 section 2.7: Uniform Resource
     * Identifiers</a>.
     */
    @Test
    @Ignore("Not Implemented")
    public void inboundMustRejectRequestsWithInvalidHostIdentifier() {
        // 400 Bad Request
    }

    /**
     * See <a href="https://tools.ietf.org/html/rfc7230#section-2.7">RFC 7230 section 2.7: Uniform Resource
     * Identifiers</a>.
     */
    @Test
    @Ignore("Not Implemented")
    public void inboundMustRejectRequestWithUserInfoOnURI() {
        // ex http://localhost:8000@username:password
    }

    /**
     * See <a href="https://tools.ietf.org/html/rfc7230#section-2.7">RFC 7230 section 2.7: Uniform Resource
     * Identifiers</a>.
     */
    @Test
    @Ignore("Not Implemented")
    public void inboundShouldAllowRequestsWithPercentCharsInURI() {
        // equivalent %chars to normal chars ?
    }
}
