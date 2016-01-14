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
package org.kaazing.specification.http2.flowcontrol;

import org.junit.Test;

/**
 * HTTP 2 - draft 16, section 5.2 "Flow Control"
 */
public class FlowControlViolationsIT {
    // 5.2.1.  Flow Control Principles
    //
    // #3 [snip] A sender MUST respect flow control limits imposed by a receiver.
    // Clients, servers and intermediaries all independently advertise
    // their flow control window as a receiver and abide by the flow control
    // limits set by their peer when sending.

    // As a result of the above section in the spec, and due to Denial of Service concerns,
    // a receiver MAY send a GOAWAY frame to a mis-behaving sender and close the connection.
    // This test will assert that behavior.

    @Test
    public void shouldCloseConnectionThatViolatesFlowControl() {
        // This test will establish a flow control window that is then violated by the
        // client resulting in the server sending a GOAWAY frame and closing the connection.
    }
}
