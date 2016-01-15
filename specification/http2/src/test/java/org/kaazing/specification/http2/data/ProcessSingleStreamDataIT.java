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
package org.kaazing.specification.http2.data;

import org.junit.Test;

/**
 * HTTP 2 - draft 16, sections 5.1 "Stream States" and 6.1 "DATA" (Frame Definitions)
 */
public class ProcessSingleStreamDataIT {
    @Test
    public void shouldProcessSingleCompleteDataFrame() {
        // a simple test that starts the HTTP2 connection over plain text, then
        // receives a single data frame with the END_STREAM flag set to indicate
        // there is no more data
    }

    @Test
    public void shouldProcessMultipleDataFrames() {
        // a simple test that starts the HTTP2 connection over plain text, then
        // receives a series of data frames with the END_STREAM flag set in the
        // final frame to indicate there is no more data
    }

    @Test
    public void shouldRejectDataFrameForIncorrectStreamId() {
        // DATA frames MUST be associated with a stream.  If a DATA frame is
        // received whose stream identifier field is 0x0, the recipient MUST
        // respond with a connection error (Section 5.4.1) of type
        // PROTOCOL_ERROR.
    }

    @Test
    public void shouldRejectDataFrameForClosedStream() {
        // If a DATA frame is received whose
        // stream is not in "open" or "half closed (local)" state, the recipient
        // MUST respond with a stream error (Section 5.4.2) of type
        // STREAM_CLOSED.
    }

    @Test
    public void shouldRejectDataFrameForIncorrectPadding() {
        // The total number of padding octets is determined by the value of the
        // Pad Length field.  If the length of the padding is the length of the
        // frame payload or greater, the recipient MUST treat this as a
        // connection error (Section 5.4.1) of type PROTOCOL_ERROR.
    }
}
