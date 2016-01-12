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
package org.kaazing.specification.http2.headers;

import org.junit.Test;

/**
 * HTTP 2 - draft 16, sections 6.2 "HEADERS" (Frame Definitions) and 8.1.2 "HTTP Header Fields"
 */
public class ProcessHeadersIT {
    // Header lists are collections of zero or more header fields.  When
    // transmitted over a connection, a header list is serialized into a
    // header block using HTTP Header Compression [COMPRESSION].  The
    // serialized header block is then divided into one or more octet
    // sequences, called header block fragments, and transmitted within the
    // payload of HEADERS (Section 6.2), PUSH_PROMISE (Section 6.6) or
    // CONTINUATION (Section 6.10) frames.

    @Test
    public void processHeadersSingleFrame() {
        // process a single HEADERS frame with the END_HEADERS flag set
    }

    @Test
    public void processHeadersWithContinuationFrames() {
        // process a HEADERS frame without the END_HEADERS flag set, then
        // multiple CONTINUATION frames with header data having the END_HEADERS
        // flag set in the final frame
    }

    @Test
    public void shouldRejectHeaderFrameThatExceedsStreamLimit() {
        // A peer can limit the number of concurrently active streams using the
        // SETTINGS_MAX_CONCURRENT_STREAMS parameter (see Section 6.5.2) within
        // a SETTINGS frame.

        // Endpoints MUST NOT exceed the limit set by their peer.  An endpoint
        // that receives a HEADERS frame that causes their advertised concurrent
        // stream limit to be exceeded MUST treat this as a stream error
        // (Section 5.4.2) of type PROTOCOL_ERROR or REFUSED_STREAM.
    }

    @Test
    public void shouldRejectUncompressibleHeaderFrame() {
        // A receiver MUST terminate the connection with a connection error (Section 5.4.1)
        // of type COMPRESSION_ERROR if it does not decompress a header block.
    }
}
