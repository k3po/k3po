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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.DisableOnDebug;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;
import org.kaazing.k3po.junit.annotation.ScriptProperty;
import org.kaazing.k3po.junit.annotation.Specification;
import org.kaazing.k3po.junit.rules.K3poRule;

public class PropertyOverrideIT {

    private final K3poRule k3po = new K3poRule().scriptProperty("RESPONSE 'Let\\'s take a selfie'");

    private final TestRule timeout = new DisableOnDebug(new Timeout(5, SECONDS));

    @Rule
    public final TestRule chain = RuleChain.outerRule(k3po).around(timeout);

    @Test
    @Specification("server.hello.world")
    @ScriptProperty("location 'tcp://localhost:8005'")
    public void testHelloWorld() throws Exception {

        // Create client connection
        Socket socket = new Socket("localhost", 8005);
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        // send hello world
        writer.write("hello world");
        writer.flush();

        // read hello client or fail
        String expected = "Let's take a selfie";
        char[] cbuf = new char[expected.length()];
        in.read(cbuf, 0, expected.length());
        String actual = new String(cbuf);
        Assert.assertTrue(expected.equals(actual));

        // close the socket
        socket.close();

        // tell the robot to finish (This blocks until complete or timeout)
        k3po.finish();

    }
}
