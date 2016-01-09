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
package org.kaazing.specification.http2.ping;

import org.junit.Test;

/**
 * HTTP 2 - draft 16, section 6.7 "PING" (Frame Definitions)
 */
public class PingControlFrameIT {
    @Test
    public void shouldAcknowledgePingFrame() {
        // Receivers of a PING frame that does not include an ACK flag MUST send
        // a PING frame with the ACK flag set in response, with an identical
        // payload.
    }

    @Test
    public void shouldReceivePingFrameAcknowledgement() {
        // PING frames can be sent from any endpoint.
    }

    @Test
    public void shouldRejectPingFrameWithInvalidStreamId() {
        // If a PING frame is received with a stream identifier field value
        // other than 0x0, the recipient MUST respond with a connection error
        // (Section 5.4.1) of type PROTOCOL_ERROR.
    }

    @Test
    public void shouldRejectPingFrameWithInvalidSize() {
        // Receipt of a PING frame with a length field value other than 8 MUST
        // be treated as a connection error (Section 5.4.1) of type FRAME_SIZE_ERROR.
    }
}
