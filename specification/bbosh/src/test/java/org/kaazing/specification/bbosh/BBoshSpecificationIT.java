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
package org.kaazing.specification.bbosh;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.rules.RuleChain.outerRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.DisableOnDebug;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;
import org.kaazing.k3po.junit.annotation.Specification;
import org.kaazing.k3po.junit.rules.K3poRule;

public class BBoshSpecificationIT {

    private final K3poRule robot = new K3poRule();

    private final TestRule timeout = new DisableOnDebug(new Timeout(5, SECONDS));

    @Rule
    public final TestRule chain = outerRule(robot).around(timeout);

    @Test
    @Specification({"polling/connect.echo.then.closed", "polling/accept.echo.then.close"})
    public void shouldEchoThenCloseViaPolling() throws Exception {
        robot.finish();
    }

    @Test
    @Specification({"polling/connect.echo.then.close", "polling/accept.echo.then.closed"})
    public void shouldEchoThenClosedViaPolling() throws Exception {
        robot.finish();
    }

    @Test
    @Specification({"long.polling/connect.echo.then.closed", "long.polling/accept.echo.then.close"})
    public void shouldEchoThenCloseViaLongPolling() throws Exception {
        robot.finish();
    }

    @Test
    @Specification({"long.polling/connect.echo.then.close", "long.polling/accept.echo.then.closed"})
    public void shouldEchoThenClosedViaLongPolling() throws Exception {
        robot.finish();
    }

    @Test
    @Specification({"streaming/connect.echo.then.closed", "streaming/accept.echo.then.close"})
    public void shouldEchoThenCloseViaStreaming() throws Exception {
        robot.finish();
    }

    @Test
    @Specification({"streaming/connect.echo.then.close", "streaming/accept.echo.then.closed"})
    public void shouldEchoThenClosedViaStreaming() throws Exception {
        robot.finish();
    }
}
