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
 * HTTP 2 - draft 16, section 5.3 "Stream priority" (excluding stream dependencies)
 */
public class SimpleStreamPrioritizationIT {
    @Test
    public void processPriorityFrame() {
        // The PRIORITY frame (type=0x2) specifies the sender-advised priority
        // of a stream (Section 5.3).  It can be sent at any time for any
        // stream, including idle or closed streams.
    }

    @Test
    public void rejectPriorityFrameForStreamZero() {
        // The PRIORITY frame is associated with an existing stream.  If a
        // PRIORITY frame is received with a stream identifier of 0x0, the
        // recipient MUST respond with a connection error (Section 5.4.1) of
        // type PROTOCOL_ERROR.
    }

    @Test
    public void rejectPriorityFrameWithInvalidSize() {
        // A PRIORITY frame with a length other than 5 octets MUST be treated as
        // a stream error (Section 5.4.2) of type FRAME_SIZE_ERROR.
    }
}
