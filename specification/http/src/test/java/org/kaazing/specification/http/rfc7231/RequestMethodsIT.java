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
package org.kaazing.specification.http.rfc7231;

import org.junit.Ignore;
import org.junit.Test;

/**
 * Test to validate behavior as specified in <a href="https://tools.ietf.org/html/rfc7231#section-4">RFC 7231 section 4:
 * Request Methods</a>.
 */
public class RequestMethodsIT {

    @Test
    @Ignore("Not Implemented")
    public void serverMustRespondToUnknownMethodWith501() {
        // When a request method is received
        // that is unrecognized or not implemented by an origin server, the
        // origin server SHOULD respond with the 501 (Not Implemented) status
        // code.
    }

    @Test
    @Ignore("Not Implemented")
    public void serverShouldImplementGet() {
        // General purpose servers should implement GET and HEAD
    }

    @Test
    @Ignore("Not Implemented")
    public void serverShouldImplementHead() {
        // General purpose servers should implement GET and HEAD
    }

    @Test
    @Ignore("Not Implemented")
    public void serverShouldRespondWith405ToUnrecognizedMethods() {
        // 405 (Method Not Allowed)
    }
}
