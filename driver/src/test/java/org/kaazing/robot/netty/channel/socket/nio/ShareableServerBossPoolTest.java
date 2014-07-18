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

package org.kaazing.robot.netty.channel.socket.nio;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import org.kaazing.executor.ExecutorServiceFactory;

public class ShareableServerBossPoolTest {

    private static ExecutorServiceFactory executorServiceFactory;

    private ShareableServerBossPool instance1;
    private ShareableServerBossPool instance2;

    @BeforeClass
    public static void setup() {
        executorServiceFactory = ExecutorServiceFactory.newInstance();
    }

    @After
    public void afterTest() {
        if (instance1 == null) {
            instance1.shutdown();
        }
        if (instance2 == null) {
            instance2.shutdown();
        }
    }


    @Test
    public void getInstanceOK() {
        instance1 = ShareableServerBossPool.getInstance(executorServiceFactory);
        instance2 = ShareableServerBossPool.getInstance(executorServiceFactory);

        assertEquals(instance1, instance2);

        instance2.shutdown();

        instance2 = ShareableServerBossPool.getInstance(executorServiceFactory);

        assertEquals(instance1, instance2);

        instance1.shutdown();
        instance2.shutdown();

        instance2 = ShareableServerBossPool.getInstance(executorServiceFactory);
        // getInstance returns a new instance if both instances were shutdown
        assertNotEquals(instance1, instance2);
    }

    @Test
    public void getNextWorkerOK() {
        instance1 = ShareableServerBossPool.getInstance(executorServiceFactory);
        // There should only be 1 worker so calling twice should get the same worker
        assertEquals(instance1.nextBoss(), instance1.nextBoss());

        instance2 = ShareableServerBossPool.getInstance(executorServiceFactory);

        assertEquals(instance1.nextBoss(), instance2.nextBoss());
    }

}

