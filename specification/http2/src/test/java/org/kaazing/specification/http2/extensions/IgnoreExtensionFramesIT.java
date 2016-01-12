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
package org.kaazing.specification.http2.extensions;

import org.junit.Test;

/**
 * HTTP 2 - draft 16, section 5.5 "Extending HTTP/2"
 */
public class IgnoreExtensionFramesIT {
    @Test
    public void shouldIgnoreExtensionFrame() {
        // HTTP/2 permits extension of the protocol.  Protocol extensions can be
        // used to provide additional services or alter any aspect of the
        // protocol, within the limitations described in this section.
        // Extensions are effective only within the scope of a single HTTP/2
        // connection.

        // Implementations MUST ignore unknown or unsupported values in all
        // extensible protocol elements.  Implementations MUST discard frames
        // that have unknown or unsupported types.  This means that any of these
        // extension points can be safely used by extensions without prior
        // arrangement or negotiation.
    }

    @Test
    public void shouldRejectExtensionFrameInHeaderBlock() {
        // Extension frames that appear in the middle of a header block
        // (Section 4.3) are not permitted; these MUST be treated as a
        // connection error (Section 5.4.1) of type PROTOCOL_ERROR.
    }
}
