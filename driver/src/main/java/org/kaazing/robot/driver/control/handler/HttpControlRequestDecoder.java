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
import static org.jboss.netty.util.CharsetUtil.UTF_8;
import static org.jboss.netty.buffer.ChannelBuffers.copiedBuffer;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.DownstreamMessageEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.UpstreamMessageEvent;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.kaazing.robot.driver.control.AbortMessage;
import org.kaazing.robot.driver.control.BadRequestMessage;
import org.kaazing.robot.driver.control.ClearCacheMessage;
import org.kaazing.robot.driver.control.ControlMessage;
import org.kaazing.robot.driver.control.PrepareMessage;
import org.kaazing.robot.driver.control.ResultRequestMessage;
import org.kaazing.robot.driver.control.StartMessage;

public class HttpControlRequestDecoder extends SimpleChannelHandler {

    private static enum State {
        INITIAL, PREPARED, STARTED, FINISHED, ERROR, ABORTED;
    };

    private State currentState = State.INITIAL;

    @Override
    public void writeRequested(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        ctx.sendDownstream(new DownstreamMessageEvent(e.getChannel(), e.getFuture(), e.getMessage(), e
                .getRemoteAddress()));
        Object msg = e.getMessage();
        if (msg instanceof ControlMessage) {
            ControlMessage controlMessage = (ControlMessage) msg;
            synchronized (this) {
                switch (controlMessage.getKind()) {
                case PREPARED:
                    currentState = State.PREPARED;
                    break;
                case STARTED:
                    currentState = State.STARTED;
                    break;
                case FINISHED:
                    if (currentState == State.ABORTED) {
                        ResultRequestMessage resultRequest = new ResultRequestMessage();
                        resultRequest.setName(controlMessage.getName());
                        ctx.sendDownstream(new DownstreamMessageEvent(e.getChannel(), e.getFuture(), resultRequest, e.getRemoteAddress()));
                    }
                    currentState = State.FINISHED;
                    break;
                case ERROR:
                    currentState = State.ERROR;
                    break;
                default:
                    break;
                }
            }
        }
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        if (e.getMessage() instanceof HttpRequest) {
            HttpRequest request = (HttpRequest) e.getMessage();
            String URI = request.getUri();
            if (URI == null || URI.length() < 2 || URI.charAt(0) != '/') {
                sendInvalidRequestResponse(
                        ctx,
                        e,
                        copiedBuffer(
                                format("Malformed HTTP POST request. Was expecting '/{PREPARE, START, RESULT_REQUEST, ABORT}' but received '%s'",
                                        URI), UTF_8));
                return;
            }

            HttpMethod method = request.getMethod();
            if (method != HttpMethod.POST) {
                sendInvalidRequestResponse(
                        ctx,
                        e,
                        copiedBuffer(
                                format("Malformed HTTP request. Was expecting 'POST' but received '%s'",
                                        method.toString()), UTF_8));
                return;
            }

            // safe, checked above
            String messageType = URI.substring(1).toUpperCase();
            ControlMessage msg = null;
            char type = messageType.charAt(0);
            switch (type) {
            case 'P':
                if (messageType.equals("PREPARE")) {
                    msg = new PrepareMessage();
                }
                break;
            case 'S':
                if (messageType.equals("START")) {
                    msg = new StartMessage();
                }
                break;
            case 'A':
                if (messageType.equals("ABORT")) {
                    msg = new AbortMessage();
                }
                break;
            case 'R':
                if (messageType.equals("RESULT_REQUEST")) {
                    msg = new ResultRequestMessage();
                }
                break;
            default:
                sendInvalidRequestResponse(
                        ctx,
                        e,
                        copiedBuffer(
                                format("Malformed HTTP POST request. Was expecting '/{PREPARE, START, RESULT_REQUEST, ABORT}' but received '%s'",
                                        URI), UTF_8));
                return;
            }

            String content = new String(request.getContent().array(), "UTF-8");

            // PREPARE, START, RESULT_REQUEST and ABORT are all of format: 'name:scriptName\n\n'
            int startIndex = content.indexOf("name:");
            int beginIndex = startIndex + "name:".length();
            int endIndex = content.lastIndexOf("\n\n");

            if (startIndex == -1 || endIndex == -1 || beginIndex == endIndex) {
                sendInvalidRequestResponse(
                        ctx,
                        e,
                        copiedBuffer(
                                format("Malformed HTTP POST request content. Was expecting 'name:scriptName\n\n' but received '%s'",
                                        content), UTF_8));
                return;
            }
            String name = content.substring(beginIndex, endIndex);

            msg.setName(name);

            // check if request is valid from given state and respond with bad request if invalid
            if (msg instanceof PrepareMessage) {
                synchronized (this) {
                    if (currentState != State.INITIAL && currentState != State.FINISHED && currentState != State.ERROR) {
                        sendBadRequestMessage(ctx, e, "PREPARE", msg.getName());
                    } else {
                        ClearCacheMessage clearCachedEntry = new ClearCacheMessage();
                        clearCachedEntry.setName(msg.getName());
                        ctx.sendDownstream(new DownstreamMessageEvent(e.getChannel(), e.getFuture(), clearCachedEntry, e.getRemoteAddress()));
                        ctx.sendUpstream(new UpstreamMessageEvent(e.getChannel(), msg, e.getRemoteAddress()));
                    }
                }
            } else if (msg instanceof StartMessage) {
                synchronized (this) {
                    if (currentState != State.PREPARED) {
                        sendBadRequestMessage(ctx, e, "START", msg.getName());
                    } else {
                        ctx.sendUpstream(new UpstreamMessageEvent(e.getChannel(), msg, e.getRemoteAddress()));
                    }
                }
            } else if (msg instanceof ResultRequestMessage) {
                synchronized (this) {
                    if (currentState != State.FINISHED && currentState != State.ERROR && currentState != State.STARTED) {
                        sendBadRequestMessage(ctx, e, "RESULT", msg.getName());
                    } else {
                        ctx.sendDownstream(new DownstreamMessageEvent(e.getChannel(), e.getFuture(), msg, e
                                .getRemoteAddress()));
                    }
                }
            } else if (msg instanceof AbortMessage) {
                synchronized (this) {
                    switch (currentState) {
                    case INITIAL:
                        sendBadRequestMessage(ctx, e, "INITIAL", msg.getName());
                        break;
                    case ERROR:
                        sendBadRequestMessage(ctx, e, "ERROR", msg.getName());
                        break;
                    case ABORTED:
                        sendBadRequestMessage(ctx, e, "ABORTED", msg.getName());
                        break;
                    case FINISHED:
                    case PREPARED:
                    case STARTED:
                    default:
                        currentState = State.ABORTED;
                        ctx.sendUpstream(new UpstreamMessageEvent(e.getChannel(), msg, e.getRemoteAddress()));
                        break;
                    }
                }
            } else {
                ctx.sendUpstream(new UpstreamMessageEvent(e.getChannel(), msg, e.getRemoteAddress()));
            }
        } else {
            // unknown msg
            ctx.sendUpstream(e);
        }
    }

    private void sendBadRequestMessage(ChannelHandlerContext ctx, MessageEvent e, String content, String name) {
        BadRequestMessage badRequest = new BadRequestMessage();
        badRequest.setName(name);
        badRequest.setContent(content);

        ctx.sendDownstream(new DownstreamMessageEvent(e.getChannel(), e.getFuture(), badRequest, e.getRemoteAddress()));
    }

    private void sendInvalidRequestResponse(ChannelHandlerContext ctx, MessageEvent e, ChannelBuffer content) {
        DefaultHttpResponse msg = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST);
        msg.headers().add(HttpHeaders.Names.CONTENT_TYPE, "text/html");
        msg.headers().add(HttpHeaders.Names.CONTENT_LENGTH, String.format("%d", content.readableBytes()));
        msg.setContent(content);

        ctx.sendDownstream(new DownstreamMessageEvent(e.getChannel(), e.getFuture(), msg, e.getRemoteAddress()));
    }
}
