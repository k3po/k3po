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

package org.kaazing.robot.driver.behavior.handler.event.http;

import static org.jboss.netty.buffer.ChannelBuffers.copiedBuffer;
import static org.jboss.netty.util.CharsetUtil.UTF_8;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.http.QueryStringDecoder;
import org.kaazing.robot.driver.behavior.handler.codec.MessageDecoder;
import org.kaazing.robot.driver.behavior.handler.codec.MessageMismatchException;
import org.kaazing.robot.driver.behavior.handler.command.AbstractCommandHandler;
import org.kaazing.robot.driver.netty.bootstrap.http.HttpChannelConfig;

public class ReadHttpParameterHandler extends AbstractCommandHandler {

    private String name;
    private MessageDecoder valueDecoder;

    public ReadHttpParameterHandler(String name, MessageDecoder valueDecoder) {
        this.name = name;
        this.valueDecoder = valueDecoder;
    }

    @Override
    protected void invokeCommand(ChannelHandlerContext ctx) throws Exception {
        HttpChannelConfig httpConfig = (HttpChannelConfig) ctx.getChannel().getConfig();
        QueryStringDecoder query = httpConfig.getReadQuery();
        Map<String, List<String>> parameters = query.getParameters();
        List<String> parameterValues = parameters.get(name);
        int parameterValueCount = parameterValues.size();
        if (parameterValueCount == 0) {
            throw new MessageMismatchException("Missing HTTP query parameter", name, null);
        }
        else if (parameterValueCount == 1) {
            // efficiently handle single-valued HTTP query parameter
            try {
                String parameterValue = parameterValues.get(0);
                valueDecoder.decode(copiedBuffer(parameterValue, UTF_8));
                getHandlerFuture().setSuccess();
            }
            catch (Exception e) {
                getHandlerFuture().setFailure(e);
            }
        }
        else {
            // attempt to match each HTTP query parameter value with decoder
            // throw last decode failure exception if none match
            Exception decodeFailure = null;
            for (Iterator<String> $i = parameterValues.iterator(); $i.hasNext();) {
                String headerValue = $i.next();
                try {
                    valueDecoder.decode(copiedBuffer(headerValue, UTF_8));
                    $i.remove();
                    break;
                }
                catch (Exception e) {
                    decodeFailure = e;
                }
            }

            if (parameterValues.size() != parameterValueCount) {
                parameters.put(name, parameterValues);
            }
            else {
                assert decodeFailure != null;
                throw decodeFailure;
            }
        }
    }

}
