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
package org.kaazing.k3po.driver.internal.control.handler;

import static java.lang.String.format;
import static java.lang.Thread.currentThread;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.FileSystems.newFileSystem;
import static org.kaazing.k3po.lang.internal.parser.ScriptParseStrategy.PROPERTY_NODE;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;

import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.socket.nio.NioSocketChannel;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.logging.InternalLoggerFactory;
import org.kaazing.k3po.driver.internal.Robot;
import org.kaazing.k3po.driver.internal.behavior.Barrier;
import org.kaazing.k3po.driver.internal.control.AwaitMessage;
import org.kaazing.k3po.driver.internal.control.ErrorMessage;
import org.kaazing.k3po.driver.internal.control.FinishedMessage;
import org.kaazing.k3po.driver.internal.control.NotifiedMessage;
import org.kaazing.k3po.driver.internal.control.NotifyMessage;
import org.kaazing.k3po.driver.internal.control.PrepareMessage;
import org.kaazing.k3po.driver.internal.control.PreparedMessage;
import org.kaazing.k3po.driver.internal.control.StartedMessage;
import org.kaazing.k3po.lang.internal.parser.ScriptParseException;
import org.kaazing.k3po.lang.internal.parser.ScriptParserImpl;

public class ControlServerHandler extends ControlUpstreamHandler {

    private static final Map<String, Object> EMPTY_ENVIRONMENT = Collections.<String, Object>emptyMap();

    private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(ControlServerHandler.class);
    private static final String ERROR_MSG_NOT_PREPARED = "Script has not been prepared or is still preparing\n";
    private static final String ERROR_MSG_ALREADY_PREPARED = "Script already prepared\n";
    private static final String ERROR_MSG_ALREADY_STARTED = "Script has already been started\n";

    // the dispose future of the robot that is executing the current test. Will be used to check when it is disposed 
    // in order to start this test
    private AtomicReference<Robot> activeRobotRef;
    
    private Robot robot;
    private ChannelFutureListener whenAbortedOrFinished;
    
    private volatile boolean isFinishedSent = false;

    private final ChannelFuture channelClosedFuture = Channels.future(null);

    private ClassLoader scriptLoader;
    
    public ControlServerHandler(AtomicReference<Robot> activeRobotRef) {
        this.activeRobotRef = activeRobotRef;
    }
    
    public void setScriptLoader(ClassLoader scriptLoader) {
        this.scriptLoader = scriptLoader;
    }

    // Note that this is more than just the channel close future. It's a future that means not only
    // that this channel has closed but it is a future that tells us when this obj has processed the closed event.
    public ChannelFuture getChannelClosedFuture() {
        return channelClosedFuture;
    }

