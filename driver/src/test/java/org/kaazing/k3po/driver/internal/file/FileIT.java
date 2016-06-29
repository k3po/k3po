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
package org.kaazing.k3po.driver.internal.file;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.DisableOnDebug;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;
import org.kaazing.k3po.driver.internal.test.utils.K3poTestRule;
import org.kaazing.k3po.driver.internal.test.utils.TestSpecification;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.rules.RuleChain.outerRule;

public class FileIT {

    private final K3poTestRule k3po = new K3poTestRule();

    private final TestRule timeout = new DisableOnDebug(new Timeout(5, SECONDS));

    @Rule
    public final TestRule chain = outerRule(timeout).around(k3po);

    @Test
    @TestSpecification({
        "read.from.existing.file"
    })
    public void shouldReadFromExistingFile() throws Exception {
        k3po.finish();
    }

    @Test
    @TestSpecification({
        "create.and.write.to.file"
    })
    public void shouldCreateAndWriteToFile() throws Exception {
        k3po.finish();
    }

    @Test
    @TestSpecification({
        "read.and.write.simultaneously.in.file"
    })
    public void shouldReadAndWriteSimultaneouslyInFile() throws Exception {
        k3po.finish();
    }

    @Test
    @TestSpecification({
        "echo.back.to.sender/sender",
        "echo.back.to.sender/receiver"
    })
    public void shouldEchoBackToSender() throws Exception {
        k3po.finish();
    }

}
