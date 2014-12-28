/*
 * Copyright 2014, Kaazing Corporation. All rights reserved.
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

package org.kaazing.specification.ws;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.rules.RuleChain.outerRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.DisableOnDebug;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;
import org.kaazing.k3po.junit.annotation.Robotic;
import org.kaazing.k3po.junit.rules.RobotRule;

/**
 * RFC-6455, section 5.3 "Client-to-Server Masking"
 */
public class MaskingIT {

    private final RobotRule robot = new RobotRule().setScriptRoot("org/kaazing/specification/ws/masking");

    private final TestRule timeout = new DisableOnDebug(new Timeout(5, SECONDS));

    @Rule
    public final TestRule chain = outerRule(robot).around(timeout);

    @Test
    @Robotic({"send.text.payload.not.masked/handshake.request.and.frame",
              "send.text.payload.not.masked/handshake.response.and.frame" })
    public void shouldFailWebSocketConnectionWhenSendTextFrameNotMasked() throws Exception {
        robot.join();
    }

    @Test
    @Robotic({"send.binary.payload.not.masked/handshake.request.and.frame",
              "send.binary.payload.not.masked/handshake.response.and.frame" })
    public void shouldFailWebSocketConnectionWhenSendBinaryFrameNotMasked() throws Exception {
        robot.join();
    }

}
