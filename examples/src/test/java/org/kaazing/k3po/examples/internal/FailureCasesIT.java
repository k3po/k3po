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
package org.kaazing.k3po.examples.internal;

import static java.util.concurrent.TimeUnit.SECONDS;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;
import org.kaazing.k3po.junit.annotation.Specification;
import org.kaazing.k3po.junit.rules.K3poRule;

public class FailureCasesIT {
    private final K3poRule k3po = new K3poRule();

    @Rule
    public final TestRule timeout = new Timeout(5, SECONDS);

    @Rule
    public final TestRule chain = RuleChain.outerRule(k3po);//.around(timeout);
    // .around(timeout);

    @Test
    @Specification("test.with.barriers")
    public void exampleTestWithBarriers() throws Exception {
        k3po.start();
         k3po.awaitBarrier("HELLO_WORLD");
    }
}
