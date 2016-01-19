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
package org.kaazing.k3po.driver.internal.test.utils;

import java.util.List;
import java.util.concurrent.Callable;

import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.kaazing.k3po.driver.internal.Robot;
import org.kaazing.k3po.driver.internal.control.handler.ControlServerHandler;

public class ScriptTestRunner implements Callable<ScriptPair> {

    private final Robot robot;
    private final List<String> scriptNames;
    private final Latch latch;
    private volatile Boolean aborted = false;

    public ScriptTestRunner(List<String> scriptNames, Latch latch, Robot robot) {
        if (scriptNames == null) {
            throw new NullPointerException("names");
        }

        if (latch == null) {
            throw new NullPointerException("latch");
        }

        this.robot = robot;
        this.scriptNames = scriptNames;
        this.latch = latch;
    }

    @Override
    public ScriptPair call() throws Exception {
        // We are already done if abort before we start
        try {
            if (aborted) {
                return new ScriptPair();
            }

            String expectedScript = null;
            expectedScript = ControlServerHandler.aggregateScript(scriptNames, Thread.currentThread().getContextClassLoader());

            ChannelFuture prepareFuture = robot.prepare(expectedScript);

            prepareFuture.addListener(new ChannelFutureListener() {

                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    latch.notifyPrepared();
                }
            });

            latch.awaitStartable();
            if (!aborted) {
                robot.start();
            }

            // if aborted then finish future will fire, so we run this logic no matter what
            ChannelFuture finishedFuture = robot.finish();
            finishedFuture.addListener(new ChannelFutureListener() {

                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    latch.notifyFinished();
                }
            });

            latch.awaitFinished();
            return new ScriptPair(expectedScript, robot.getObservedScript());
        } catch (Exception e) {
            latch.notifyException(e);
            throw e;
        }
    }

    public void abort() {
        if (robot != null) {
            // no need to capture future as it is the finish future which we get by calling finish
            robot.abort();
            aborted = true;
        }
    }
}
