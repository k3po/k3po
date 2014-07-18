/*
 * Copyright (c) 2014 "Kaazing Corporation," (www.kaazing.com)
 *
 * This file is part of Robot.
 *
 * Robot is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.kaazing.robot.junit.rules;

import java.util.concurrent.CountDownLatch;

class RoboticLatch {

    static enum State { INIT, PREPARED, STARTABLE, FINISHED }

    private volatile State state;
    private volatile Exception exception;

    private final CountDownLatch prepared;
    private final CountDownLatch startable;
    private final CountDownLatch finished;

    RoboticLatch() {
        state = State.INIT;

        prepared = new CountDownLatch(1);
        startable = new CountDownLatch(1);
        finished = new CountDownLatch(1);
    }

    void notifyPrepared() {
        switch (state) {
        case INIT:
            state = State.PREPARED;
            prepared.countDown();
            break;
        default:
            throw new IllegalStateException(state.name());
        }
    }

    void awaitPrepared() throws Exception {
        prepared.await();
        if (exception != null) {
            throw exception;
        }
    }

    boolean isPrepared() {
        return prepared.getCount() == 0L;
    }

    void notifyStartable() {
        switch (state) {
        case PREPARED:
            state = State.STARTABLE;
            startable.countDown();
            break;
        default:
            throw new IllegalStateException(state.name());
        }
    }

    void awaitStartable() throws Exception {
        startable.await();
        if (exception != null) {
            throw exception;
        }
    }

    boolean isStartable() {
        return startable.getCount() == 0L;
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
        case PREPARED:
            notifyStartable();
            break;
        default:
        }
    }

    void awaitFinished() throws Exception {
        finished.await();
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

    void notifyException(Exception exception) {
        this.exception = exception;
        prepared.countDown();
        startable.countDown();
        finished.countDown();
    }
}
