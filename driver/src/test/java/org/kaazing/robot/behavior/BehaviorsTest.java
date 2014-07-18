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

package org.kaazing.robot.behavior;


public class BehaviorsTest {
//    static Behaviors behaviors;
//
//    @Before
//    public void setUp()
//        throws Exception {
//        behaviors = new Behaviors("<Testing>");
//    }
//
//    @After
//    public void tearDown()
//        throws Exception {
//        behaviors.stopAll();
//        behaviors = null;
//    }
//
//    @Test(expected = NullPointerException.class)
//    public void startBehaviorMissingScript()
//        throws Exception {
//        behaviors.startBehavior(null, "behaviors.test");
//    }
//
//    @Test
//    public void startBehaviorOK()
//        throws Exception {
//
//        String behaviorName = "foo";
//        File f = null;
//
//        try {
//            f = File.createTempFile(behaviorName, ".tmp");
//
//            FileWriter writer = new FileWriter(f);
//            writer.write("# tcp.server.accept-then-close\n");
//            writer.write("accept tcp://localhost:7788\n");
//            writer.write("connected\n");
//            writer.write("close\n");
//            writer.write("closed\n");
//            writer.close();
//
//            InputStream is = new FileInputStream(f);
//
//            behaviors.startBehavior(is, "behaviors.test");
//
//        } finally {
//            if (f != null) {
//                f.delete();
//            }
//        }
//    }
//
//    @Test
//    public void stopBehaviorFailed()
//        throws Exception {
//
//        String behaviorID = "foo";
//        boolean stopped = behaviors.stopBehavior(behaviorID);
//        Assert.assertTrue(String.format("Expected stopped value of 'false', got %s", stopped), stopped == false);
//    }
//
//    @Test
//    public void stopBehaviorOK()
//        throws Exception {
//
//        File f = null;
//
//        try {
//            f = File.createTempFile("foo", ".tmp");
//
//            FileWriter writer = new FileWriter(f);
//            writer.write("# tcp.server.accept-then-close\n");
//            writer.write("accept tcp://localhost:7788\n");
//            writer.write("connected\n");
//            writer.write("close\n");
//            writer.write("closed\n");
//            writer.close();
//
//            InputStream is = new FileInputStream(f);
//
//            String behaviorID = behaviors.startBehavior(is, "behaviors.test");
//            boolean stopped = behaviors.stopBehavior(behaviorID);
//            Assert.assertTrue(String.format("Expected stopped value of 'true', got %s", stopped), stopped == true);
//
//        } finally {
//            if (f != null) {
//                f.delete();
//            }
//        }
//    }
//
//    @Test
//    public void checkBehaviorOK()
//        throws Exception {
//
//        File f = null;
//
//        try {
//            f = File.createTempFile("foo", ".tmp");
//
//            FileWriter writer = new FileWriter(f);
//            writer.write("# tcp.server.accept-then-close\n");
//            writer.write("accept tcp://localhost:7788\n");
//            writer.write("connected\n");
//            writer.write("close\n");
//            writer.write("closed\n");
//            writer.close();
//
//            InputStream is = new FileInputStream(f);
//
//            String behaviorID = behaviors.startBehavior(is, "behaviors.test");
//            boolean checked = behaviors.checkBehavior(behaviorID, null);
//            Assert.assertTrue(String.format("Expected checked value of 'true', got %s", checked), checked == true);
//
//        } finally {
//            if (f != null) {
//                f.delete();
//            }
//        }
//    }
}
