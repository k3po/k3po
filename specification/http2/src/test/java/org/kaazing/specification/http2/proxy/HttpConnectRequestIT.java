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
package org.kaazing.specification.http2.proxy;

import org.junit.Test;

/**
 * HTTP 2 - draft 16, section 8.3 "The CONNECT Method"
 */
public class HttpConnectRequestIT {
    // In HTTP/2, the CONNECT method is used to establish a tunnel over a
    // single HTTP/2 stream to a remote host, for similar purposes.  The
    // HTTP header field mapping works as defined in Request Header Fields
    // (Section 8.1.2.3), with a few differences.  Specifically:
    //
    //   o  The ":method" header field is set to "CONNECT".
    //   o  The ":scheme" and ":path" header fields MUST be omitted.
    //   o  The ":authority" header field contains the host and port to
    //      connect to (equivalent to the authority-form of the request-target
    //      of CONNECT requests, see [RFC7230], Section 5.3).

    @Test
    public void processHTTPConnect() {
        // A proxy that supports CONNECT establishes a TCP connection [TCP] to
        // the server identified in the ":authority" header field.  Once this
        // connection is successfully established, the proxy sends a HEADERS
        // frame containing a 2xx series status code to the client, as defined
        // in [RFC7231], Section 4.3.6.
    }

    @Test
    public void shouldCloseStreamOnInvalidFrameType() {
        // Frame types other than DATA or stream management frames (RST_STREAM,
        // WINDOW_UPDATE, and PRIORITY) MUST NOT be sent on a connected stream,
        // and MUST be treated as a stream error (Section 5.4.2) if received.
    }
}
