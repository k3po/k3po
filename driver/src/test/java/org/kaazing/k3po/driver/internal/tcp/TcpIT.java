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
package org.kaazing.k3po.driver.internal.tcp;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.rules.RuleChain.outerRule;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.DisableOnDebug;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.rules.Timeout;
import org.junit.runner.Description;
import org.kaazing.k3po.driver.internal.test.utils.K3poTestRule;
import org.kaazing.k3po.driver.internal.test.utils.TestSpecification;

public class TcpIT {

    private final K3poTestRule k3po = new K3poTestRule();

    private final TestRule timeout = new DisableOnDebug(new Timeout(5, SECONDS));

    private final TestWatcher failureTrace = new TestWatcher() {
        @Override
        protected void failed(Throwable e, Description description) {
            System.out.println(description + " failed - " + e);
        };
    };

    @Rule
    public final TestRule chain = outerRule(failureTrace).around(k3po).around(timeout);

    @TestSpecification("client.close.notify.closed")
    @Test // Test case for k2po#222
    @Ignore("k3po#222")
    public void clientCloseNotifyClosed() throws Exception {
        k3po.finish();
    }

    @TestSpecification("server.close.notify.closed")
    @Test // Test case for k2po#222
    @Ignore("k3po#222")
    public void serverCloseNotifyClosed() throws Exception {
        k3po.finish();
    }


    @TestSpecification("server.write.flush.closed")
    @Test // Test case for k3po#128
    @Ignore("k3po#128")
    public void serverWriteFlushClosed() throws Exception {
        k3po.finish();
    }
}
