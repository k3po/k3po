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

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelDownstreamHandler;

import org.kaazing.robot.driver.control.ControlMessage;

public class ControlDownstreamHandler extends SimpleChannelDownstreamHandler {

    @Override
    public void writeRequested(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        ControlMessage message = (ControlMessage) e.getMessage();

        switch (message.getKind()) {
        case PREPARED:
            writePreparedRequested(ctx, e);
            break;
        case STARTED:
            writeStartedRequested(ctx, e);
            break;
        case FINISHED:
            writeFinishedRequested(ctx, e);
            break;
            case ERROR:
                writeErrorRequested(ctx, e);
                break;
        default:
            throw new IllegalArgumentException(String.format("Unexpected control message: %s", message.getKind()));
        }

    }

    public void writePreparedRequested(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        super.writeRequested(ctx, e);
    }

    public void writeStartedRequested(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        super.writeRequested(ctx, e);
    }

    public void writeFinishedRequested(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        super.writeRequested(ctx, e);
    }

    public void writeErrorRequested(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        super.writeRequested(ctx, e);
    }

}
