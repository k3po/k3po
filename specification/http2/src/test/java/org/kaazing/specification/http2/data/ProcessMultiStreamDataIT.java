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
public class ProcessMultiStreamDataIT {
    @Test
    public void shouldProcessMultipleStreamsOfDataFrames() {
        // simple test that negotiates http2, gets data frame for
        // initial response, then initiates multiple GET requests
        // that result in streams with overlapping data frames (e.g.
        // stream 2 sends a data frame without ES flag, stream 3 does likewise,
        // stream 2 sends another, stream 4 sends a data frame without ES flag,
        // stream 3 sends data frame, then 4, 3, 2, followed by 2-ES, 3-ES, 4-ES.
    }
}
