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

package org.kaazing.specification.amqp;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.rules.RuleChain.outerRule;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.DisableOnDebug;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;
import org.kaazing.k3po.junit.annotation.ScriptProperty;
import org.kaazing.k3po.junit.annotation.Specification;
import org.kaazing.k3po.junit.rules.K3poRule;

/**
 * Test to validate AMQP initial handshake as specified in section 2.2.4 of
 * <a href="https://www.rabbitmq.com/resources/specs/amqp0-9-1.pdf">the specification</a>.
 */
public class AmqpIndenityWsIT {

    private final K3poRule k3po = new K3poRule().setScriptRoot("org/kaazing/specification/amqp/ws");

    private final TestRule timeout = new DisableOnDebug(new Timeout(5, SECONDS));

    @Rule
    public final TestRule chain = outerRule(k3po).around(timeout);

    @Test
    @ScriptProperty({ "connectLocation \"http://localhost:8001/amqp\"", "acceptLocation \"http://localhost:8001/amqp\"" })
    @Specification({ "open/identity/request", "open/identity/response" })
    public void connectWithIdentity() throws Exception {
        k3po.finish();
    }

    @Test
    @ScriptProperty({ "connectLocation \"http://localhost:8001/amqp\"", "acceptLocation \"http://localhost:8001/amqp\"" })
    @Specification({ "open/noidentity/request",  "open/noidentity/response" })
    public void connectWithNoIdentity() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({ "open/identity/request", "close/request",
                    "open/identity/response", "close/response" })
    public void closeConnection() throws Exception {
        k3po.finish();
    }

}
