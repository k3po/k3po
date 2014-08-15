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

import static org.jboss.netty.buffer.ChannelBuffers.copiedBuffer;
import static org.jboss.netty.util.CharsetUtil.UTF_8;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;
import org.kaazing.robot.driver.control.BadRequestMessage;
import org.kaazing.robot.driver.control.ControlMessage;
import org.kaazing.robot.driver.control.ErrorMessage;

import com.fasterxml.jackson.databind.ObjectMapper;

public class HttpControlResponseEncoder extends OneToOneEncoder {

    private static final long TIME_LIMIT_MILLIS = 500;

    private Map<String, Object> scriptResultCache = new HashMap<String, Object>();
    private Date lastResultRequestTime;
    private String lastResultRequestName;
    private final Runnable clearLastRequestEntry = new Runnable() {
        public void run() {
            if (lastResultRequestName != null) {
                scriptResultCache.remove(lastResultRequestName);
            }
        }
    };
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @Override
    protected Object encode(ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception {
        if (msg instanceof ControlMessage) {
            ControlMessage controlMessage = (ControlMessage) msg;
            ObjectMapper mapper = new ObjectMapper();

            switch (controlMessage.getKind()) {
            case PREPARED:
            case STARTED:
            case ERROR:
                return createHttpResponse(mapper, controlMessage, HttpResponseStatus.OK);
            case BAD_REQUEST:
                return createHttpResponse(mapper, controlMessage, HttpResponseStatus.BAD_REQUEST);
            case FINISHED:
                scriptResultCache.put(controlMessage.getName(), createHttpResponse(mapper, controlMessage, HttpResponseStatus.OK));
                return ChannelBuffers.EMPTY_BUFFER;
            case CLEAR_CACHE:
                scriptResultCache.clear();
                lastResultRequestTime = null;
                lastResultRequestName = null;
                return ChannelBuffers.EMPTY_BUFFER;
            case RESULT_REQUEST:
                if (scriptResultCache.containsKey(controlMessage.getName())
                        && (lastResultRequestName == null || System.currentTimeMillis()
                                - lastResultRequestTime.getTime() <= TIME_LIMIT_MILLIS)) {
                    lastResultRequestName = controlMessage.getName();
                    lastResultRequestTime = new Date();
                    scheduler.schedule(clearLastRequestEntry, TIME_LIMIT_MILLIS, TimeUnit.MILLISECONDS);
                    return scriptResultCache.get(controlMessage.getName());
                } else if (lastResultRequestName != null && lastResultRequestName.equals(controlMessage.getName())) {
                    BadRequestMessage badRequest = new BadRequestMessage();
                    badRequest.setName(controlMessage.getName());
                    badRequest.setContent("Invalid Request. No results for requested script.");
                    return createHttpResponse(mapper, badRequest, HttpResponseStatus.BAD_REQUEST);
                } else {
                    ErrorMessage errorMessage = new ErrorMessage();
                    errorMessage.setName(controlMessage.getName());
                    errorMessage.setDescription("Script execution is not complete. Try again later");
                    errorMessage.setSummary("Early Request");
                    return createHttpResponse(mapper, errorMessage, HttpResponseStatus.OK);
                }
            default:
                break;
            }
        }

        // unknown message
        return msg;
    }

    private Object createHttpResponse(ObjectMapper mapper, ControlMessage message, HttpResponseStatus status) throws Exception {
        DefaultHttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, status);
        response.headers().add(HttpHeaders.Names.CONTENT_TYPE, "text/html");

        String jsonString = mapper.writeValueAsString(message);
        ChannelBuffer content = copiedBuffer(jsonString, UTF_8);
        response.headers().add(HttpHeaders.Names.CONTENT_LENGTH, String.format("%d", content.readableBytes()));
        response.setContent(content);

        return response;
    }
}
