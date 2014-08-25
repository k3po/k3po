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

package org.kaazing.robot.driver.control.handler;

import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.FileSystems.newFileSystem;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.logging.InternalLoggerFactory;
import org.kaazing.robot.driver.Robot;
import org.kaazing.robot.driver.behavior.RobotCompletionFuture;
import org.kaazing.robot.driver.control.AbortMessage;
import org.kaazing.robot.driver.control.ErrorMessage;
import org.kaazing.robot.driver.control.FinishedMessage;
import org.kaazing.robot.driver.control.PrepareMessage;
import org.kaazing.robot.driver.control.PreparedMessage;
import org.kaazing.robot.driver.control.StartMessage;
import org.kaazing.robot.driver.control.StartedMessage;
import org.kaazing.robot.lang.parser.ScriptParseException;

public class ControlServerHandler extends ControlUpstreamHandler {

    private static final InternalLogger logger = InternalLoggerFactory.getInstance(ControlServerHandler.class);

    private Robot robot;
    private RobotCompletionFuture scriptDoneFuture;

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

    // public void completeShutDown(long timeout) throws TimeoutException {
    // long timeoutExpiredMs = System.currentTimeMillis() + timeout;
    // if (robot != null && !robot.isDestroyed()) {
    // boolean destroyed = robot.destroy();
    // while (!destroyed) {
    // Thread.yield();
    // destroyed = robot.destroy();
    // if (!destroyed && (System.currentTimeMillis() >= timeoutExpiredMs)) {
    // throw new TimeoutException("Could not destroy robot in " + timeout + " milliseconds.");
    // }
    // }
    // }
    // logger.info("Shutdown robot succesfully");
    // }

    @Override
    public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        logger.debug("Control channel closed");
        if (robot != null) {
            robot.destroy();
        }
        channelClosedFuture.setSuccess();
        ctx.sendUpstream(e);
    }

    @Override
    public void prepareReceived(final ChannelHandlerContext ctx, MessageEvent evt) throws Exception {

        final PrepareMessage prepare = (PrepareMessage) evt.getMessage();

        String scriptName = prepare.getName();
        if (logger.isDebugEnabled()) {
            logger.debug("preparing robot execution for script " + scriptName);
        }

        String scriptNameWithExtension = format("%s.rpt", scriptName);

        robot = new Robot();

        ChannelFuture prepareFuture;
        try {
            // @formatter:off
            Path scriptPath = Paths.get(scriptNameWithExtension);
            String script = null;

            if (!scriptPath.isAbsolute()) {
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
                        }
                        else {
                            try (FileSystem fileSystem = newFileSystem(resourceURI, Collections.<String, Object>emptyMap())) {
                                Path resourcePath = Paths.get(resourceURI);
                                script = readScript(resourcePath);
                            }
                        }
                    }
                }
            }
            else {
                // backwards compatibility
                script = readScript(Paths.get(scriptName));
            }

            if (script == null) {
                throw new RuntimeException("Script not found: " + scriptPath);
            }

            prepareFuture = robot.prepare(script);
            // @formatter:on
        } catch (Exception e) {
            sendErrorMessage(ctx, e, scriptName);
            return;
        }

        prepareFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(final ChannelFuture f) {
                PreparedMessage prepared = new PreparedMessage();
                prepared.setName(prepare.getName());
                Channels.write(ctx, Channels.future(null), prepared);
            }
        });
    }

    private String readScript(Path scriptPath) throws IOException {
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

        final boolean infoDebugEnabled = logger.isDebugEnabled();
        final StartMessage start = (StartMessage) evt.getMessage();
        final String name = start.getName();

        if (infoDebugEnabled) {
            logger.debug("starting robot execution for script " + name);
        }

        try {
            ChannelFuture startFuture = robot.start();
            startFuture.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(final ChannelFuture f) {
                    final StartedMessage started = new StartedMessage();
                    started.setName(name);
                    Channels.write(ctx, Channels.future(null), started);
                }
            });
        } catch (Exception e) {
            sendErrorMessage(ctx, e, name);
            return;
        }

        scriptDoneFuture = robot.getScriptCompleteFuture();

        scriptDoneFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(final ChannelFuture f) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Script " + name + " completed");
                }

                sendFinishedMessage(ctx, name, scriptDoneFuture);
            }
        });
    }

    @Override
    public void abortReceived(ChannelHandlerContext ctx, MessageEvent evt) throws Exception {
        AbortMessage abort = (AbortMessage) evt.getMessage();
        if (logger.isInfoEnabled()) {
            logger.debug("Aborting script " + abort.getName());
        }
        robot.abort();
        if (ctx.getPipeline().get("http.control.request.decoder") != null && robot != null && robot.getScriptCompleteFuture().isDone()) {
            // is HTTP
            sendFinishedMessage(ctx, abort.getName(), robot.getScriptCompleteFuture());
        } else if (robot != null && !robot.getStartedFuture().isDone()) {
            // is TCP. handle case where abort before started
            sendFinishedMessage(ctx, abort.getName(), robot.getScriptCompleteFuture());
        }
    }

    private void sendFinishedMessage(ChannelHandlerContext ctx, String name, RobotCompletionFuture scriptDoneFuture) {
        String expectedScript = scriptDoneFuture.getExpectedScript();
        String observedScript = scriptDoneFuture.getObservedScript();

        FinishedMessage finished = new FinishedMessage();
        finished.setName(name);
        finished.setExpectedScript(expectedScript);
        finished.setObservedScript(observedScript);
        Channels.write(ctx, Channels.future(null), finished);
    }

    private void sendErrorMessage(ChannelHandlerContext ctx, Exception exception, String name) {
        ErrorMessage error = new ErrorMessage();
        error.setDescription(exception.getMessage());
        error.setName(name);

        if (exception instanceof ScriptParseException) {
            if (logger.isDebugEnabled()) {
                logger.error("Caught exception trying to parse script. Sending error to client", exception);
            } else {
                logger.error("Caught exception trying to parse script. Sending error to client. Due to " + exception);
            }
            error.setSummary("Parse Error");
            Channels.write(ctx, Channels.future(null), error);
        } else {
            logger.error("Internal Error. Sending error to client", exception);
            error.setSummary("Internal Error");
            Channels.write(ctx, Channels.future(null), error);
        }
    }
}
