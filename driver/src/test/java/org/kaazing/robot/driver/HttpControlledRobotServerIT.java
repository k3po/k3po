package org.kaazing.robot.driver;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.kaazing.robot.driver.behavior.RobotCompletionFuture;

public class HttpControlledRobotServerIT {

    private RobotServer httpRobot;
    private String httpUrl = "http://localhost:61234";
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

    @Ignore("script not completed")
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

    // TODO: paths in script need to be generated dynamically to work across machines
    @Test
    public void testFullPipeline() throws Exception {
        String script = loadScript("test.rpt");

        robot.prepareAndStart(script).await();

        RobotCompletionFuture doneFuture = robot.getScriptCompleteFuture();

        doneFuture.await();
    }

    @Test
    public void testInvalidScriptLocation() throws Exception {
        String scriptName = "HttpRequestWithInvalidScriptLocation.rpt";
        String script = loadScript(scriptName);
        
        script = script.replaceAll("name:PATH_PLACEHOLDER", "name:" + Paths.get(String.format("%s%s%s", Paths.get("").toAbsolutePath()
                    .toString(), SCRIPT_PATH, scriptName).toString()));

        robot.prepareAndStart(script).await();

        RobotCompletionFuture doneFuture = robot.getScriptCompleteFuture();

        doneFuture.await();
    }

    private String SCRIPT_PATH = "/src/test/scripts/org/kaazing/robot/control/";

    private String loadScript(String... scriptNames) throws IOException {
        StringBuilder sb = new StringBuilder();
        for (String scriptName : scriptNames) {
            sb.append("#");
            sb.append(scriptName);
            sb.append("\n");
            List<String> lines = Files.readAllLines(Paths.get(String.format("%s%s%s", Paths.get("").toAbsolutePath()
                    .toString(), SCRIPT_PATH, scriptName)), StandardCharsets.UTF_8);
            for (String line : lines) {
                sb.append(line);
                sb.append("\n");
            }
        }
        return sb.toString();
    }
}
