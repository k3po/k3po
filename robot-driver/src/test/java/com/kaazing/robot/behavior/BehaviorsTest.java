/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package com.kaazing.robot.behavior;


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
