/*
 * Copyright 2014, Kaazing Corporation. All rights reserved.
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
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.logging.InternalLoggerFactory;
import org.kaazing.k3po.driver.internal.Robot;
import org.kaazing.k3po.driver.internal.control.AwaitMessage;
import org.kaazing.k3po.driver.internal.control.ErrorMessage;
import org.kaazing.k3po.driver.internal.control.FinishedMessage;
import org.kaazing.k3po.driver.internal.control.NotifiedMessage;
import org.kaazing.k3po.driver.internal.control.NotifyMessage;
import org.kaazing.k3po.driver.internal.control.PrepareMessage;
import org.kaazing.k3po.driver.internal.control.PreparedMessage;
import org.kaazing.k3po.driver.internal.control.StartedMessage;
import org.kaazing.k3po.lang.internal.parser.ScriptParseException;

public class ControlServerHandler extends ControlUpstreamHandler {

    private static final Map<String, Object> EMPTY_ENVIRONMENT = Collections.<String, Object>emptyMap();

    private static final InternalLogger logger = InternalLoggerFactory.getInstance(ControlServerHandler.class);

    private Robot robot;
    private ChannelFutureListener whenAbortedOrFinished;

    private final ChannelFuture channelClosedFuture = Channels.future(null);

    private ClassLoader scriptLoader;

    public void setScriptLoader(ClassLoader scriptLoader) {
        this.scriptLoader = scriptLoader;
    }

    // Note that this is more than just the channel close future. It's a future that means not only
    // that this channel has closed but it is a future that tells us when this obj has processed the closed event.
    public ChannelFuture getChannelClosedFuture() {
        return channelClosedFuture;
    }

    @Override
    public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        if (robot != null) {
            robot.destroy();
        }
        channelClosedFuture.setSuccess();
        ctx.sendUpstream(e);
    }

    @Override
    public void prepareReceived(final ChannelHandlerContext ctx, MessageEvent evt) throws Exception {

        final PrepareMessage prepare = (PrepareMessage) evt.getMessage();

        // enforce control protocol version
        String version = prepare.getVersion();
        if (!"2.0".equals(version)) {
            sendVersionError(ctx);
            return;
        }

        List<String> scriptNames = prepare.getNames();
        if (logger.isDebugEnabled()) {
            logger.debug("preparing script(s) " + scriptNames);
        }

        robot = new Robot();
        whenAbortedOrFinished = whenAbortedOrFinished(ctx);

        ChannelFuture prepareFuture;
        try {

            final String aggregatedScript = aggregateScript(scriptNames, scriptLoader);

            if (scriptLoader != null) {
                Thread currentThread = currentThread();
                ClassLoader contextClassLoader = currentThread.getContextClassLoader();
                try {
                    currentThread.setContextClassLoader(scriptLoader);
                    prepareFuture =
                            robot.prepare(aggregatedScript.toString());
                }
                finally {
                    currentThread.setContextClassLoader(contextClassLoader);
                }
            }
            else {
                prepareFuture =
                        robot.prepare(aggregatedScript.toString());
            }

            prepareFuture.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(final ChannelFuture f) {
                    PreparedMessage prepared = new PreparedMessage();
                    prepared.setScript(aggregatedScript);
                    prepared.getBarriers().addAll(robot.getBarriersByName().keySet());
                    Channels.write(ctx, Channels.future(null), prepared);
                }
            });
        } catch (Exception e) {
            sendErrorMessage(ctx, e);
            return;
        }
    }

    /*
     * Public static because it is used in test utils
     */
    public static String aggregateScript(List<String> scriptNames, ClassLoader scriptLoader) throws URISyntaxException,
            IOException {
        final StringBuilder aggregatedScript = new StringBuilder();
        for (String scriptName : scriptNames) {
            String scriptNameWithExtension = format("%s.rpt", scriptName);
            Path scriptPath = Paths.get(scriptNameWithExtension);
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
        for (String line: lines) {
            sb.append(line);
            sb.append("\n");
        }
        String script = sb.toString();
        return script;
    }

    @Override
    public void startReceived(final ChannelHandlerContext ctx, MessageEvent evt) throws Exception {

        try {
            ChannelFuture startFuture = robot.start();
            startFuture.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(final ChannelFuture f) {
                    if (f.isSuccess()) {
                        final StartedMessage started = new StartedMessage();
                        Channels.write(ctx, Channels.future(null), started);
                    }
                    else {
                        sendErrorMessage(ctx, f.getCause());
                    }
                }
            });
        } catch (Exception e) {
            sendErrorMessage(ctx, e);
            return;
        }

        assert whenAbortedOrFinished != null;
        robot.finish().addListener(whenAbortedOrFinished);
    }

    @Override
    public void abortReceived(final ChannelHandlerContext ctx, MessageEvent evt) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("ABORT");
        }
        assert whenAbortedOrFinished != null;
        robot.abort().addListener(whenAbortedOrFinished);
    }

    @Override
    public void notifyReceived(final ChannelHandlerContext ctx, MessageEvent evt) throws Exception {
        NotifyMessage notifyMessage = (NotifyMessage) evt.getMessage();
        final String barrier = notifyMessage.getBarrier();
        if (logger.isDebugEnabled()) {
            logger.debug("NOTIFY: " + barrier);
        }
        robot.notifyBarrier(barrier);
        final NotifiedMessage notifiedMessaged = new NotifiedMessage();
        notifiedMessaged.setBarrier(barrier);
        logger.debug("sending NOTIFIED: " + barrier);
        ChannelFuture pendingNotify = ctx.getChannel().write(notifiedMessaged);
        pendingWrites.add(pendingNotify);
    }

    CopyOnWriteArrayList<ChannelFuture> pendingWrites = new CopyOnWriteArrayList<>();

    @Override
    public void awaitReceived(final ChannelHandlerContext ctx, MessageEvent evt) throws Exception {
        AwaitMessage awaitMessage = (AwaitMessage) evt.getMessage();
        final String barrier = awaitMessage.getBarrier();
        if (logger.isDebugEnabled()) {
            logger.debug("AWAIT: " + barrier);
        }
        robot.awaitBarrier(barrier).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    logger.debug("sending NOTIFIED: " + barrier);
                    final NotifiedMessage notified = new NotifiedMessage();
                    notified.setBarrier(barrier);
                    Channels.write(ctx, Channels.future(null), notified);
                }
            }
        });
    }

    private ChannelFutureListener whenAbortedOrFinished(final ChannelHandlerContext ctx) {
        final AtomicBoolean latch = new AtomicBoolean();
        return new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (latch.compareAndSet(false, true)) {
                    sendFinishedMessage(ctx);
                }
            }
        };
    }

    private void sendFinishedMessage(ChannelHandlerContext ctx) {

        Channel channel = ctx.getChannel();
        String observedScript = robot.getObservedScript();

        FinishedMessage finished = new FinishedMessage();
        finished.setScript(observedScript);
        for (ChannelFuture pendingWrite : pendingWrites) {
            try {
                pendingWrite.await(100);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        channel.write(finished);
    }

    private void sendVersionError(ChannelHandlerContext ctx) {
        Channel channel = ctx.getChannel();
        ErrorMessage error = new ErrorMessage();
        error.setSummary("Bad control protocol version");
        error.setDescription("Robot requires control protocol version 2.0");
        channel.write(error);
    }

    private void sendErrorMessage(ChannelHandlerContext ctx, Throwable throwable) {
        ErrorMessage error = new ErrorMessage();
        error.setDescription(throwable.getMessage());

        if (throwable instanceof ScriptParseException) {
            if (logger.isDebugEnabled()) {
                logger.error("Caught exception trying to parse script. Sending error to client", throwable);
            } else {
                logger.error("Caught exception trying to parse script. Sending error to client. Due to " + throwable);
            }
            error.setSummary("Parse Error");
            Channels.write(ctx, Channels.future(null), error);
        } else {
            logger.error("Internal Error. Sending error to client", throwable);
            error.setSummary("Internal Error");
            Channels.write(ctx, Channels.future(null), error);
        }
    }
}
