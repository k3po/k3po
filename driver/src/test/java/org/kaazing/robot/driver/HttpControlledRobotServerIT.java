package org.kaazing.robot.driver;

import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kaazing.robot.driver.behavior.RobotCompletionFuture;
import org.kaazing.robot.driver.control.BadRequestMessage;
import org.kaazing.robot.driver.control.ErrorMessage;
import org.kaazing.robot.driver.control.FinishedMessage;
import org.kaazing.robot.driver.control.PreparedMessage;
import org.kaazing.robot.driver.control.StartedMessage;

import com.fasterxml.jackson.databind.ObjectMapper;

public class HttpControlledRobotServerIT {

    private RobotServer httpRobot;
    private String httpUrl = "http://localhost:61234";
    private Socket client;
    private OutputStream outputStream;
    private InputStream inputStream;
    private ObjectMapper mapper;
    private Robot robot;
    
    private static final int TEST_TIMEOUT = 2500;

    @Before
    public void setupRobot() throws Exception {
        httpRobot = new HttpControlledRobotServer(URI.create(httpUrl), new URLClassLoader(new URL[] { new File("src/test/scripts").toURI().toURL() }));
        httpRobot.start();
        robot = new Robot();
        
        client = new Socket();
        client.connect(new InetSocketAddress("localhost", 61234));
        outputStream = client.getOutputStream();
        inputStream = client.getInputStream();    
        mapper = new ObjectMapper();
    }

    @After
    public void shutdownRobot() throws Exception {
        httpRobot.stop();
        client.close();
    }
    
