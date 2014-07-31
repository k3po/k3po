package org.kaazing.robot.driver;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kaazing.robot.driver.behavior.RobotCompletionFuture;

public class HttpControlledRobotServerIT {

    private RobotServer httpRobot;
    private String httpUrl = "http://localhost:61234";
    private static final Charset UTF_8 = Charset.forName("UTF-8");
    Robot robot;

    @Before
    public void setupRobot() throws Exception {
        httpRobot = new HttpControlledRobotServer(URI.create(httpUrl));
        httpRobot.start();
        robot = new Robot();
    }

    @After
    public void shutdownRobot() throws Exception {
        httpRobot.stop();

    }

    @Test
    public void testFullSessionClientHelloWorldPass() throws Exception {
        String script = loadScript("fullHttpSessionConnectAccept.rpt");

        robot.prepareAndStart(script).await();

        RobotCompletionFuture doneFuture = robot.getScriptCompleteFuture();

        doneFuture.await();
    }

    @Test
    public void testFullSessionClientHelloWorldFail() {

    }

    @Test
    public void testFullSessionServerHelloWorldPass() {

    }

    @Test
    public void testFullSessionServerHelloWorldFail() {

    }

    @Test
    public void testInvalidScriptLocation() {

    }

    private String SCRIPT_PATH = "/src/test/scripts/org/kaazing/robot/control/";

    private String loadScript(String... scriptNames) throws IOException {
        StringBuilder sb = new StringBuilder();
        for (String scriptName : scriptNames) {
            sb.append("#");
            sb.append(scriptName);
            sb.append("\n");
            byte[] encoded = Files.readAllBytes(Paths.get(String.format("%s%s%s", Paths.get("").toAbsolutePath().toString(), SCRIPT_PATH, scriptName)));
            sb.append(new String(encoded, UTF_8));
        }
        return sb.toString();
    }
}
