package org.kaazing.robot.driver;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;
import java.nio.ByteBuffer;
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

    @Test
    public void testAbortBeforePrepare() throws Exception {
        String path = Paths
                .get(String.format("%s%s%s", Paths.get("").toAbsolutePath().toString(), SCRIPT_PATH,
                        "clientHelloWorld.rpt")).toString();

        Socket client = new Socket();

        client.connect(new InetSocketAddress("localhost", 61234));

        OutputStream outputStream = client.getOutputStream();

        InputStream inputStream = client.getInputStream();

        byte[] abort = ("POST /ABORT HTTP/1.1\r\n" + "Content-Length: "
                + ("name:".length() + path.length() + "\n\n".length()) + "\r\n" + "\r\n" + "name:" + path + "\n\n\r\n")
                .getBytes("UTF-8");

        outputStream.write(abort);
        outputStream.flush();

        String badRequestContent = "{\n" + "    \"kind\": \"BAD_REQUEST\",\n" + "    \"name\": \"" + path + "\",\n"
                + "    \"content\": \"The script cannot be aborted from the current state\"\n" + "}";
        ByteBuffer badRequestExpected = ByteBuffer.wrap(("HTTP/1.1 400 Bad Request\r\n" + "Content-Type: text/html\r\n"
                + "Content-Length: " + badRequestContent.length() + "\r\n" + "\r\n" + badRequestContent)
                .getBytes("UTF-8"));

        ByteBuffer badRequest = ByteBuffer.allocate(badRequestExpected.capacity());
        while (badRequest.hasRemaining()) {
            badRequest.put((byte) inputStream.read());
        }
        badRequest.flip();

        client.close();

        assertEquals(badRequestExpected, badRequest);
    }

    @Test
    public void testAbortAfterFinished() throws Exception {
        String path = Paths.get(
                String.format("%s%s%s", Paths.get("").toAbsolutePath().toString(), SCRIPT_PATH, "basicScript.rpt"))
                .toString();

        Socket client = new Socket();

        client.connect(new InetSocketAddress("localhost", 61234));

        OutputStream outputStream = client.getOutputStream();

        InputStream inputStream = client.getInputStream();

        byte[] prepare = ("POST /PREPARE HTTP/1.1\r\n" + "Content-Length: "
                + ("name:".length() + path.length() + "\n\n".length()) + "\r\n" + "\r\n" + "name:" + path + "\n\n\r\n")
                .getBytes("UTF-8");

        outputStream.write(prepare);
        outputStream.flush();

        String preparedContent = "{\n    \"kind\": \"PREPARED\",\n    \"name\": \"" + path + "\"\n}";
        ByteBuffer preparedExpected = ByteBuffer.wrap(("HTTP/1.1 200 OK\r\n" + "Content-Type: text/html\r\n"
                + "Content-Length: " + preparedContent.length() + "\r\n" + "\r\n" + preparedContent).getBytes("UTF-8"));

        ByteBuffer prepared = ByteBuffer.allocate(preparedExpected.capacity());
        while (prepared.hasRemaining()) {
            prepared.put((byte) inputStream.read());
        }
        prepared.flip();

        assertEquals(preparedExpected, prepared);

        byte[] start = ("POST /START HTTP/1.1\r\n" + "Content-Length: "
                + ("name:".length() + path.length() + "\n\n".length()) + "\r\n" + "\r\n" + "name:" + path + "\n\n\r\n")
                .getBytes("UTF-8");

        outputStream.write(start);
        outputStream.flush();

        String startedContent = "{\n" + "    \"kind\": \"STARTED\",\n" + "    \"name\": \"" + path + "\"\n}";
        ByteBuffer startedExpected = ByteBuffer.wrap(("HTTP/1.1 200 OK\r\n" + "Content-Type: text/html\r\n"
                + "Content-Length: " + startedContent.length() + "\r\n" + "\r\n" + startedContent).getBytes("UTF-8"));

        ByteBuffer started = ByteBuffer.allocate(startedExpected.capacity());
        while (started.hasRemaining()) {
            started.put((byte) inputStream.read());
        }
        started.flip();

        assertEquals(startedExpected, started);

        byte[] finish = ("POST /FINISH HTTP/1.1\r\n" + "Content-Length: "
                + ("name:".length() + path.length() + "\n\n".length()) + "\r\n" + "\r\n" + "name:" + path + "\n\n\r\n")
                .getBytes("UTF-8");

        outputStream.write(finish);
        outputStream.flush();

        String finishedContent = "{\n"
                + "    \"kind\": \"FINISHED\",\n"
                + "    \"name\": \""
                + path
                + "\",\n"
                + "    \"expected_script\": \"accept tcp://localhost:61111\\naccepted\\nconnected\\n\\nwrite \\\"Hello, World!\\\"\\nread \\\"Hello, World!\\\"\\n\\nclosed\\nconnect tcp://localhost:61111\\nconnected\\n\\nread \\\"Hello, World!\\\"\\nwrite \\\"Hello, World!\\\"\\n\\nclose\\nclosed\\n\",\n"
                + "    \"observed_script\": \"accept tcp://localhost:61111\\naccepted\\nconnected\\n\\nwrite \\\"Hello, World!\\\"\\nread \\\"Hello, World!\\\"\\n\\nclosed\\nconnect tcp://localhost:61111\\nconnected\\n\\nread \\\"Hello, World!\\\"\\nwrite \\\"Hello, World!\\\"\\n\\nclose\\nclosed\\n\"\n"
                + "}";
        ByteBuffer finishedExpected = ByteBuffer.wrap(("HTTP/1.1 200 OK\r\n" + "Content-Type: text/html\r\n"
                + "Content-Length: " + finishedContent.length() + "\r\n" + "\r\n" + finishedContent).getBytes("UTF-8"));

        String errorContent = "{\n" + "    \"kind\": \"ERROR\",\n" + "    \"name\": \"" + path + "\",\n"
                + "    \"summary\": \"Early Request\",\n"
                + "    \"content\": \"Script execution is not complete. Try again later\"\n" + "}";

        ByteBuffer errorExpected = ByteBuffer.wrap(("HTTP/1.1 200 OK\r\n" + "Content-Type: text/html\r\n"
                + "Content-Length: " + errorContent.length() + "\r\n" + "\r\n" + errorContent).getBytes("UTF-8"));

        ByteBuffer finished = ByteBuffer.allocate(errorExpected.capacity());
        while (finished.hasRemaining()) {
            finished.put((byte) inputStream.read());
            if (!finished.hasRemaining() && finished.capacity() == errorExpected.capacity()) {
                // only allocated space for error message, but receiving finished message
                if (inputStream.available() > 0) {
                    ByteBuffer temp = ByteBuffer.allocate(finishedExpected.capacity());
                    finished.flip();
                    temp.put(finished);
                    finished = temp;
                } else {
                    // received an error message because request for finish was too soon, wait a bit and retry
                    finished.flip();
                    assertEquals(errorExpected, finished);
                    Thread.sleep(200);
                    finished = ByteBuffer.allocate(errorExpected.capacity());
                    outputStream.write(finish);
                    outputStream.flush();
                }
            }
        }
        finished.flip();

        assertEquals(finishedExpected, finished);

        byte[] abort = ("POST /ABORT HTTP/1.1\r\n" + "Content-Length: "
                + ("name:".length() + path.length() + "\n\n".length()) + "\r\n" + "\r\n" + "name:" + path + "\n\n\r\n")
                .getBytes("UTF-8");

        outputStream.write(abort);
        outputStream.flush();

        ByteBuffer finishedRecv = ByteBuffer.allocate(finishedExpected.capacity());
        while (finishedRecv.hasRemaining()) {
            finishedRecv.put((byte) inputStream.read());
        }
        finishedRecv.flip();

        client.close();

        assertEquals(finishedExpected, finishedRecv);
    }

    @Test
    public void testWaitThenAbortAfterFinished() throws Exception {

        String path = Paths.get(
                String.format("%s%s%s", Paths.get("").toAbsolutePath().toString(), SCRIPT_PATH, "basicScript.rpt"))
                .toString();

        Socket client = new Socket();

        client.connect(new InetSocketAddress("localhost", 61234));

        OutputStream outputStream = client.getOutputStream();

        InputStream inputStream = client.getInputStream();

        byte[] prepare = ("POST /PREPARE HTTP/1.1\r\n" + "Content-Length: "
                + ("name:".length() + path.length() + "\n\n".length()) + "\r\n" + "\r\n" + "name:" + path + "\n\n\r\n")
                .getBytes("UTF-8");

        outputStream.write(prepare);
        outputStream.flush();

        String preparedContent = "{\n    \"kind\": \"PREPARED\",\n    \"name\": \"" + path + "\"\n}";
        ByteBuffer preparedExpected = ByteBuffer.wrap(("HTTP/1.1 200 OK\r\n" + "Content-Type: text/html\r\n"
                + "Content-Length: " + preparedContent.length() + "\r\n" + "\r\n" + preparedContent).getBytes("UTF-8"));

        ByteBuffer prepared = ByteBuffer.allocate(preparedExpected.capacity());
        while (prepared.hasRemaining()) {
            prepared.put((byte) inputStream.read());
        }
        prepared.flip();

        assertEquals(preparedExpected, prepared);

        byte[] start = ("POST /START HTTP/1.1\r\n" + "Content-Length: "
                + ("name:".length() + path.length() + "\n\n".length()) + "\r\n" + "\r\n" + "name:" + path + "\n\n\r\n")
                .getBytes("UTF-8");

        outputStream.write(start);
        outputStream.flush();

        String startedContent = "{\n" + "    \"kind\": \"STARTED\",\n" + "    \"name\": \"" + path + "\"\n}";
        ByteBuffer startedExpected = ByteBuffer.wrap(("HTTP/1.1 200 OK\r\n" + "Content-Type: text/html\r\n"
                + "Content-Length: " + startedContent.length() + "\r\n" + "\r\n" + startedContent).getBytes("UTF-8"));

        ByteBuffer started = ByteBuffer.allocate(startedExpected.capacity());
        while (started.hasRemaining()) {
            started.put((byte) inputStream.read());
        }
        started.flip();

        assertEquals(startedExpected, started);

        byte[] finish = ("POST /FINISH HTTP/1.1\r\n" + "Content-Length: "
                + ("name:".length() + path.length() + "\n\n".length()) + "\r\n" + "\r\n" + "name:" + path + "\n\n\r\n")
                .getBytes("UTF-8");

        outputStream.write(finish);
        outputStream.flush();

        String finishedContent = "{\n"
                + "    \"kind\": \"FINISHED\",\n"
                + "    \"name\": \""
                + path
                + "\",\n"
                + "    \"expected_script\": \"accept tcp://localhost:61111\\naccepted\\nconnected\\n\\nwrite \\\"Hello, World!\\\"\\nread \\\"Hello, World!\\\"\\n\\nclosed\\nconnect tcp://localhost:61111\\nconnected\\n\\nread \\\"Hello, World!\\\"\\nwrite \\\"Hello, World!\\\"\\n\\nclose\\nclosed\\n\",\n"
                + "    \"observed_script\": \"accept tcp://localhost:61111\\naccepted\\nconnected\\n\\nwrite \\\"Hello, World!\\\"\\nread \\\"Hello, World!\\\"\\n\\nclosed\\nconnect tcp://localhost:61111\\nconnected\\n\\nread \\\"Hello, World!\\\"\\nwrite \\\"Hello, World!\\\"\\n\\nclose\\nclosed\\n\"\n"
                + "}";
        ByteBuffer finishedExpected = ByteBuffer.wrap(("HTTP/1.1 200 OK\r\n" + "Content-Type: text/html\r\n"
                + "Content-Length: " + finishedContent.length() + "\r\n" + "\r\n" + finishedContent).getBytes("UTF-8"));

        String errorContent = "{\n" + "    \"kind\": \"ERROR\",\n" + "    \"name\": \"" + path + "\",\n"
                + "    \"summary\": \"Early Request\",\n"
                + "    \"content\": \"Script execution is not complete. Try again later\"\n" + "}";

        ByteBuffer errorExpected = ByteBuffer.wrap(("HTTP/1.1 200 OK\r\n" + "Content-Type: text/html\r\n"
                + "Content-Length: " + errorContent.length() + "\r\n" + "\r\n" + errorContent).getBytes("UTF-8"));

        ByteBuffer finished = ByteBuffer.allocate(errorExpected.capacity());
        while (finished.hasRemaining()) {
            finished.put((byte) inputStream.read());
            if (!finished.hasRemaining() && finished.capacity() == errorExpected.capacity()) {
                // only allocated space for error message, but receiving finished message
                if (inputStream.available() > 0) {
                    ByteBuffer temp = ByteBuffer.allocate(finishedExpected.capacity());
                    finished.flip();
                    temp.put(finished);
                    finished = temp;
                } else {
                    // received an error message because request for finish was too soon, wait a bit and retry
                    finished.flip();
                    assertEquals(errorExpected, finished);
                    Thread.sleep(200);
                    finished = ByteBuffer.allocate(errorExpected.capacity());
                    outputStream.write(finish);
                    outputStream.flush();
                }
            }
        }
        finished.flip();

        assertEquals(finishedExpected, finished);

        // wait for abort requests to expire after 500 ms after finish sent
        Thread.sleep(1000);

        byte[] abort = ("POST /ABORT HTTP/1.1\r\n" + "Content-Length: "
                + ("name:".length() + path.length() + "\n\n".length()) + "\r\n" + "\r\n" + "name:" + path + "\n\n\r\n")
                .getBytes("UTF-8");

        outputStream.write(abort);
        outputStream.flush();

        String badRequestContent = "{\n" + "    \"kind\": \"BAD_REQUEST\",\n" + "    \"name\": \"" + path + "\",\n"
                + "    \"content\": \"The script cannot be aborted from the current state\"\n" + "}";
        ByteBuffer badRequestExpected = ByteBuffer.wrap(("HTTP/1.1 400 Bad Request\r\n" + "Content-Type: text/html\r\n"
                + "Content-Length: " + badRequestContent.length() + "\r\n" + "\r\n" + badRequestContent)
                .getBytes("UTF-8"));

        ByteBuffer badRequest = ByteBuffer.allocate(badRequestExpected.capacity());
        while (badRequest.hasRemaining()) {
            badRequest.put((byte) inputStream.read());
        }
        badRequest.flip();

        client.close();

        assertEquals(badRequestExpected, badRequest);
    }

    @Test
    public void testStartedThenAbort() throws Exception {
        String path = Paths
                .get(String.format("%s%s%s", Paths.get("").toAbsolutePath().toString(), SCRIPT_PATH,
                        "clientHelloWorld.rpt")).toString();

        Socket client = new Socket();

        client.connect(new InetSocketAddress("localhost", 61234));

        OutputStream outputStream = client.getOutputStream();

        InputStream inputStream = client.getInputStream();

        byte[] prepare = ("POST /PREPARE HTTP/1.1\r\n" + "Content-Length: "
                + ("name:".length() + path.length() + "\n\n".length()) + "\r\n" + "\r\n" + "name:" + path + "\n\n\r\n")
                .getBytes("UTF-8");

        outputStream.write(prepare);
        outputStream.flush();

        String preparedContent = "{\n    \"kind\": \"PREPARED\",\n    \"name\": \"" + path + "\"\n}";
        ByteBuffer preparedExpected = ByteBuffer.wrap(("HTTP/1.1 200 OK\r\n" + "Content-Type: text/html\r\n"
                + "Content-Length: " + preparedContent.length() + "\r\n" + "\r\n" + preparedContent).getBytes("UTF-8"));

        ByteBuffer prepared = ByteBuffer.allocate(preparedExpected.capacity());
        while (prepared.hasRemaining()) {
            prepared.put((byte) inputStream.read());
        }
        prepared.flip();

        assertEquals(preparedExpected, prepared);

        byte[] start = ("POST /START HTTP/1.1\r\n" + "Content-Length: "
                + ("name:".length() + path.length() + "\n\n".length()) + "\r\n" + "\r\n" + "name:" + path + "\n\n\r\n")
                .getBytes("UTF-8");

        outputStream.write(start);
        outputStream.flush();

        String startedContent = "{\n" + "    \"kind\": \"STARTED\",\n" + "    \"name\": \"" + path + "\"\n}";
        ByteBuffer startedExpected = ByteBuffer.wrap(("HTTP/1.1 200 OK\r\n" + "Content-Type: text/html\r\n"
                + "Content-Length: " + startedContent.length() + "\r\n" + "\r\n" + startedContent).getBytes("UTF-8"));

        ByteBuffer started = ByteBuffer.allocate(startedExpected.capacity());
        while (started.hasRemaining()) {
            started.put((byte) inputStream.read());
        }
        started.flip();

        assertEquals(startedExpected, started);

        byte[] abort = ("POST /ABORT HTTP/1.1\r\n" + "Content-Length: "
                + ("name:".length() + path.length() + "\n\n".length()) + "\r\n" + "\r\n" + "name:" + path + "\n\n\r\n")
                .getBytes("UTF-8");

        outputStream.write(abort);
        outputStream.flush();

        String finishedContent = "{\n"
                + "    \"kind\": \"FINISHED\",\n"
                + "    \"name\": \""
                + path
                + "\",\n"
                + "    \"expected_script\": \"connect tcp://localhost:61111\\nconnected\\n\\nwrite \\\"Hello, World!\\\"\\n\\nclose\\nclosed\\n\",\n"
                + "    \"observed_script\": \"connect tcp://localhost:61111\\n\"\n" + "}";
        ByteBuffer finishedExpected = ByteBuffer.wrap(("HTTP/1.1 200 OK\r\n" + "Content-Type: text/html\r\n"
                + "Content-Length: " + finishedContent.length() + "\r\n" + "\r\n" + finishedContent).getBytes("UTF-8"));

        ByteBuffer finished = ByteBuffer.allocate(finishedExpected.capacity());
        while (finished.hasRemaining()) {
            finished.put((byte) inputStream.read());
        }
        finished.flip();

        client.close();

        assertEquals(finishedExpected, finished);
    }

    @Test
    public void testPreparedThenAbort() throws Exception {
        String path = Paths.get(
                String.format("%s%s%s", Paths.get("").toAbsolutePath().toString(), SCRIPT_PATH, "basicScript.rpt"))
                .toString();

        Socket client = new Socket();

        client.connect(new InetSocketAddress("localhost", 61234));

        OutputStream outputStream = client.getOutputStream();

        InputStream inputStream = client.getInputStream();

        byte[] prepare = ("POST /PREPARE HTTP/1.1\r\n" + "Content-Length: "
                + ("name:".length() + path.length() + "\n\n".length()) + "\r\n" + "\r\n" + "name:" + path + "\n\n\r\n")
                .getBytes("UTF-8");

        outputStream.write(prepare);
        outputStream.flush();

        String preparedContent = "{\n    \"kind\": \"PREPARED\",\n    \"name\": \"" + path + "\"\n}";
        ByteBuffer preparedExpected = ByteBuffer.wrap(("HTTP/1.1 200 OK\r\n" + "Content-Type: text/html\r\n"
                + "Content-Length: " + preparedContent.length() + "\r\n" + "\r\n" + preparedContent).getBytes("UTF-8"));

        ByteBuffer prepared = ByteBuffer.allocate(preparedExpected.capacity());
        while (prepared.hasRemaining()) {
            prepared.put((byte) inputStream.read());
        }
        prepared.flip();

        assertEquals(preparedExpected, prepared);

        byte[] abort = ("POST /ABORT HTTP/1.1\r\n" + "Content-Length: "
                + ("name:".length() + path.length() + "\n\n".length()) + "\r\n" + "\r\n" + "name:" + path + "\n\n\r\n")
                .getBytes("UTF-8");

        outputStream.write(abort);
        outputStream.flush();

        String finishedContent = "{\n"
                + "    \"kind\": \"FINISHED\",\n"
                + "    \"name\": \""
                + path
                + "\",\n"
                + "    \"expected_script\": \"accept tcp://localhost:61111\\naccepted\\nconnected\\n\\nwrite \\\"Hello, World!\\\"\\nread \\\"Hello, World!\\\"\\n\\nclosed\\nconnect tcp://localhost:61111\\nconnected\\n\\nread \\\"Hello, World!\\\"\\nwrite \\\"Hello, World!\\\"\\n\\nclose\\nclosed\\n\",\n"
                + "    \"observed_script\": \"accept tcp://localhost:61111\\n\"\n" + "}";
        ByteBuffer finishedExpected = ByteBuffer.wrap(("HTTP/1.1 200 OK\r\n" + "Content-Type: text/html\r\n"
                + "Content-Length: " + finishedContent.length() + "\r\n" + "\r\n" + finishedContent).getBytes("UTF-8"));

        ByteBuffer finished = ByteBuffer.allocate(finishedExpected.capacity());
        while (finished.hasRemaining()) {
            finished.put((byte) inputStream.read());
        }
        finished.flip();

        client.close();

        assertEquals(finishedExpected, finished);

    }

    @Test
    public void testFullSession() throws Exception {

        String path = Paths.get(
                String.format("%s%s%s", Paths.get("").toAbsolutePath().toString(), SCRIPT_PATH, "basicScript.rpt"))
                .toString();

        Socket client = new Socket();

        client.connect(new InetSocketAddress("localhost", 61234));

        OutputStream outputStream = client.getOutputStream();

        InputStream inputStream = client.getInputStream();

        byte[] prepare = ("POST /PREPARE HTTP/1.1\r\n" + "Content-Length: "
                + ("name:".length() + path.length() + "\n\n".length()) + "\r\n" + "\r\n" + "name:" + path + "\n\n\r\n")
                .getBytes("UTF-8");

        outputStream.write(prepare);
        outputStream.flush();

        String preparedContent = "{\n    \"kind\": \"PREPARED\",\n    \"name\": \"" + path + "\"\n}";
        ByteBuffer preparedExpected = ByteBuffer.wrap(("HTTP/1.1 200 OK\r\n" + "Content-Type: text/html\r\n"
                + "Content-Length: " + preparedContent.length() + "\r\n" + "\r\n" + preparedContent).getBytes("UTF-8"));

        ByteBuffer prepared = ByteBuffer.allocate(preparedExpected.capacity());
        while (prepared.hasRemaining()) {
            prepared.put((byte) inputStream.read());
        }
        prepared.flip();

        assertEquals(preparedExpected, prepared);

        byte[] start = ("POST /START HTTP/1.1\r\n" + "Content-Length: "
                + ("name:".length() + path.length() + "\n\n".length()) + "\r\n" + "\r\n" + "name:" + path + "\n\n\r\n")
                .getBytes("UTF-8");

        outputStream.write(start);
        outputStream.flush();

        String startedContent = "{\n" + "    \"kind\": \"STARTED\",\n" + "    \"name\": \"" + path + "\"\n}";
        ByteBuffer startedExpected = ByteBuffer.wrap(("HTTP/1.1 200 OK\r\n" + "Content-Type: text/html\r\n"
                + "Content-Length: " + startedContent.length() + "\r\n" + "\r\n" + startedContent).getBytes("UTF-8"));

        ByteBuffer started = ByteBuffer.allocate(startedExpected.capacity());
        while (started.hasRemaining()) {
            started.put((byte) inputStream.read());
        }
        started.flip();

        assertEquals(startedExpected, started);

        byte[] finish = ("POST /FINISH HTTP/1.1\r\n" + "Content-Length: "
                + ("name:".length() + path.length() + "\n\n".length()) + "\r\n" + "\r\n" + "name:" + path + "\n\n\r\n")
                .getBytes("UTF-8");
        outputStream.write(finish);
        outputStream.flush();

        String finishedContent = "{\n"
                + "    \"kind\": \"FINISHED\",\n"
                + "    \"name\": \""
                + path
                + "\",\n"
                + "    \"expected_script\": \"accept tcp://localhost:61111\\naccepted\\nconnected\\n\\nwrite \\\"Hello, World!\\\"\\nread \\\"Hello, World!\\\"\\n\\nclosed\\nconnect tcp://localhost:61111\\nconnected\\n\\nread \\\"Hello, World!\\\"\\nwrite \\\"Hello, World!\\\"\\n\\nclose\\nclosed\\n\",\n"
                + "    \"observed_script\": \"accept tcp://localhost:61111\\naccepted\\nconnected\\n\\nwrite \\\"Hello, World!\\\"\\nread \\\"Hello, World!\\\"\\n\\nclosed\\nconnect tcp://localhost:61111\\nconnected\\n\\nread \\\"Hello, World!\\\"\\nwrite \\\"Hello, World!\\\"\\n\\nclose\\nclosed\\n\"\n"
                + "}";
        ByteBuffer finishedExpected = ByteBuffer.wrap(("HTTP/1.1 200 OK\r\n" + "Content-Type: text/html\r\n"
                + "Content-Length: " + finishedContent.length() + "\r\n" + "\r\n" + finishedContent).getBytes("UTF-8"));

        String errorContent = "{\n" + "    \"kind\": \"ERROR\",\n" + "    \"name\": \"" + path + "\",\n"
                + "    \"summary\": \"Early Request\",\n"
                + "    \"content\": \"Script execution is not complete. Try again later\"\n" + "}";

        ByteBuffer errorExpected = ByteBuffer.wrap(("HTTP/1.1 200 OK\r\n" + "Content-Type: text/html\r\n"
                + "Content-Length: " + errorContent.length() + "\r\n" + "\r\n" + errorContent).getBytes("UTF-8"));

        ByteBuffer finished = ByteBuffer.allocate(errorExpected.capacity());
        while (finished.hasRemaining()) {
            finished.put((byte) inputStream.read());
            if (!finished.hasRemaining() && finished.capacity() == errorExpected.capacity()) {
                // only allocated space for error message, but receiving finished message
                if (inputStream.available() > 0) {
                    ByteBuffer temp = ByteBuffer.allocate(finishedExpected.capacity());
                    finished.flip();
                    temp.put(finished);
                    finished = temp;
                } else {
                    // received an error message because request for finish was too soon, wait a bit and retry
                    finished.flip();
                    assertEquals(errorExpected, finished);
                    Thread.sleep(200);
                    finished = ByteBuffer.allocate(errorExpected.capacity());
                    outputStream.write(finish);
                    outputStream.flush();
                }
            }
        }
        finished.flip();

        client.close();

        assertEquals(finishedExpected, finished);
    }

    @Test
    public void testInvalidScriptLocation() throws Exception {
        String script = loadScript("HttpRequestWithInvalidScriptLocation.rpt");

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