    @Test(timeout = TEST_TIMEOUT)
    public void testMultipleRuns() throws Exception {
        String path = Paths.get(
                String.format("%s%s", SCRIPT_PATH, "basicScript"))
                .toString();
        
        byte[] prepare = ("POST /PREPARE HTTP/1.1\r\n" + "Content-Length: "
                + ("name:".length() + path.length()) + "\r\n" + "\r\n" + "name:" + path + "\r\n")
                .getBytes("UTF-8");
        
        PreparedMessage preparedMessage = new PreparedMessage();
        preparedMessage.setName(path);
        
        String preparedContent = mapper.writeValueAsString(preparedMessage);
        ByteBuffer preparedExpected = ByteBuffer.wrap(("HTTP/1.1 200 OK\r\n" + "Content-Type: text/html\r\n"
                + "Content-Length: " + preparedContent.length() + "\r\n" + "\r\n" + preparedContent).getBytes("UTF-8"));
        
        
        byte[] start = ("POST /START HTTP/1.1\r\n" + "Content-Length: "
                + ("name:".length() + path.length()) + "\r\n" + "\r\n" + "name:" + path + "\r\n")
                .getBytes("UTF-8");
        
        StartedMessage startedMessage = new StartedMessage();
        startedMessage.setName(path);

        String startedContent = mapper.writeValueAsString(startedMessage);
        ByteBuffer startedExpected = ByteBuffer.wrap(("HTTP/1.1 200 OK\r\n" + "Content-Type: text/html\r\n"
                + "Content-Length: " + startedContent.length() + "\r\n" + "\r\n" + startedContent).getBytes("UTF-8"));
        
        
        FinishedMessage finishedMessage = new FinishedMessage();
        finishedMessage.setName(path);
        finishedMessage.setExpectedScript("accept tcp://localhost:61111\n" + "accepted\n"
                + "write \"Hello, World!\"\n" + "read \"Hello, World!\"\n\n" + "closed\n"
                + "connect tcp://localhost:61111\n" + "connected\n\n" + "read \"Hello, World!\"\n"
                + "write \"Hello, World!\"\n\n" + "close\n" + "closed\n");
        finishedMessage.setObservedScript("accept tcp://localhost:61111\n" + "accepted\n"
                + "write \"Hello, World!\"\n" + "read \"Hello, World!\"\n\n" + "closed\n"
                + "connect tcp://localhost:61111\n" + "connected\n\n" + "read \"Hello, World!\"\n"
                + "write \"Hello, World!\"\n\n" + "close\n" + "closed\n");

        String finishedContent = mapper.writeValueAsString(finishedMessage);
        ByteBuffer finishedExpected = ByteBuffer.wrap(("HTTP/1.1 200 OK\r\n" + "Content-Type: text/html\r\n"
                + "Content-Length: " + finishedContent.length() + "\r\n" + "\r\n" + finishedContent).getBytes("UTF-8"));

        ErrorMessage errorMessage = new ErrorMessage();
        errorMessage.setName(path);
        errorMessage.setDescription("Script execution is not complete. Try again later");
        errorMessage.setSummary("Early Request");
        
        String errorContent = mapper.writeValueAsString(errorMessage);
        ByteBuffer errorExpected = ByteBuffer.wrap(("HTTP/1.1 200 OK\r\n" + "Content-Type: text/html\r\n"
                + "Content-Length: " + errorContent.length() + "\r\n" + "\r\n" + errorContent).getBytes("UTF-8"));
        
        
        byte[] resultRequest = ("POST /RESULT_REQUEST HTTP/1.1\r\n" + "Content-Length: "
                + ("name:".length() + path.length()) + "\r\n" + "\r\n" + "name:" + path + "\r\n")
                .getBytes("UTF-8");
        
        for (int i = 0; i < 3; i++) {

    
            Socket client = new Socket();
    
            client.connect(new InetSocketAddress("localhost", 61234));
    
            OutputStream outputStream = client.getOutputStream();
    
            InputStream inputStream = client.getInputStream();

    
            outputStream.write(prepare);
            outputStream.flush();
    

    
            ByteBuffer prepared = ByteBuffer.allocate(preparedExpected.capacity());
            while (prepared.hasRemaining()) {
                prepared.put((byte) inputStream.read());
            }
            prepared.flip();
    
            assertEquals(preparedExpected, prepared);

    
            outputStream.write(start);
            outputStream.flush();

    
            ByteBuffer started = ByteBuffer.allocate(startedExpected.capacity());
            while (started.hasRemaining()) {
                started.put((byte) inputStream.read());
            }
            started.flip();
    
            assertEquals(startedExpected, started);

    
            outputStream.write(resultRequest);
            outputStream.flush();
    
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
                        outputStream.write(resultRequest);
                        outputStream.flush();
                    }
                }
            }
            finished.flip();
    
            client.close();
    
            assertEquals(finishedExpected, finished);
        }
    }

    @Test(timeout = TEST_TIMEOUT)
    public void testAbortBeforePrepare() throws Exception {
        String path = format("%s%s", SCRIPT_PATH, "clientHelloWorld");

        byte[] abort = ("POST /ABORT HTTP/1.1\r\n" + "Content-Length: "
                + ("name:".length() + path.length()) + "\r\n" + "\r\n" + "name:" + path + "\r\n")
                .getBytes("UTF-8");

        outputStream.write(abort);
        outputStream.flush();
        
        BadRequestMessage badRequestMessage = new BadRequestMessage();
        badRequestMessage.setName(path);
        badRequestMessage.setContent("Abort cannot be requested for the given script in the current state");

        String badRequestContent = mapper.writeValueAsString(badRequestMessage);
        ByteBuffer badRequestExpected = ByteBuffer.wrap(("HTTP/1.1 400 Bad Request\r\n" + "Content-Type: text/html\r\n"
                + "Content-Length: " + badRequestContent.length() + "\r\n" + "\r\n" + badRequestContent)
                .getBytes("UTF-8"));

        ByteBuffer badRequest = ByteBuffer.allocate(badRequestExpected.capacity());
        while (badRequest.hasRemaining()) {
            badRequest.put((byte) inputStream.read());
        }
        badRequest.flip();

        assertEquals(badRequestExpected, badRequest);
    }

    @Test(timeout = TEST_TIMEOUT)
    public void testAbortAfterFinished() throws Exception {
        String path = format("%s%s", SCRIPT_PATH, "basicScript");

        byte[] prepare = ("POST /PREPARE HTTP/1.1\r\n" + "Content-Length: "
                + ("name:".length() + path.length()) + "\r\n" + "\r\n" + "name:" + path + "\r\n")
                .getBytes("UTF-8");

        outputStream.write(prepare);
        outputStream.flush();

        PreparedMessage preparedMessage = new PreparedMessage();
        preparedMessage.setName(path);
        
        String preparedContent = mapper.writeValueAsString(preparedMessage);
        
        ByteBuffer preparedExpected = ByteBuffer.wrap(("HTTP/1.1 200 OK\r\n" + "Content-Type: text/html\r\n"
                + "Content-Length: " + preparedContent.length() + "\r\n" + "\r\n" + preparedContent).getBytes("UTF-8"));

        ByteBuffer prepared = ByteBuffer.allocate(preparedExpected.capacity());
        while (prepared.hasRemaining()) {
            prepared.put((byte) inputStream.read());
        }
        prepared.flip();

        assertEquals(preparedExpected, prepared);

        byte[] start = ("POST /START HTTP/1.1\r\n" + "Content-Length: "
                + ("name:".length() + path.length()) + "\r\n" + "\r\n" + "name:" + path + "\r\n")
                .getBytes("UTF-8");

        outputStream.write(start);
        outputStream.flush();

        StartedMessage startedMessage = new StartedMessage();
        startedMessage.setName(path);

        String startedContent = mapper.writeValueAsString(startedMessage);
        ByteBuffer startedExpected = ByteBuffer.wrap(("HTTP/1.1 200 OK\r\n" + "Content-Type: text/html\r\n"
                + "Content-Length: " + startedContent.length() + "\r\n" + "\r\n" + startedContent).getBytes("UTF-8"));

        ByteBuffer started = ByteBuffer.allocate(startedExpected.capacity());
        while (started.hasRemaining()) {
            started.put((byte) inputStream.read());
        }
        started.flip();

        assertEquals(startedExpected, started);

        byte[] resultRequest = ("POST /RESULT_REQUEST HTTP/1.1\r\n" + "Content-Length: "
                + ("name:".length() + path.length()) + "\r\n" + "\r\n" + "name:" + path + "\r\n")
                .getBytes("UTF-8");

        outputStream.write(resultRequest);
        outputStream.flush();

        FinishedMessage finishedMessage = new FinishedMessage();
        finishedMessage.setName(path);
        finishedMessage.setExpectedScript("accept tcp://localhost:61111\n" + "accepted\n"
                + "write \"Hello, World!\"\n" + "read \"Hello, World!\"\n\n" + "closed\n"
                + "connect tcp://localhost:61111\n" + "connected\n\n" + "read \"Hello, World!\"\n"
                + "write \"Hello, World!\"\n\n" + "close\n" + "closed\n");
        finishedMessage.setObservedScript("accept tcp://localhost:61111\n" + "accepted\n"
                + "write \"Hello, World!\"\n" + "read \"Hello, World!\"\n\n" + "closed\n"
                + "connect tcp://localhost:61111\n" + "connected\n\n" + "read \"Hello, World!\"\n"
                + "write \"Hello, World!\"\n\n" + "close\n" + "closed\n");

        String finishedContent = mapper.writeValueAsString(finishedMessage);
        ByteBuffer finishedExpected = ByteBuffer.wrap(("HTTP/1.1 200 OK\r\n" + "Content-Type: text/html\r\n"
                + "Content-Length: " + finishedContent.length() + "\r\n" + "\r\n" + finishedContent).getBytes("UTF-8"));

        ErrorMessage errorMessage = new ErrorMessage();
        errorMessage.setName(path);
        errorMessage.setDescription("Script execution is not complete. Try again later");
        errorMessage.setSummary("Early Request");
        
        String errorContent = mapper.writeValueAsString(errorMessage);

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
                    outputStream.write(resultRequest);
                    outputStream.flush();
                }
            }
        }
        finished.flip();

        assertEquals(finishedExpected, finished);

        byte[] abort = ("POST /ABORT HTTP/1.1\r\n" + "Content-Length: "
                + ("name:".length() + path.length()) + "\r\n" + "\r\n" + "name:" + path + "\r\n")
                .getBytes("UTF-8");

        outputStream.write(abort);
        outputStream.flush();

        ByteBuffer finishedRecv = ByteBuffer.allocate(finishedExpected.capacity());
        while (finishedRecv.hasRemaining()) {
            finishedRecv.put((byte) inputStream.read());
        }
        finishedRecv.flip();

        assertEquals(finishedExpected, finishedRecv);
    }

    @Test(timeout = TEST_TIMEOUT)
    public void testWaitThenAbortAfterFinished() throws Exception {

        String path = format("%s%s", SCRIPT_PATH, "basicScript");

        byte[] prepare = ("POST /PREPARE HTTP/1.1\r\n" + "Content-Length: "
                + ("name:".length() + path.length()) + "\r\n" + "\r\n" + "name:" + path + "\r\n")
                .getBytes("UTF-8");

        outputStream.write(prepare);
        outputStream.flush();

        PreparedMessage preparedMessage = new PreparedMessage();
        preparedMessage.setName(path);
        
        String preparedContent = mapper.writeValueAsString(preparedMessage);
        ByteBuffer preparedExpected = ByteBuffer.wrap(("HTTP/1.1 200 OK\r\n" + "Content-Type: text/html\r\n"
                + "Content-Length: " + preparedContent.length() + "\r\n" + "\r\n" + preparedContent).getBytes("UTF-8"));

        ByteBuffer prepared = ByteBuffer.allocate(preparedExpected.capacity());
        while (prepared.hasRemaining()) {
            prepared.put((byte) inputStream.read());
        }
        prepared.flip();

        assertEquals(preparedExpected, prepared);

        byte[] start = ("POST /START HTTP/1.1\r\n" + "Content-Length: "
                + ("name:".length() + path.length()) + "\r\n" + "\r\n" + "name:" + path + "\r\n")
                .getBytes("UTF-8");

        outputStream.write(start);
        outputStream.flush();

        StartedMessage startedMessage = new StartedMessage();
        startedMessage.setName(path);

        String startedContent = mapper.writeValueAsString(startedMessage);
        ByteBuffer startedExpected = ByteBuffer.wrap(("HTTP/1.1 200 OK\r\n" + "Content-Type: text/html\r\n"
                + "Content-Length: " + startedContent.length() + "\r\n" + "\r\n" + startedContent).getBytes("UTF-8"));

        ByteBuffer started = ByteBuffer.allocate(startedExpected.capacity());
        while (started.hasRemaining()) {
            started.put((byte) inputStream.read());
        }
        started.flip();

        assertEquals(startedExpected, started);

        byte[] resultRequest = ("POST /RESULT_REQUEST HTTP/1.1\r\n" + "Content-Length: "
                + ("name:".length() + path.length()) + "\r\n" + "\r\n" + "name:" + path + "\r\n")
                .getBytes("UTF-8");

        outputStream.write(resultRequest);
        outputStream.flush();

        FinishedMessage finishedMessage = new FinishedMessage();
        finishedMessage.setName(path);
        finishedMessage.setExpectedScript("accept tcp://localhost:61111\n" + "accepted\n"
                + "write \"Hello, World!\"\n" + "read \"Hello, World!\"\n\n" + "closed\n"
                + "connect tcp://localhost:61111\n" + "connected\n\n" + "read \"Hello, World!\"\n"
                + "write \"Hello, World!\"\n\n" + "close\n" + "closed\n");
        finishedMessage.setObservedScript("accept tcp://localhost:61111\n" + "accepted\n"
                + "write \"Hello, World!\"\n" + "read \"Hello, World!\"\n\n" + "closed\n"
                + "connect tcp://localhost:61111\n" + "connected\n\n" + "read \"Hello, World!\"\n"
                + "write \"Hello, World!\"\n\n" + "close\n" + "closed\n");

        String finishedContent = mapper.writeValueAsString(finishedMessage);
        ByteBuffer finishedExpected = ByteBuffer.wrap(("HTTP/1.1 200 OK\r\n" + "Content-Type: text/html\r\n"
                + "Content-Length: " + finishedContent.length() + "\r\n" + "\r\n" + finishedContent).getBytes("UTF-8"));

        ErrorMessage errorMessage = new ErrorMessage();
        errorMessage.setName(path);
        errorMessage.setDescription("Script execution is not complete. Try again later");
        errorMessage.setSummary("Early Request");
        
        String errorContent = mapper.writeValueAsString(errorMessage);

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
                    outputStream.write(resultRequest);
                    outputStream.flush();
                }
            }
        }
        finished.flip();

        assertEquals(finishedExpected, finished);

        // wait for abort requests to expire after 500 ms after finish sent
        Thread.sleep(1000);

        byte[] abort = ("POST /ABORT HTTP/1.1\r\n" + "Content-Length: "
                + ("name:".length() + path.length()) + "\r\n" + "\r\n" + "name:" + path + "\r\n")
                .getBytes("UTF-8");

        outputStream.write(abort);
        outputStream.flush();

        
        BadRequestMessage badRequestMessage = new BadRequestMessage();
        badRequestMessage.setName(path);
        badRequestMessage.setContent("Invalid Request. No results for requested script.");

        String badRequestContent = mapper.writeValueAsString(badRequestMessage);
        ByteBuffer badRequestExpected = ByteBuffer.wrap(("HTTP/1.1 400 Bad Request\r\n" + "Content-Type: text/html\r\n"
                + "Content-Length: " + badRequestContent.length() + "\r\n" + "\r\n" + badRequestContent)
                .getBytes("UTF-8"));

        ByteBuffer badRequest = ByteBuffer.allocate(badRequestExpected.capacity());
        while (badRequest.hasRemaining()) {
            badRequest.put((byte) inputStream.read());
        }
        badRequest.flip();

        assertEquals(badRequestExpected, badRequest);
    }

    @Test(timeout = TEST_TIMEOUT)
    public void testStartedThenAbort() throws Exception {
        String path = format("%s%s", SCRIPT_PATH, "serverHelloWorld");
        
        byte[] prepare = ("POST /PREPARE HTTP/1.1\r\n" + "Content-Length: "
                + ("name:".length() + path.length()) + "\r\n" + "\r\n" + "name:" + path + "\r\n")
                .getBytes("UTF-8");

        outputStream.write(prepare);
        outputStream.flush();
        
        PreparedMessage preparedMessage = new PreparedMessage();
        preparedMessage.setName(path);
        
        String preparedContent = mapper.writeValueAsString(preparedMessage);
        ByteBuffer preparedExpected = ByteBuffer.wrap(("HTTP/1.1 200 OK\r\n" + "Content-Type: text/html\r\n"
                + "Content-Length: " + preparedContent.length() + "\r\n" + "\r\n" + preparedContent).getBytes("UTF-8"));

        ByteBuffer prepared = ByteBuffer.allocate(preparedExpected.capacity());
        while (prepared.hasRemaining()) {
            prepared.put((byte) inputStream.read());
        }
        prepared.flip();

        assertEquals(preparedExpected, prepared);

        byte[] start = ("POST /START HTTP/1.1\r\n" + "Content-Length: "
                + ("name:".length() + path.length()) + "\r\n" + "\r\n" + "name:" + path + "\r\n")
                .getBytes("UTF-8");

        outputStream.write(start);
        outputStream.flush();
        
        StartedMessage startedMessage = new StartedMessage();
        startedMessage.setName(path);

        String startedContent = mapper.writeValueAsString(startedMessage);
        ByteBuffer startedExpected = ByteBuffer.wrap(("HTTP/1.1 200 OK\r\n" + "Content-Type: text/html\r\n"
                + "Content-Length: " + startedContent.length() + "\r\n" + "\r\n" + startedContent).getBytes("UTF-8"));

        ByteBuffer started = ByteBuffer.allocate(startedExpected.capacity());
        while (started.hasRemaining()) {
            started.put((byte) inputStream.read());
        }
        started.flip();

        assertEquals(startedExpected, started);

        byte[] abort = ("POST /ABORT HTTP/1.1\r\n" + "Content-Length: "
                + ("name:".length() + path.length()) + "\r\n" + "\r\n" + "name:" + path + "\r\n")
                .getBytes("UTF-8");

        outputStream.write(abort);
        outputStream.flush();
        
        FinishedMessage finishedMessage = new FinishedMessage();
        finishedMessage.setName(path);
        finishedMessage.setExpectedScript("accept tcp://localhost:61111\n" + "accepted\n" + "write \"Hello, World!\"\n\n" + "closed\n");
        finishedMessage.setObservedScript("accept tcp://localhost:61111\n");

        String finishedContent = mapper.writeValueAsString(finishedMessage);
        ByteBuffer finishedExpected = ByteBuffer.wrap(("HTTP/1.1 200 OK\r\n" + "Content-Type: text/html\r\n"
                + "Content-Length: " + finishedContent.length() + "\r\n" + "\r\n" + finishedContent).getBytes("UTF-8"));

        ByteBuffer finished = ByteBuffer.allocate(finishedExpected.capacity());
        while (finished.hasRemaining()) {
            finished.put((byte) inputStream.read());
        }
        finished.flip();

        assertEquals(finishedExpected, finished);
    }

    @Test(timeout = TEST_TIMEOUT)
    public void testPreparedThenAbort() throws Exception {
        String path = format("%s%s", SCRIPT_PATH, "serverHelloWorld");

        byte[] prepare = ("POST /PREPARE HTTP/1.1\r\n" + "Host: localhost:11642\r\n" + "Content-Length: " 
                + ("name:".length() + path.length()) + "\r\n" + "Connection: keep-alive\r\n" + 
                "Origin: null\r\n" + "\r\n" + "name:" + path + "\r\n")
                .getBytes("UTF-8");

        outputStream.write(prepare);
        outputStream.flush();

        PreparedMessage preparedMessage = new PreparedMessage();
        preparedMessage.setName(path);
        
        String preparedContent = mapper.writeValueAsString(preparedMessage);
        ByteBuffer preparedExpected = ByteBuffer.wrap(("HTTP/1.1 200 OK\r\n" + "Content-Type: text/html\r\n"
                + "Content-Length: " + preparedContent.length() + "\r\n" + "\r\n" + preparedContent).getBytes("UTF-8"));

        ByteBuffer prepared = ByteBuffer.allocate(preparedExpected.capacity());
        while (prepared.hasRemaining()) {
            prepared.put((byte) inputStream.read());
        }
        prepared.flip();

        assertEquals(preparedExpected, prepared);

        byte[] abort = ("POST /ABORT HTTP/1.1\r\n" + "Content-Length: "
                + ("name:".length() + path.length()) + "\r\n" + "\r\n" + "name:" + path + "\r\n")
                .getBytes("UTF-8");

        outputStream.write(abort);
        outputStream.flush();

        FinishedMessage finishedMessage = new FinishedMessage();
        finishedMessage.setName(path);
        finishedMessage.setExpectedScript("accept tcp://localhost:61111\n" + "accepted\n" + "write \"Hello, World!\"\n\n" + "closed\n");
        finishedMessage.setObservedScript("accept tcp://localhost:61111\n");

        String finishedContent = mapper.writeValueAsString(finishedMessage);
        ByteBuffer finishedExpected = ByteBuffer.wrap(("HTTP/1.1 200 OK\r\n" + "Content-Type: text/html\r\n"
                + "Content-Length: " + finishedContent.length() + "\r\n" + "\r\n" + finishedContent).getBytes("UTF-8"));

        ByteBuffer finished = ByteBuffer.allocate(finishedExpected.capacity());
        while (finished.hasRemaining()) {
            finished.put((byte) inputStream.read());
        }
        finished.flip();

        assertEquals(finishedExpected, finished);

    }

    @Test(timeout = TEST_TIMEOUT)
    public void testFullSession() throws Exception {

        String path = format("%s%s", SCRIPT_PATH, "basicScript");

        byte[] prepare = ("POST /PREPARE HTTP/1.1\r\n" + "Content-Length: "
                + ("name:".length() + path.length()) + "\r\n" + "\r\n" + "name:" + path + "\r\n")
                .getBytes("UTF-8");

        outputStream.write(prepare);
        outputStream.flush();

        PreparedMessage preparedMessage = new PreparedMessage();
        preparedMessage.setName(path);
        
        String preparedContent = mapper.writeValueAsString(preparedMessage);
        ByteBuffer preparedExpected = ByteBuffer.wrap(("HTTP/1.1 200 OK\r\n" + "Content-Type: text/html\r\n"
                + "Content-Length: " + preparedContent.length() + "\r\n" + "\r\n" + preparedContent).getBytes("UTF-8"));

        ByteBuffer prepared = ByteBuffer.allocate(preparedExpected.capacity());
        while (prepared.hasRemaining()) {
            prepared.put((byte) inputStream.read());
        }
        prepared.flip();

        assertEquals(preparedExpected, prepared);

        byte[] start = ("POST /START HTTP/1.1\r\n" + "Content-Length: "
                + ("name:".length() + path.length()) + "\r\n" + "\r\n" + "name:" + path + "\r\n")
                .getBytes("UTF-8");

        outputStream.write(start);
        outputStream.flush();

        StartedMessage startedMessage = new StartedMessage();
        startedMessage.setName(path);

        String startedContent = mapper.writeValueAsString(startedMessage);
        ByteBuffer startedExpected = ByteBuffer.wrap(("HTTP/1.1 200 OK\r\n" + "Content-Type: text/html\r\n"
                + "Content-Length: " + startedContent.length() + "\r\n" + "\r\n" + startedContent).getBytes("UTF-8"));

        ByteBuffer started = ByteBuffer.allocate(startedExpected.capacity());
        while (started.hasRemaining()) {
            started.put((byte) inputStream.read());
        }
        started.flip();

        assertEquals(startedExpected, started);

        byte[] resultRequest = ("POST /RESULT_REQUEST HTTP/1.1\r\n" + "Content-Length: "
                + ("name:".length() + path.length()) + "\r\n" + "\r\n" + "name:" + path + "\r\n")
                .getBytes("UTF-8");

        outputStream.write(resultRequest);
        outputStream.flush();

        FinishedMessage finishedMessage = new FinishedMessage();
        finishedMessage.setName(path);
        finishedMessage.setExpectedScript("accept tcp://localhost:61111\n" + "accepted\n"
                + "write \"Hello, World!\"\n" + "read \"Hello, World!\"\n\n" + "closed\n"
                + "connect tcp://localhost:61111\n" + "connected\n\n" + "read \"Hello, World!\"\n"
                + "write \"Hello, World!\"\n\n" + "close\n" + "closed\n");
        finishedMessage.setObservedScript("accept tcp://localhost:61111\n" + "accepted\n"
                + "write \"Hello, World!\"\n" + "read \"Hello, World!\"\n\n" + "closed\n"
                + "connect tcp://localhost:61111\n" + "connected\n\n" + "read \"Hello, World!\"\n"
                + "write \"Hello, World!\"\n\n" + "close\n" + "closed\n");

        String finishedContent = mapper.writeValueAsString(finishedMessage);
        ByteBuffer finishedExpected = ByteBuffer.wrap(("HTTP/1.1 200 OK\r\n" + "Content-Type: text/html\r\n"
                + "Content-Length: " + finishedContent.length() + "\r\n" + "\r\n" + finishedContent).getBytes("UTF-8"));

        ErrorMessage errorMessage = new ErrorMessage();
        errorMessage.setName(path);
        errorMessage.setDescription("Script execution is not complete. Try again later");
        errorMessage.setSummary("Early Request");
        
        String errorContent = mapper.writeValueAsString(errorMessage);

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
                    outputStream.write(resultRequest);
                    outputStream.flush();
                }
            }
        }
        finished.flip();

        assertEquals(finishedExpected, finished);
    }

    @Test(timeout = TEST_TIMEOUT)
    public void testInvalidScriptLocation() throws Exception {
        String script = loadScript("HttpRequestWithInvalidScriptLocation");

        robot.prepareAndStart(script).await();

        RobotCompletionFuture doneFuture = robot.getScriptCompleteFuture();

        doneFuture.await();
    }

    private String SCRIPT_PATH = "org/kaazing/robot/control/";

    private String loadScript(String... scriptNames) throws IOException {
        StringBuilder sb = new StringBuilder();
        for (String scriptName : scriptNames) {
            sb.append("#");
            sb.append(scriptName);
            sb.append("\n");
            List<String> lines = Files.readAllLines(Paths.get(format("src/test/scripts/%s%s.rpt", SCRIPT_PATH, scriptName)), UTF_8);
            for (String line : lines) {
                sb.append(line);
                sb.append("\n");
            }
        }
        return sb.toString();
    }
}
