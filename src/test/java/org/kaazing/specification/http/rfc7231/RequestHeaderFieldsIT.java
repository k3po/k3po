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
package org.kaazing.specification.http.rfc7231;

import org.junit.Ignore;
import org.junit.Test;

/**
 * Test to validate behavior as specified in <a href="https://tools.ietf.org/html/rfc7231#section-5">RFC 7231 section 5:
 * Request Header Fields</a>.
 */
public class RequestHeaderFieldsIT {

    /**
     * See <a href="https://tools.ietf.org/html/rfc7231#section-5.1">RFC 7230 section 5.1: Controls</a>.
     */
    @Test
    @Ignore("Not Implemented")
    public void serverShouldRespondToMeetableExpectWith417() {
        // A server that receives an Expect field-value other than 100-continue
        // MAY respond with a 417 (Expectation Failed) status code to indicate
        // that the unexpected expectation cannot be met.
    }

    /**
     * See <a href="https://tools.ietf.org/html/rfc7231#section-5.1">RFC 7230 section 5.1: Controls</a>.
     */
    @Test
    @Ignore("Not Implemented")
    public void intermediaryMustDecrementMaxForwardHeaderOnOptionsOrTraceRequest() {

    }

    /**
     * See <a href="https://tools.ietf.org/html/rfc7231#section-5.1">RFC 7230 section 5.1: Controls</a>.
     */
    @Test
    @Ignore("Not Implemented")
    public void intermediaryThatReceivesMaxForwardOfZeroOnOptionsOrTraceMustRespondToRequest() {

    }
}
