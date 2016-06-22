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
package org.kaazing.k3po.junit.rules;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.kaazing.k3po.control.internal.TransportException;

class Latch {

    enum State {
        INIT, PREPARED, STARTABLE, FINISHED
    }

    private volatile State state;
    private volatile TransportException exception;

    private final CountDownLatch prepared;
    private final CountDownLatch startable;
    private final CountDownLatch finished;
    private final CountDownLatch disposed;
    private volatile Thread testThread;

    Latch() {
        state = State.INIT;

        prepared = new CountDownLatch(1);
        startable = new CountDownLatch(1);
        finished = new CountDownLatch(1);
        disposed = new CountDownLatch(1);
    }

    void notifyPrepared() {
        if (state == State.INIT) {
            state = State.PREPARED;
            prepared.countDown();
        } else {
            throw new IllegalStateException(state.name());
        }
    }

    void awaitPrepared() throws TransportException {
        await(prepared, "prepared");
    }

    boolean isPrepared() {
        return prepared.getCount() == 0L;
    }

    boolean isInInitState() {
        return this.state == State.INIT;
    }

    void notifyStartable() {
        switch (state) {
        case PREPARED:
            state = State.STARTABLE;
            startable.countDown();
            break;
        case STARTABLE:
        case FINISHED:
            // its all right to call this multiple times if its prepared
            break;
        default:
            throw new IllegalStateException(state.name());
        }
    }

    boolean isStartable() {
        return startable.getCount() == 0L;
    }

    void awaitStartable() throws TransportException {
        await(startable, "startable");
    }

    void notifyFinished() {
        switch (state) {
        case INIT:
            notifyPrepared();
            break;
        // We could abort before started.
        case PREPARED:
        case STARTABLE:
            state = State.FINISHED;
            finished.countDown();
            break;
        default:
            throw new IllegalStateException(state.name());
        }
    }

    void notifyAbort() {
        switch (state) {
        case INIT:
            notifyPrepared();
            notifyStartable();
            break;
        case PREPARED:
            notifyStartable();
            break;
        default:
        }
    }

    void awaitFinished() throws TransportException {
        await(finished, "finished");
    }

    void awaitDisposed() throws TransportException {
        await(disposed, "disposed");
    }

    private void await(CountDownLatch latch, String name) throws TransportException {
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            if (exception != null) {
                exception = new TransportException(String.format("Failed to await %s", name), e);
            }
        }
        if (exception != null) {
            throw exception;
        }
    }

    boolean isFinished() {
        return finished.getCount() == 0L;
    }

    boolean hasException() {
        return exception != null;
    }

    void setException(TransportException exception) {
        this.exception = exception;
        prepared.countDown();
        startable.countDown();
        finished.countDown();
        if (testThread != null) {
            testThread.interrupt();
        }
    }

    public void setInterruptOnException(Thread testThread) {
        this.testThread = testThread;
        if (this.exception != null) {
            testThread.interrupt();
        }
    }

    public void notifyDisposed() {
        disposed.countDown();
    }

}