    @Override
    public void channelClosed(final ChannelHandlerContext ctx, final ChannelStateEvent e) throws Exception {
        if (robot != null) {
            robot.dispose().addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    channelClosedFuture.setSuccess();
                    ctx.sendUpstream(e);
                    activeRobotRef.compareAndSet(robot, null);
                }
            });
        }
    }

    @Override
    public void closeReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        if (robot != null) {
            robot.dispose().addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    ctx.getChannel().close();
                }
            });
        }
    }

    @Override
    public void prepareReceived(final ChannelHandlerContext ctx, MessageEvent evt) throws Exception {
        if (robot != null && robot.getPreparedFuture() != null) {
            sendErrorMessage(ctx, ERROR_MSG_ALREADY_PREPARED);
            return;
        }

        if (robot == null) {
            robot = new Robot();
        }

        if (activeRobotRef.get() != robot && ! activeRobotRef.compareAndSet(null, robot)) {
            Robot activeRobot = activeRobotRef.get();
            if (activeRobot == null) {
                // it seems the active robot finished in the mean time, so we will try again
                prepareReceived(ctx, evt);
            } else {
                activeRobot.getDisposedFuture().addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        ((NioSocketChannel) ctx.getChannel()).getWorker().executeInIoThread(() -> {
                            try {
                                prepareReceived(ctx, evt);
                            } catch (Exception e) {
                                sendErrorMessage(ctx, e);
                            }
                        }, true);
                    }
                });
                return;
            }
        }

        //just in case it was called after connection was closed (test timeout ?)
        if (ctx.getChannel().getCloseFuture().isDone()) {
            return;
        }

        final PrepareMessage prepare = (PrepareMessage) evt.getMessage();

        // enforce control protocol version
        String version = prepare.getVersion();
        if (!"2.0".equals(version)) {
            sendVersionError(ctx);
            return;
        }

        List<String> scriptNames = prepare.getNames();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("preparing script(s) " + scriptNames);
        }

        whenAbortedOrFinished = whenAbortedOrFinished(ctx);

        String originScript = "";
        String origin = prepare.getOrigin();
        if (origin != null) {
            try {
                originScript = OriginScript.get(origin);
            } catch (URISyntaxException e) {
                throw new Exception("Could not find origin: ", e);
            }
        }

        ChannelFuture prepareFuture;

        String aggregatedScript = originScript + aggregateScript(scriptNames, scriptLoader);
        List<String> properyOverrides = prepare.getProperties();
        // consider hard fail in the future, when test frameworks support
        // override per test method

        aggregatedScript = injectOverridenProperties(aggregatedScript, properyOverrides);

        if (scriptLoader != null) {
            Thread currentThread = currentThread();
            ClassLoader contextClassLoader = currentThread.getContextClassLoader();
            try {
                currentThread.setContextClassLoader(scriptLoader);
                prepareFuture = robot.prepare(aggregatedScript);
            } finally {
                currentThread.setContextClassLoader(contextClassLoader);
            }
        } else {
            prepareFuture = robot.prepare(aggregatedScript);
        }

        final String scriptToRun = aggregatedScript;
        prepareFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(final ChannelFuture f) {
                PreparedMessage prepared = new PreparedMessage();
                prepared.setScript(scriptToRun);
                prepared.getBarriers().addAll(robot.getBarriersByName().keySet());
                writeEvent(ctx, prepared);
            }
        });
    }

    private String injectOverridenProperties(String aggregatedScript, List<String> scriptProperties)
            throws Exception, ScriptParseException {

        ScriptParserImpl parser = new ScriptParserImpl();

        for (String propertyToInject : scriptProperties) {
            String propertyName = parser.parseWithStrategy(propertyToInject, PROPERTY_NODE).getPropertyName();
            StringBuilder replacementScript = new StringBuilder();
            Pattern pattern = Pattern.compile("property\\s+" + propertyName + "\\s+.+");
            boolean matchFound = false;
            for (String scriptLine : aggregatedScript.split("\\r?\\n")) {
                if (pattern.matcher(scriptLine).matches()) {
                    matchFound = true;
                    replacementScript.append(propertyToInject + "\n");
                } else {
                    replacementScript.append(scriptLine + "\n");
                }
            }
            if (!matchFound) {
                String errorMsg = "Received " + propertyToInject + " in PREPARE but found no where to substitute it";
                LOGGER.error(errorMsg);
                throw new Exception(errorMsg);
            }
            aggregatedScript = replacementScript.toString();
        }
        return aggregatedScript;
    }

    /*
     * Public static because it is used in test utils
     */
    public static String aggregateScript(List<String> scriptNames, ClassLoader scriptLoader)
            throws URISyntaxException, IOException {
        final StringBuilder aggregatedScript = new StringBuilder();
        for (String scriptName : scriptNames) {
            String scriptNameWithExtension = format("%s.rpt", scriptName);
            Path scriptPath = Paths.get(scriptNameWithExtension);
            scriptNameWithExtension = URI.create(scriptNameWithExtension).normalize().getPath();
            String script = null;

            assert !scriptPath.isAbsolute();

            // resolve relative scripts in local file system
            if (scriptLoader != null) {
                // resolve relative scripts from class loader to support
                // separated specification projects that include Robot scripts only
                URL resource = scriptLoader.getResource(scriptNameWithExtension);
                if (resource != null) {
                    URI resourceURI = resource.toURI();
                    if ("file".equals(resourceURI.getScheme())) {
                        Path resourcePath = Paths.get(resourceURI);
                        script = readScript(resourcePath);
                    } else {
                        try (FileSystem fileSystem = newFileSystem(resourceURI, EMPTY_ENVIRONMENT)) {
                            Path resourcePath = Paths.get(resourceURI);
                            script = readScript(resourcePath);
                        }
                    }
                }
            }

            if (script == null) {
                throw new RuntimeException("Script not found: " + scriptPath);
            }

            aggregatedScript.append(script);
        }
        return aggregatedScript.toString();
    }

    private static String readScript(Path scriptPath) throws IOException {
        List<String> lines = Files.readAllLines(scriptPath, UTF_8);
        StringBuilder sb = new StringBuilder();
        for (String line : lines) {
            sb.append(line);
            sb.append("\n");
        }
        String script = sb.toString();
        return script;
    }

    @Override
    public void startReceived(final ChannelHandlerContext ctx, MessageEvent evt) throws Exception {
        if (robot == null || robot.getPreparedFuture() == null) {
            sendErrorMessage(ctx, ERROR_MSG_NOT_PREPARED);
            return;
        }

        if (robot.getStartedFuture().isDone()) {
            sendErrorMessage(ctx, ERROR_MSG_ALREADY_STARTED);
            return;
        }

        ChannelFuture startFuture = robot.start();
        startFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(final ChannelFuture f) {
                if (f.isSuccess()) {
                    final StartedMessage started = new StartedMessage();
                    writeEvent(ctx, started);
                } else {
                    sendErrorMessage(ctx, f.getCause());
                }
            }
        });

        assert whenAbortedOrFinished != null;
        robot.finish().addListener(whenAbortedOrFinished);
    }

    @Override
    public void abortReceived(final ChannelHandlerContext ctx, MessageEvent evt) throws Exception {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("ABORT");
        }

        if (robot == null || robot.getPreparedFuture() == null) {
            sendErrorMessage(ctx, ERROR_MSG_NOT_PREPARED);
            return;
        }

        assert whenAbortedOrFinished != null;
            robot.abort().addListener(whenAbortedOrFinished);
    }

    @Override
    public void notifyReceived(final ChannelHandlerContext ctx, MessageEvent evt) throws Exception {
        NotifyMessage notifyMessage = (NotifyMessage) evt.getMessage();
        final String barrier = notifyMessage.getBarrier();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("NOTIFY: " + barrier);
        }

        if (robot == null || robot.getPreparedFuture() == null) {
            sendErrorMessage(ctx, ERROR_MSG_NOT_PREPARED);
            return;
        }

        writeNotifiedOnBarrier(barrier, ctx);
        robot.notifyBarrier(barrier);
    }

    @Override
    public void awaitReceived(final ChannelHandlerContext ctx, MessageEvent evt) throws Exception {
        AwaitMessage awaitMessage = (AwaitMessage) evt.getMessage();
        final String barrier = awaitMessage.getBarrier();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("AWAIT: " + barrier);
        }

        if (robot == null || robot.getPreparedFuture() == null) {
            sendErrorMessage(ctx, ERROR_MSG_NOT_PREPARED);
            return;
        }

        writeNotifiedOnBarrier(barrier, ctx);
    }

    private void writeNotifiedOnBarrier(final String barrier, final ChannelHandlerContext ctx) throws Exception {
        robot.awaitBarrier(barrier).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    LOGGER.debug("sending NOTIFIED: " + barrier);
                    final NotifiedMessage notified = new NotifiedMessage();
                    notified.setBarrier(barrier);
                    writeEvent(ctx, notified);
                }
            }
        });
    }

    private ChannelFutureListener whenAbortedOrFinished(final ChannelHandlerContext ctx) {
        final AtomicBoolean oneTimeOnly = new AtomicBoolean();
        return new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (oneTimeOnly.compareAndSet(false, true)) {
                    sendFinishedMessage(ctx);
                }
            }
        };
    }

    private void sendFinishedMessage(ChannelHandlerContext ctx) {

        String observedScript = robot.getObservedScript();

        FinishedMessage finishedMessage = new FinishedMessage();
        finishedMessage.setScript(observedScript);
        Map<String, Barrier> barriers = robot.getBarriersByName();
        
        for (String name : barriers.keySet()) {
            if (barriers.get(name).getFuture().isSuccess())
                finishedMessage.getCompletedBarriers().add(name);
            else
                finishedMessage.getIncompleteBarriers().add(name);
        }
        writeEvent(ctx, finishedMessage);
    }

    private void sendVersionError(ChannelHandlerContext ctx) {
        ErrorMessage errorMessage = new ErrorMessage();
        errorMessage.setSummary("Bad control protocol version");
        errorMessage.setDescription("Robot requires control protocol version 2.0");
        writeEvent(ctx, errorMessage);
    }

    // will send no message after the FINISHED
    private void writeEvent(final ChannelHandlerContext ctx, final Object message) {
        if (isFinishedSent)
            return;
        
        if (message instanceof FinishedMessage)
            isFinishedSent = true;

        Channels.write(ctx, Channels.future(null), message);
    }
}
