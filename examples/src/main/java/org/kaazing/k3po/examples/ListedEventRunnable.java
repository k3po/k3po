/*
 * Copyright (c) 2007-2014 Kaazing Corporation. All rights reserved.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.kaazing.k3po.examples;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * A simple abstraction on a Runnable that runs a list of steps in order. This class is extended by ListedEventClient
 * and ListedEventServer so that simple uses of the client and server can be used to show example K3po Tests.
 *
 */
public abstract class ListedEventRunnable implements Runnable {
    private final ArrayList<Step> steps;
    private final ConcurrentLinkedDeque<Exception> errors;

    ListedEventRunnable() {
        steps = new ArrayList<Step>();
        errors = new ConcurrentLinkedDeque<Exception>();
    }

    @Override
    public void run() {
        Iterator<Step> iter = steps.iterator();
        while (iter.hasNext()) {
            Step step = iter.next();
            try {
                step.run();
            } catch (Exception e) {
                if (errors != null) {
                    errors.add(e);
                }
                break;
            }
        }
        cleanUp();
    }

    protected abstract void cleanUp();

    ConcurrentLinkedDeque<Exception> getErrors() {
        return errors;
    }

    protected void addStep(Step step) {
        steps.add(step);
    }

    abstract static class Step {
        public abstract void run() throws Exception;
    }

}
