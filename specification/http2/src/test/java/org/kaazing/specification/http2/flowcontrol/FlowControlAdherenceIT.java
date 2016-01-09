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
 * HTTP 2 - draft 16, section 5.2 "Flow Control" and section 6.9 "WINDOW_UPDATE"
 */
public class FlowControlAdherenceIT {
    // 6.9.1.  The Flow Control Window

    // Flow control in HTTP/2 is implemented using a window kept by each
    // sender on every stream.  The flow control window is a simple integer
    // value that indicates how many octets of data the sender is permitted
    // to transmit; as such, its size is a measure of the buffering capacity
    // of the receiver.

    @Test
    public void shouldProcessWindowUpdateForConnection() {
        // The WINDOW_UPDATE frame can be specific to a stream or to the entire
        // connection. A streamId of "0" indicates that the entire connection is
        // the subject of the frame.
    }

    @Test
    public void shouldProcessWindowUpdateForStream() {
        // The WINDOW_UPDATE frame can be specific to a stream or to the entire
        // connection. A non-zero streamId indicates the stream for which flow
        // control is being applied.
    }

    @Test
    public void shouldAllowWindowUpdateOnClosedStream() {
        // WINDOW_UPDATE can be sent by a peer that has sent a frame bearing the
        // END_STREAM flag.  This means that a receiver could receive a
        // WINDOW_UPDATE frame on a "half closed (remote)" or "closed" stream.
        // A receiver MUST NOT treat this as an error, see Section 5.1.
    }

    @Test
    public void shouldRejectWindowUpdateOfSizeZero() {
        // A receiver MUST treat the receipt of a WINDOW_UPDATE frame with an
        // flow control window increment of 0 as a stream error (Section 5.4.2)
        // of type PROTOCOL_ERROR; errors on the connection flow control window
        // MUST be treated as a connection error (Section 5.4.1).
    }

    @Test
    public void shouldRejectLargeWindowUpdateFrame() {
        // A WINDOW_UPDATE frame with a length other than 4 octets MUST be
        // treated as a connection error (Section 5.4.1) of type
        // FRAME_SIZE_ERROR.
    }

    @Test
    public void shouldRejectWindowUpdateOverflowForConnection() {
        // A sender MUST NOT allow a flow control window to exceed 2^31-1
        // octets.  If a sender receives a WINDOW_UPDATE that causes a flow
        // control window to exceed this maximum it MUST terminate either the
        // stream or the connection, as appropriate.  For streams, the sender
        // sends a RST_STREAM with the error code of FLOW_CONTROL_ERROR code;
        //for the connection, a GOAWAY frame with a FLOW_CONTROL_ERROR code.
    }

    @Test
    public void shouldRejectWindowUpdateOverflowForStream() {
        // A sender MUST NOT allow a flow control window to exceed 2^31-1
        // octets.  If a sender receives a WINDOW_UPDATE that causes a flow
        // control window to exceed this maximum it MUST terminate either the
        // stream or the connection, as appropriate.  For streams, the sender
        // sends a RST_STREAM with the error code of FLOW_CONTROL_ERROR code;
        //for the connection, a GOAWAY frame with a FLOW_CONTROL_ERROR code.
    }

    @Test
    public void shouldNotSendDataForNegativeFlowControlWindow() {
        // A change to SETTINGS_INITIAL_WINDOW_SIZE can cause the available
        // space in a flow control window to become negative.  A sender MUST
        // track the negative flow control window, and MUST NOT send new flow
        // controlled frames until it receives WINDOW_UPDATE frames that cause
        //the flow control window to become positive.
    }

    @Test
    public void shouldRejectWindowSizeSettingThatExceedsMaximumFlowControlWindow() {
        // An endpoint MUST treat a change to SETTINGS_INITIAL_WINDOW_SIZE that
        // causes any flow control window to exceed the maximum size as a
        // connection error (Section 5.4.1) of type FLOW_CONTROL_ERROR.
    }
}
