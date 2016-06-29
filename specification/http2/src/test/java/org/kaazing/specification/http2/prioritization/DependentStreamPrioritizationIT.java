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
package org.kaazing.specification.http2.prioritization;

import org.junit.Test;

/**
 * HTTP 2 - draft 16, sections 5.3.1 "Stream Dependencies" and 5.3.2 "Dependency Weighting"
 */
public class DependentStreamPrioritizationIT {
    // The HEADERS frame contains the following field:
    //
    //        Stream Dependency:  A 31-bit stream identifier for the stream that this
    //                            stream depends on, see Section 5.3.  This field is
    //                            only present if the PRIORITY flag is set.

    @Test
    public void createDependentStream() {
        // simple test that creates two streams with the second as a dependency of the first
    }

    @Test
    public void rejectSelfDependentStream() {
        // A stream cannot depend on itself.  An endpoint MUST treat this as a
        // stream error (Section 5.4.2) of type PROTOCOL_ERROR.
    }

    @Test
    public void updateWeightOfDependentStream() {
        // After creating a dependent stream, a peer can send a PRIORITY frame containing
        // the stream dependency and new weight to adjust the weight of a dependent stream.
    }

    @Test
    public void adjustTreeOfDependentStreams() {
        // See https://tools.ietf.org/html/draft-ietf-httpbis-http2-16#section-5.3.3
        // for a discussion of re-prioritizing a stream to be a dependent of one of its
        // dependencies, thus adjusting the tree of stream dependencies.
    }
}
